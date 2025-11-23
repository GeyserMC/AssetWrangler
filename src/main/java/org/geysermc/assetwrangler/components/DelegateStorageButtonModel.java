package org.geysermc.assetwrangler.components;

import lombok.Getter;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

public class DelegateStorageButtonModel implements ButtonModel {
    @Getter
    private final String storedId;
    private final ButtonModel delegate;

    public DelegateStorageButtonModel(ButtonModel delegate, String storedId) {
        this.delegate = delegate;
        this.storedId = storedId;
    }

    @Override
    public boolean isArmed() {
        return delegate.isArmed();
    }

    @Override
    public boolean isSelected() {
        return delegate.isSelected();
    }

    @Override
    public boolean isEnabled() {
        return delegate.isEnabled();
    }

    @Override
    public boolean isPressed() {
        return delegate.isPressed();
    }

    @Override
    public boolean isRollover() {
        return delegate.isRollover();
    }

    @Override
    public void setArmed(boolean b) {
        delegate.setArmed(b);
    }

    @Override
    public void setSelected(boolean b) {
        delegate.setSelected(b);
    }

    @Override
    public void setEnabled(boolean b) {
        delegate.setEnabled(b);
    }

    @Override
    public void setPressed(boolean b) {
        delegate.setPressed(b);
    }

    @Override
    public void setRollover(boolean b) {
        delegate.setRollover(b);
    }

    @Override
    public void setMnemonic(int key) {
        delegate.setMnemonic(key);
    }

    @Override
    public int getMnemonic() {
        return delegate.getMnemonic();
    }

    @Override
    public void setActionCommand(String s) {
        delegate.setActionCommand(s);
    }

    @Override
    public String getActionCommand() {
        return delegate.getActionCommand();
    }

    @Override
    public void setGroup(ButtonGroup group) {
        delegate.setGroup(group);
    }

    @Override
    public void addActionListener(ActionListener l) {
        delegate.addActionListener(l);
    }

    @Override
    public void removeActionListener(ActionListener l) {
        delegate.removeActionListener(l);
    }

    @Override
    public Object[] getSelectedObjects() {
        return delegate.getSelectedObjects();
    }

    @Override
    public void addItemListener(ItemListener l) {
        delegate.addItemListener(l);
    }

    @Override
    public void removeItemListener(ItemListener l) {
        delegate.removeItemListener(l);
    }

    @Override
    public void addChangeListener(ChangeListener l) {
        delegate.addChangeListener(l);
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
        delegate.removeChangeListener(l);
    }
}
