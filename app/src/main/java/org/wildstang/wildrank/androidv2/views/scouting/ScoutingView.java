package org.wildstang.wildrank.androidv2.views.scouting;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.RelativeLayout;

import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.interfaces.IScoutingView;

public abstract class ScoutingView extends RelativeLayout implements IScoutingView {

    protected String key;
    protected boolean isComplete = true;

    public ScoutingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ScoutingView, 0, 0);
        key = a.getString(R.styleable.ScoutingView_key);
        a.recycle();
    }

    @Override
    public String getKey() {
        return key;
    }

    public boolean isComplete(boolean highlight) {
        // Android resets the padding when we manually set a background
        // We save the padding beforehand and re-add it after we set a new background
        int l = getChildAt(0).getPaddingLeft();
        int r = getChildAt(0).getPaddingRight();
        int t = getChildAt(0).getPaddingTop();
        int b = getChildAt(0).getPaddingBottom();
        if (highlight && !isComplete) {
            getChildAt(0).setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
        } else {
            TypedValue outValue = new TypedValue();
            getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground,
                    outValue, true);
            getChildAt(0).setBackgroundResource(outValue.resourceId);
        }
        getChildAt(0).setPadding(l, t, r, b);
        return isComplete;
    }

}
