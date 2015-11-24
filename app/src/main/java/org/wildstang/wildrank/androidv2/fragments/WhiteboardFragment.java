package org.wildstang.wildrank.androidv2.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.wildstang.wildrank.androidv2.R;

/**
 * Created by Liam on 1/24/2015.
 * <p>
 * Pretty straight forward this inflates the layout file fragment_whiteboard which contains a custom
 * view WhiteboardView
 */
public class WhiteboardFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_whiteboard, container, false);
    }

}
