package org.wildstang.wildrank.androidv2.models;

import java.util.HashMap;
import java.util.Map;

public class BallsModel {

    public static final String BALL_LOCATION = "ball_location";
    public static final String FIRE_RATE = "fire_rate";
    public static final String FIRE_ACCURACY = "fire_accuracy";

    public String fireRate;
    public double fireAccuracy;
    public String ballLocation;

    public BallsModel() {
        // Initialize everything to zero/false
        fireRate = "Very Slow";
        fireAccuracy = 0.0;
    }

    public static BallsModel fromMap(Map<String, Object> map) {
        BallsModel balls = new BallsModel();
        balls.fireRate = (String) map.get(FIRE_ACCURACY);
        balls.fireAccuracy = (double) map.get(FIRE_ACCURACY);
        balls.ballLocation = (String) map.get(BALL_LOCATION);
        return balls;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(BallsModel.FIRE_RATE, this.fireRate);
        map.put(BallsModel.FIRE_ACCURACY, this.fireAccuracy);
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
        equals &= (comparing.fireRate == this.fireRate);
        equals &= (comparing.fireAccuracy == this.fireAccuracy);
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
