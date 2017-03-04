package org.wildstang.wildrank.androidv2.models;

import java.util.HashMap;
import java.util.Map;

public class GearModel {

    public static final String PICKUP_TYPE_KEY = "gear_pickup_type";
    public static final String PICKUP_SPEED_KEY = "gear_pickup_speed";
    public static final String DROPOFF_SPEED_KEY = "gear_dropoff_speed";
    public static final String GEAR_END = "gear_end_location";

    public String pickupType;
    public String pickupSpeed;
    public String dropoffSpeed;
    public String gearEnd;

    public GearModel() {
        // Initialize everything to zero/false
        pickupType = "Player Station";
        pickupSpeed = "Very Slow";
        dropoffSpeed = "Very Slow";
        gearEnd = "On peg";
    }

    public static GearModel fromMap(Map<String, Object> map) {
        GearModel gear = new GearModel();
        gear.pickupType = (String) map.get(PICKUP_TYPE_KEY);
        gear.pickupSpeed = (String) map.get(PICKUP_SPEED_KEY);
        gear.pickupSpeed = (String) map.get(DROPOFF_SPEED_KEY);
        gear.pickupSpeed = (String) map.get(GEAR_END);
        return gear;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(GearModel.PICKUP_TYPE_KEY, this.pickupType);
        map.put(GearModel.PICKUP_SPEED_KEY, this.pickupSpeed);
        map.put(GearModel.DROPOFF_SPEED_KEY, this.pickupSpeed);
        map.put(GearModel.GEAR_END, this.gearEnd);
        return map;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GearModel)) {
            return false;
        }
        if (o == this) {
            return true;
        }
        GearModel comparing = (GearModel) o;
        boolean equals = true;
        equals &= (comparing.pickupType == this.pickupType);
        equals &= (comparing.pickupSpeed == this.pickupSpeed);
        equals &= (comparing.dropoffSpeed == this.dropoffSpeed);
        equals &= (comparing.gearEnd == this.gearEnd);
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
