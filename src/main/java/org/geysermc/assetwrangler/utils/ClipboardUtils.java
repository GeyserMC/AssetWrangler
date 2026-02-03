package org.geysermc.assetwrangler.utils;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.image.BufferedImage;

public class ClipboardUtils {
    public static void copyToClipboard(BufferedImage img) {
        TransferableImage trans = new TransferableImage(img);
        Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
        c.setContents(trans, null);
    }

    public static void copyToClipboard(String str) {
        StringSelection stringSelection = new StringSelection(str);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    private record TransferableImage(Image i) implements Transferable {
        public @NotNull Object getTransferData(DataFlavor flavor)
                    throws UnsupportedFlavorException {
                if (flavor.equals(DataFlavor.imageFlavor) && i != null) {
                    return i;
                } else {
                    throw new UnsupportedFlavorException(flavor);
                }
            }

            public DataFlavor[] getTransferDataFlavors() {
                DataFlavor[] flavors = new DataFlavor[1];
                flavors[0] = DataFlavor.imageFlavor;
                return flavors;
            }

            public boolean isDataFlavorSupported(DataFlavor flavor) {
                DataFlavor[] flavors = getTransferDataFlavors();
                for (DataFlavor dataFlavor : flavors) {
                    if (flavor.equals(dataFlavor)) return true;
                }

                return false;
            }
        }
}
