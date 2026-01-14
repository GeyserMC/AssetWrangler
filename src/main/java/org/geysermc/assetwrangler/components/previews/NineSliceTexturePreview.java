package org.geysermc.assetwrangler.components.previews;

import com.twelvemonkeys.image.BufferedImageIcon;
import org.geysermc.assetwrangler.components.MainComponentMenu;
import org.geysermc.assetwrangler.panels.AssetPanel;
import org.geysermc.assetwrangler.treemodels.AssetTreeModel;
import org.geysermc.assetwrangler.utils.ClipboardUtils;
import org.geysermc.assetwrangler.utils.ImageUtil;
import org.geysermc.assetwrangler.utils.NinesliceData;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class NineSliceTexturePreview extends JPanel {
    private final BufferedImage img;
    private final String relativePath;
    private final NinesliceData ninesliceData;
    private final BufferedImage A;
    private final BufferedImage B;
    private final BufferedImage C;
    private final BufferedImage D;
    private final BufferedImage E;
    private final BufferedImage F;
    private final BufferedImage G;
    private final BufferedImage H;
    private final BufferedImage I;
    private final NineSliceRenderer renderer;
    
    private int width;
    private int height;

    public NineSliceTexturePreview(BufferedImage img, String relativePath, NinesliceData data) {
        this.img = img;
        this.relativePath = relativePath;
        this.ninesliceData = data;
        this.renderer = new NineSliceRenderer();

        this.A = ImageUtil.crop(
                img, 0, 0, ninesliceData.x1(), ninesliceData.y1()
        );
        this.B = ImageUtil.crop(
                img, ninesliceData.x1(), 0,
                ninesliceData.x2() - ninesliceData.x1(), ninesliceData.y1()
        );
        this.C = ImageUtil.crop(
                img, ninesliceData.x2(), 0,
                img.getWidth() - ninesliceData.x2(), ninesliceData.y1()
        );
        this.D = ImageUtil.crop(
                img, 0, ninesliceData.y1(),
                ninesliceData.x1(), ninesliceData.y2() - ninesliceData.y1()
        );
        this.E = ImageUtil.crop(
                img, ninesliceData.x1(), ninesliceData.y1(),
                ninesliceData.x2() - ninesliceData.x1(), ninesliceData.y2() - ninesliceData.y1()
        );
        this.F = ImageUtil.crop(
                img, ninesliceData.x2(), ninesliceData.y1(),
                img.getWidth() - ninesliceData.x2(), ninesliceData.y2() - ninesliceData.y1()
        );
        this.G = ImageUtil.crop(
                img, 0, ninesliceData.y2(),
                ninesliceData.x1(), img.getHeight() - ninesliceData.y2()
        );
        this.H = ImageUtil.crop(
                img, ninesliceData.x1(), ninesliceData.y2(),
                ninesliceData.x2() - ninesliceData.x1(), img.getHeight() - ninesliceData.y2()
        );
        this.I = ImageUtil.crop(
                img, ninesliceData.x2(), ninesliceData.y2(),
                img.getWidth() - ninesliceData.x2(), img.getHeight() - ninesliceData.y2()
        );

        width = img.getWidth();
        height = img.getHeight();

        this.setLayout(new GridLayout(1, 2));

        this.add(renderer);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(0, 1));

        String label = "<html>" + "Path: " +
                relativePath +
                "<br/>Resolution: %dx%d".formatted(img.getWidth(), img.getHeight()) +
                "<br/>X1: %d Y1: %d X2: %d Y2: %d".formatted(data.x1(), data.y1(), data.x2(), data.y2()) +
                "</html>";
        controlPanel.add(new JLabel(label));

        JLabel widthLabel = new JLabel("Preview Width: %d".formatted(width));
        controlPanel.add(widthLabel);
        JSlider widthControl = new JSlider(width, width + 100, width);
        widthControl.addChangeListener(e -> {
            width = widthControl.getValue();
            widthLabel.setText("Width: %d".formatted(width));
            /*if (!widthControl.getModel().getValueIsAdjusting())*/ redraw();
        });
        controlPanel.add(widthControl);

        JLabel heightLabel = new JLabel("Preview Height: %d".formatted(height));
        controlPanel.add(heightLabel);
        JSlider heightControl = new JSlider(height, height + 100, height);
        heightControl.addChangeListener(e -> {
            height = heightControl.getValue();
            heightLabel.setText("Height: %d".formatted(height));
            /*if (!heightControl.getModel().getValueIsAdjusting())*/ redraw();
        });
        controlPanel.add(heightControl);

        this.add(controlPanel);
    }

    private void redraw() {
        BufferedImage canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = canvas.getGraphics();

        int widthArea = width - ninesliceData.x1() - (img.getWidth() - ninesliceData.x2());
        int heightArea = height - ninesliceData.y1() - (img.getHeight() - ninesliceData.y2());

        if (A != null) g.drawImage(A, 0, 0, null);
        if (B != null) g.drawImage(repeatWidthImgUntilFilled(B, widthArea), ninesliceData.x1(), 0, null);
        if (C != null) g.drawImage(C, widthArea + ninesliceData.x1(), 0, null);

        if (D != null) g.drawImage(repeatHeightImgUntilFilled(D, heightArea), 0, ninesliceData.y1(), null);
        if (E != null) g.drawImage(
                repeatHeightImgUntilFilled(repeatWidthImgUntilFilled(E, widthArea), heightArea),
                ninesliceData.x1(), ninesliceData.y1(), null
        );
        if (F != null) g.drawImage(repeatHeightImgUntilFilled(F, heightArea), widthArea + ninesliceData.x1(), ninesliceData.y1(), null);

        if (G != null) g.drawImage(G, 0, heightArea + ninesliceData.y1(), null);
        if (H != null) g.drawImage(repeatWidthImgUntilFilled(H, widthArea), ninesliceData.x1(), heightArea + ninesliceData.y1(), null);
        if (I != null) g.drawImage(I, widthArea + ninesliceData.x1(), heightArea + ninesliceData.y1(), null);

        renderer.setIcon(canvas);
    }

    private BufferedImage repeatWidthImgUntilFilled(BufferedImage img, int size) {
        BufferedImage canvas = new BufferedImage(size, img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = canvas.getGraphics();

        int times = size / img.getWidth();

        for (int i = 0; i < times; i++) {
            g.drawImage(img, i * img.getWidth(), 0, null);
        }

        int remainder = size - (img.getWidth() * times);
        if (remainder == 0) return canvas;

        g.drawImage(ImageUtil.crop(img, 0, 0, remainder, img.getHeight()), img.getWidth() * times, 0, null);

        return canvas;
    }

    private BufferedImage repeatHeightImgUntilFilled(BufferedImage img, int size) {
        BufferedImage canvas = new BufferedImage(img.getWidth(), size, BufferedImage.TYPE_INT_ARGB);
        Graphics g = canvas.getGraphics();

        int times = size / img.getHeight();

        for (int i = 0; i < times; i++) {
            g.drawImage(img, 0, i * img.getHeight(), null);
        }

        int remainder = size - (img.getHeight() * times);
        if (remainder == 0) return canvas;

        g.drawImage(ImageUtil.crop(img, 0, 0, img.getWidth(), remainder), 0, img.getHeight() * times, null);

        return canvas;
    }

    private class NineSliceRenderer extends JLabel {
        public NineSliceRenderer() {
            BufferedImage startingImg = NineSliceTexturePreview.this.img;
            BufferedImage img = new BufferedImage(startingImg.getWidth(), startingImg.getHeight(), BufferedImage.TYPE_INT_ARGB);
            this.setIcon(img);

            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (!e.isPopupTrigger()) return;
                    Icon thisIcon = NineSliceRenderer.this.getIcon();

                    if (thisIcon instanceof BufferedImageIcon icon) {
                        JPopupMenu menu = new JPopupMenu();

                        JMenuItem item = new JMenuItem("Copy image");

                        item.addActionListener(ev -> {
                            BufferedImage img = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);

                            icon.paintIcon(null, img.getGraphics(), 0, 0);

                            ClipboardUtils.copyToClipboard(img);
                        });

                        menu.add(item);

                        menu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            });
        }

        public void setIcon(BufferedImage img) {
            this.setIcon(new BufferedImageIcon(ImageUtil.scale(img, 2)));
        }
    }
}
