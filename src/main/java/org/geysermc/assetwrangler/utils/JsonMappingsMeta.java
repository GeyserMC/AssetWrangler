package org.geysermc.assetwrangler.utils;

import lombok.Getter;
import lombok.Setter;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
@Getter
@Setter
public class JsonMappingsMeta {
    private Section java = new Section();
    private Section bedrock = new Section();

    @ConfigSerializable
    @Getter
    @Setter
    public static class Section {
        private List<String> ignoredPaths = new ArrayList<>();
        private List<String> matchingPaths = new ArrayList<>();
        private List<String> transformedPaths = new ArrayList<>();
        private String relativePath = "";

        public void ignorePath(String path) {
            List<String> paths = new ArrayList<>(getIgnoredPaths());
            paths.add(path);
            setIgnoredPaths(paths);
        }

        public void unignorePath(String path) {
            List<String> paths = new ArrayList<>(getIgnoredPaths());
            paths.remove(path);
            setIgnoredPaths(paths);
        }

        public void matchPath(String path) {
            List<String> paths = new ArrayList<>(getMatchingPaths());
            paths.add(path);
            setMatchingPaths(paths);
        }

        public void unmatchPath(String path) {
            List<String> paths = new ArrayList<>(getMatchingPaths());
            paths.remove(path);
            setMatchingPaths(paths);
        }

        public void transformPath(String path) {
            List<String> paths = new ArrayList<>(getTransformedPaths());
            paths.add(path);
            setTransformedPaths(paths);
        }

        public void untransformPath(String path) {
            List<String> paths = new ArrayList<>(getTransformedPaths());
            paths.remove(path);
            setTransformedPaths(paths);
        }
    }
}
