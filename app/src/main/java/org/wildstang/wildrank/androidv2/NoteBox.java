package org.wildstang.wildrank.androidv2;

import android.content.Context;
import android.content.res.Configuration;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NoteBox {
    LinearLayout layout;
    Button switchView;
    EditText noteBox;
    TextView oldNotes;
    View mid;

    public NoteBox(View v, String team) {
        layout = (LinearLayout) v;
        mid = v.findViewById(R.id.midborder);
        mid.setVisibility(View.GONE);
        oldNotes = (TextView) v.findViewById(R.id.oldnotes);
        oldNotes.setMovementMethod(new ScrollingMovementMethod());
        switchView = (Button) v.findViewById(R.id.toSix);
        noteBox = (EditText) v.findViewById(R.id.note);
        ((TextView) v.findViewById(R.id.team)).setText("Team: " + team);
    }

    public String getNote() {
        return noteBox.getText().toString();
    }

    public Button getButton() {
        return switchView;
    }

    public LinearLayout getLayout() {
        return layout;
    }

    public void setOldNotes(String[] notes, Context c) {
        mid.setVisibility(View.VISIBLE);
        oldNotes.setVisibility(View.VISIBLE);
        oldNotes.setText("");
        Configuration configuration = c.getResources().getConfiguration();
        int screenWidthDp = configuration.screenWidthDp;
        oldNotes.setWidth(screenWidthDp / 3);
        for (int i = 0; i < notes.length; i++) {
            oldNotes.append("*" + notes[i] + "\n");
        }
    }

    public void clearNotes() {
        mid.setVisibility(View.GONE);
        oldNotes.setVisibility(View.GONE);
        oldNotes.setText("");
    }
}
