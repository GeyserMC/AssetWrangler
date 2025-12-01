package org.geysermc.assetwrangler.actions;

import org.geysermc.assetwrangler.windows.AssetViewerWindow;
import org.geysermc.assetwrangler.windows.MappingsWindow;

import java.util.ArrayList;
import java.util.List;

public class ActionManager {
    private final List<UndoableAction> undos = new ArrayList<>();
    private final List<UndoableAction> redos = new ArrayList<>();

    private final AssetViewerWindow window;

    public ActionManager(AssetViewerWindow window) {
        this.window = window;
    }

    public void doAction(Runnable doAction, Runnable undoAction, boolean causesSaveMark) {
        boolean saveCurrentlyRequired = ActionManager.this.window.isSavesRequired();
        UndoableAction action = new UndoableAction() {
            @Override
            public void doAction() {
                doAction.run();
            }

            @Override
            public void undoAction() {
                undoAction.run();
            }

            @Override
            public boolean willCauseSaveMarkTrigger() {
                return causesSaveMark && !saveCurrentlyRequired;
            }
        };
        action.doAction();
        if (action.willCauseSaveMarkTrigger()) {
            this.window.markSave();
        }
        undos.add(action);

        redos.clear();
    }

    public boolean canUndo() {
        return !this.undos.isEmpty();
    }

    public void undo() {
        if (!this.canUndo()) return;
        UndoableAction action = this.undos.removeLast();
        action.undoAction();
        if (action.willCauseSaveMarkTrigger()) {
            // We're undoing, so the save mark is no longer required
            this.window.unmarkSave();
        }

        this.redos.add(action);
    }

    public boolean canRedo() {
        return !this.redos.isEmpty();
    }

    public void redo() {
        if (!this.canRedo()) return;
        UndoableAction action = this.redos.removeLast();
        action.doAction();
        if (action.willCauseSaveMarkTrigger()) {
            // We're redoing, so the save mark is now required again
            this.window.markSave();
        }

        this.undos.add(action);
    }
}
