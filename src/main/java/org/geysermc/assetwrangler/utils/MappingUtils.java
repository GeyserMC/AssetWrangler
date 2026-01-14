package org.geysermc.assetwrangler.utils;

import org.geysermc.assetwrangler.Logger;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class MappingUtils {
    private MappingUtils() {
        throw new AssertionError("Whoops. My finger slipped.");
    }

    public static JsonMappings getMappings(File file) {
        return JsonMappings.getMapping(file);
    }

    public static void saveMappings(File file, JsonMappings mappings) {
        try {
            mappings.save(file);
        } catch (IOException e) {
            Logger.errorWithDialog(
                    "Something went wrong while saving mappings!", e, null
            );
        }
    }
}
