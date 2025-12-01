package org.geysermc.assetwrangler.actions;

public interface UndoableAction {
    void doAction();

    void undoAction();

    boolean willCauseSaveMarkTrigger();
}
