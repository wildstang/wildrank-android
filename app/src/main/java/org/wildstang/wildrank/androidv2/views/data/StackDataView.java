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

    int matchCount = 0;
    int matchWidth;

    Paint textPaint, climbPaint, winPaint, lowPaint, outlinePaint, highPaint, pickupPaint, dropoffPaint, attemptPaint, successPaint, notScoredPaint;

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
        attemptPaint.setColor(Color.argb(255, 128, 128, 0));
        successPaint = new Paint();
        successPaint.setColor(Color.argb(255, 255, 0, 0));
        outlinePaint = new Paint();
        outlinePaint.setColor(Color.BLACK);
        outlinePaint.setStyle(Paint.Style.STROKE);
        notScoredPaint = new Paint();
        notScoredPaint.setColor(Color.argb(200, 0, 0, 255)); // Translucent blue
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

                //manually find gear attempt/success in auto
                if (!data.get("auto-gear").equals("No Attempt")) {
                    matchGearsAttempted++;
                    if (data.get("auto-gear").equals("Success")) {
                        totalDropoffTime += Math.floor((double) data.get("auto-time"));
                        matchGearsSuccessful++;
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
                    totalTimeLow += (Math.floor((double) data.get("auto-low_time")));
                    totalShotsLow += (Math.floor((double) data.get("auto-low_shots")));
                }
                List<Map<String, Object>> lowData = (List<Map<String, Object>>) data.get("low");
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


                //adds all temporary data to longer term lists
                gearsAttemptedArray.add(matchGearsAttempted);
                gearsSuccessfulArray.add(matchGearsSuccessful);
                if (matchGearsAttempted > 0) {
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


                //Match Number
                c.drawText("Match "+(i+1), (float) (matchWidth*(i+.5)-25), 30, winPaint);
                //climb Data
                c.drawRect(matchWidth*i+5, 45, matchWidth*(i+1)-5, 65, climbPaint);
                c.drawText("Climb", (float) (matchWidth*(i+.5)-15), 60, textPaint);
                //gear Fire Rate graph
                c.drawLine(matchWidth*i, 200, matchWidth*(i+1), 200, outlinePaint);
                c.drawLine(matchWidth*(i+1), 200, matchWidth*(i+1), 100, outlinePaint);
                c.drawLine(matchWidth*(i+1)-3, 180, matchWidth*(i+1)+3, 180, outlinePaint);
                c.drawLine(matchWidth*(i+1)-3, 160, matchWidth*(i+1)+3, 160, outlinePaint);
                c.drawLine(matchWidth*(i+1)-3, 140, matchWidth*(i+1)+3, 140, outlinePaint);
                c.drawLine(matchWidth*(i+1)-3, 120, matchWidth*(i+1)+3, 120, outlinePaint);
                c.drawLine(matchWidth*(i+1)-3, 100, matchWidth*(i+1)+3, 100, outlinePaint);
                if (averageHighRate.get(i)!=-1) {
                    c.drawCircle((float) (matchWidth * (i + .5)), (float) (200 - 5 * highRate), 4, highPaint);
                    if (i > 0) {
                        int prev = 1;
                        while ((prev<i)&&(averageHighRate.get(i-prev)==-1)){
                            prev++;
                        }
                        if (prev<=i) {
                            double prevValue;
                            prevValue = averageHighRate.get(i - prev);
                            c.drawLine((float) (matchWidth * (i - prev + .5)), (float) (200 - 5 * prevValue), (float) (matchWidth * (i + .5)), (float) (200 - 5 * highRate), highPaint);
                        }
                    }
                }
                if (averageLowRate.get(i)!=-1) {
                    c.drawCircle((float) (matchWidth * (i + .5)), (float) (200 - 5 * lowRate), 3, lowPaint);
                    if (i > 0) {
                        int prev = 1;
                        while ((prev<=i)&&(averageLowRate.get(i-prev)==-1)){
                            prev++;
                        }
                        if (prev<=i) {
                            double prevValue;
                            prevValue = averageLowRate.get(i - prev);
                            c.drawLine((float) (matchWidth * (i - prev + .5)), (float) (200 - 5 * prevValue), (float) (matchWidth * (i + .5)), (float) (200 - 5 * lowRate), lowPaint);
                        }
                    }
                }
                //gear Gear Time graph
                c.drawLine(matchWidth*i, 350, matchWidth*(i+1), 350, outlinePaint);
                c.drawLine(matchWidth*(i+1), 350, matchWidth*(i+1), 250, outlinePaint);
                c.drawLine(matchWidth*(i+1)-3, 335, matchWidth*(i+1)+3, 335, outlinePaint);
                c.drawLine(matchWidth*(i+1)-3, 320, matchWidth*(i+1)+3, 320, outlinePaint);
                c.drawLine(matchWidth*(i+1)-3, 305, matchWidth*(i+1)+3, 305, outlinePaint);
                c.drawLine(matchWidth*(i+1)-3, 290, matchWidth*(i+1)+3, 290, outlinePaint);
                c.drawLine(matchWidth*(i+1)-3, 275, matchWidth*(i+1)+3, 275, outlinePaint);
                c.drawLine(matchWidth*(i+1)-3, 260, matchWidth*(i+1)+3, 260, outlinePaint);
                if (averagePickupTimes.get(i)!=-1) {
                    c.drawCircle((float) (matchWidth*(i+.5)),(float) (350-5*pickup), 4, pickupPaint);
                    if (i > 0) {
                        int prev = 1;
                        while ((prev<=i)&&(averagePickupTimes.get(i-prev)==-1)){
                            prev++;
                        }
                        if (prev<=i) {
                            double prevValue;
                            prevValue = averagePickupTimes.get(i - prev);
                            c.drawLine((float) (matchWidth * (i - prev + .5)), (float) (350 - 5 * prevValue), (float) (matchWidth * (i + .5)), (float) (350 - 5 * pickup), pickupPaint);
                        }
                    }
                }
                if (averageDropoffTimes.get(i)!=-1) {
                    c.drawCircle((float) (matchWidth*(i+.5)),(float) (350-5*dropoff), 3, dropoffPaint);
                    if (i > 0) {
                        int prev = 1;
                        while ((prev<=i)&&(averageDropoffTimes.get(i-prev)==-1)){
                            prev++;
                        }
                        if (prev<=i) {
                            double prevValue;
                            prevValue = averageDropoffTimes.get(i - prev);
                            c.drawLine((float) (matchWidth * (i - prev + .5)), (float) (350 - 5 * prevValue), (float) (matchWidth * (i + .5)), (float) (350 - 5 * dropoff), dropoffPaint);
                        }
                    }
                }
                //gear Gear Numbers graph
                c.drawLine(matchWidth*i, 500, matchWidth*(i+1), 500, outlinePaint);
                c.drawLine(matchWidth*(i+1), 500, matchWidth*(i+1), 400, outlinePaint);
                c.drawLine(matchWidth*(i+1)-3, 480, matchWidth*(i+1)+3, 480, outlinePaint);
                c.drawLine(matchWidth*(i+1)-3, 460, matchWidth*(i+1)+3, 460, outlinePaint);
                c.drawLine(matchWidth*(i+1)-3, 440, matchWidth*(i+1)+3, 440, outlinePaint);
                c.drawLine(matchWidth*(i+1)-3, 420, matchWidth*(i+1)+3, 420, outlinePaint);
                c.drawLine(matchWidth*(i+1)-3, 400, matchWidth*(i+1)+3, 400, outlinePaint);
                c.drawCircle((float) (matchWidth*(i+.5)),(float) (500-10*gearsAttempted), 4, attemptPaint);
                c.drawCircle((float) (matchWidth*(i+.5)),(float) (500-10*gearsSuccessful), 3, successPaint);
                if (i>0){
                    double prevAttempt = 0;
                    double prevSuccess = 0;
                    prevAttempt = (double) gearsAttemptedArray.get(i-1);
                    if (gearsAttemptedArray.get(i-1)==-1.0){
                        prevAttempt = 0;
                    }
                    prevSuccess = (double) gearsSuccessfulArray.get(i-1);
                    if (gearsSuccessfulArray.get(i-1)==-1.0){
                        prevSuccess = 0;
                    }
                    c.drawLine((float) (matchWidth*(i-.5)), (float) (500-10*prevAttempt), (float) (matchWidth*(i+.5)),(float) (500-10*gearsAttempted), attemptPaint);
                    c.drawLine((float) (matchWidth*(i-.5)), (float) (500-10*prevSuccess), (float) (matchWidth*(i+.5)),(float) (500-10*gearsSuccessful), successPaint);
                }
            }
            // Fire Rate Info
            c.drawText("Fire Rates (balls/second)", 15, 90, textPaint);
            c.drawText("High goal rate", 155, 90, highPaint);
            c.drawText("Low goal rate", 235, 90, lowPaint);
            c.drawText("0 b/s", matchWidth*matchCount+3, 205, textPaint);
            c.drawText("4 b/s", matchWidth*matchCount+3, 185, textPaint);
            c.drawText("8 b/s", matchWidth*matchCount+3, 165, textPaint);
            c.drawText("12 b/s", matchWidth*matchCount+3, 145, textPaint);
            c.drawText("16 b/s", matchWidth*matchCount+3, 125, textPaint);
            c.drawText("20 b/s", matchWidth*matchCount+3, 105, textPaint);
            // Gear Time Info
            c.drawText("Gear times (seconds)", 15, 240, textPaint);
            c.drawText("Pickup time", 150, 240, pickupPaint);
            c.drawText("Dropoff time", 230, 240, dropoffPaint);
            c.drawText("0 s", matchWidth*matchCount+3, 355, textPaint);
            c.drawText("3 s", matchWidth*matchCount+3, 340, textPaint);
            c.drawText("6 s", matchWidth*matchCount+3, 325, textPaint);
            c.drawText("9 s", matchWidth*matchCount+3, 310, textPaint);
            c.drawText("12 s", matchWidth*matchCount+3, 295, textPaint);
            c.drawText("15 s", matchWidth*matchCount+3, 280, textPaint);
            c.drawText("18 s", matchWidth*matchCount+3, 265, textPaint);
            // Gear Number Info
            c.drawText("Gears in a Match (gears)", 15, 390, textPaint);
            c.drawText("Attempted gears", 150, 390, attemptPaint);
            c.drawText("Successful gears", 240, 390, successPaint);
            c.drawText("0", matchWidth*matchCount+3, 505, textPaint);
            c.drawText("2", matchWidth*matchCount+3, 485, textPaint);
            c.drawText("4", matchWidth*matchCount+3, 465, textPaint);
            c.drawText("6", matchWidth*matchCount+3, 445, textPaint);
            c.drawText("8", matchWidth*matchCount+3, 425, textPaint);
            c.drawText("10", matchWidth*matchCount+3, 405, textPaint);
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