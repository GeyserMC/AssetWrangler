package org.geysermc.assetwrangler.components;

import lombok.Getter;

public class SourceLabel {
    @Getter
    private final String id;
    private final String name;

    public SourceLabel(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
