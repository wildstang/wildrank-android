package org.wildstang.wildrank.androidv2.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;

import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.Utilities;
import org.wildstang.wildrank.androidv2.data.DatabaseManager;
import org.wildstang.wildrank.androidv2.views.TemplatedTextView;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TeamSummariesInfoFragment extends TeamSummariesFragment {

    private int teamNumber;
    private String teamName;
    private String teamKey;

    private TextView teamNameView;
    private TextView teamNumberView;
    private TextView notesView;
    private ImageView teamImageView;

    private Bitmap bitmap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_summaries_info, container, false);
        teamNumberView = (TextView) v.findViewById(R.id.team_number);
        teamNameView = (TextView) v.findViewById(R.id.team_name);
        teamImageView = (ImageView) v.findViewById(R.id.team_image);
        v.findViewById(R.id.content).setVisibility(View.GONE);
        return v;
    }

    @Override
    public void acceptNewTeamData(String teamKey, Document teamDoc, Document pitDoc, List<Document> matchDocs) {
        if (bitmap != null && !bitmap.isRecycled()) {
            System.out.println("Recycling bitmap");
            bitmap.recycle();
        }
        this.teamKey = teamKey;
        this.teamNumber = Utilities.teamNumberFromTeamKey(teamKey);

        getView().findViewById(R.id.select_a_team).setVisibility(View.GONE);
        getView().findViewById(R.id.content).setVisibility(View.VISIBLE);

        try {
            System.out.println("team doc null? " + teamDoc == null);
            teamName = (String) teamDoc.getProperty("nickname");
            teamNumberView.setText("Team " + teamNumber);
            teamNameView.setText(teamName);

            notesView = (TextView) getView().findViewById(R.id.notes);
            notesView.setText("");

            String[] notes = DatabaseManager.getInstance(getActivity()).getNotes(teamKey);
            for (int i = 0; i < notes.length; i++) {
                notesView.append("-" + notes[i] + "\n");
            }

            if (pitDoc != null) {
                Log.d("wildrank", pitDoc.getProperties().toString());
                TemplatedTextView.initializeViewsInViewGroupWithMap((ViewGroup) getView(), (Map<String, Object>) pitDoc.getProperty("data"));
            } else {
                TemplatedTextView.clearAllViewsInViewGroup((ViewGroup) getView());
            }

            // Load team images
            /*Document imagesDoc = DatabaseManager.getInstance(getActivity()).getTeamImagesDocument(teamKey);
            if (imagesDoc == null) {
                loadDefaultTeamImage();
            } else {
                Revision currentRevision = imagesDoc.getCurrentRevision();
                List<String> imageNames = currentRevision.getAttachmentNames();
                if (imageNames.size() == 0) {
                    loadDefaultTeamImage();
                } else {
                    Attachment attachment = currentRevision.getAttachment(imageNames.get(0));
                    loadTeamImageFromStream(attachment.getContent());
                }
            }*/
            File image = new File(Environment.getExternalStorageDirectory().getPath() + "/wildrank/" + teamNumber + ".jpg");
            if (image.exists()) {
                bitmap = BitmapFactory.decodeFile(image.toString());
                loadTeamImageFromStream();
            } else {
                System.out.println("Image not found");
                loadDefaultTeamImage();
            }
        } catch (CouchbaseLiteException | IOException e) {
            e.printStackTrace();
        }
    }

    private void loadDefaultTeamImage() {
        teamImageView.setImageDrawable(getView().getResources().getDrawable(R.drawable.frc4212));
    }

    private void loadTeamImageFromStream() {
        double width = bitmap.getWidth();
        double height = bitmap.getHeight();
        if (height > 750) {
            width = width * (750 / height);
            height = 750;
        }
        if (width > 1000) {
            height = height * (750 / width);
            width = 750;
        }
        System.out.println("Width " + width + " Height " + height);
        bitmap = Bitmap.createScaledBitmap(bitmap, (int) width, (int) height, false);
        teamImageView.setImageBitmap(bitmap);
    }
}