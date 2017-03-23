package org.wildstang.wildrank.androidv2.models;

import java.util.HashMap;
import java.util.Map;

public class BallsModel {

    public static final String BALL_LOCATION = "ball_location";
    public static final String TIME_TAKEN = "time_taken";
    public static final String SHOTS_MADE = "shots_made";

    public int timeTaken;
    public int shotsMade;
    public String ballLocation;

    public BallsModel() {
        // Initialize everything to zero/false
        timeTaken = 0;
        shotsMade = 0;
    }

    public static BallsModel fromMap(Map<String, Object> map) {
        BallsModel balls = new BallsModel();
        balls.timeTaken = (int) map.get(TIME_TAKEN);
        balls.shotsMade = (int) map.get(SHOTS_MADE);
        balls.ballLocation = (String) map.get(BALL_LOCATION);
        return balls;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(BallsModel.TIME_TAKEN, this.timeTaken);
        map.put(BallsModel.SHOTS_MADE, this.shotsMade);
        map.put(BallsModel.BALL_LOCATION, this.ballLocation);
        return map;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BallsModel)) {
            return false;
        }
        if (o == this) {
            return true;
        }
        BallsModel comparing = (BallsModel) o;
        boolean equals = true;
        equals &= (comparing.timeTaken == this.timeTaken);
        equals &= (comparing.shotsMade == this.shotsMade);
        equals &= (comparing.ballLocation == this.ballLocation);
        return equals;
    }

    /**
     * A meaningful stack is defined as one that indicates something actually happened.
     * <p>
     * We will define a meaningful stack as one that has totes, a bin, or both.
     *
     * @return true if it is meaningful stack, false if otherwise
     */
}
