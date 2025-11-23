package org.geysermc.assetwrangler.utils;

import org.geysermc.assetwrangler.BuildConstants;

import java.net.*;

public class NetUtils {
    public static final String USER_AGENT = "Geyser-%s/%s".formatted(BuildConstants.getInstance().getName(), BuildConstants.getInstance().getVersion());

    public static URL asUrl(String url) {
        try {
            return new URI(url).toURL();
        } catch (URISyntaxException | MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
