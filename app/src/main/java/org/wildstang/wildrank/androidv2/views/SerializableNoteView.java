package org.wildstang.wildrank.androidv2.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.wildstang.wildrank.androidv2.R;

import java.util.Locale;
import java.util.Map;

/**
 * Created by Liam on 2/21/2015.
 */
public class SerializableNoteView extends LinearLayout
{
    private TextView teamTV;
    private EditText noteET;
    private Button button;
    private Boolean expanded;

    public SerializableNoteView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.custom_view_note, this, true);

        //I need to figure this out
        String team = "111";
        teamTV = (TextView) findViewById(R.id.team);
        teamTV.setText("Team: " + team);

        noteET = (EditText) findViewById(R.id.note);
        noteET.setClickable(false);
        // When we receive focus after pressing "Next", pass focus onto the edittext
        setFocusable(true);
        setFocusableInTouchMode(true);
        setOnFocusChangeListener(new OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (hasFocus)
                {
                    noteET.requestFocus();
                }

            }
        });
    }

    public void setValue(String string)
    {
        noteET.setText(string);
    }
}