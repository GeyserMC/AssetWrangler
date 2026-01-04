package org.geysermc.assetwrangler.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class ColorUtils {
    private static final Map<Color, BufferedImage> ICON_CACHE = new HashMap<>();

    public static BufferedImage getSolidImg(Color color) {
        if (ICON_CACHE.containsKey(color)) return ICON_CACHE.get(color);

        BufferedImage img = new BufferedImage(2, 14, BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.getGraphics();

        g.setColor(color);
        g.fillRect(0, 0, 2, 14);

        ICON_CACHE.put(color, img);

        return img;
    }

    /**
     * Returns the color with a certain transparency
     * @param c the color
     * @param alpha the alpha
     * @return the color with the alpha applied
     */
    public static Color withTransparency(Color c, float alpha) {
        if (c == null) return null;
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) alpha * 255);
    }
}
