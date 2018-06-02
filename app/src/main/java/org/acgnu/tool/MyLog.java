package org.acgnu.tool;

import de.robv.android.xposed.XposedBridge;

public class MyLog {
    public static final String TAG = "###Acgnu###：";
    public static void log(String content) {
        XposedBridge.log(TAG + content);
//        Log.d(TAG, content);
    }
}
