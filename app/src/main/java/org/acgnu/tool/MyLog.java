package org.acgnu.tool;

import android.util.Log;
import com.alibaba.fastjson.JSON;
import de.robv.android.xposed.XposedBridge;

public class MyLog {
    public static final String TAG = "#Acgnu#：";
    private static boolean isdebug = true;
    public static void log(Object content, boolean doLog) {
        if(!doLog) return;
        if (content instanceof String) {
            doLog((String) content);
        } else {
            doLog(getObjectString(content));
        }
    }
    public static void log(String title, Object content, boolean doLog) {
        if(!doLog) return;
        if (content instanceof String) {
            doLog(title + "：" + content);
        } else {
            doLog(title + "：" + getObjectString(content));
        }
    }
    private static void doLog(String content) {
        if (isdebug) {
            Log.d(TAG, content);
            XposedBridge.log(TAG + content);
        }
    }

    private static String logWithFastJson(Object object){
        try {
            return JSON.toJSONString(object);
        } catch (Exception e) {

        }
        return "fastjson fail";
    }

    public static String getObjectString(Object object){
        String result = logWithFastJson(object);
        return result;
    }
}
