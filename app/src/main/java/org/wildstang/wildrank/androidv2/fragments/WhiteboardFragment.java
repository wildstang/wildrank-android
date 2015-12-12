package org.wildstang.wildrank.androidv2.fragments;

import android.content.ClipData;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.views.whiteboard.DraggableImage;

import java.util.ArrayList;
import java.util.List;

public class WhiteboardFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context context = container.getContext();
        View v = inflater.inflate(R.layout.fragment_whiteboard, container, false);

        LinearLayout magnetList = (LinearLayout) v.findViewById(R.id.magnet_list);

        List<DraggableImage> images = new ArrayList<>();
        images.add(new DraggableImage(R.drawable.dozer, 0.6f));
        images.add(new DraggableImage(R.drawable.noodle, 2f));
        images.add(new DraggableImage(R.drawable.greenbin, 3f));
        images.add(new DraggableImage(R.drawable.graytote, 3f));
        images.add(new DraggableImage(R.drawable.yellowtote, 3f));

        for(DraggableImage image : images) {
            ImageView imageView = new ImageView(magnetList.getContext());
            imageView.setImageBitmap(image.getBitmap(context));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 10, 0, 10);
            imageView.setLayoutParams(params);
            magnetList.addView(imageView);

            imageView.setOnTouchListener((v1, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ClipData data = ClipData.newPlainText("", "");
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v1);
                    v1.startDrag(data, shadowBuilder, image, 0);
                    return true;
                } else {
                    return false;
                }
            });
        }

        return v;
    }
}
