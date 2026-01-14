package org.geysermc.assetwrangler.utils;

import net.kyori.adventure.text.format.TextColor;

import java.awt.*;

public class ComponentUtils {
    public static TextColor colorFromAwt(Color color) {
        return TextColor.color(color.getRed(), color.getGreen(), color.getBlue());
    }

    public static Color colorToAwt(TextColor color) {
        if (color == null) return Color.WHITE;
        return new Color(color.red(), color.green(), color.blue());
    }
}
