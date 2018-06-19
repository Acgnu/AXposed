package org.acgnu.tool;

import android.util.Log;
import de.robv.android.xposed.XposedBridge;

public class MyLog {
    public static final String TAG = "###Acgnu###：";
    public static void log(String content) {
        if (true) {
            Log.d(TAG, content);
//            XposedBridge.log(TAG + content);
        }
    }
}
