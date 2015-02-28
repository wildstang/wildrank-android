package org.wildstang.wildrank.androidv2.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import org.wildstang.wildrank.androidv2.R;

import java.util.Map;

public class SerializableCheckboxView extends ScoutingView {

    private TextView labelView;
    private CheckBox checkboxView;

    public SerializableCheckboxView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.custom_view_checkbox, this, true);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ScoutingView, 0, 0);
        String label = a.getString(R.styleable.ScoutingView_label);
        a.recycle();

        labelView = (TextView) findViewById(R.id.label);
        labelView.setText(label);

        checkboxView = (CheckBox) findViewById(R.id.checkbox);
        checkboxView.setClickable(false);
        // This conflicts with our custom state saving
        checkboxView.setSaveEnabled(false);

        this.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                checkboxView.setChecked(!checkboxView.isChecked());
            }
        });
    }

    public void setState(boolean state) {
        checkboxView.setChecked(state);
    }

    @Override
    public void writeContentsToMap(Map<String, Object> map) {
        map.put(key, checkboxView.isChecked());
    }

    @Override
    public void restoreFromMap(Map<String, Object> map) {
        Object checked = map.get(key);
        if (checked != null && checked instanceof Boolean) {
            setState((Boolean) checked);
        }
    }
}
