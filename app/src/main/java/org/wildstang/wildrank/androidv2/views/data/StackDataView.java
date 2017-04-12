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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class StackDataView extends View {

    List<Double> averagePickupTimes = new ArrayList<>();
    List<Double> averageDropoffTimes = new ArrayList<>();
    List<Double> averageLowRate = new ArrayList<>();
    List<Double> averageHighRate = new ArrayList<>();
    List<Integer> gearsAttemptedArray = new ArrayList<>();
    List<Integer> gearsSuccessfulArray = new ArrayList<>();
    List<String> climbStatus = new ArrayList<>();
    List<String> winStatus = new ArrayList<>();
    List<Double> ballPoints = new ArrayList<>();
    List<Integer> AutoHigh = new ArrayList<>();
    List<Integer> AutoLow = new ArrayList<>();
    List<Integer> AutoGear = new ArrayList<>();
    List<String> disabled = new ArrayList<>();
    int matchCount = 0;
    int matchWidth;

    Paint textPaint, climbPaint, winPaint, lowPaint, outlinePaint, highPaint, pickupPaint, dropoffPaint, attemptPaint, successPaint, troublePaint;

    public StackDataView(Context context, AttributeSet attrs) {
        super(context, attrs);
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        climbPaint = new Paint();
        climbPaint.setColor(Color.GRAY);
        winPaint = new Paint();
        winPaint.setColor(Color.GREEN);
        highPaint = new Paint();
        highPaint.setColor(Color.argb(255, 128, 0, 128));
        lowPaint = new Paint();
        lowPaint.setColor(Color.argb(255, 0, 0, 255));
        pickupPaint = new Paint();
        pickupPaint.setColor(Color.argb(255, 0, 128, 128));
        dropoffPaint = new Paint();
        dropoffPaint.setColor(Color.argb(255, 0, 255, 0));
        attemptPaint = new Paint();
        attemptPaint.setColor(Color.argb(255, 255, 128, 0));
        successPaint = new Paint();
        successPaint.setColor(Color.argb(255, 0, 255, 0));
        outlinePaint = new Paint();
        outlinePaint.setColor(Color.BLACK);
        outlinePaint.setStyle(Paint.Style.STROKE);
        troublePaint = new Paint();
        troublePaint.setColor(Color.argb(128, 32, 32, 255)); // Translucent blue
    }

    public void acceptNewTeamData(List<Document> matchDocs) {
        if (matchDocs == null || matchDocs.isEmpty()) {
            averagePickupTimes = new ArrayList<>();
            averageDropoffTimes = new ArrayList<>();
            averageLowRate = new ArrayList<>();
            averageHighRate = new ArrayList<>();
            gearsAttemptedArray = new ArrayList<>();
            gearsSuccessfulArray = new ArrayList<>();
            climbStatus = new ArrayList<>();
            winStatus = new ArrayList<>();
            ballPoints = new ArrayList<>();
            disabled = new ArrayList<>();
            AutoHigh = new ArrayList<>();
            AutoLow = new ArrayList<>();
            AutoGear = new ArrayList<>();
            matchCount = 0;
            invalidate();
            return;
        }
        averagePickupTimes = new ArrayList<>();
        averageDropoffTimes = new ArrayList<>();
        averageLowRate = new ArrayList<>();
        averageHighRate = new ArrayList<>();
        gearsAttemptedArray = new ArrayList<>();
        gearsSuccessfulArray = new ArrayList<>();
        climbStatus = new ArrayList<>();
        winStatus = new ArrayList<>();
        ballPoints = new ArrayList<>();
        disabled = new ArrayList<>();
        AutoHigh = new ArrayList<>();
        AutoLow = new ArrayList<>();
        AutoGear = new ArrayList<>();
        matchCount = 0;
        // Sorts the matches by match number
        Collections.sort(matchDocs, new MatchDocumentComparator());
        // Default, empty StackModel to compare to
        if (matchDocs.size() != 0) {
            for (Document doc : matchDocs) {
                Map<String, Object> data = (Map<String, Object>) doc.getProperty("data");

                int totalShotsHigh = 0;
                int totalTimeHigh = 0;
                int totalShotsLow = 0;
                int totalTimeLow = 0;
                int gearsAcquired = 0;
                int matchGearsAttempted = 0;
                int matchGearsSuccessful = 0;
                int totalDropoffTime = 0;
                int totalPickupTime = 0;
                int lowBallsScoredInTeleop = 0;
                int autoHigh = 0;
                int autoLow = 0;
                int autoGear = 0;
                String matchDisabled = "";


                //manually find gear attempt/success in auto
                if (!data.get("auto-gear").equals("No Attempt")) {
                    matchGearsAttempted++;
                    autoGear = 1;
                    if (data.get("auto-gear").equals("Success")) {
                        totalDropoffTime += Math.floor((double) data.get("auto-time"));
                        matchGearsSuccessful++;
                        autoGear = 2;
                    }
                }

                List<Map<String, Object>> gearData = (List<Map<String, Object>>) data.get("gear");
                List<GearModel> matchGears = new ArrayList<>();
                for (int j = 0; j < gearData.size(); j++) {
                    //Log.d("wildrank", "gears for match " + (String) doc.getProperty("match_key") + ": " + gearData.get(j));
                    gearData.get(j).get("gear_dropoff_speed");

                    GearModel gear = GearModel.fromMap(gearData.get(j));
                    matchGears.add(gear);
                    matchGearsAttempted++;
                    gearsAcquired++;
                    totalPickupTime += (int) gear.pickupSpeed;
                    if (gear.gearEnd.equals("On peg")) {
                        matchGearsSuccessful++;
                        totalDropoffTime += (int) gear.dropoffSpeed;
                    }
                }

                //manually find fire rate for high goal in auto
                if ((Math.floor((double) data.get("auto-high_time")))!=0) {
                    totalTimeHigh += (Math.floor((double) data.get("auto-high_time")));
                    totalShotsHigh += (Math.floor((double) data.get("auto-high_shots")));
                    lowBallsScoredInTeleop+=(9*(Math.floor((double) data.get("auto-high_shots"))));
                    autoHigh += (int) (Math.floor((double) data.get("auto-high_shots")));
                }
                List<Map<String, Object>> highData = (List<Map<String, Object>>) data.get("high");
                List<BallsModel> matchHigh = new ArrayList<>();
                for (int j = 0; j < highData.size(); j++) {
                    //Log.d("wildrank", "High goal for match " + (String) doc.getProperty("match_key") + ": " + highData.get(j));
                    BallsModel high = BallsModel.fromMap(highData.get(j));
                    matchHigh.add(high);
                    if (high.timeTaken!=0) {
                        totalShotsHigh += high.shotsMade;
                        totalTimeHigh += high.timeTaken;
                        lowBallsScoredInTeleop+=(3*high.shotsMade);
                    }
                }

                //manually find fire rate for low goal in auto
                if ((Math.floor((double) data.get("auto-low_time")))!=0) {
                    totalTimeLow += (Math.floor((double) data.get("auto-low_time")));
                    totalShotsLow += (Math.floor((double) data.get("auto-low_shots")));
                    lowBallsScoredInTeleop+=(3*(Math.floor((double) data.get("auto-low_shots"))));
                    autoLow += (int) (Math.floor((double) data.get("auto-low_shots")));
                }
                List<Map<String, Object>> lowData = (List<Map<String, Object>>) data.get("low");
                List<BallsModel> matchLow = new ArrayList<>();
                for (int j = 0; j < lowData.size(); j++) {
                    //Log.d("wildrank", "Low goal for match " + (String) doc.getProperty("match_key") + ": " + gearData.get(j));
                    BallsModel low = BallsModel.fromMap(lowData.get(j));
                    matchLow.add(low);
                    if (low.timeTaken!=0) {
                        totalShotsLow += low.shotsMade;
                        totalTimeLow+= low.timeTaken;
                        lowBallsScoredInTeleop+=(1*low.shotsMade);
                    }

                }


                //adds all temporary data to longer term lists
                gearsAttemptedArray.add(matchGearsAttempted);
                gearsSuccessfulArray.add(matchGearsSuccessful);
                if (gearsAcquired > 0) {
                    averagePickupTimes.add(totalPickupTime / (double) gearsAcquired);
                } else {
                    averagePickupTimes.add(-1.0);
                }
                if (matchGearsSuccessful > 0) {
                    averageDropoffTimes.add(totalDropoffTime / (double) matchGearsSuccessful);
                } else {
                    averageDropoffTimes.add(-1.0);
                }
                if (totalTimeHigh > 0) {
                    averageHighRate.add((double) totalShotsHigh / (double) totalTimeHigh);
                } else {
                    averageHighRate.add(-1.0);
                }
                if (totalTimeLow > 0) {
                    averageLowRate.add((double) totalShotsLow / (double) totalTimeLow);
                } else {
                    averageLowRate.add(-1.0);
                }
                climbStatus.add((String) data.get("post_match-did_climb"));
                winStatus.add((String) data.get("post_match-did_win"));
                ballPoints.add((double) lowBallsScoredInTeleop/9);
                AutoHigh.add(autoHigh);
                AutoLow.add(autoLow);
                AutoGear.add(autoGear);
                if((boolean) data.get("post_match-broke_down")){
                    matchDisabled += "broke down";
                }
                if((boolean) data.get("post_match-tipped")){
                    if (matchDisabled!=""){
                        matchDisabled += ", ";
                    }
                    matchDisabled += "tipped";
                }
                if((boolean) data.get("post_match-lost_comm")){
                    if (matchDisabled!=""){
                        matchDisabled += ", ";
                    }
                    matchDisabled += "lost comm";
                }
//                if((boolean) data.get("post_match-froze")){
//                    if (matchDisabled!=""){
//                        matchDisabled += ", ";
//                    }
//                    matchDisabled += "froze";
//                }
                disabled.add(matchDisabled);

                matchCount++;
            }
        }
        invalidate();
    }

    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);
        Log.d("wildrank", ""+matchCount);
        if (matchCount == 0) {
            c.drawText("No data exists for this team.", 100, 100, textPaint);
        } else {

            matchWidth = 150;
            // First, compute the dimensions of our drawn items so that they're scaled properly
            if ((matchCount * matchWidth + 45) > c.getWidth()){
                Log.d("wallace", ""+c.getWidth());
                matchWidth = (c.getWidth() - 45)/matchCount;
            }
            double highRate;
            double lowRate;
            double pickup;
            double dropoff;
            int gearsAttempted;
            int gearsSuccessful;
            int points;
            int autoHigh;
            int autoLow;
            int autoGear;
            String matchDisabled;
            for (int i = 0; i<matchCount; i++) {
                highRate = (averageHighRate.get(i));
                lowRate = (averageLowRate.get(i));
                pickup = (averagePickupTimes.get(i));
                dropoff = (averageDropoffTimes.get(i));
                winPaint.setColor(Color.GREEN);
                if (!winStatus.get(i).equals("Won")){
                    winPaint.setColor(Color.RED);
                    if (winStatus.get(i).equals("Tied")){
                        winPaint.setColor(Color.BLUE);
                    }
                }
                climbPaint.setColor(Color.GRAY);
                if (!climbStatus.get(i).equals("No Attempt")){
                    climbPaint.setColor(Color.RED);
                    if (climbStatus.get(i).equals("Successful")){
                        climbPaint.setColor(Color.GREEN);
                    }
                }
                gearsAttempted = gearsAttemptedArray.get(i);
                gearsSuccessful = gearsSuccessfulArray.get(i);
                points = (int) Math.floor(ballPoints.get(i));
                autoHigh = AutoHigh.get(i);
                autoLow = AutoLow.get(i);
                autoGear = AutoGear.get(i);
                matchDisabled = disabled.get(i);

                //Match Number
                c.drawText("Match "+(i+1), (float) (matchWidth*(i+.5)-25), 30, winPaint);
                //climb Data
                c.drawRect(matchWidth*i+5, 45, matchWidth*(i+1)-5, 65, climbPaint);
                c.drawText("Climb", (float) (matchWidth*(i+.5)-15), 60, textPaint);
                //gear Fire Rate graph
                drawFireRate(c, 100, 200, i, highRate, lowRate, 20);
                //gear Gear Time graph
                drawGearsTime(c, 90, 320, i, pickup, dropoff, 18);
                //gear Gear Numbers graph
                drawGearsNumber(c, 100, 450, i, gearsAttempted, gearsSuccessful, 10);
                // Bottom Info Here
                c.drawText("Total points: " + points, (float) (matchWidth*i+10), 470, textPaint);
                c.drawText("Auto: ", matchWidth*i+10, 485, textPaint);
                c.drawText("High: "+autoHigh, matchWidth*i+10, 500, textPaint);
                c.drawText("Low: "+autoLow, matchWidth*i+10, 515, textPaint);
                if(autoGear != 0){
                    if(autoGear == 1){
                        c.drawText("Gear: failed", matchWidth * i + 10, 530, textPaint);
                    } else {
                        c.drawText("Gear: success", matchWidth * i + 10, 530, textPaint);
                    }
                } else {
                    c.drawText("Gear: no try", matchWidth * i + 10, 530, textPaint);
                }
                // Trouble info here
                if(matchDisabled!=""){
                    c.drawRect((float) (matchWidth*i+5), 70, (float) (matchWidth*(i+1)-5), c.getHeight()-25, troublePaint);
                    c.drawText("Issue: "+disabled.get(i), (float) (matchWidth*i+5), c.getHeight()-15, textPaint);
                }
            }
            // Fire Rate Info
            c.drawText("Fire Rates (balls/second)", 15, 90, textPaint);
            c.drawText("High goal rate", 155, 90, highPaint);
            c.drawText("Low goal rate", 235, 90, lowPaint);
            for(int i = 1; i<=5; i++){
                c.drawText((4*i) + " b/s", matchWidth*matchCount+3, 205-(20*i), textPaint);
            }
            // Gear Time Info
            c.drawText("Gear times (seconds)", 15, 220, textPaint);
            c.drawText("Pickup time", 150, 220, pickupPaint);
            c.drawText("Dropoff time", 230, 220, dropoffPaint);
            for(int i = 1; i<=6; i++){
                c.drawText((3*i) + " s", matchWidth*matchCount+3, 325-(15*i), textPaint);
            }
            // Gear Number Info
            c.drawText("Gears in a Match (gears)", 15, 340, textPaint);
            c.drawText("Attempted gears", 150, 340, attemptPaint);
            c.drawText("Successful gears", 240, 340, successPaint);
            for(int i = 1; i<=5; i++){
                c.drawText((2*i) + "", matchWidth*matchCount+3, 455-(20*i), textPaint);
            }
        }
    }

    void drawFireRate(Canvas c, int height, int bottom, int step, double highrate, double lowrate, int max){
        //gear Fire Rate graph
        //unit is 5
        int unit = height/max;
        double heightChange = 4*unit;
        c.drawLine(matchWidth*step, bottom, matchWidth*(step+1), bottom, outlinePaint);
        c.drawLine(matchWidth*(step+1), bottom, matchWidth*(step+1), bottom-height, outlinePaint);
        for(int i = 1; i<=height/heightChange;i++) {
            c.drawLine(matchWidth * (step + 1) - 3, (float) (bottom-(heightChange*i)), matchWidth * (step + 1) + 3, (float) (bottom-(heightChange*i)), outlinePaint);
        }
        if (averageHighRate.get(step)!=-1) {
            c.drawCircle((float) (matchWidth * (step + .5)), (float) (bottom - unit * highrate), 4, highPaint);
            if (step > 0) {
                int prev = 1;
                while ((prev<=step)&&(averageHighRate.get(step-prev)==-1)){
                    prev++;
                }
                if (prev<=step) {
                    double prevValue;
                    prevValue = averageHighRate.get(step - prev);
                    c.drawLine((float) (matchWidth * (step - prev + .5)), (float) (bottom - unit * prevValue), (float) (matchWidth * (step + .5)), (float) (bottom - unit * highrate), highPaint);
                }
            }
        }
        if (averageLowRate.get(step)!=-1) {
            c.drawCircle((float) (matchWidth * (step + .5)), (float) (bottom - unit * lowrate), 3, lowPaint);
            if (step > 0) {
                int prev = 1;
                while ((prev<=step)&&(averageLowRate.get(step-prev)==-1)){
                    prev++;
                }
                if (prev<=step) {
                    double prevValue;
                    prevValue = averageLowRate.get(step - prev);
                    c.drawLine((float) (matchWidth * (step - prev + .5)), (float) (bottom - unit * prevValue), (float) (matchWidth * (step + .5)), (float) (bottom - unit * lowrate), lowPaint);
                }
            }
        }
    }

    void drawGearsTime(Canvas c, int height, int bottom, int step, double pickUp, double dropOff, int max){
        int unit = height/max;
        double heightChange = 3*unit;
        //gear Gear Time graph
        //unit is 5
        c.drawLine(matchWidth*step, bottom, matchWidth*(step+1), bottom, outlinePaint);
        c.drawLine(matchWidth*(step+1), bottom, matchWidth*(step+1), (bottom-height), outlinePaint);
        for(int i = 1; i<=height/heightChange;i++) {
            c.drawLine(matchWidth * (step + 1) - 3, (float) (bottom-(heightChange*i)), matchWidth * (step + 1) + 3, (float) (bottom-(heightChange*i)), outlinePaint);
        }
        if (averagePickupTimes.get(step)!=-1) {
            c.drawCircle((float) (matchWidth*(step+.5)),(float) (bottom-unit*pickUp), 4, pickupPaint);
            if (step > 0) {
                int prev = 1;
                while ((prev<=step)&&(averagePickupTimes.get(step-prev)==-1)){
                    prev++;
                }
                if (prev<=step) {
                    double prevValue;
                    prevValue = averagePickupTimes.get(step - prev);
                    c.drawLine((float) (matchWidth * (step - prev + .5)), (float) (bottom - unit * prevValue), (float) (matchWidth * (step + .5)), (float) (bottom - unit * pickUp), pickupPaint);
                }
            }
        }
        if (averageDropoffTimes.get(step)!=-1) {
            c.drawCircle((float) (matchWidth*(step+.5)),(float) (bottom-unit*dropOff), 3, dropoffPaint);
            if (step > 0) {
                int prev = 1;
                while ((prev<=step)&&(averageDropoffTimes.get(step-prev)==-1)){
                    prev++;
                }
                if (prev<=step) {
                    double prevValue;
                    prevValue = averageDropoffTimes.get(step - prev);
                    c.drawLine((float) (matchWidth * (step - prev + .5)), (float) (bottom - unit * prevValue), (float) (matchWidth * (step + .5)), (float) (bottom - unit * dropOff), dropoffPaint);
                }
            }
        }
    }

    void drawGearsNumber(Canvas c, int height, int bottom, int step, double gearsattempted, double gearssuccessful, int max){
        //unit is 10
        int unit = height/max;
        double heightChange = 2*unit;
        //gear Gear Numbers graph
        c.drawRect(matchWidth*step+4, (float) (bottom-unit*gearsattempted), matchWidth*(1+step)-4, bottom, attemptPaint);
        c.drawRect(matchWidth*step+6, (float) (bottom-unit*gearssuccessful), matchWidth*(1+step)-6, bottom, successPaint);
        c.drawLine(matchWidth*step, bottom, matchWidth*(step+1), bottom, outlinePaint);
        c.drawLine(matchWidth*(step+1), bottom, matchWidth*(step+1), bottom-height, outlinePaint);
        for(int i = 1; i<=height/heightChange;i++) {
            c.drawLine(matchWidth * (step + 1) - 3, (float) (bottom-(heightChange*i)), matchWidth * (step + 1) + 3, (float) (bottom-(heightChange*i)), outlinePaint);
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