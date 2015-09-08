package org.wildstang.wildrank.androidv2;

import android.view.animation.Interpolator;

public class ReverseInterpolator implements Interpolator {
    @Override
    public float getInterpolation(float paramFloat) {
        return Math.abs(paramFloat - 1f);
    }
}