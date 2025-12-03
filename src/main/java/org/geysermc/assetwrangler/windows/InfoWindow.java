package org.geysermc.assetwrangler.windows;

import org.geysermc.assetwrangler.BuildConstants;
import org.geysermc.assetwrangler.Main;
import org.geysermc.assetwrangler.utils.JButtonUtils;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Random;

public class InfoWindow extends JDialog {
    private static final List<String> RNG_STRINGS = List.of(
            "The barrels... THEY'RE HERE!!!", "GayserMC will rise once more.", "Thunder will always strike harder than Rainbow!",
            "Crazy? I was crazy once. They locked me in a room.<br/>A bedrock room. A bedrock room with pink glitch.<br/>The pink glitch made me crazy. Crazy? I was crazy<br/>once...",
            "The Merge™ will come eventually... I hope.", "Trans rights are human rights!", "Unable to connect to world.", "Your version is not supported.",
            "Multiplayer Connection Failed", "Taking fine quality content and dumping it into<br/>a buggy mess since 2025.",
            "Minecraft", "Minceraft", "Rory VS Snapshot, who wins? (They're both cats)", "Mapping support when?",
            "java.io.IOException: Message not found!", "Your bug report will be handled within 5-10 business<br/>months.",
            "Mreow!", "eclipse will hear about what??", "Auri now has your IP address. (This is satire)",
            "If snapshot day got moved, is it cat day<br/>now instead of frog day?", "But... what if I can unbake the models?"
    );
    private static final Random RANDOM = new Random();

    public InfoWindow(JFrame parent) {
        super(parent);
        Main.registerForFrame(this);
        this.setLayout(new FlowLayout());
        StringBuilder labelText = new StringBuilder("<html>");
        labelText.append(BuildConstants.getInstance().getName());
        labelText.append(" (");
        labelText.append(BuildConstants.getInstance().getVersion());
        labelText.append(")<br/>Authors: ");
        labelText.append(BuildConstants.getInstance().getAuthors());
        labelText.append("<br/><br/>");
        labelText.append(RNG_STRINGS.get(RANDOM.nextInt(RNG_STRINGS.size())));
        labelText.append("</html>");

        JLabel nameVersionLabel = new JLabel(labelText.toString());

        nameVersionLabel.setIcon(Main.ICON);
        nameVersionLabel.setVerticalTextPosition(JLabel.TOP);
        nameVersionLabel.setHorizontalAlignment(JLabel.RIGHT);
        nameVersionLabel.setAlignmentX(0.0f);

        this.add(nameVersionLabel);

        this.add(JButtonUtils.linkButton("Discord Support Server", "https://discord.gg/GeyserMC/"));

        this.add(JButtonUtils.linkButton("GeyserMC Site", "https://geysermc.org/"));

        this.setSize(550, 280);
        this.setResizable(false);
        this.setLocationRelativeTo(parent);
        this.setVisible(true);
        this.setTitle(BuildConstants.getInstance().getName() + " Information");
    }
}
