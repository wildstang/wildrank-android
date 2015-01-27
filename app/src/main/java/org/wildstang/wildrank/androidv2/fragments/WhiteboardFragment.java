package org.wildstang.wildrank.androidv2.fragments;

import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.couchbase.lite.QueryRow;

import org.wildstang.wildrank.androidv2.R;

/**
 * Created by Liam on 1/24/2015.
 */
public class WhiteboardFragment extends Fragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_whiteboard, container, false);
    }

}
