package org.acgnu.tool;

import android.util.Log;
import de.robv.android.xposed.XposedBridge;

public class MyLog {
    public static final String TAG = "#Acgnu#：";
    private static boolean isdebug = false;
    public static void log(String content) {
        doLog(content);
    }
    public static void log(String title, String content) {
        doLog(title + "：" + content);
    }
    private static void doLog(String content) {
        if (isdebug) {
            Log.d(TAG, content);
//            XposedBridge.log(TAG + content);
        }
    }
}
