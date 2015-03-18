package org.wildstang.wildrank.androidv2.views.scouting;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import org.wildstang.wildrank.androidv2.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Nathan on 2/21/2015.
 */
public class ScoutingStacksView extends ScoutingView implements View.OnClickListener {

    public static final String TOTE_COUNT_KEY = "tote_count";
    public static final String PREEXISTING_KEY = "preexisting";
    public static final String PREEXISTING_HEIGHT_KEY = "preexisting_height";
    public static final String HAS_BIN_KEY = "has_bin";
    public static final String HAS_NOODLE_KEY = "has_noodle";
    public static final String STACK_DROPPED_KEY = "stack_dropped";
    public static final String BIN_DROPPED_KEY = "bin_dropped";

    private List<StackData> stackData = new ArrayList<>();

    private ScoutingCounterView totesCounter;
    private ScoutingCheckboxView preexistingStackCheckbox;
    private ScoutingSpinnerView preexistingHeightSpinner;
    private ScoutingCheckboxView hasBinCheckbox;
    private ScoutingCheckboxView hasNoodleCheckbox;
    private ScoutingCheckboxView stackDroppedCheckbox;
    private ScoutingCheckboxView binDroppedCheckbox;

    public ScoutingStacksView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.scouting_view_stacks, this, true);

        totesCounter = (ScoutingCounterView) findViewById(R.id.tote_counter);
        preexistingStackCheckbox = (ScoutingCheckboxView) findViewById(R.id.preexisting);
        preexistingHeightSpinner = (ScoutingSpinnerView) findViewById(R.id.preexisting_height);
        hasBinCheckbox = (ScoutingCheckboxView) findViewById(R.id.includes_bin);
        hasNoodleCheckbox = (ScoutingCheckboxView) findViewById(R.id.includes_noodle);
        stackDroppedCheckbox = (ScoutingCheckboxView) findViewById(R.id.stack_dropped);
        binDroppedCheckbox = (ScoutingCheckboxView) findViewById(R.id.bin_dropped);

        preexistingStackCheckbox.setOnValueChangedListener(new ScoutingCheckboxView.OnValueChangedListener() {
            @Override
            public void onValueChanged(boolean newValue) {
                // Enable/disable the preexisting height spinner
                preexistingHeightSpinner.setEnabled(newValue);
            }
        });

        // Synchronize the state of the preexisting height spinner with the checkbox
        preexistingHeightSpinner.setEnabled(preexistingStackCheckbox.isChecked());

        findViewById(R.id.finish_stack).setOnClickListener(this);
    }

    @Override
    public void writeContentsToMap(Map<String, Object> map) {
        List<Map<String, Object>> mappedDataList = new ArrayList<>();
        for (StackData data : stackData) {
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put(TOTE_COUNT_KEY, data.toteCount);
            dataMap.put(PREEXISTING_KEY, data.isPreexisting);
            dataMap.put(PREEXISTING_HEIGHT_KEY, data.preexistingHeight);
            dataMap.put(HAS_BIN_KEY, data.hasBin);
            dataMap.put(HAS_NOODLE_KEY, data.hasNoodle);
            dataMap.put(STACK_DROPPED_KEY, data.stackDropped);
            dataMap.put(BIN_DROPPED_KEY, data.binDropped);
            mappedDataList.add(dataMap);
        }
        map.put(key, mappedDataList);
    }

    @Override
    public void restoreFromMap(Map<String, Object> map) {
        List<Map<String, Object>> mappedDataList;
        try {
            mappedDataList = (List<Map<String, Object>>) map.get(key);
        } catch (ClassCastException e) {
            e.printStackTrace();
            return;
        }

        if (mappedDataList != null) {
            stackData.clear();
            for (Map<String, Object> dataMap : mappedDataList) {
                StackData data = new StackData();
                data.toteCount = (Integer) dataMap.get(TOTE_COUNT_KEY);
                data.isPreexisting = (Boolean) dataMap.get(PREEXISTING_KEY);
                data.preexistingHeight = (Integer) dataMap.get(PREEXISTING_HEIGHT_KEY);
                data.hasBin = (Boolean) dataMap.get(HAS_BIN_KEY);
                data.hasNoodle = (Boolean) dataMap.get(HAS_NOODLE_KEY);
                data.stackDropped = (Boolean) dataMap.get(STACK_DROPPED_KEY);
                data.binDropped = (Boolean) dataMap.get(BIN_DROPPED_KEY);
                stackData.add(data);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.finish_stack) {
            StackData data = new StackData();
            data.toteCount = totesCounter.getCount();
            data.hasBin = hasBinCheckbox.isChecked();
            data.hasNoodle = hasNoodleCheckbox.isChecked();
            data.binDropped = binDroppedCheckbox.isChecked();
            data.stackDropped = stackDroppedCheckbox.isChecked();
            data.isPreexisting = preexistingStackCheckbox.isChecked();
            int preexistingHeight = Integer.parseInt(preexistingHeightSpinner.getSelectedItem());
            data.preexistingHeight = preexistingHeight;

            stackData.add(data);

            // Reset all the views by creating a default StackData
            updateViewsFromData(new StackData());
        }
    }

    private void updateViewsFromData(StackData data) {
        totesCounter.setCount(data.toteCount);
        hasBinCheckbox.setChecked(data.hasBin);
        hasNoodleCheckbox.setChecked(data.hasNoodle);
        binDroppedCheckbox.setChecked(data.binDropped);
        stackDroppedCheckbox.setChecked(data.stackDropped);
        preexistingStackCheckbox.setChecked(data.isPreexisting);
        preexistingHeightSpinner.setSelectionBasedOnText(Integer.toString(data.preexistingHeight));
    }

    private class StackData {
        public int toteCount;
        public boolean hasBin;
        public boolean hasNoodle;
        public boolean isPreexisting;
        public int preexistingHeight;
        public boolean stackDropped;
        public boolean binDropped;

        public StackData() {
            // Initialize everything to zero/false
            toteCount = 0;
            hasBin = false;
            hasNoodle = false;
            isPreexisting = false;
            preexistingHeight = 1;
            stackDropped = false;
            binDropped = false;
        }
    }
}
