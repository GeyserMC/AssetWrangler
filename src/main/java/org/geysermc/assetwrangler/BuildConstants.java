package org.geysermc.assetwrangler;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Getter
public class BuildConstants {
    private static final Logger LOGGER = LoggerFactory.getLogger("BuildConstants");
    private static BuildConstants INSTANCE;

    public static BuildConstants getInstance() {
        if (INSTANCE == null) {
            Properties properties = new Properties();
            try {
                InputStream stream = BuildConstants.class.getResourceAsStream("/build.properties");
                if (stream == null) throw new IOException("Missing input stream.");
                properties.load(stream);
                stream.close();
            } catch (IOException e) {
                LOGGER.error("Error loading.", e);
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
