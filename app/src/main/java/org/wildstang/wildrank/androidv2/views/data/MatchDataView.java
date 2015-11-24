package org.wildstang.wildrank.androidv2.views.data;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.couchbase.lite.Document;

import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.interfaces.IMatchDataView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public abstract class MatchDataView extends RelativeLayout implements IMatchDataView {

    protected int format;
    private TextView labelView;
    private TextView valueView;
    private String expression;
    private String explanation;

    public MatchDataView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.custom_view_data_view, this, true);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MatchDataView, 0, 0);
        String label = a.getString(R.styleable.MatchDataView_label);
        expression = a.getString(R.styleable.MatchDataView_expression);
        // Default format is double
        format = a.getInt(R.styleable.MatchDataView_format, 0);
        explanation = a.getString(R.styleable.MatchDataView_explanation);
        a.recycle();

        labelView = (TextView) findViewById(R.id.label);
        if (label != null) {
            labelView.setText(label);
        }

        valueView = (TextView) findViewById(R.id.value);

        this.setOnClickListener(v -> {
            if (explanation != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Explanation");
                builder.setMessage(explanation);
                builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                builder.create().show();
            }
        });
    }

    public static void initializeViewsInViewGroupWithDocuments(ViewGroup v, List<Document> docs) {
        if (v == null) {
            return;
        }
        int childCount = v.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = v.getChildAt(i);
            if (view instanceof IMatchDataView) {
                ((IMatchDataView) view).calculateFromDocuments(docs);
            } else if (view instanceof ViewGroup) {
                initializeViewsInViewGroupWithDocuments((ViewGroup) view, docs);
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
            if (view instanceof IMatchDataView) {
                ((IMatchDataView) view).clearValue();
            } else if (view instanceof ViewGroup) {
                clearAllViewsInViewGroup((ViewGroup) view);
            }
        }
    }

    protected static String formatNumberAsString(double number) {
        return new DecimalFormat("#.##").format(number);
    }

    protected static String formatPercentageAsString(double percentage) {
        NumberFormat format = NumberFormat.getPercentInstance();
        format.setMaximumFractionDigits(2);
        return format.format(percentage);
    }

    protected void setLabelText(String text) {
        labelView.setText(text);
    }

    protected void setValueText(String text) {
        valueView.setText(text);
    }

    public void clearValue() {
        valueView.setText("");
    }

}
