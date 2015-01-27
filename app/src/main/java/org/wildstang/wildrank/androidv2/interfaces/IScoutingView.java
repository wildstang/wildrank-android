package org.wildstang.wildrank.androidv2.interfaces;

import java.util.Map;

/**
 * Created by Nathan on 1/22/2015.
 */
public interface IScoutingView {
    public void writeContentsToMap(Map<String, Object> map);

    public void restoreFromMap(final Map<String, Object> map);

    public String getKey();

    public boolean isComplete(boolean highlight);
}
