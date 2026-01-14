package org.geysermc.assetwrangler.components;

import net.kyori.adventure.text.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.object.ObjectContents;
import net.kyori.adventure.text.object.PlayerHeadObjectContents;
import net.kyori.adventure.text.object.SpriteObjectContents;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.geysermc.assetwrangler.Logger;
import org.geysermc.assetwrangler.utils.ComponentUtils;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ComponentTextArea extends JTextPane {
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacySection();
    private static final PlainTextComponentSerializer PLAIN_SERIALIZER = PlainTextComponentSerializer.plainText();

    private final Style parentStyle;

    public ComponentTextArea() {
        this(Style.empty());
    }

    public ComponentTextArea(Style parentStyle) {
        setEditable(false);

        parentStyle.colorIfAbsent(NamedTextColor.WHITE);
        this.parentStyle = parentStyle;
    }

    public void appendLegacyText(String text) {
        for (String val : text.split("\n")) {
            Component component = LEGACY_SERIALIZER.deserialize(val);

            append(component);
            append("\n");
        }
    }

    public void append(Component c) {
        handleComponent(c, this.parentStyle);
    }

    public void append(String s) {
        append(s, null);
    }

    public void append(String s, AttributeSet attributes) {
        try {
            getStyledDocument().insertString(getStyledDocument().getLength(), s, attributes);
        } catch (BadLocationException e) {
            Logger.error("Unable to add to ComponentTextArea", e);
        }
    }

    private void handleComponent(Component component, Style style) {
        style.colorIfAbsent(NamedTextColor.WHITE);
        handleComponent(
                component, ComponentUtils.colorToAwt(style.color()),
                style.decoration(TextDecoration.BOLD).equals(TextDecoration.State.TRUE),
                style.decoration(TextDecoration.ITALIC).equals(TextDecoration.State.TRUE),
                style.decoration(TextDecoration.OBFUSCATED).equals(TextDecoration.State.TRUE),
                style.decoration(TextDecoration.STRIKETHROUGH).equals(TextDecoration.State.TRUE),
                style.decoration(TextDecoration.UNDERLINED).equals(TextDecoration.State.TRUE)
        );
    }

    private void handleComponent(
            Component component, Color parentColor, boolean parentBold,
            boolean parentItalics, boolean parentObfuscated, boolean parentStrikethrough,
            boolean parentUnderline
    ) {
        Style currentStyle = component.style();

        Color currentColor = parentColor;
        boolean currentBold = parentBold;
        boolean currentItalics = parentItalics;
        boolean currentObfuscated = parentObfuscated;
        boolean currentStrikethrough = parentStrikethrough;
        boolean currentUnderline = parentUnderline;

        if (currentStyle.color() != null) currentColor = ComponentUtils.colorToAwt(currentStyle.color());

        TextDecoration.State boldState = currentStyle.decoration(TextDecoration.BOLD);
        if (boldState.equals(TextDecoration.State.FALSE)) currentBold = false;
        else if (boldState.equals(TextDecoration.State.TRUE)) currentBold = true;

        TextDecoration.State italicsState = currentStyle.decoration(TextDecoration.ITALIC);
        if (italicsState.equals(TextDecoration.State.FALSE)) currentItalics = false;
        else if (italicsState.equals(TextDecoration.State.TRUE)) currentItalics = true;

        TextDecoration.State obfuscatedState = currentStyle.decoration(TextDecoration.OBFUSCATED);
        if (obfuscatedState.equals(TextDecoration.State.FALSE)) currentObfuscated = false;
        else if (obfuscatedState.equals(TextDecoration.State.TRUE)) currentObfuscated = true;

        TextDecoration.State strikethroughState = currentStyle.decoration(TextDecoration.STRIKETHROUGH);
        if (strikethroughState.equals(TextDecoration.State.FALSE)) currentStrikethrough = false;
        else if (strikethroughState.equals(TextDecoration.State.TRUE)) currentStrikethrough = true;

        TextDecoration.State underlineState = currentStyle.decoration(TextDecoration.BOLD);
        if (underlineState.equals(TextDecoration.State.FALSE)) currentUnderline = false;
        else if (underlineState.equals(TextDecoration.State.TRUE)) currentUnderline = true;

        SimpleAttributeSet set = new SimpleAttributeSet();

        StyleConstants.setForeground(set, currentColor);
        StyleConstants.setBold(set, currentBold);
        StyleConstants.setItalic(set, currentItalics);
        StyleConstants.setSuperscript(set, currentObfuscated);
        StyleConstants.setStrikeThrough(set, currentStrikethrough);
        StyleConstants.setUnderline(set, currentUnderline);

        switch (component) {
            case VirtualComponent virtual -> append(virtual.renderer().fallbackString(), set); // We aren't rendering
            case TextComponent text -> append(text.content(), set);
            case NBTComponent<?, ?> nbt -> //noinspection DataFlowIssue I checked for null! Respect that IntelliJ.
                    append("<nbt:path=%s:interpret=%b:separator=%s>".formatted(
                    nbt.nbtPath(), nbt.interpret(),
                    nbt.separator() == null ? " " :
                            PLAIN_SERIALIZER.serialize(nbt.separator())
            ), set);
            case KeybindComponent keybind -> append("<keybind:%s>".formatted(keybind.keybind()), set);
            case ObjectComponent object -> {
                ObjectContents contents = object.contents();
                if (contents instanceof PlayerHeadObjectContents playerHead) {
                    Map<String, String> properties = new HashMap<>();
                    if (playerHead.name() != null) properties.put("name", playerHead.name());
                    if (playerHead.id() != null) //noinspection DataFlowIssue I checked for null! Respect that IntelliJ.
                        properties.put("uuid", playerHead.id().toString());
                    if (playerHead.texture() != null) //noinspection DataFlowIssue I checked for null! Respect that IntelliJ.
                        properties.put("texture", playerHead.texture().asString());
                    properties.put("hat", String.valueOf(playerHead.hat()));
                    append("<playerhead:%s>".formatted(
                            String.join(":", properties.entrySet().stream().map(
                                    entry -> "%s=%s".formatted(entry.getKey(), entry.getValue())
                            ).toList())
                    ), set);
                } else if (contents instanceof SpriteObjectContents sprite) {
                    append("<sprite:atlas=%s:texture=%s>".formatted(
                            sprite.atlas().asString(), sprite.sprite().asString()
                    ), set);
                }
            }
            case ScoreComponent score -> append("<score:name=%s:objective=%s>".formatted(
                    score.name(), score.objective()
            ), set);
            case SelectorComponent selector -> //noinspection DataFlowIssue I checked for null! Respect that IntelliJ.
                    append("<selector:pattern=%s:separator=%s>".formatted(
                        selector.pattern(), selector.separator() == null ? " " :
                                PLAIN_SERIALIZER.serialize(selector.separator())
            ), set);
            case TranslatableComponent translatable -> append("<translatable:%s>".formatted(translatable.key()), set);
            default -> append("<unknowncomponent:%s>".formatted(component.getClass().getSimpleName()), set);
        }

        for (Component child : component.children()) {
            handleComponent(child, currentColor, currentBold, currentItalics,
                    currentObfuscated, currentStrikethrough, currentUnderline);
        }
    }
}
