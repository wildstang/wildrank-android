package org.wildstang.wildrank.androidv2.adapters;

import android.content.Context;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SerializableSpinnerAdapter extends BaseAdapter {

    private static final String NEW_CHOICE = "New choice...";
    private final Object lock = new Object();
    private List<String> backingArray;
    private LayoutInflater inflater;
    private Context context;
    private String preferencesKey;
    private boolean editable;

    public SerializableSpinnerAdapter(Context context, String key, int textArrayResId) {
        init(context, key, textArrayResId, false);
    }

    public SerializableSpinnerAdapter(Context context, String key, int textArrayResId, boolean editable) {
        init(context, key, textArrayResId, editable);
    }

    private void init(Context context, String key, int textArrayResId, boolean editable) {
        CharSequence[] strings = context.getResources().getTextArray(textArrayResId);

        String[] newStrings = new String[strings.length];
        System.arraycopy(strings, 0, newStrings, 0, strings.length);

        this.context = context;
        this.editable = editable;
        backingArray = new ArrayList<>(Arrays.asList(newStrings));
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (editable) {
            preferencesKey = "spinner-" + key;
            Set<String> savedValues = PreferenceManager.getDefaultSharedPreferences(context).getStringSet(preferencesKey, null);
            if (savedValues != null) {
                for (String string : savedValues) {
                    if (!backingArray.contains(string)) {
                        add(string);
                    }
                }
            }
        }
        sortList();
    }

    @Override
    public int getCount() {
        return backingArray.size();
    }

    @Override
    public Object getItem(int position) {
        return backingArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void add(CharSequence string) {
        synchronized (lock) {
            // Ensure we don't insert duplicates
            if (!backingArray.contains(string)) {
                backingArray.add(backingArray.size() - 1, string.toString());
                // Store new value in SharedPreferences
                Set<String> savedValues = new LinkedHashSet<>();
                // Prevent saving a preference with a null name
                if (preferencesKey != null) {
                    if (PreferenceManager.getDefaultSharedPreferences(context).getStringSet(preferencesKey, null) != null) {
                        savedValues.addAll(PreferenceManager.getDefaultSharedPreferences(context).getStringSet(preferencesKey, null));
                    }
                    savedValues.add(string.toString());
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putStringSet(preferencesKey, savedValues).apply();
                }
            }
        }
        sortList();
        notifyDataSetChanged();
    }

    public void remove(CharSequence string) {
        synchronized (lock) {
            if (backingArray.contains(string)) {
                backingArray.remove(string);
                // Delete value from SharedPreferences
                Set<String> savedValues = new LinkedHashSet<>();
                if (PreferenceManager.getDefaultSharedPreferences(context).getStringSet(preferencesKey, null) != null) {
                    savedValues.addAll(PreferenceManager.getDefaultSharedPreferences(context).getStringSet(preferencesKey, null));
                }
                savedValues.remove(string.toString());
                PreferenceManager.getDefaultSharedPreferences(context).edit().putStringSet(preferencesKey, savedValues).apply();
            }
        }
        notifyDataSetChanged();
    }

    public void remove(int position) {
        if (position >= 0 && position < backingArray.size() && position != backingArray.indexOf(NEW_CHOICE)) {
            String name = backingArray.toString();
            synchronized (lock) {
                backingArray.remove(position);
                // Delete value from SharedPreferences
                Set<String> savedValues = new LinkedHashSet<>();
                if (PreferenceManager.getDefaultSharedPreferences(context).getStringSet(preferencesKey, null) != null) {
                    savedValues.addAll(PreferenceManager.getDefaultSharedPreferences(context).getStringSet(preferencesKey, null));
                }
                savedValues.remove(name);
                PreferenceManager.getDefaultSharedPreferences(context).edit().putStringSet(preferencesKey, savedValues).apply();
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, android.R.layout.simple_spinner_item);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, android.R.layout.simple_spinner_dropdown_item);
    }

    private View createViewFromResource(int position, View convertView, ViewGroup parent, int resource) {
        View view;
        TextView text;

        if (convertView == null) {
            view = inflater.inflate(resource, parent, false);
        } else {
            view = convertView;
        }

        text = (TextView) view;

        CharSequence item = (CharSequence) getItem(position);
        text.setText(item);
        return view;

    }

    private void sortList() {
        backingArray.remove(NEW_CHOICE);
        Collections.sort(backingArray, String.CASE_INSENSITIVE_ORDER);
        if (editable) {
            backingArray.add(NEW_CHOICE);
        }
        notifyDataSetChanged();
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public int getIndexOfString(String string) {
        return backingArray.indexOf(string);
    }

}

