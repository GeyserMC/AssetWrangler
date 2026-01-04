package org.geysermc.assetwrangler.components.previews;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.Map;

public class JsonPreview extends JScrollPane {
    public JsonPreview(JsonElement element, String relativePath) {
        JTree tree = new JTree(new DefaultTreeModel(createTree(relativePath, element)));
        setViewportView(tree);
    }

    private static DefaultMutableTreeNode createTree(String name, JsonElement element) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(name);

        if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                node.add(createTree(entry.getKey(), entry.getValue()));
            }
        } else if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            for (int i = 0; i < array.size(); i++) {
                node.add(createTree("[%d]".formatted(i), array.get(i)));
            }
        } else {
            node.setUserObject(name + ": " + element);
        }
        return node;
    }
}
