package org.wildstang.wildrank.androidv2.views.scouting;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.TextView;

import org.wildstang.wildrank.androidv2.R;

import java.util.Map;

public class ScoutingTextView extends ScoutingView {

    private TextView labelView;
    private EditText valueView;

    public ScoutingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.custom_view_edit_text, this, true);

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

        this.setOnClickListener(v -> valueView.requestFocus());

    }

    public void setText(String text) {
        valueView.setText(text);
    }

    @Override
    public void writeContentsToMap(Map<String, Object> map) {
        map.put(key, valueView.getText().toString());
    }

    @Override
    public void restoreFromMap(Map<String, Object> map) {
        Object text = map.get(key);
        if (text != null) {
            valueView.setText(text.toString());
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
