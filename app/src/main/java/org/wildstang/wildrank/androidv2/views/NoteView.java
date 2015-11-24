package org.wildstang.wildrank.androidv2.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import org.wildstang.wildrank.androidv2.R;

public class NoteView extends LinearLayout {

    public NoteView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.custom_view_note, this, true);
    }
}