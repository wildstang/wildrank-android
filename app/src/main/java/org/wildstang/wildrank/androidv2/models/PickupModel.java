package org.wildstang.wildrank.androidv2.models;

import java.util.HashMap;
import java.util.Map;

public class PickupModel {

    public static final String PICKUP_TYPE_KEY = "gear_pickup_type";
    public static final String PICKUP_SPEED_KEY = "gear_pickup_speed";
    public static final String DROPOFF_SPEED_KEY = "gear_dropoff_speed";
    public static final String GEAR_END = "gear_end_location";

    public String pickupType;
    public String pickupSpeed;
    public String dropoffSpeed;
    public String gearEnd;

    public PickupModel() {
        // Initialize everything to zero/false
        pickupType = "Player Station";
        pickupSpeed = "Very Slow";
        dropoffSpeed = "Very Slow";
        gearEnd = "On peg";
    }

    public static PickupModel fromMap(Map<String, Object> map) {
        PickupModel pickup = new PickupModel();
        pickup.pickupType = (String) map.get(PICKUP_TYPE_KEY);
        pickup.pickupSpeed = (String) map.get(PICKUP_SPEED_KEY);
        pickup.pickupSpeed = (String) map.get(DROPOFF_SPEED_KEY);
        pickup.pickupSpeed = (String) map.get(GEAR_END);
        return pickup;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(PickupModel.PICKUP_TYPE_KEY, this.pickupType);
        map.put(PickupModel.PICKUP_SPEED_KEY, this.pickupSpeed);
        map.put(PickupModel.DROPOFF_SPEED_KEY, this.pickupSpeed);
        map.put(PickupModel.GEAR_END, this.gearEnd);
        return map;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PickupModel)) {
            return false;
        }
        if (o == this) {
            return true;
        }
        PickupModel comparing = (PickupModel) o;
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
