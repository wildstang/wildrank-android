package org.wildstang.wildrank.androidv2.views.data;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.couchbase.lite.Document;

import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.models.BallsModel;
import org.wildstang.wildrank.androidv2.models.GearModel;
import org.wildstang.wildrank.androidv2.models.StackModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StackDataView extends View {
    List<List<GearModel>> gear = new ArrayList<>();
    List<List<BallsModel>> high = new ArrayList<>();
    List<List<BallsModel>> low = new ArrayList<>();
    List<Double> averagePickupTimes = new ArrayList<>();
    List<Double> averageDropoffTimes = new ArrayList<>();
    List<Double> averageLowAcc = new ArrayList<>();
    List<Double> averageHighAcc = new ArrayList<>();
    List<Integer> GearsAttemptedArray = new ArrayList<>();
    List<Integer> GearsSuccessfulArray = new ArrayList<>();
    List<String> climbStatus = new ArrayList<>();
    String teamKey = "This ain't workin";

    int matchCount = 0;

    Paint textPaint, climbSuccessful, climbAttempted, climbNoAttempt, noodlePaint, outlinePaint, clearColor, notScoredPaint;

    public StackDataView(Context context, AttributeSet attrs) {
        super(context, attrs);
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        climbNoAttempt = new Paint();
        climbNoAttempt.setColor(Color.GRAY);
        climbAttempted = new Paint();
        climbAttempted.setColor(Color.RED);
        climbSuccessful = new Paint();
        climbSuccessful.setColor(Color.GREEN);
        noodlePaint = new Paint();
        noodlePaint.setColor(Color.YELLOW);
        outlinePaint = new Paint();
        outlinePaint.setColor(Color.BLACK);
        outlinePaint.setStyle(Paint.Style.STROKE);
        clearColor = new Paint();
        clearColor.setColor(Color.WHITE); // Translucent red
        notScoredPaint = new Paint();
        notScoredPaint.setColor(Color.argb(200, 0, 0, 255)); // Translucent blue
    }

    public void acceptNewTeamData(List<Document> matchDocs) {
        if (matchDocs == null || matchDocs.isEmpty()) {
            Log.d("Message", "matchDocs are empty");
            gear = new ArrayList<>();
            high = new ArrayList<>();
            low = new ArrayList<>();
            return;
        }
        gear = new ArrayList<>();
        high = new ArrayList<>();
        low = new ArrayList<>();

        // Sorts the matches by match number
        Collections.sort(matchDocs, new MatchDocumentComparator());
        matchCount = 0;
        // Default, empty StackModel to compare to
        for (Document doc : matchDocs) {
            Map<String, Object> data = (Map<String, Object>) doc.getProperty("data");


            double totalFireHigh = 0.0;
            int timesFiredHigh = 0;
            double totalFireLow = 0.0;
            int timesFiredLow = 0;
            int gearsAcquired = 0;
            int matchGearsAttempted = 0;
            int matchGearsSuccessful = 0;
            int totalDropoffTime = 0;
            int totalPickupTime = 0;
            teamKey = (String) doc.getProperty("team_key");
            Log.d("wallace", "Team key: " + teamKey);

            //manually find gear attempt/success in auto
            if (!data.get("auto-gear").equals("No Attempt")){
                matchGearsAttempted++;
                if (data.get("auto-gear").equals("Success")){
                    totalDropoffTime += (int) data.get("auto-time");
                    matchGearsSuccessful++;
                }
            }

            List<Map<String, Object>> gearData = (List<Map<String, Object>>) data.get("gear");
            List<GearModel> matchGears = new ArrayList<>();
            for (int j = 0; j < gearData.size(); j++) {
                Log.d("wildrank", "gears for match " + (String) doc.getProperty("match_key") + ": " + gearData.get(j));
                gearData.get(j).get("gear_dropoff_speed");

                GearModel gear = GearModel.fromMap(gearData.get(j));
                matchGears.add(gear);
                matchGearsAttempted++;
                gearsAcquired++;
                totalPickupTime += (int) gear.pickupSpeed;
                if (gear.gearEnd.equals("On peg")){
                    matchGearsSuccessful++;
                    totalDropoffTime += (int) gear.dropoffSpeed;
                    Log.d("wallace", (int) gear.dropoffSpeed+"");
                }
            }
            gear.add(matchGears);

            //manually find fire accuracy for high goal in auto
            if(!(data.get("auto-high_rate").equals("None"))) {
                totalFireHigh += Double.parseDouble(((String) data.get("auto-high_accuracy")).substring(0,((String) data.get("auto-high_accuracy")).length()-1))/100;
                timesFiredHigh++;
            }
            List<Map<String, Object>> highData = (List<Map<String, Object>>) data.get("high");
            List<BallsModel> matchHigh = new ArrayList<>();
            for (int j = 0; j < highData.size(); j++) {
                Log.d("wildrank", "High goal for match " + (String) doc.getProperty("match_key") + ": " + highData.get(j));
                BallsModel high = BallsModel.fromMap(highData.get(j));
                matchHigh.add(high);
                if (!high.fireRate.equals("None")){
                    totalFireHigh += (double) high.fireAccuracy;
                    timesFiredHigh++;
                }
            }
            high.add(matchHigh);

            //manually find fire accuracy for low goal in auto
            if(!(data.get("auto-low_rate").equals("None"))) {
                totalFireLow += Double.parseDouble(((String) data.get("auto-low_accuracy")).substring(0,((String) data.get("auto-low_accuracy")).length()-1))/100;
                timesFiredLow++;
            }
            List<Map<String, Object>> lowData = (List<Map<String, Object>>) data.get("high");
            List<BallsModel> matchLow = new ArrayList<>();
            for (int j = 0; j < lowData.size(); j++) {
                Log.d("wildrank", "Low goal for match " + (String) doc.getProperty("match_key") + ": " + gearData.get(j));
                BallsModel low = BallsModel.fromMap(lowData.get(j));
                matchLow.add(low);
                if (!low.fireRate.equals("None")){
                    totalFireLow += (double) low.fireAccuracy;
                    timesFiredLow++;
                }

            }
            low.add(matchLow);



            //adds all temporary data to longer term lists
            GearsAttemptedArray.add(matchGearsAttempted);
            GearsSuccessfulArray.add(matchGearsSuccessful);
            if (matchGearsAttempted>0){
                averagePickupTimes.add(totalPickupTime/ (double) gearsAcquired);
            }else{
                averagePickupTimes.add(-1.0);
            }
            if(matchGearsSuccessful>0){
                averageDropoffTimes.add(totalDropoffTime/ (double) matchGearsSuccessful);
            }else{
                averageDropoffTimes.add(-1.0);
            }
            if (timesFiredHigh>0) {
                averageHighAcc.add((double) totalFireHigh/ (double) timesFiredHigh);
            }else{
                averageHighAcc.add(-1.0);
            }
            if (timesFiredLow>0) {
                averageLowAcc.add((double) totalFireLow/ (double) timesFiredLow);
            }else{
                averageLowAcc.add(-1.0);
            }
            climbStatus.add((String) data.get("post_match-did_climb"));

            Log.d("wallace", matchGearsSuccessful+"");

            matchCount++;
        }
        invalidate();
    }

    @Override
    public void onDraw(Canvas c) {
        c.drawRect(0,0,c.getWidth(), c.getHeight(), clearColor);
        if (gear.isEmpty()&&high.isEmpty()&&low.isEmpty()) {
            c.drawText("No data exists for this team.", 100, 100, textPaint);
        }
        Log.d("wallace", "Height: "+c.getHeight());
        Log.d("wallace", "Wisth: "+c.getWidth());


        // First, compute the dimensions of our drawn items so that they're scaled properly
        float toteWidthToHeightRatio = 0.7f;
        // Default tote width is calculated so that the stacks will completely fill the screen horizontally
        float toteWidth = (float) getWidth() / (float) matchCount;
        // Default height is calculated based on the width
        float toteHeight = toteWidth * toteWidthToHeightRatio;
        // If this would result in a complete stack (7 elements) overflowing the screen, we'll calculate the stack height
        // so that a complete stack would completely fill the screen vertically
        if (toteHeight * 7 > getHeight()) {
            toteHeight = (float) getHeight() / 7f; // gives equal vertical space to 6 totes and 1 bin
            toteWidth = toteHeight / toteWidthToHeightRatio;
        }

        Log.d("wildrank", "Match count: " + matchCount);
        Log.d("wallace", "Gears: "+gear);
        Log.d("wallace", "High: "+high);
        Log.d("wallace", "Low: "+low);
        Log.d("wallace", "Team key: "+teamKey);
        c.drawText(teamKey, 50, 30, textPaint);
        //Double.parseDouble(((String) data.get("auto-low_accuracy")).substring(0,((String) data.get("auto-low_accuracy")).length()-1))/100;
        String climb;
        Paint climbPaint;
        String highAcc;
        String lowAcc;
        String pickup;
        String dropoff;
        String gearsAttempted;
        String gearsSuccessful;
        for (int i = 0; i<matchCount; i++) {
            climb = climbStatus.get(i);
            if (climb.equals("Unsuccessful Attempt")) climb = "Unsuccessful";
            highAcc = (double) Math.round(averageHighAcc.get(i)*10000)/100 + "%";
            if (averageHighAcc.get(i)==-1.0){
                highAcc = "No Shots";
            }
            lowAcc = (double) Math.round(averageLowAcc.get(i)*10000)/100 + "%";
            if (averageLowAcc.get(i)==-1.0){
                lowAcc = "No Shots";
            }
            pickup = "" + (double) Math.round(averagePickupTimes.get(i)*100)/100;
            if (averagePickupTimes.get(i)==-1.0){
                pickup = "No Gears";
            }
            dropoff = "" + (double) Math.round(averageDropoffTimes.get(i)*100)/100;
            if (averageDropoffTimes.get(i)==-1.0){
                dropoff = "No Gears";
            }
            climbPaint = climbNoAttempt;
            if (!climb.equals("No Attempt")){
                climbPaint = climbAttempted;
                if (climb.equals("Successful")){
                    climbPaint = climbSuccessful;
                }
            }
            gearsAttempted = ""+ GearsAttemptedArray.get(i);
            gearsSuccessful = ""+ GearsSuccessfulArray.get(i);


            c.drawLine(150*i, 0, 150*i, 500, outlinePaint);
            c.drawText("Match "+(i+1), 150*i+50, 60, textPaint);
            c.drawText("Climb: " + climb, 150 * i+15, 100, climbPaint);
            c.drawText("High goal %: " + highAcc, 150*i+15, 130, textPaint);
            c.drawText("Low goal %: " + lowAcc, 150*i+15, 160, textPaint);
            c.drawText("Pickup: " + pickup, 150*i+15, 190, textPaint);
            c.drawText("Dropoff: " + dropoff, 150*i+15, 220, textPaint);
            c.drawText("Attempted Gears: " + gearsAttempted, 150*i+15, 250, textPaint);
            c.drawText("Successful Gears: " + gearsSuccessful, 150*i+15, 280, textPaint);
        }
    }

    class MatchDocumentComparator implements Comparator<Document> {

        @Override
        public int compare(Document lhs, Document rhs) {
            try {
                int lhsMatchNumber = Integer.parseInt(((String) lhs.getProperty("match_key")).replaceAll("[0-9]+[a-zA-Z]+_[a-zA-Z]+", ""));
                int rhsMatchNumber = Integer.parseInt(((String) rhs.getProperty("match_key")).replaceAll("[0-9]+[a-zA-Z]+_[a-zA-Z]+", ""));
                Log.d("wildrank", "lhs: " + lhsMatchNumber + ", rhs: " + rhsMatchNumber);
                return (lhsMatchNumber - rhsMatchNumber);
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
    }
}