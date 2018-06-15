package org.acgnu.tool;

import org.json.JSONObject;

import java.util.Map;

public class DataUtils {
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
}
