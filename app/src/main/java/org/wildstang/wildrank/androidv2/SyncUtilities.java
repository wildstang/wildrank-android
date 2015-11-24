package org.wildstang.wildrank.androidv2;

import java.io.File;

public class SyncUtilities {

    public static boolean isFlashDriveConnected() {
        return new File(Utilities.getExternalRootDirectory()).exists();
    }
}
