package org.wildstang.wildrank.androidv2.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.couchbase.lite.Document;

import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.Utilities;
import org.wildstang.wildrank.androidv2.models.TeamDocumentsModel;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TeamStrategyView extends View {
    private Integer teamNumber;

    private double highGoalAccuracy = 0;
    private double lowGoalAccuracy = 0;
    private double averageLowBarCrosses;
    private double averagePortcullisCrosses;
    private double averageQuadRampCrosses;
    private double averageMoatCrosses;
    private double averageRampartsCrosses;
    private double averageDrawbridgeCrosses;
    private double averageSallyportCrosses;
    private double averageRockWallCrosses;
    private double averageRoughTerrainCrosses;
    ArrayList<Integer> averages = new ArrayList<Integer>();
    ArrayList<String> bestDefenses = new ArrayList<String>();

    private int highGoalMade = 0;
    private int highGoalMissed = 0;
    private int lowGoalMade = 0;
    private int lowGoalMissed = 0;
    private int lowBarCrosses = 0;
    private int portcullisCrosses = 0;
    private int portcullisMatches = 0;
    private int quadRampCrosses = 0;
    private int quadRampMatches = 0;
    private int moatCrosses = 0;
    private int moatMatches = 0;
    private int rampartsCrosses = 0;
    private int rampartsMatches = 0;
    private int drawbridgeCrosses = 0;
    private int drawbridgeMatches = 0;
    private int sallyportCrosses = 0;
    private int sallyportMatches = 0;
    private int rockWallCrosses = 0;
    private int rockWallMatches = 0;
    private int roughTerrainCrosses = 0;
    private int roughTerrainMatches = 0;
    private int challenges = 0;
    private int scales = 0;

    private List<Document> matchDocs;

    Paint textPaint, boulderPaint, scaledPaint, scoredPaint, notScoredPaint, outlinePaint, smallTextPaint, idlePaint;

    public TeamStrategyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        textPaint = new Paint();
        textPaint.setTextSize(25);
        textPaint.setColor(Color.BLACK);
        smallTextPaint = new Paint();
        smallTextPaint.setTextSize(15);
        textPaint.setColor(Color.BLACK);
        boulderPaint = new Paint();
        boulderPaint.setColor(Color.DKGRAY);
        outlinePaint = new Paint();
        outlinePaint.setColor(Color.BLACK);
        outlinePaint.setStyle(Paint.Style.STROKE);
        scoredPaint = new Paint();
        scoredPaint.setColor(Color.argb(150, 0, 255, 0));
        notScoredPaint = new Paint();
        notScoredPaint.setColor(Color.argb(150, 255, 0, 0));
        idlePaint = new Paint();
        idlePaint.setColor(Color.argb(150, 0, 0, 255));

    }

    public void populateFromTeamDocuments(TeamDocumentsModel team) {
        matchDocs = team.getMatchDocuments();
        // TODO set R.id.team TextView to team number from team.getTeamDocument()
        teamNumber = (Integer) team.getTeamDocument().getProperty("team_number");
        //String teamName = (String) team.getTeamDocument().getProperty("nickname");



        if (matchDocs != null) {
            //System.out.println(teamNumber);
            //((TextView) findViewById(R.id.team)).setText(teamNumber + " (" + matchDocs.size() + " Matches w/ Data");
            int matches = matchDocs.size();


            for (Document d: matchDocs) {
//                Map<String, Object> data = (Map<String, Object>)d.getProperty("data");
//                highGoalMade += (int) data.get("teleop-highGoalMade");
//                highGoalMissed += (int) data.get("teleop-highGoalMissed");
//
//                lowGoalMade += (int) data.get("teleop-lowGoalMade");
//                lowGoalMissed += (int) data.get("teleop-lowGoalMissed");
//
//
//                portcullisMatches += (boolean) data.get("defense-portcullis") ? 1 : 0;
//                quadRampMatches += (boolean) data.get("defense-quadRamp") ? 1 : 0;
//                moatMatches += (boolean) data.get("defense-moat") ? 1 : 0;
//                rampartsMatches += (boolean) data.get("defense-ramparts") ? 1 : 0;
//                drawbridgeMatches += (boolean) data.get("defense-drawbridge") ? 1 : 0;
//                sallyportMatches += (boolean) data.get("defense-sallyport") ? 1 : 0;
//                rockWallMatches += (boolean) data.get("defense-rockWall") ? 1 : 0;
//                roughTerrainMatches += (boolean) data.get("defense-roughTerrain") ? 1 : 0;
//
//                lowBarCrosses += (int) data.get("teleop-lowBar");
//                portcullisCrosses += (int) data.get("teleop-portcullis");
//                quadRampCrosses += (int) data.get("teleop-quadRamp");
//                moatCrosses += (int) data.get("teleop-moat");
//                rampartsCrosses += (int) data.get("teleop-ramparts");
//                drawbridgeCrosses += (int) data.get("teleop-drawbridge");
//                sallyportCrosses += (int) data.get("teleop-sallyport");
//                rockWallCrosses += (int) data.get("teleop-rockWall");
//                roughTerrainCrosses += (int) data.get("teleop-roughTerrain");
//
//                challenges += (boolean) data.get("teleop-challenged") ? 1 : 0;
//                scales += (boolean) data.get("teleop-scaleSuccessful") ? 1 : 0;
            }

//            highGoalAccuracy = getAccuracy(highGoalMade, highGoalMissed);
//            lowGoalAccuracy = getAccuracy(lowGoalMade, lowGoalMissed);

//            if (!bestDefenses.isEmpty()) {
//                bestDefenses.clear();
//            }
//            if (!averages.isEmpty()) {
//                averages.clear();
//            }
//
//
//
//            averages.add(lowBarCrosses);
//            averages.add(portcullisCrosses);
//            averages.add(quadRampCrosses);
//            averages.add(rampartsCrosses);
//            averages.add(moatCrosses);
//            averages.add(drawbridgeCrosses);
//            averages.add(sallyportCrosses);
//            averages.add(rockWallCrosses);
//            averages.add(roughTerrainCrosses);
//            getBestDefenses(averages, 3);
            //System.out.println(bestDefenses.toString());
//            ((TextView) findViewById(R.id.high_goal_made)).setText("High Made: " + formatNumberAsString(highGoalMade));
//            ((TextView) findViewById(R.id.high_goal_missed)).setText("High Missed: " + formatNumberAsString(highGoalMissed));
//            ((TextView) findViewById(R.id.low_goal_made)).setText("Low Made: " + formatNumberAsString(lowGoalMade));
//            ((TextView) findViewById(R.id.low_goal_missed)).setText("Low Missed: " + formatNumberAsString(lowGoalMissed));
//            ((TextView) findViewById(R.id.average_portcullis)).setText("Avg. Portcullis: " + formatNumberAsString(averagePortcullisCrosses));
//            ((TextView) findViewById(R.id.average_quadRamp)).setText("Avg. Quad Ramp: " + formatNumberAsString(averageQuadRampCrosses));
//            ((TextView) findViewById(R.id.average_moat)).setText("Avg. Moat: " + formatNumberAsString(averageMoatCrosses));
//            ((TextView) findViewById(R.id.average_ramparts)).setText("Avg. Ramparts: " + formatNumberAsString(averageRampartsCrosses));
//            ((TextView) findViewById(R.id.average_drawbridge)).setText("Avg. Drawbridge" + formatNumberAsString(averageDrawbridgeCrosses));
//            ((TextView) findViewById(R.id.average_sallyport)).setText("Avg. Sallyport: " + formatNumberAsString(averageSallyportCrosses));
//            ((TextView) findViewById(R.id.average_rockWall)).setText("Avg. Rock Wall: " + formatNumberAsString(averageRockWallCrosses));
//            ((TextView) findViewById(R.id.average_roughTerrain)).setText("Avg. Rock Wall: " + formatNumberAsString(averageRoughTerrainCrosses));
//            ((TextView) findViewById(R.id.average_lowBar)).setText("Avg. Low Bar: " + formatNumberAsString(averageLowBarCrosses));
//            ((TextView) findViewById(R.id.challenge)).setText("Challenges: " + formatNumberAsString(challenges));
//            ((TextView) findViewById(R.id.scales)).setText("Scales: " + formatNumberAsString(scales));
        }  else {
            //((TextView) findViewById(R.id.team)).setText(teamNumber + " (No Matches)");
        }
        invalidate();

    }

    public void onDraw(Canvas c) {
//        int width = getWidth();
//        if (matchDocs == null || matchDocs.isEmpty()) {
//            c.drawLine(0,50,width, 50, outlinePaint );
//            c.drawText(teamNumber + " (No Match Data)", 10, 32, textPaint);
//        } else {
//
//            c.drawLine(0,50,width, 50, outlinePaint );
//            c.drawText(teamNumber + " (" + matchDocs.size() + " Matches w/ Data)", 10, 32, textPaint);
//
//            c.drawCircle(115, 83, 25, boulderPaint);
//            c.drawCircle(115, 83, 25, outlinePaint);
//            c.drawText("H:" , 30, 90, textPaint);
//
//            c.drawCircle(315, 83, 25, boulderPaint);
//            c.drawCircle(315, 83, 25, outlinePaint);
//            c.drawText("L:" , 230, 90, textPaint);
//
//            if (highGoalMade == 0 && highGoalMissed == 0) {
//                c.drawText("N/A", 93, 93, textPaint);
//            } else {
//                double angle = (highGoalMissed * 360) / (highGoalMade + highGoalMissed);
//                c.drawArc(new RectF(90, 58, 140, 108), (float) (angle / 2), (float) (angle * -1), true, notScoredPaint);
//                c.drawArc(new RectF(90, 58, 140, 108), (float) (angle / 2), (float) (360 - (angle)), true, scoredPaint);
//                c.drawText(highGoalMade + "", 68, 88, smallTextPaint);
//                c.drawText(highGoalMissed + "", 145, 88, smallTextPaint);
//            }
//
//            if (lowGoalMade == 0 && lowGoalMissed == 0) {
//                c.drawText("N/A", 293, 93, textPaint);
//            } else {
//                double angle = (lowGoalMissed * 360) / (lowGoalMade + lowGoalMissed);
//                c.drawArc(new RectF(290, 58, 340, 108), (float) (angle / 2), (float) (angle * -1), true, notScoredPaint);
//                c.drawArc(new RectF(290, 58, 340, 108), (float) (angle / 2), (float) (360 - (angle)), true, scoredPaint);
//                c.drawText(lowGoalMade + "", 268, 88, smallTextPaint);
//                c.drawText(lowGoalMissed + "", 345, 88, smallTextPaint);
//            }
//
//            c.drawText("A: Portcullis: " + formatNumberAsString(portcullisCrosses), 5, 140, textPaint);
//            c.drawText("Quad Ramp: " + formatNumberAsString(quadRampCrosses), 225, 140, textPaint);
//            c.drawText("B: Ramparts: " + formatNumberAsString(rampartsCrosses), 5, 170, textPaint);
//            c.drawText("Moat: " + formatNumberAsString(moatCrosses), 225, 170, textPaint);
//            c.drawText("C: Drawbrdg.: " + formatNumberAsString(drawbridgeCrosses), 5, 200, textPaint);
//            c.drawText("Sallyport: " + formatNumberAsString(sallyportCrosses), 225, 200, textPaint);
//            c.drawText("D: Rock Wall: " + formatNumberAsString(rockWallCrosses), 5, 230, textPaint);
//            c.drawText("Rough Trn.: " + formatNumberAsString(roughTerrainCrosses), 225, 230, textPaint);
//            c.drawText("Low Bar: " + formatNumberAsString(lowBarCrosses), 115, 260, textPaint);
//
//            c.drawText("Challenges: " + challenges, 35, 305, textPaint);
//            c.drawText("Scales: " + scales, 225, 305, textPaint);
//        }
    }

    private double getAccuracy(int success, int failed) {
//        if (success != 0 && failed != 0) {
//            return (double) success / (double) (success + failed);
//        } else {
//            return -1;
//        }
        return 0.0;
    }

    private String formatNumberAsString(double number) {
//        return new DecimalFormat("#.##").format(number);
        return "";
    }

    private String formatPercentageAsString(double percentage) {
//        NumberFormat format = NumberFormat.getPercentInstance();
//        format.setMaximumFractionDigits(2);
//        return format.format(percentage);
        return "";
    }

    private double scaledAt(double percent, int totalWidth, int matchNum) {
//        double oneWidth = (double) totalWidth / (double) matchNum;
//        return totalWidth - oneWidth + (oneWidth * percent);
        return 0.0;
    }

    private double getAverageCrosses(int num, int denom) {
//        if (denom != 0) {
//            return (double) num / (double) denom;
//        } else {
//            return -1;
//        }
        return 0.0;
    }

//    private void getBestDefenses(ArrayList<Integer> a, int cyclesToGo) {
//
//        int cycles = cyclesToGo - 1;
//        Integer max = -2;
//        int index = -1;
//        for (int i = 0; i < a.size(); i++) {
//            if (a.get(i) > max) {
//                max = a.get(i);
//                index = i;
//            }
//        }
//        bestDefenses.add(getString(index));
//        a.set(index, -1);
//        if (cycles != 0) {
//            getBestDefenses(a, cycles);
//        }
//    }

    private String getString(int index) {
        return "";
    }

}
