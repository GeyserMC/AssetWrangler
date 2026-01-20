package org.geysermc.assetwrangler.utils;

import com.google.gson.*;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class JsonMappings {
    @SneakyThrows
    public static JsonMappings getMapping(File file) {
        JsonObject jsonMappings = JsonParser.parseReader(new FileReader(file)).getAsJsonObject();

        Map<String, List<String>> mappings = new HashMap<>();

        for (Map.Entry<String, JsonElement> entry : jsonMappings.entrySet()) {
            mappings.putAll(extractMapping(entry.getValue(), entry.getKey(), List.of()));
        }

        return new JsonMappings(mappings);
    }

    private static Map<String, List<String>> extractMapping(JsonElement element, String key, List<String> parents) {
        if (element.isJsonObject()) {
            Map<String, List<String>> mappings = new HashMap<>();

            for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
                mappings.putAll(extractMapping(entry.getValue(), key + "/" + entry.getKey(), List.of(key)));
            }

            return mappings;
        } else if (element.isJsonArray()) {
            String prefix = "";
            if (!parents.isEmpty()) prefix = String.join("/", parents) + "/";

            List<String> paths = new ArrayList<>();

            for (JsonElement arrayElement : element.getAsJsonArray()) {
                if (arrayElement.isJsonPrimitive()) {
                    paths.add(prefix + arrayElement.getAsString());
                } else {
                    throw new RuntimeException("Invalid item found within mapping file, items in an array must be primitives.");
                }
            }

            return Map.of(key, paths);
        } else if (element.isJsonPrimitive()) {
            String prefix = "";
            if (!parents.isEmpty()) prefix = String.join("/", parents) + "/";
            return Map.of(key, List.of(prefix + element.getAsString()));
        }

        return Map.of();
    }

    private final Map<String, List<String>> mappings;

    public JsonMappings() {
        this.mappings = new HashMap<>();
    }

    private JsonMappings(Map<String, List<String>> mappings) {
        this.mappings = new HashMap<>(mappings);
    }

    public List<String> presentJavaPaths() {
        return this.mappings.keySet().stream().toList();
    }

    public List<String> presentBedrockPaths() {
        List<String> paths = new ArrayList<>();
        for (List<String> morePaths : this.mappings.values()) {
            paths.addAll(morePaths);
        }
        return Collections.unmodifiableList(paths);
    }

    public List<String> removeJava(String javaInput) {
        return mappings.remove(javaInput);
    }

    public String removeBedrock(String bedrockInput) {
        for (Map.Entry<String, List<String>> entry : mappings.entrySet()) {
            if (entry.getValue().contains(bedrockInput)) {
                mappings.remove(entry.getKey());
                return entry.getKey();
            }
        }

        return "";
    }

    public List<String> map(String javaPath) {
        return mappings.get(javaPath);
    }

    public String getJavaOrigin(String bedrockPath) {
        for (Map.Entry<String, List<String>> entry : mappings.entrySet()) {
            if (entry.getValue().contains(bedrockPath)) {
                return entry.getKey();
            }
        }

        return "";
    }

    public void map(String javaInput, List<String> bedrockOutputs) {
        mappings.put(javaInput, bedrockOutputs);
    }

    public boolean isJavaInMappings(String path) {
        return mappings.containsKey(path);
    }

    public boolean isBedrockInMappings(String path) {
        for (List<String> outputs : mappings.values()) {
            if (outputs.contains(path)) return true;
        }

        return false;
    }

    public void save(File file) throws IOException {
        // Sort so the output looks nice
        List<String> sortedKeys = new ArrayList<>(this.mappings.keySet().stream().toList());
        Collections.sort(sortedKeys);

        // Compacting time.
        JsonObject root = new JsonObject();

        for (String sortedKey : sortedKeys) {
            handleObjectSave(root, sortedKey, this.mappings.get(sortedKey));
        }

        Gson gson = new GsonBuilder()
                .setFormattingStyle(FormattingStyle.PRETTY.withIndent("    "))
                .create();
        String data = gson.toJson(root);
        Files.writeString(file.toPath(), data);
    }

    private void handleObjectSave(JsonObject parent, String javaInput, List<String> bedrockOutputs) {
        if (javaInput.indexOf('/') == -1) {
            if (bedrockOutputs.size() == 1) {
                parent.addProperty(javaInput, bedrockOutputs.getFirst());
                return;
            }
            JsonArray array = new JsonArray();
            for (String output : bedrockOutputs) {
                array.add(output);
            }
            parent.add(javaInput, array);
            return;
        }
        String rootJavaPath = javaInput.substring(0, javaInput.indexOf('/'));

        for (String bedrockOutput : bedrockOutputs) {
            if (!bedrockOutput.startsWith(rootJavaPath + "/")) {
                if (bedrockOutputs.size() == 1) {
                    parent.addProperty(javaInput, bedrockOutputs.getFirst());
                    return;
                }
                JsonArray array = new JsonArray();
                for (String output : bedrockOutputs) {
                    array.add(output);
                }
                parent.add(javaInput, array);
                return;
            }
        }

        JsonObject newObject;
        if (parent.has(rootJavaPath)) {
            newObject = parent.getAsJsonObject(rootJavaPath);
        } else {
            newObject = new JsonObject();
        }


        String newJavaInput = javaInput.substring(javaInput.indexOf('/') + 1);
        List<String> newBedrockOutputs = bedrockOutputs.stream()
                .map(str -> str.substring(str.indexOf('/') + 1))
                .toList();

        handleObjectSave(newObject, newJavaInput, newBedrockOutputs);

        parent.add(rootJavaPath, newObject);
    }
}
