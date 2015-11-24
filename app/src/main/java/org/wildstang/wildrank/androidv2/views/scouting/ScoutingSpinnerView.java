package org.wildstang.wildrank.androidv2.views.scouting;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.adapters.SerializableSpinnerAdapter;

import java.util.Map;

public class ScoutingSpinnerView extends ScoutingView implements OnItemSelectedListener, OnClickListener {

    private TextView labelView;
    private Spinner spinner;
    private ImageButton deleteButton;

    private int deletePosition = -1;

    private boolean editable;
    private String defaultValue;

    private String[] resourceStrings;

    public ScoutingSpinnerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.custom_view_spinner, this, true);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ScoutingView, 0, 0);
        String label = a.getString(R.styleable.ScoutingView_label);
        a.recycle();
        a = context.obtainStyledAttributes(attrs, R.styleable.ScoutingSpinnerView, 0, 0);
        int spinnerValuesId = a.getResourceId(R.styleable.ScoutingSpinnerView_values, 0);
        // Store a local copy of resource strings
        CharSequence[] strings = getContext().getResources().getTextArray(spinnerValuesId);

        resourceStrings = new String[strings.length];
        System.arraycopy(strings, 0, resourceStrings, 0, strings.length);
        editable = a.getBoolean(R.styleable.ScoutingSpinnerView_editable, false);
        defaultValue = a.getString(R.styleable.ScoutingSpinnerView_defaultValue);
        a.recycle();

        labelView = (TextView) findViewById(R.id.label);
        labelView.setText(label);

        spinner = (Spinner) findViewById(R.id.spinner);
        // This conflicts with our custom state-saving
        spinner.setSaveEnabled(false);

        SerializableSpinnerAdapter adapter = new SerializableSpinnerAdapter(getContext(), key, spinnerValuesId, editable);
        spinner.setAdapter(adapter);
        if (defaultValue != null) {
            setSelectionBasedOnText(defaultValue);
        } else {
            spinner.setSelection(0);
        }
        spinner.setOnItemSelectedListener(this);
        spinner.setOnTouchListener((v, event) -> {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(spinner.getWindowToken(), 0);
            return false;
        });

        deleteButton = (ImageButton) findViewById(R.id.delete_current_spinner_selection);
        if (!editable) {
            deleteButton.setVisibility(View.GONE);
        } else {
            deleteButton.setOnClickListener(this);
        }

        // Open the spinner when this view is touched
        this.setOnClickListener(v -> spinner.performClick());
    }

    public void setEnabled(boolean enabled) {
        if (enabled) {
            labelView.setAlpha(1.0f);
            spinner.setEnabled(true);
        } else {
            labelView.setAlpha(0.5f);
            spinner.setEnabled(false);
        }
    }

    public String getSelectedItem() {
        return (String) spinner.getSelectedItem();
    }

    public void setSelectionBasedOnText(String text) {
        int itemCount = spinner.getAdapter().getCount();
        for (int i = 0; i < itemCount; i++) {
            if (spinner.getItemAtPosition(i).equals(text)) {
                spinner.setSelection(i);
                return;
            }
        }
        // If the option does not currently exist, add it
        ((SerializableSpinnerAdapter) spinner.getAdapter()).add(text);
        int indexOfNew = ((SerializableSpinnerAdapter) spinner.getAdapter()).getIndexOfString(text);
        spinner.setSelection(indexOfNew);
    }

    @Override
    public void writeContentsToMap(Map<String, Object> map) {
        map.put(key, spinner.getSelectedItem().toString());
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        if (editable) {
            if (id == adapterView.getAdapter().getCount() - 1) {
                // This is the last option in the spinner (New choice)
                // We should prompt the user to add a new choice
                promptForNewSpinnerItem();
            }
            updateDeleteButton();
        }
    }

    // Used to show/hide the delete button based on spinner selection
    private void updateDeleteButton() {
        // Only show delete button for deletable choices
        boolean isItemFromResources = false;
        int position = spinner.getSelectedItemPosition();
        for (int i = 0; i < resourceStrings.length; i++) {
            if (spinner.getItemAtPosition(position).equals(resourceStrings[i])) {
                isItemFromResources = true;
                break;
            }
        }
        if (isItemFromResources) {
            deleteButton.setVisibility(View.GONE);
        } else {
            deleteButton.setVisibility(View.VISIBLE);
        }
    }

    private void promptForNewSpinnerItem() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

        alert.setTitle("New option");
        alert.setMessage("Please enter your new option.");

        // Set an EditText view to get user input
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        alert.setView(input);

        alert.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String text = input.getText().toString();
                ((SerializableSpinnerAdapter) spinner.getAdapter()).add(text);
                int indexOfNew = ((SerializableSpinnerAdapter) spinner.getAdapter()).getIndexOfString(text);
                spinner.setSelection(indexOfNew);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                spinner.setSelection(0);
            }
        });

        alert.show();
    }

    @Override
    public void onClick(View v) {
        if (editable && spinner.getSelectedItemPosition() == spinner.getAdapter().getCount() - 1) {
            // Don't let the user delete the "New choice..." option
            return;
        }
        if (deletePosition == spinner.getSelectedItemPosition()) {
            ((SerializableSpinnerAdapter) spinner.getAdapter()).remove(deletePosition);
            deletePosition = -1;
            updateDeleteButton();
        } else {
            Toast.makeText(getContext(), "Press again to confirm delete", Toast.LENGTH_SHORT).show();
            deletePosition = spinner.getSelectedItemPosition();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void restoreFromMap(Map<String, Object> map) {
        Object object = map.get(key);
        if (object instanceof String) {
            setSelectionBasedOnText((String) object);
        }
    }

    @Override
    public boolean isComplete(boolean highlight) {
        if (getSelectedItem().isEmpty()) {
            isComplete = false;
        } else {
            isComplete = true;
        }
        return super.isComplete(highlight);
    }
}
