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
public class NoteView extends LinearLayout
{

    public NoteView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.custom_view_note, this, true);
    }
}