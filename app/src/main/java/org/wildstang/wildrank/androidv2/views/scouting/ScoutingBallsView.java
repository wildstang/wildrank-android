package org.wildstang.wildrank.androidv2.views.scouting;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.models.BallsModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScoutingBallsView extends ScoutingView implements View.OnClickListener {

    private List<BallsModel> stacks = new ArrayList<>();

    private ScoutingSpinnerView fireRateSpinner;
    private ScoutingSpinnerView fireAccuracySpinner;
    private ScoutingSpinnerView ballLocationSpinner;

    public ScoutingBallsView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.scouting_view_balls, this, true);

        fireRateSpinner = (ScoutingSpinnerView) findViewById(R.id.fire_rate);
        fireAccuracySpinner = (ScoutingSpinnerView) findViewById(R.id.fire_accuracy);
        ballLocationSpinner = (ScoutingSpinnerView) findViewById(R.id.ball_location);


        // Synchronize the state of the preexisting height spinner with the checkbox

        findViewById(R.id.finish_balls).setOnClickListener(this);
    }

    @Override
    public void writeContentsToMap(Map<String, Object> map) {
        List<Map<String, Object>> mappedDataList = new ArrayList<>();
        for (BallsModel stack : stacks) {
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
                stacks.add(BallsModel.fromMap(dataMap));
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.finish_balls) {
            BallsModel data = new BallsModel();
            data.fireRate = fireRateSpinner.getSelectedItem();
            double fireAccNumber = Double.parseDouble(fireAccuracySpinner.getSelectedItem().substring(0,fireAccuracySpinner.getSelectedItem().length()-1))/100;
            if(fireRateSpinner.getSelectedItem()=="None"){fireAccNumber = -1.0;}
            data.fireAccuracy = fireAccNumber;

            // If the stack was not marked as preexisting, set the preexisting height to 0

            stacks.add(data);

            // Reset all the views by creating a default StackData
            updateViewsFromData(new BallsModel());
        }
    }

    private void updateViewsFromData(BallsModel data) {
        fireRateSpinner.setSelectionBasedOnText("None");
        fireAccuracySpinner.setSelectionBasedOnText("0%");
        ballLocationSpinner.setSelectionBasedOnText("Player Station");
    }
}
