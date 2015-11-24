package org.wildstang.wildrank.androidv2.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Html;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.wildstang.wildrank.androidv2.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplatedTextView extends TextView {

    private String originalString;
    private String trueString, falseString;

    public TemplatedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TemplatedTextView, 0, 0);
        originalString = a.getString(R.styleable.TemplatedTextView_text);
        trueString = a.getString(R.styleable.TemplatedTextView_trueString);
        if (trueString == null) {
            trueString = "true";
        }
        falseString = a.getString(R.styleable.TemplatedTextView_falseString);
        if (falseString == null) {
            falseString = "false";
        }
        a.recycle();
    }

    public static void initializeViewsInViewGroupWithMap(ViewGroup v, Map<String, Object> data) {
        if (v == null) {
            return;
        }
        int childCount = v.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = v.getChildAt(i);
            if (view instanceof TemplatedTextView) {
                ((TemplatedTextView) view).populateFromMap(data);
            } else if (view instanceof ViewGroup) {
                initializeViewsInViewGroupWithMap((ViewGroup) view, data);
            }
        }
    }

    public static void clearAllViewsInViewGroup(ViewGroup v) {
        if (v == null) {
            return;
        }
        int childCount = v.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = v.getChildAt(i);
            if (view instanceof TemplatedTextView) {
                ((TemplatedTextView) view).clearData();
            } else if (view instanceof ViewGroup) {
                clearAllViewsInViewGroup((ViewGroup) view);
            }
        }
    }

    public void populateFromMap(Map<String, Object> data) {
        String text = originalString;
        // First, parse through the text
        ArrayList<String> keys = new ArrayList<>();
        // Regex that matches against keys in the form {{json-key}}
        String patternString = "\\{\\{([0-9a-zA-Z\\-_]+)\\}\\}";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(text);
        Log.d("TemplatedTextView", "Matcher found groups: " + matcher.groupCount());

        while (matcher.find()) {
            keys.add(matcher.group(1));
        }

        for (String key : keys) {
            String replacement;
            Object o = data.get(key);
            if (o == null) {
                Log.d("wildrank", "TemplatedTextView: data not found for key " + key);
                continue;
            } else if (o instanceof Integer || o instanceof String) {
                replacement = o.toString();
            } else if (o instanceof Double) {
                replacement = new DecimalFormat("#.##").format(o);
            } else if (o instanceof Boolean) {
                if (((Boolean) o) == true) {
                    replacement = trueString;
                } else {
                    replacement = falseString;
                }
            } else {
                Log.d("TemplatedTextView", "Object found of type " + o.getClass().getName());
                replacement = o.toString();
            }
            text = text.replace("{{" + key + "}}", replacement);
        }

        setText(Html.fromHtml(text));
    }

    public void clearData() {
        setText("");
    }
}
