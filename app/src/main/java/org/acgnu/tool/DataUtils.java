package org.acgnu.tool;

import android.content.Intent;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class DataUtils {
    public static final String GLOBAL_RESULT_CODE_KEY = "code";
    public static final String GLOBAL_RESULT_MSG_KEY = "msg";

    public static JSONObject transferMap2Json(Map<String, ?> map) {
        JSONObject json = new JSONObject();
        try {
            for (Map.Entry<String, ?> entry : map.entrySet()) {
                json.put(entry.getKey(), entry.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    public static void setMapResult(Map<String, String> data, Integer code, String msg) {
        data.put(GLOBAL_RESULT_CODE_KEY, code.toString());
        data.put(GLOBAL_RESULT_MSG_KEY, msg);
    }
}
