package org.geysermc.assetwrangler;

import lombok.Getter;

import java.io.IOException;
import java.util.Properties;

@Getter
public class BuildConstants {
    private static BuildConstants INSTANCE;

    public static BuildConstants getInstance() {
        if (INSTANCE == null) {
            Properties properties = new Properties();
            try {
                properties.load(BuildConstants.class.getResourceAsStream("/build.properties"));
            } catch (IOException e) {
                e.printStackTrace();
                properties.put("project.name", "Unknown");
                properties.put("project.version", "Unknown");
                properties.put("project.authors", "Unknown");
            }

            INSTANCE = new BuildConstants(properties);
        }

        return INSTANCE;
    }

    private final String name;
    private final String version;
    private final String authors;

    private BuildConstants(Properties properties) {
        this.name = properties.getProperty("project.name");
        this.version = properties.getProperty("project.version");
        this.authors = properties.getProperty("project.authors");
    }
}
