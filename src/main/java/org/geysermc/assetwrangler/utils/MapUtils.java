package org.geysermc.assetwrangler.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MapUtils {
    public static <K, V> Map<V, K> invertMap(Map<K, V> map) {
        Map<V, K> newMap = new HashMap<>();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            newMap.put(entry.getValue(), entry.getKey());
        }
        return Collections.unmodifiableMap(newMap);
    }
}
