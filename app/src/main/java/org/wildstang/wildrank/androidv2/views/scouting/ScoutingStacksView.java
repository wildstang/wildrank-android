package org.wildstang.wildrank.androidv2.views.scouting;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.models.StackModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScoutingStacksView extends ScoutingView implements View.OnClickListener {

    private List<StackModel> stacks = new ArrayList<>();

    private ScoutingCounterView totesCounter;
    private ScoutingCheckboxView preexistingStackCheckbox;
    private ScoutingSpinnerView preexistingHeightSpinner;
    private ScoutingCheckboxView hasBinCheckbox;
    private ScoutingCheckboxView hasNoodleCheckbox;
    private ScoutingCheckboxView stackDroppedCheckbox;
    private ScoutingCheckboxView binDroppedCheckbox;
    private ScoutingCheckboxView notScoredCheckbox;

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
        notScoredCheckbox = (ScoutingCheckboxView) findViewById(R.id.not_scored);

        preexistingStackCheckbox.setOnValueChangedListener(preexistingHeightSpinner::setEnabled);

        // Synchronize the state of the preexisting height spinner with the checkbox
        preexistingHeightSpinner.setEnabled(preexistingStackCheckbox.isChecked());

        findViewById(R.id.finish_stack).setOnClickListener(this);
    }

    @Override
    public void writeContentsToMap(Map<String, Object> map) {
        List<Map<String, Object>> mappedDataList = new ArrayList<>();
        for (StackModel stack : stacks) {
            mappedDataList.add(stack.toMap());
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
            stacks.clear();
            for (Map<String, Object> dataMap : mappedDataList) {
                stacks.add(StackModel.fromMap(dataMap));
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.finish_stack) {
            StackModel data = new StackModel();
            data.toteCount = totesCounter.getCount();
            data.hasBin = hasBinCheckbox.isChecked();
            data.hasNoodle = hasNoodleCheckbox.isChecked();
            data.binDropped = binDroppedCheckbox.isChecked();
            data.stackDropped = stackDroppedCheckbox.isChecked();
            data.isPreexisting = preexistingStackCheckbox.isChecked();
            data.notScored = notScoredCheckbox.isChecked();
            int preexistingHeight = Integer.parseInt(preexistingHeightSpinner.getSelectedItem());
            data.preexistingToteCount = preexistingHeight;

            // If the stack was not marked as preexisting, set the preexisting height to 0
            if (data.isPreexisting == false) {
                data.preexistingToteCount = 0;
            }

            stacks.add(data);

            // Reset all the views by creating a default StackData
            updateViewsFromData(new StackModel());
        }
    }

    private void updateViewsFromData(StackModel data) {
        totesCounter.setCount(data.toteCount);
        hasBinCheckbox.setChecked(data.hasBin);
        hasNoodleCheckbox.setChecked(data.hasNoodle);
        binDroppedCheckbox.setChecked(data.binDropped);
        stackDroppedCheckbox.setChecked(data.stackDropped);
        preexistingStackCheckbox.setChecked(data.isPreexisting);
        notScoredCheckbox.setChecked(data.notScored);
        preexistingHeightSpinner.setSelectionBasedOnText(Integer.toString(data.preexistingToteCount));
    }
}
