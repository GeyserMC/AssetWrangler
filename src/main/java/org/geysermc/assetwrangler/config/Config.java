package org.geysermc.assetwrangler.config;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
@Accessors(fluent = true)
@Getter
@Setter
public class Config {
    private boolean showPreviewPane = true;
    private boolean showMappedEntries = false;
    private boolean showIgnoredEntries = false;
    private boolean showMatchingEntries = false;
    private boolean showTransformedEntries = false;

    private boolean showTextureMetadata = false;
    private boolean disableAnimationInterpolation = false;

    private String javaAssetSourceKey = "";
    private String bedrockAssetSourceKey = "";
}
