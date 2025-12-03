package org.geysermc.assetwrangler.utils;

import org.geysermc.assetwrangler.BuildConstants;
import org.geysermc.assetwrangler.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

public class WebUtils {
    /**
     * Makes a web request to the given URL and returns the body as a string
     *
     * @param reqURL URL to fetch
     * @return body content or
     * @throws IOException / a wrapped UnknownHostException for nicer errors.
     */
    public static String getBody(String reqURL) throws IOException {
        try {
            URL url = new URL(reqURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", getUserAgent()); // Otherwise Java 8 fails on checking updates
            con.setConnectTimeout(10000);
            con.setReadTimeout(10000);

            return connectionToString(con);
        } catch (UnknownHostException e) {
            throw new IllegalStateException("Unable to resolve requested url (%s)! Are you offline?".formatted(reqURL), e);
        }
    }

    /**
     * Get the string output from the passed {@link HttpURLConnection}
     *
     * @param con The connection to get the string from
     * @return The body of the returned page
     * @throws IOException If the request fails
     */
    private static String connectionToString(HttpURLConnection con) throws IOException {
        // Send the request (we dont use this but its required for getErrorStream() to work)
        con.getResponseCode();

        // Read the error message if there is one if not just read normally
        InputStream inputStream = con.getErrorStream();
        if (inputStream == null) {
            inputStream = con.getInputStream();
        }

        StringBuilder content = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(inputStream))) {
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
                content.append("\n");
            }

            con.disconnect();
        }

        return content.toString();
    }

    public static String getUserAgent() {
        return "Geyser-" + BuildConstants.getInstance().getName() + "/" + BuildConstants.getInstance().getVersion();
    }
}

