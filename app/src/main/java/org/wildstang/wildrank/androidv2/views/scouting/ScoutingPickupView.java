package org.wildstang.wildrank.androidv2.views.scouting;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.models.GearModel;
import org.wildstang.wildrank.androidv2.models.PickupModel;
import org.wildstang.wildrank.androidv2.models.StackModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScoutingPickupView extends ScoutingView implements View.OnClickListener {

    private List<PickupModel> stacks = new ArrayList<>();

    private ScoutingSpinnerView gearPickupTypeSpinner;
    private ScoutingSpinnerView gearPickupSpeedSpinner;
    private ScoutingSpinnerView gearDropoffSpeedSpinner;
    private ScoutingSpinnerView gearEndSpinner;

    public ScoutingPickupView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.scouting_view_pickup, this, true);

        gearPickupTypeSpinner = (ScoutingSpinnerView) findViewById(R.id.gear_pickup_type);
        gearPickupSpeedSpinner = (ScoutingSpinnerView) findViewById(R.id.gear_pickup_speed);
        gearDropoffSpeedSpinner = (ScoutingSpinnerView) findViewById(R.id.gear_dropoff_speed);
        gearEndSpinner = (ScoutingSpinnerView) findViewById(R.id.gear_end_location);


        // Synchronize the state of the preexisting height spinner with the checkbox

        findViewById(R.id.finish_gear).setOnClickListener(this);
    }

    @Override
    public void writeContentsToMap(Map<String, Object> map) {
        List<Map<String, Object>> mappedDataList = new ArrayList<>();
        for (PickupModel stack : stacks) {
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
                stacks.add(PickupModel.fromMap(dataMap));
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.finish_gear) {
            PickupModel data = new PickupModel();
            data.pickupType = gearPickupTypeSpinner.getSelectedItem();;
            data.pickupSpeed = gearPickupSpeedSpinner.getSelectedItem();
            data.dropoffSpeed = gearDropoffSpeedSpinner.getSelectedItem();
            data.gearEnd = gearEndSpinner.getSelectedItem();

            // If the stack was not marked as preexisting, set the preexisting height to 0

            stacks.add(data);

            // Reset all the views by creating a default StackData
            updateViewsFromData(new PickupModel());
        }
    }

    private void updateViewsFromData(PickupModel data) {
        gearPickupTypeSpinner.setSelectionBasedOnText("Player Station");
        gearPickupSpeedSpinner.setSelectionBasedOnText("Very Slow");
        gearDropoffSpeedSpinner.setSelectionBasedOnText("Very Slow");
        gearEndSpinner.setSelectionBasedOnText("On peg");
    }
}