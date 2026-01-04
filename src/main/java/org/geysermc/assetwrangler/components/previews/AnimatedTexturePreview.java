package org.geysermc.assetwrangler.components.previews;

import org.geysermc.assetwrangler.Main;
import org.geysermc.assetwrangler.components.ClosableComponent;
import org.geysermc.assetwrangler.utils.AnimationMeta;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class AnimatedTexturePreview extends JLabel implements ClosableComponent {
    private final List<ImagePart> imageParts;
    private final boolean interpolate;

    private final Thread timer;

    private double progressCurrently = 0;
    private int currentIndex = 0;
    private int nextIndex = 1;

    public AnimatedTexturePreview(BufferedImage img, AnimationMeta animationMeta, String relativePath) {
        this.setHorizontalTextPosition(JLabel.RIGHT);
        this.setVerticalTextPosition(JLabel.TOP);
        this.interpolate = animationMeta.isInterpolate();

        List<ImagePart> imageParts = new ArrayList<>();

        for (int i = 0; i < animationMeta.getFrames().size(); i++) {
            AnimationMeta.Frame frame = animationMeta.getFrames().get(i);
            imageParts.add(new ImagePart(img.getSubimage(0, i * animationMeta.getChunkHeight(), animationMeta.getWidth(), animationMeta.getHeight()), frame.getTime()));
        }

        this.imageParts = imageParts;

        String label = "<html>" + "Path: " +
                relativePath +
                "<br/>Animated Resolution: %dx%d".formatted(animationMeta.getWidth(), animationMeta.getHeight()) +
                "<br/>Actual Resolution: %dx%d".formatted(img.getWidth(), img.getHeight()) +
                "<br/>Frames: %d".formatted(animationMeta.getFrames().size()) +
                "<br/>Interpolation: %b".formatted(animationMeta.isInterpolate()) +
                "</html>";
        this.setText(label);
        this.calculateIcon();

        timer = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    return;
                }

                SwingUtilities.invokeLater(this::calculateIcon);
            }
        });

        timer.start();
    }

    public void close() {
        timer.interrupt();
    }

    public void calculateIcon() {
        Icon icon;
        BufferedImage image;
        if (interpolate && !Main.CONFIG.disableAnimationInterpolation()) {
            double progress = progressCurrently / imageParts.get(currentIndex).time;

            BufferedImage img1 = imageParts.get(currentIndex).image;
            BufferedImage img2 = imageParts.get(nextIndex).image;

            BufferedImage blendedImage = new BufferedImage(img1.getWidth(), img1.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = (Graphics2D) blendedImage.getGraphics();

            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            graphics.setComposite(AlphaComposite.SrcOver);
            graphics.drawImage(img1, 0, 0, null);

            graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) progress));
            graphics.drawImage(img2, 0, 0, null);

            image = blendedImage;
        } else {
            image = imageParts.get(currentIndex).image;
        }

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

        BufferedImage scaledImage = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = (Graphics2D) scaledImage.getGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        graphics.drawImage(image, xOffset, yOffset, (int) (image.getWidth() * scale), (int) (image.getHeight() * scale), null);
        icon = new ImageIcon(scaledImage);
        this.setIcon(icon);
        this.repaint();

        progressCurrently += 0.1;

        if (progressCurrently >= imageParts.get(currentIndex).time) {
            currentIndex = nextIndex;
            nextIndex++;
            if (nextIndex == imageParts.size()) nextIndex = 0;
            progressCurrently = 0;
        }
    }

    public static class ImagePart {
        public final BufferedImage image;
        public final int time;

        public ImagePart(BufferedImage image, int time) {
            this.image = image;
            this.time = time;
        }
    }
}
