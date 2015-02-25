package org.wildstang.wildrank.androidv2.fragments;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

import org.wildstang.wildrank.androidv2.NoteBox;
import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.ReverseInterpolator;
import org.wildstang.wildrank.androidv2.activities.NotesActivity;
import org.wildstang.wildrank.androidv2.activities.ScoutMatchActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Liam on 2/21/2015.
 */
public class NotesSixFragment extends Fragment
{
    public List<NoteBox> boxes = new ArrayList<>();
    String[] teams;
    int i;
    boolean sixMode;

    public NotesSixFragment(String[] teams)
    {
        this.teams = teams;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_notes_six, container, false);
        boxes.add(new NoteBox(view.findViewById(R.id.one), teams[0]));
        boxes.add(new NoteBox(view.findViewById(R.id.two), teams[1]));
        boxes.add(new NoteBox(view.findViewById(R.id.three), teams[2]));
        boxes.add(new NoteBox(view.findViewById(R.id.four), teams[3]));
        boxes.add(new NoteBox(view.findViewById(R.id.five), teams[4]));
        boxes.add(new NoteBox(view.findViewById(R.id.six), teams[5]));
        sixMode = true;
        for(int i = 0; i < 6; i++)
        {
            setupAnimation(i);
        }
        return view;
    }

    View section;

    public void setupAnimation(final int i)
    {
        boxes.get(i).getButton().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final View main = getView();
                final View view = boxes.get(i).getLayout();
                if(i < 3)
                {
                    section = main.findViewById(R.id.top);
                }
                else
                {
                    section = main.findViewById(R.id.bottom);
                }

                final int targetWidth = main.getWidth();
                final int targetHeight = main.getHeight();

                Animation animate = new Animation()
                {
                    @Override
                    protected void applyTransformation(float interpolatedTime, Transformation t)
                    {
                        section.getLayoutParams().height = (int) (targetHeight * interpolatedTime);
                        section.requestLayout();
                        if(!sixMode)
                        {
                            view.getLayoutParams().height = (int) (targetHeight * interpolatedTime);
                        }
                        view.getLayoutParams().width = (int) (targetWidth * interpolatedTime);
                        view.requestLayout();
                    }

                    @Override
                    public boolean willChangeBounds()
                    {
                        return true;
                    }
                };

                if(sixMode)
                {
                    sixMode = false;
                }
                else
                {
                    animate.setInterpolator(new ReverseInterpolator());
                    sixMode = true;
                }

                animate.setDuration(Math.abs((int)(targetWidth / view.getContext().getResources().getDisplayMetrics().density))/4);
                view.startAnimation(animate);

                view.getLayoutParams().height = LinearLayout.LayoutParams.FILL_PARENT;
            }
        });
    }
}