package org.geysermc.assetwrangler.components.previews;

import com.twelvemonkeys.image.BufferedImageIcon;
import org.geysermc.assetwrangler.utils.ClipboardUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class TexturePreview extends JLabel {
    public TexturePreview(BufferedImage image, String relativePath) {
        StringBuilder label = new StringBuilder("<html>");
        label.append("Path: ");
        label.append(relativePath);
        float scaleX = 256f / image.getWidth();
        float scaleY = 256f / image.getHeight();

        float scale;
        int xOffset = 0, yOffset = 0;
        if (scaleX > scaleY) {
            scale = scaleY;
            xOffset = (256 - image.getWidth()) / 2;
        } else if (scaleX < scaleY) {
            scale = scaleX;
            yOffset = (256 - image.getHeight()) / 2;
        } else {
            scale = scaleX;
        }

        label.append("<br/>Resolution: %dx%d".formatted(image.getWidth(), image.getHeight()));
        BufferedImage scaledImage = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = (Graphics2D) scaledImage.getGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        graphics.drawImage(image, xOffset, yOffset, (int) (image.getWidth() * scale), (int) (image.getHeight() * scale), null);
        Icon icon = new ImageIcon(scaledImage);
        label.append("</html>");

        setText(label.toString());
        setIcon(icon);
        setHorizontalAlignment(SwingConstants.LEFT);
        setHorizontalTextPosition(JLabel.RIGHT);
        setVerticalTextPosition(JLabel.TOP);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (!e.isPopupTrigger()) return;
                JPopupMenu menu = new JPopupMenu();

                JMenuItem regularItem = new JMenuItem("Copy image");
                regularItem.addActionListener(ev -> {
                    ClipboardUtils.copyToClipboard(image);
                });
                menu.add(regularItem);
                JMenuItem scaledItem = new JMenuItem("Copy scaled image");
                scaledItem.addActionListener(ev -> {
                    ClipboardUtils.copyToClipboard(scaledImage);
                });
                menu.add(scaledItem);

                menu.show(e.getComponent(), e.getX(), e.getY());
            }
        });
    }
}
