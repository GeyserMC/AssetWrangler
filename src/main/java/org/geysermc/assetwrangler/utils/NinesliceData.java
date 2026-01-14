package org.geysermc.assetwrangler.utils;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.awt.image.BufferedImage;

public interface NinesliceData {
    int x1();
    int y1();
    int x2();
    int y2();

    @Getter
    @Accessors(fluent = true)
    class Bedrock implements NinesliceData {
        private final int x1;
        private final int y1;
        private final int x2;
        private final int y2;

        public Bedrock(int[] ninesliceSize, BufferedImage img, int baseWidth, int baseHeight) {
            float xScale = (float) img.getWidth() / baseWidth;
            float yScale = (float) img.getHeight() / baseHeight;

            this.x1 = (int) (ninesliceSize[0] * xScale);
            this.y1 = (int) (ninesliceSize[1] * yScale);
            this.x2 = img.getWidth() - (int) (ninesliceSize[2] * xScale);
            this.y2 = img.getHeight() - (int) (ninesliceSize[3] * yScale);
        }
    }

    @Getter
    @Accessors(fluent = true)
    class Java implements NinesliceData {
        private final int x1;
        private final int y1;
        private final int x2;
        private final int y2;

        public Java(BufferedImage img, int baseWidth, int baseHeight, int x1, int y1, int x2, int y2) {
            float xScale = (float) img.getWidth() / baseWidth;
            float yScale = (float) img.getHeight() / baseHeight;

            this.x1 = (int) (x1 * xScale);
            this.y1 = (int) (y1 * yScale);
            this.x2 = img.getWidth() - (int) (x2 * xScale);
            this.y2 = img.getHeight() - (int) (y2 * yScale);
        }
    }
}
