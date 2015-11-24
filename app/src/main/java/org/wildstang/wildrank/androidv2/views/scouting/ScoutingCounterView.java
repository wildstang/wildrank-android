package org.wildstang.wildrank.androidv2.views.scouting;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SoundEffectConstants;
import android.widget.TextView;

import org.wildstang.wildrank.androidv2.R;

import java.util.Map;

public class ScoutingCounterView extends ScoutingView {

    private TextView labelView;
    private TextView countView;
    private int count;

    public ScoutingCounterView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.custom_view_counter, this, true);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ScoutingView, 0, 0);
        String label = a.getString(R.styleable.ScoutingView_label);
        a.recycle();

        labelView = (TextView) findViewById(R.id.label);
        labelView.setText(label);

        countView = (TextView) findViewById(R.id.count);
        countView.setText(Integer.toString(count));

        // Make view clickable
        this.setOnClickListener(v -> {
            count++;
            countView.setText(Integer.toString(count));
        });

        // Long clicks subtract from count
        this.setOnLongClickListener(v -> {
            if (count > 0) {
                count--;
                playSoundEffect(SoundEffectConstants.CLICK);
            }
            countView.setText(Integer.toString(count));
            return true;
        });
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
        countView.setText(Integer.toString(this.count));
    }

    @Override
    public void writeContentsToMap(Map<String, Object> map) {
        map.put(key, count);
    }

    @Override
    public void restoreFromMap(Map<String, Object> map) {
        Object count = map.get(key);
        if (count instanceof Integer) {
            setCount((Integer) count);
        }

    }
}
