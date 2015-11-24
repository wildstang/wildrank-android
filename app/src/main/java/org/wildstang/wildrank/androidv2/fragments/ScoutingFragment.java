package org.wildstang.wildrank.androidv2.fragments;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import org.wildstang.wildrank.androidv2.interfaces.IScoutingView;

import java.util.Map;

public abstract class ScoutingFragment extends Fragment {

    public ScoutingFragment() {

    }

    public void writeContentsToMap(Map<String, Object> map) {
        // Get the ViewGroup holding all of the widgets
        ViewGroup vg = (ViewGroup) getView();
        if (vg == null) {
            // If the view has been destroyed, state should already be saved
            // to parent activity
            return;
        }
        int childCount = vg.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = vg.getChildAt(i);
            if (view instanceof IScoutingView) {
                ((IScoutingView) view).writeContentsToMap(map);
            } else if (view instanceof ViewGroup) {
                writeContentsToMap(map, (ViewGroup) view);
            }
        }
    }

    private void writeContentsToMap(Map<String, Object> map, ViewGroup viewGroup) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof IScoutingView) {
                ((IScoutingView) view).writeContentsToMap(map);
            } else if (view instanceof ViewGroup) {
                writeContentsToMap(map, (ViewGroup) view);
            }
        }
    }

    public void restoreContentsFromMap(Map<String, Object> map) {
        // Get the ViewGroup holding all of the widgets
        ViewGroup vg = (ViewGroup) getView();
        if (vg == null) {
            Log.d("wildrank", "view is null");
            // If the view has been destroyed, state should already be saved
            // to parent activity
            return;
        }
        int childCount = vg.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = vg.getChildAt(i);
            if (view instanceof IScoutingView) {
                ((IScoutingView) view).restoreFromMap(map);
            } else if (view instanceof ViewGroup) {
                restoreContentsFromMap(map, (ViewGroup) view);
            }
        }
    }

    private void restoreContentsFromMap(Map<String, Object> map, ViewGroup viewGroup) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof IScoutingView) {
                ((IScoutingView) view).restoreFromMap(map);
            } else if (view instanceof ViewGroup) {
                restoreContentsFromMap(map, (ViewGroup) view);
            }
        }
    }
}
