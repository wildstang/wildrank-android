package org.wildstang.wildrank.androidv2.views.scouting;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.wildstang.wildrank.androidv2.R;

import java.util.Locale;
import java.util.Map;

public class ScoutingNumberView extends ScoutingView {

    private TextView labelView;
    private EditText valueView;

    public ScoutingNumberView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.custom_view_number, this, true);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ScoutingView, 0, 0);
        String label = a.getString(R.styleable.ScoutingView_label);
        a.recycle();

        labelView = (TextView) findViewById(R.id.label);
        labelView.setText(label);

        valueView = (EditText) findViewById(R.id.value);
        valueView.setClickable(false);
        // When we receive focus after pressing "Next", pass focus onto the edittext
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
        this.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                valueView.requestFocus();
            }
        });

        this.setOnClickListener(v -> {
            valueView.requestFocus();
            ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(valueView, InputMethodManager.SHOW_FORCED);
        });
    }

    public static String formatDouble(double d) {
        if (d == (int) d) {
            return String.format(Locale.US, "%d", (int) d);
        } else {
            return String.format(Locale.US, "%s", d);
        }
    }

    public void setValue(double value) {
        valueView.setText(formatDouble(value));
    }

    @Override
    public void writeContentsToMap(Map<String, Object> map) {
        if (valueView.getText().toString().trim().length() != 0) {
            // Only save the value if it isn't empty
            map.put(key, Double.parseDouble(valueView.getText().toString()));
        } else {
            map.put(key, Double.valueOf(0.0));
        }
    }

    @Override
    public void restoreFromMap(Map<String, Object> map) {
        Object object = map.get(key);
        Log.d("wildrank", "restoring " + key + " from object " + object.toString());
        if (object instanceof Integer) {
            setValue((double) ((Integer) object).intValue());
        } else if (object instanceof Double) {
            setValue((Double) object);
        }
    }

    @Override
    public boolean isComplete(boolean highlight) {
        if (valueView.getText().toString().isEmpty()) {
            isComplete = false;
        } else {
            isComplete = true;
        }
        return super.isComplete(highlight);
    }

}
