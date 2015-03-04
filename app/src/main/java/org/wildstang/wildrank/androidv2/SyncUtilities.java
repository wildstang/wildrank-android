package org.wildstang.wildrank.androidv2;

import java.io.File;

/**
 * Created by Nathan on 2/16/2015.
 */
public class SyncUtilities {

    public static boolean isFlashDriveConnected() {
        return new File(Utilities.getExternalRootDirectory()).exists();
    }
}
