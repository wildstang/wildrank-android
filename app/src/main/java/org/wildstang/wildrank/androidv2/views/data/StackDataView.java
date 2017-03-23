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

    List<Double> averagePickupTimes = new ArrayList<>();
    List<Double> averageDropoffTimes = new ArrayList<>();
    List<Double> averageLowRate = new ArrayList<>();
    List<Double> averageHighRate = new ArrayList<>();
    List<Integer> GearsAttemptedArray = new ArrayList<>();
    List<Integer> GearsSuccessfulArray = new ArrayList<>();
    List<String> climbStatus = new ArrayList<>();

    int matchCount = 0;
    int matchWidth;
    int columnWidth;

    Paint textPaint, climbSuccessful, climbAttempted, climbNoAttempt, lowPaint, outlinePaint, highPaint, pickupPaint, dropoffPaint, attemptPaint, successPaint, notScoredPaint;

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
            GearsAttemptedArray = new ArrayList<>();
            GearsSuccessfulArray = new ArrayList<>();
            climbStatus = new ArrayList<>();
            return;
        }
        averagePickupTimes = new ArrayList<>();
        averageDropoffTimes = new ArrayList<>();
        averageLowRate = new ArrayList<>();
        averageHighRate = new ArrayList<>();
        GearsAttemptedArray = new ArrayList<>();
        GearsSuccessfulArray = new ArrayList<>();
        climbStatus = new ArrayList<>();
        // Sorts the matches by match number
        Collections.sort(matchDocs, new MatchDocumentComparator());
        matchCount = 0;
        // Default, empty StackModel to compare to
        if (matchDocs.size() != 0) {
            for (Document doc : matchDocs) {
                Map<String, Object> data = (Map<String, Object>) doc.getProperty("data");

                Log.d("Test", (String) data.get("post_match-did_climb"));
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


                //adds all temporary data to longer term lists
                GearsAttemptedArray.add(matchGearsAttempted);
                GearsSuccessfulArray.add(matchGearsSuccessful);
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



                matchCount++;
            }
        }
        invalidate();
    }

    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);
        if (matchCount == 0) {
            c.drawText("No data exists for this team.", 100, 100, textPaint);
        } else {

            matchWidth = 150;
            columnWidth = 50;
            // First, compute the dimensions of our drawn items so that they're scaled properly
            if (matchCount * matchWidth > c.getWidth()){
                Log.d("wallace", ""+c.getWidth());
                columnWidth = c.getWidth()/matchCount/3;
                matchWidth = columnWidth*3;
            }
            //resize font for different amounts of data
            if (matchCount>7){
                textPaint.setTextSize(19f-matchCount);
                highPaint.setTextSize(19f-matchCount);
                lowPaint.setTextSize(19f-matchCount);
                successPaint.setTextSize(19f-matchCount);
                attemptPaint.setTextSize(19f-matchCount);
                pickupPaint.setTextSize(19f-matchCount);
                dropoffPaint.setTextSize(19f-matchCount);

            }
            String climb;
            Paint climbPaint;
            double highRate;
            double lowRate;
            double pickup;
            double dropoff;
            int gearsAttempted;
            int gearsSuccessful;
            for (int i = 0; i<matchCount; i++) {
                climb = climbStatus.get(i);
                if (climb.equals("Unsuccessful Attempt")) climb = "Unsuccessful";
                highRate = (double) Math.round(averageHighRate.get(i));
                if (averageHighRate.get(i)==-1.0){
                    highRate = 0;
                }
                lowRate = (double) Math.round(averageLowRate.get(i));
                if (averageLowRate.get(i)==-1.0){
                    lowRate = 0;
                }
                pickup = (double) Math.round(averagePickupTimes.get(i)*100)/100;
                if (averagePickupTimes.get(i)==-1.0){
                    pickup = 0;
                }
                dropoff = (double) Math.round(averageDropoffTimes.get(i)*100)/100;
                if (averageDropoffTimes.get(i)==-1.0){
                    dropoff = 0;
                }
                climbPaint = climbNoAttempt;
                if (!climb.equals("No Attempt")){
                    climbPaint = climbAttempted;
                    if (climb.equals("Successful")){
                        climbPaint = climbSuccessful;
                    }
                }
                gearsAttempted = GearsAttemptedArray.get(i);
                gearsSuccessful = GearsSuccessfulArray.get(i);


                //Match Number
                c.drawText("Match "+(i+1), matchWidth*i+columnWidth, 40, textPaint);
                //climb Data
                c.drawRect(matchWidth*i+5, 55, matchWidth*(i+1)-5, 75, climbPaint);
                c.drawText("Climb: "+climb, matchWidth*i+(textPaint.getTextSize()+3), 70, textPaint);
                //gear Fire Rate graph
                c.drawLine(matchWidth*i, 200, matchWidth*(i+1), 200, outlinePaint);
                c.drawLine(matchWidth*(i+1), 200, matchWidth*(i+1), 100, outlinePaint);
                c.drawLine(matchWidth*(i+1)-3, 180, matchWidth*(i+1)+3, 180, outlinePaint);
                c.drawLine(matchWidth*(i+1)-3, 160, matchWidth*(i+1)+3, 160, outlinePaint);
                c.drawLine(matchWidth*(i+1)-3, 140, matchWidth*(i+1)+3, 140, outlinePaint);
                c.drawLine(matchWidth*(i+1)-3, 120, matchWidth*(i+1)+3, 120, outlinePaint);
                c.drawLine(matchWidth*(i+1)-3, 100, matchWidth*(i+1)+3, 100, outlinePaint);
                c.drawCircle((float) (matchWidth*(i+.5)),(float) (200-5*highRate), 3, highPaint);
                c.drawCircle((float) (matchWidth*(i+.5)),(float) (200-5*lowRate), 3, lowPaint);
                if (i>0){
                    double prevHigh = 0;
                    double prevLow = 0;
                    prevHigh = (double) Math.round(averageHighRate.get(i-1));
                    if (averageHighRate.get(i-1)==-1.0){
                        prevHigh = 0;
                    }
                    prevLow = (double) Math.round(averageLowRate.get(i-1));
                    if (averageLowRate.get(i-1)==-1.0){
                        prevLow = 0;
                    }
                    c.drawLine((float) (matchWidth*(i-.5)), (float) (200-5*prevHigh), (float) (matchWidth*(i+.5)),(float) (200-5*highRate), highPaint);
                    c.drawLine((float) (matchWidth*(i-.5)), (float) (200-5*prevLow), (float) (matchWidth*(i+.5)),(float) (200-5*lowRate), lowPaint);
                }
                //gear Gear Time graph
                c.drawLine(matchWidth*i, 350, matchWidth*(i+1), 350, outlinePaint);
                c.drawLine(matchWidth*(i+1), 350, matchWidth*(i+1), 250, outlinePaint);
                c.drawLine(matchWidth*(i+1)-3, 335, matchWidth*(i+1)+3, 335, outlinePaint);
                c.drawLine(matchWidth*(i+1)-3, 320, matchWidth*(i+1)+3, 320, outlinePaint);
                c.drawLine(matchWidth*(i+1)-3, 305, matchWidth*(i+1)+3, 305, outlinePaint);
                c.drawLine(matchWidth*(i+1)-3, 290, matchWidth*(i+1)+3, 290, outlinePaint);
                c.drawLine(matchWidth*(i+1)-3, 275, matchWidth*(i+1)+3, 275, outlinePaint);
                c.drawCircle((float) (matchWidth*(i+.5)),(float) (350-5*pickup), 3, pickupPaint);
                c.drawCircle((float) (matchWidth*(i+.5)),(float) (350-5*dropoff), 3, dropoffPaint);
                if (i>0){
                    double prevPickup = 0;
                    double prevDropoff = 0;
                    prevPickup = (double) Math.round(averagePickupTimes.get(i-1));
                    if (averagePickupTimes.get(i-1)==-1.0){
                        prevPickup = 0;
                    }
                    prevDropoff = (double) Math.round(averageDropoffTimes.get(i-1));
                    if (averageDropoffTimes.get(i-1)==-1.0){
                        prevDropoff = 0;
                    }
                    c.drawLine((float) (matchWidth*(i-.5)), (float) (350-5*prevPickup), (float) (matchWidth*(i+.5)),(float) (350-5*pickup), pickupPaint);
                    c.drawLine((float) (matchWidth*(i-.5)), (float) (350-5*prevDropoff), (float) (matchWidth*(i+.5)),(float) (350-5*dropoff), dropoffPaint);
                }
                //gear Gear Numbers graph
                c.drawLine(matchWidth*i, 500, matchWidth*(i+1), 500, outlinePaint);
                c.drawLine(matchWidth*(i+1), 500, matchWidth*(i+1), 400, outlinePaint);
                c.drawLine(matchWidth*(i+1)-3, 480, matchWidth*(i+1)+3, 480, outlinePaint);
                c.drawLine(matchWidth*(i+1)-3, 460, matchWidth*(i+1)+3, 460, outlinePaint);
                c.drawLine(matchWidth*(i+1)-3, 440, matchWidth*(i+1)+3, 440, outlinePaint);
                c.drawLine(matchWidth*(i+1)-3, 420, matchWidth*(i+1)+3, 420, outlinePaint);
                c.drawLine(matchWidth*(i+1)-3, 400, matchWidth*(i+1)+3, 400, outlinePaint);
                c.drawCircle((float) (matchWidth*(i+.5)),(float) (500-10*gearsAttempted), 3, attemptPaint);
                c.drawCircle((float) (matchWidth*(i+.5)),(float) (500-10*gearsSuccessful), 3, successPaint);
                if (i>0){
                    double prevAttempt = 0;
                    double prevSuccess = 0;
                    prevAttempt = (double) Math.round(GearsAttemptedArray.get(i-1));
                    if (GearsAttemptedArray.get(i-1)==-1.0){
                        prevAttempt = 0;
                    }
                    prevSuccess = (double) Math.round(GearsSuccessfulArray.get(i-1));
                    if (GearsSuccessfulArray.get(i-1)==-1.0){
                        prevSuccess = 0;
                    }
                    c.drawLine((float) (matchWidth*(i-.5)), (float) (500-10*prevAttempt), (float) (matchWidth*(i+.5)),(float) (500-10*gearsAttempted), attemptPaint);
                    c.drawLine((float) (matchWidth*(i-.5)), (float) (500-10*prevSuccess), (float) (matchWidth*(i+.5)),(float) (500-10*gearsSuccessful), successPaint);
                }
            }
            // Fire Rate Info
            c.drawText("Fire Rates (balls/second)", 15, 90, textPaint);
            c.drawText("High goal rate", matchWidth, 90, highPaint);
            c.drawText("Low goal rate", matchWidth+80, 90, lowPaint);
            c.drawText("0 b/s", matchWidth*matchCount+3, 200, textPaint);
            c.drawText("4 b/s", matchWidth*matchCount+3, 180, textPaint);
            c.drawText("8 b/s", matchWidth*matchCount+3, 160, textPaint);
            c.drawText("12 b/s", matchWidth*matchCount+3, 140, textPaint);
            c.drawText("16 b/s", matchWidth*matchCount+3, 120, textPaint);
            c.drawText("20 b/s", matchWidth*matchCount+3, 100, textPaint);
            // Gear Time Info
            c.drawText("Gear times (seconds)", 15, 240, textPaint);
            c.drawText("Pickup time", matchWidth, 240, dropoffPaint);
            c.drawText("Dropoff time", matchWidth+80, 240, pickupPaint);
            c.drawText("0 s", matchWidth*matchCount+3, 350, textPaint);
            c.drawText("3 s", matchWidth*matchCount+3, 335, textPaint);
            c.drawText("6 s", matchWidth*matchCount+3, 320, textPaint);
            c.drawText("9 s", matchWidth*matchCount+3, 305, textPaint);
            c.drawText("12 s", matchWidth*matchCount+3, 290, textPaint);
            c.drawText("15 s", matchWidth*matchCount+3, 275, textPaint);
            // Gear Number Info
            c.drawText("Gears in a Match (gears)", 15, 390, textPaint);
            c.drawText("Attempted gears", matchWidth, 390, attemptPaint);
            c.drawText("Successful gears", matchWidth+90, 390, successPaint);
            c.drawText("0", matchWidth*matchCount+3, 500, textPaint);
            c.drawText("2", matchWidth*matchCount+3, 480, textPaint);
            c.drawText("4", matchWidth*matchCount+3, 460, textPaint);
            c.drawText("6", matchWidth*matchCount+3, 440, textPaint);
            c.drawText("8", matchWidth*matchCount+3, 420, textPaint);
            c.drawText("10", matchWidth*matchCount+3, 400, textPaint);
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