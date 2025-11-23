package org.geysermc.assetwrangler;

public interface RowColumnSpan {
    default int rows() { return 1; }
    default int columns() { return 1; }
}
