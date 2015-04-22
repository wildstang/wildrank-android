package org.wildstang.wildrank.androidv2.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nathan on 3/24/2015.
 */
public class StackModel {

    public static final String TOTE_COUNT_KEY = "tote_count";
    public static final String PREEXISTING_KEY = "preexisting";
    public static final String PREEXISTING_HEIGHT_KEY = "preexisting_height";
    public static final String HAS_BIN_KEY = "has_bin";
    public static final String HAS_NOODLE_KEY = "has_noodle";
    public static final String STACK_DROPPED_KEY = "stack_dropped";
    public static final String BIN_DROPPED_KEY = "bin_dropped";
    public static final String DIDNT_SCORE_KEY = "didnt_score";

    public int toteCount;
    public int preexistingToteCount;
    public boolean hasBin;
    public boolean hasNoodle;
    public boolean isPreexisting;
    public boolean stackDropped;
    public boolean binDropped;
    public boolean didntScore;

    public StackModel() {
        // Initialize everything to zero/false
        toteCount = 0;
        hasBin = false;
        hasNoodle = false;
        isPreexisting = false;
        preexistingToteCount = 1;
        stackDropped = false;
        binDropped = false;
        didntScore = false;
    }

    public static StackModel fromMap(Map<String, Object> map) {
        StackModel stack = new StackModel();
        stack.toteCount = (Integer) map.get(TOTE_COUNT_KEY);
        stack.isPreexisting = (Boolean) map.get(PREEXISTING_KEY);
        stack.preexistingToteCount = (Integer) map.get(PREEXISTING_HEIGHT_KEY);
        stack.hasBin = (Boolean) map.get(HAS_BIN_KEY);
        stack.hasNoodle = (Boolean) map.get(HAS_NOODLE_KEY);
        stack.stackDropped = (Boolean) map.get(STACK_DROPPED_KEY);
        stack.binDropped = (Boolean) map.get(BIN_DROPPED_KEY);
        stack.didntScore = (Boolean) map.get(DIDNT_SCORE_KEY);
        return stack;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(StackModel.TOTE_COUNT_KEY, this.toteCount);
        map.put(StackModel.PREEXISTING_KEY, this.isPreexisting);
        map.put(StackModel.PREEXISTING_HEIGHT_KEY, this.preexistingToteCount);
        map.put(StackModel.HAS_BIN_KEY, this.hasBin);
        map.put(StackModel.HAS_NOODLE_KEY, this.hasNoodle);
        map.put(StackModel.STACK_DROPPED_KEY, this.stackDropped);
        map.put(StackModel.BIN_DROPPED_KEY, this.binDropped);
        map.put(StackModel.DIDNT_SCORE_KEY, this.didntScore);
        return map;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof StackModel)) {
            return false;
        }
        if (o == this) {
            return true;
        }
        StackModel comparing = (StackModel) o;
        boolean equals = true;
        equals &= (comparing.toteCount == this.toteCount);
        equals &= (comparing.isPreexisting == this.isPreexisting);
        equals &= (comparing.preexistingToteCount == this.preexistingToteCount);
        equals &= (comparing.hasBin == this.hasBin);
        equals &= (comparing.hasNoodle == this.hasNoodle);
        equals &= (comparing.stackDropped == this.stackDropped);
        equals &= (comparing.binDropped == this.binDropped);
        equals &= (comparing.didntScore == this.didntScore);
        return equals;
    }

    /**
     * A meaningful stack is defined as one that indicates something actually happened.
     * <p/>
     * We will define a meaningful stack as one that has totes, a bin, or both.
     *
     * @return true if it is meaningful stack, false if otherwise
     */
    public boolean isMeaningfulStack() {
        if (toteCount > 0 || hasBin == true) {
            return true;
        }
        return false;
    }
}
