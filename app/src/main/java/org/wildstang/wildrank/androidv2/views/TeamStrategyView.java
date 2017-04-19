package org.wildstang.wildrank.androidv2.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.couchbase.lite.Document;

import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.Utilities;
import org.wildstang.wildrank.androidv2.models.BallsModel;
import org.wildstang.wildrank.androidv2.models.GearModel;
import org.wildstang.wildrank.androidv2.models.TeamDocumentsModel;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TeamStrategyView extends Button {

    private Integer teamNumber;

    private double totalPickupTimes = 0.0;
    private double totalDropoffTimes = 0.0;
    private double lowBallsTeleopMatch = 0.0;
    private double totalLowBallsTeleop = 0.0;
    private int gearsAttempted = 0;
    private int gearsSuccessful = 0;
    int gearsAcquired = 0;
    private int hopperSize = 0;


    private double averagePickupTimes = 0.0;
    private double averageDropoffTimes = 0.0;
    private double averagePointsPerMatch = 0.0;
    private double maximumPointsPerMatch = 0.0;
    private double averageGearsAttempted = 0.0;
    private double averageGearsSuccessful = 0.0;


    private int climbData[] = {0,0,0};
    private int winData[] = {0,0,0};

    int matchCount = 0;

    private List<Document> matchDocs;
    private  Document pitDocs;

    Paint coverPaint, textPaint, titlePaint, dataPaint, climbSuccessful, climbAttempted, climbNoAttempt, outlinePaint, borderPaint;



    public TeamStrategyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        coverPaint = new Paint();
        coverPaint.setColor(Color.WHITE);
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(23);
        titlePaint = new Paint();
        titlePaint.setColor(Color.BLACK);
        titlePaint.setTextSize(28);
        titlePaint.setFakeBoldText(true);
        dataPaint = new Paint();
        dataPaint.setColor(Color.BLACK);
        dataPaint.setTextSize(12);
        dataPaint.setFakeBoldText(true);
        climbNoAttempt = new Paint();
        climbNoAttempt.setColor(Color.GRAY);
        climbAttempted = new Paint();
        climbAttempted.setColor(Color.RED);
        climbSuccessful = new Paint();
        climbSuccessful.setColor(Color.GREEN);
        outlinePaint = new Paint();
        outlinePaint.setColor(Color.BLACK);
        outlinePaint.setStyle(Paint.Style.STROKE);
        borderPaint = new Paint();
        borderPaint.setColor(Color.argb(128, 128, 128, 128));
        borderPaint.setStyle(Paint.Style.STROKE);
    }

    public void populateFromTeamDocuments(TeamDocumentsModel team) {
        matchDocs = team.getMatchDocuments();
        pitDocs = team.getPitDocument();
        // TODO set R.id.team TextView to team number from team.getTeamDocument()
        teamNumber = (Integer) team.getTeamDocument().getProperty("team_number");
        //String teamName = (String) team.getTeamDocument().getProperty("nickname");

        if (pitDocs != null){
            Map<String, Object> pitData = (Map<String, Object>) pitDocs.getProperty("data");
            hopperSize = (int) Math.floor((double) pitData.get("hopper_volume"));
        }
        if (matchDocs != null) {
            for (Document doc : matchDocs) {
                Map<String, Object> data = (Map<String, Object>) doc.getProperty("data");

                //manually find gear attempt/success in auto
                if (!data.get("auto-gear").equals("No Attempt")) {
                    gearsAttempted++;
                    if (data.get("auto-gear").equals("Success")) {
                        totalDropoffTimes += Math.floor((double) data.get("auto-time"));
                        gearsSuccessful++;
                    }
                }

                List<Map<String, Object>> gearData = (List<Map<String, Object>>) data.get("gear");
                List<GearModel> matchGears = new ArrayList<>();
                for (int j = 0; j < gearData.size(); j++) {
                    //Log.d("wildrank", "gears for match " + (String) doc.getProperty("match_key") + ": " + gearData.get(j));
                    gearData.get(j).get("gear_dropoff_speed");

                    GearModel gear = GearModel.fromMap(gearData.get(j));
                    matchGears.add(gear);
                    gearsAttempted++;
                    if (!gear.pickupType.equals("From Auto")) { gearsAcquired++; }
                    totalPickupTimes += (int) gear.pickupSpeed;
                    if (gear.gearEnd.equals("On peg")) {
                        gearsSuccessful++;
                        totalDropoffTimes += (int) gear.dropoffSpeed;
                    }
                }

                //manually find fire accuracy for high goal in auto
                if ((Math.floor((double) data.get("auto-high_time")))!=0) {
                    lowBallsTeleopMatch += 9*(Math.floor((double) data.get("auto-high_shots")));
                }
                List<Map<String, Object>> highData = (List<Map<String, Object>>) data.get("high");
                List<BallsModel> matchHigh = new ArrayList<>();
                for (int j = 0; j < highData.size(); j++) {
                    //Log.d("wildrank", "High goal for match " + (String) doc.getProperty("match_key") + ": " + highData.get(j));
                    BallsModel high = BallsModel.fromMap(highData.get(j));
                    matchHigh.add(high);
                    if (high.timeTaken!=0) {
                        lowBallsTeleopMatch += 3*high.shotsMade;
                    }
                }

                //manually find fire accuracy for low goal in auto
                if ((Math.floor((double) data.get("auto-low_time")))!=0) {
                    lowBallsTeleopMatch += 3*(Math.floor((double) data.get("auto-low_shots")));
                }
                List<Map<String, Object>> lowData = (List<Map<String, Object>>) data.get("low");
                List<BallsModel> matchLow = new ArrayList<>();
                for (int j = 0; j < lowData.size(); j++) {
                    //Log.d("wildrank", "Low goal for match " + (String) doc.getProperty("match_key") + ": " + gearData.get(j));
                    BallsModel low = BallsModel.fromMap(lowData.get(j));
                    matchLow.add(low);
                    if (low.timeTaken!=0) {
                        lowBallsTeleopMatch += low.shotsMade;
                    }

                }

                if (!data.get("post_match-did_climb").equals("No Attempt")) {
                    if (data.get("post_match-did_climb").equals("Successful")) {
                        climbData[2] += 1;
                    } else {
                        climbData[1] += 1;
                    }
                } else {
                    climbData[0] += 1;
                }

                if (!data.get("post_match-did_win").equals("Won")) {
                    if (data.get("post_match-did_win").equals("Lost")){
                        winData[1] += 1;
                    } else {
                        winData[2] += 1;
                    }
                } else {
                    winData[0] += 1;
                }
                totalLowBallsTeleop += lowBallsTeleopMatch;
                if (lowBallsTeleopMatch>maximumPointsPerMatch) maximumPointsPerMatch = lowBallsTeleopMatch;
                lowBallsTeleopMatch = 0.0;
                matchCount++;
            }
            averagePointsPerMatch = (double) totalLowBallsTeleop / matchCount;
            averageDropoffTimes = totalDropoffTimes / gearsSuccessful;
            averagePickupTimes = totalPickupTimes / gearsAcquired;
            averageGearsAttempted = (double) gearsAttempted / matchCount;
            averageGearsSuccessful = (double) gearsSuccessful / matchCount;
        }  else {
            //((TextView) findViewById(R.id.team)).setText(teamNumber + " (No Matches)");
        }
        invalidate();
    }


    public void onDraw(Canvas c) {
//        c.drawRect(0,0,c.getWidth(), c.getHeight(), coverPaint);
        c.drawText(teamNumber+" record: "+winData[0]+"-"+winData[1]+"-"+winData[2], c.getWidth()/4, 30, titlePaint);
        if (matchCount!= 0) {
            //Draw borders and frame
            c.drawLine(10, 175, c.getWidth()-10, 175, borderPaint);
            c.drawLine(125, 50, 125, 175, borderPaint);
            c.drawLine(220, 175, 220, c.getHeight()-10, borderPaint);
            //All climb drawing done here
            c.drawText("Climb:", 20, 70, textPaint);
            RectF climb = new RectF();
            climb.set(30, 80, 110, 160);
            c.drawOval(climb, climbNoAttempt);
            c.drawArc(climb, -90f, (float) (360 * (climbData[1] + climbData[2]) / matchCount), true, climbAttempted);
            c.drawArc(climb, -90f, (float) (360 * climbData[2] / matchCount), true, climbSuccessful);
            if (climbData[0]*climbData[1]+climbData[1]*climbData[2]+climbData[2]*climbData[0]==0) {
                if (climbData[2] != 0)
                    c.drawText(climbData[2] + "", 67f, 125f, dataPaint);
                if (climbData[1] != 0)
                    c.drawText(climbData[1] + "", 67f, 125f, dataPaint);
                if (climbData[0] != 0)
                    c.drawText(climbData[0] + "", 67f, 125f, dataPaint);
            } else {
                double angle = 0.0;
                angle = Math.PI / 2 - (2 * Math.PI * (double) climbData[2] / matchCount / 2);
                if (climbData[2] != 0)
                    c.drawText(climbData[2] + "", (float) (67 + 20 * Math.cos(angle)), (float) (125 - 20 * Math.sin(angle)), dataPaint);
                angle = Math.PI / 2 - (2 * Math.PI * climbData[2] / matchCount) - (2 * Math.PI * climbData[1] / matchCount / 2);
                if (climbData[1] != 0)
                    c.drawText(climbData[1] + "", (float) (67 + 20 * Math.cos(angle)), (float) (125 - 20 * Math.sin(angle)), dataPaint);
                angle = Math.PI / 2 - (2 * Math.PI * (climbData[2] + climbData[1]) / matchCount) - (2 * Math.PI * climbData[0] / matchCount / 2);
                if (climbData[0] != 0)
                    c.drawText(climbData[0] + "", (float) (67 + 20 * Math.cos(angle)), (float) (125 - 20 * Math.sin(angle)), dataPaint);
            }
            //All firing drawing done here
            String fireText = "Boiler Points";
            if (hopperSize!=0)
                fireText = fireText +" (hopper: "+ hopperSize+")";
            c.drawText(fireText, 140, 70, textPaint);
            c.drawText("Average: "+(Math.floor(averagePointsPerMatch*100/9)/100), 150, 110, textPaint);
            c.drawText("Maximum: "+(Math.floor(maximumPointsPerMatch*100/9)/100), 150, 150, textPaint);
            //All gear numbers done here
            c.drawText("Gears per match", 20, 210, textPaint);
            c.drawText("Attempted: "+(Math.floor(averageGearsAttempted*100)/100), 30, 250, textPaint);
            c.drawText("Successful: "+(Math.floor(averageGearsSuccessful*100)/100), 30, 290, textPaint);
            //All gear times done here
            c.drawText("Gear Speeds", 230, 210, textPaint);
            if (gearsAcquired!=0){
                c.drawText("Pickup: "+(Math.floor(averagePickupTimes*100)/100)+" s", 240, 250, textPaint);
            } else {
                c.drawText("Pickup: No data", 240, 250, textPaint);
            }
            if (gearsSuccessful!=0){
                c.drawText("Dropoff: "+(Math.floor(averageDropoffTimes*100)/100)+" s", 240, 290, textPaint);
            } else {
                c.drawText("Dropoff: No data", 240, 290, textPaint);
            }        }
    }
}
