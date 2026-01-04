package org.geysermc.assetwrangler.utils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;
import java.util.List;

public class ImmutableBoundedRangeModel implements BoundedRangeModel {
    private final List<ChangeListener> listeners = new ArrayList<>();

    private final int minimum;
    private final int maximum;
    private int value = 0;

    public ImmutableBoundedRangeModel(int minimum, int maximum) {
        this.minimum = minimum;
        this.maximum = maximum;
    }

    @Override
    public int getMinimum() {
        return this.minimum;
    }

    @Override
    public void setMinimum(int newMinimum) {
        throw new UnsupportedOperationException("Cannot modify range in immutable range model");
    }

    @Override
    public int getMaximum() {
        return this.maximum;
    }

    @Override
    public void setMaximum(int newMaximum) {
        throw new UnsupportedOperationException("Cannot modify range in immutable range model");
    }

    @Override
    public int getValue() {
        return this.value;
    }

    @Override
    public void setValue(int newValue) {
        setValue(newValue, true);
    }

    public void setValue(int newValue, boolean broadcastChangeEvent) {
        if (newValue < getMinimum()) throw new IllegalArgumentException("Cannot go below minimum value");
        if (newValue > getMaximum()) throw new IllegalArgumentException("Cannot go above maximum value");
        this.value = newValue;

        if (broadcastChangeEvent) {
            listeners.forEach(listener -> listener.stateChanged(new ChangeEvent(ImmutableBoundedRangeModel.this)));
        }
    }

    @Override
    public void setValueIsAdjusting(boolean b) {

    }

    @Override
    public boolean getValueIsAdjusting() {
        return false;
    }

    @Override
    public int getExtent() {
        return 0;
    }

    @Override
    public void setExtent(int newExtent) {

    }

    @Override
    public void setRangeProperties(int value, int extent, int min, int max, boolean adjusting) {
        setValue(value);
    }

    @Override
    public void addChangeListener(ChangeListener x) {
        listeners.add(x);
    }

    @Override
    public void removeChangeListener(ChangeListener x) {
        listeners.remove(x);
    }
}
