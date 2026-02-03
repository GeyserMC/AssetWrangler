package org.geysermc.assetwrangler.components.previews;

import com.twelvemonkeys.image.BufferedImageIcon;
import org.geysermc.assetwrangler.Main;
import org.geysermc.assetwrangler.utils.ClipboardUtils;
import org.geysermc.assetwrangler.utils.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class TexturePreview extends JLabel {
    public TexturePreview(BufferedImage image, String relativePath) {
        String label = "<html>Path: " + relativePath +
                "<br/>Resolution: " + image.getWidth() + "x" + image.getHeight() +
                "</html>";

        setText(label);

        BufferedImage squareImage = ImageUtil.squareImage(Main.CONFIG.addCheckeredBackground() ? ImageUtil.checkerBackgroundImage(image) : image);
        float scale = 256f / squareImage.getWidth();

        BufferedImage scaledImage = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = (Graphics2D) scaledImage.getGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        graphics.drawImage(squareImage, 0, 0, (int) (squareImage.getWidth() * scale), (int) (squareImage.getHeight() * scale), null);

        setIcon(new BufferedImageIcon(scaledImage));

        setHorizontalAlignment(SwingConstants.LEFT);
        setHorizontalTextPosition(JLabel.RIGHT);
        setVerticalTextPosition(JLabel.TOP);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (!e.isPopupTrigger()) return;
                JPopupMenu menu = new JPopupMenu();

                JMenuItem pathItem = new JMenuItem("Copy relative path");
                pathItem.addActionListener(ev -> {
                    ClipboardUtils.copyToClipboard(relativePath);
                });
                menu.add(pathItem);

                JMenuItem regularItem = new JMenuItem("Copy x1 scaled image");
                regularItem.addActionListener(ev -> {
                    ClipboardUtils.copyToClipboard(image);
                });
                menu.add(regularItem);

                JMenuItem scaled5Item = new JMenuItem("Copy x5 scaled image");
                scaled5Item.addActionListener(ev -> {
                    ClipboardUtils.copyToClipboard(ImageUtil.scale(image, 5));
                });
                menu.add(scaled5Item);

                JMenuItem scaled10Item = new JMenuItem("Copy x10 scaled image");
                scaled10Item.addActionListener(ev -> {
                    ClipboardUtils.copyToClipboard(ImageUtil.scale(image, 10));
                });
                menu.add(scaled10Item);

                menu.show(e.getComponent(), e.getX(), e.getY());
            }
        });
    }
}
