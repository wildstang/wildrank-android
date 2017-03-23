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

public class TeamStrategyView extends View {

    private Integer teamNumber;

    private double totalPickupTimes = 0.0;
    private double totalDropoffTimes = 0.0;
    private double totalTimeHigh = 0.0;
    private double totalTimeLow = 0.0;
    private int gearsAttempted = 0;
    private int gearsSuccessful = 0;
    private int totalShotsHigh = 0;
    private int totalShotsLow = 0;
    int gearsAcquired = 0;
    private int hopperSize = 0;


    private double averagePickupTimes = 0.0;
    private double averageDropoffTimes = 0.0;
    private double averageLowRate = 0.0;
    private double averageHighRate = 0.0;
    private double averageGearsAttempted = 0.0;
    private double averageGearsSuccessful = 0.0;


    private int climbData[] = {0,0,0};

    int matchCount = 0;

    private List<Document> matchDocs;
    private  Document pitDocs;

    Paint textPaint, titlePaint, dataPaint, climbSuccessful, climbAttempted, climbNoAttempt, lowPaint, outlinePaint, highPaint, pickupPaint, dropoffPaint, attemptPaint, successPaint, borderPaint;



    public TeamStrategyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(24);
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
        highPaint = new Paint();
        highPaint.setColor(Color.argb(255, 32, 32, 255));
        lowPaint = new Paint();
        lowPaint.setColor(Color.argb(255, 0, 0, 128));
        pickupPaint = new Paint();
        pickupPaint.setColor(Color.argb(255, 32, 255, 32));
        dropoffPaint = new Paint();
        dropoffPaint.setColor(Color.argb(255, 0, 128, 0));
        attemptPaint = new Paint();
        attemptPaint.setColor(Color.argb(255, 255, 32, 32));
        successPaint = new Paint();
        successPaint.setColor(Color.argb(255, 128, 0, 0));
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
                if ((Math.floor((double) data.get("auto-high_shots")))!=0) {
                    totalTimeHigh += (Math.floor((double) data.get("auto-high_time")));
                    totalShotsHigh += (Math.floor((double) data.get("auto-high_shots")));
                }
                List<Map<String, Object>> highData = (List<Map<String, Object>>) data.get("high");
                List<BallsModel> matchHigh = new ArrayList<>();
                for (int j = 0; j < highData.size(); j++) {
                    //Log.d("wildrank", "High goal for match " + (String) doc.getProperty("match_key") + ": " + highData.get(j));
                    BallsModel high = BallsModel.fromMap(highData.get(j));
                    matchHigh.add(high);
                    if (high.shotsMade!=0) {
                        totalShotsHigh += high.shotsMade;
                        totalTimeHigh++;
                    }
                }

                //manually find fire accuracy for low goal in auto
                if ((Math.floor((double) data.get("auto-low_shots")))!=0) {
                    totalTimeHigh += (Math.floor((double) data.get("auto-low_time")));
                    totalShotsHigh += (Math.floor((double) data.get("auto-low_shots")));
                }
                List<Map<String, Object>> lowData = (List<Map<String, Object>>) data.get("high");
                List<BallsModel> matchLow = new ArrayList<>();
                for (int j = 0; j < lowData.size(); j++) {
                    //Log.d("wildrank", "Low goal for match " + (String) doc.getProperty("match_key") + ": " + gearData.get(j));
                    BallsModel low = BallsModel.fromMap(lowData.get(j));
                    matchLow.add(low);
                    if (low.shotsMade!=0) {
                        totalShotsLow += low.shotsMade;
                        totalTimeLow+= low.timeTaken;
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
                matchCount++;
            }
            averageHighRate = (double) totalShotsHigh / totalTimeHigh;
            averageLowRate = (double) totalShotsLow / totalTimeLow;
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
        if (matchCount==1){
            c.drawText(teamNumber+" (1 match)", c.getWidth()/4, 30, titlePaint);
        } else {
            c.drawText(teamNumber+" ("+matchCount+" matches)", c.getWidth()/4, 30, titlePaint);
        }
        if (matchCount!= 0) {
            //Draw borders and frame
            c.drawLine(10, 175, c.getWidth()-10, 175, borderPaint);
            c.drawLine(145, 50, 145, 175, borderPaint);
            c.drawLine(230, 175, 230, c.getHeight()-10, borderPaint);
            //All climb drawing done here
            c.drawText("Climb:", 30, 70, textPaint);
            RectF climb = new RectF();
            climb.set(40, 80, 120, 160);
            c.drawOval(climb, climbNoAttempt);
            c.drawArc(climb, -90f, (float) (360 * (climbData[1] + climbData[2]) / matchCount), true, climbAttempted);
            c.drawArc(climb, -90f, (float) (360 * climbData[2] / matchCount), true, climbSuccessful);
            if (climbData[0]*climbData[1]+climbData[1]*climbData[2]+climbData[2]*climbData[0]==0) {
                if (climbData[2] != 0)
                    c.drawText(climbData[2] + "", 77f, 125f, dataPaint);
                if (climbData[1] != 0)
                    c.drawText(climbData[1] + "", 77f, 125f, dataPaint);
                if (climbData[0] != 0)
                    c.drawText(climbData[0] + "", 77f, 125f, dataPaint);
            } else {
                double angle = 0.0;
                angle = Math.PI / 2 - (2 * Math.PI * (double) climbData[2] / matchCount / 2);
                if (climbData[2] != 0)
                    c.drawText(climbData[2] + "", (float) (77 + 20 * Math.cos(angle)), (float) (125 - 20 * Math.sin(angle)), dataPaint);
                angle = Math.PI / 2 - (2 * Math.PI * climbData[2] / matchCount) - (2 * Math.PI * climbData[1] / matchCount / 2);
                if (climbData[1] != 0)
                    c.drawText(climbData[1] + "", (float) (77 + 20 * Math.cos(angle)), (float) (125 - 20 * Math.sin(angle)), dataPaint);
                angle = Math.PI / 2 - (2 * Math.PI * (climbData[2] + climbData[1]) / matchCount) - (2 * Math.PI * climbData[0] / matchCount / 2);
                if (climbData[0] != 0)
                    c.drawText(climbData[0] + "", (float) (77 + 20 * Math.cos(angle)), (float) (125 - 20 * Math.sin(angle)), dataPaint);
            }
            //All firing drawing done here
            String fireText = "Fire Rate";
            if (hopperSize!=0)
                fireText = fireText +" (hopper: "+ hopperSize+")";
            c.drawText(fireText, 170, 70, textPaint);
            c.drawText("High goal: "+(Math.floor(averageHighRate*100)/100)+" b/s", 180, 110, textPaint);
            c.drawText("Low goal: "+(Math.floor(averageLowRate*100)/100)+" b/s", 180, 150, textPaint);
            //All gear numbers done here
            c.drawText("Gears per match", 30, 210, textPaint);
            c.drawText("Attempted: "+(Math.floor(averageGearsAttempted*100)/100), 40, 250, textPaint);
            c.drawText("Successful: "+(Math.floor(averageGearsSuccessful*100)/100), 40, 290, textPaint);
            //All gear times done here
            c.drawText("Gear Speeds", 240, 210, textPaint);
            c.drawText("Pickup: "+(Math.floor(averagePickupTimes*100)/100)+"s", 250, 250, textPaint);
            c.drawText("Dropoff: "+(Math.floor(averageDropoffTimes*100)/100)+"s", 250, 290, textPaint);
        }
    }
}
