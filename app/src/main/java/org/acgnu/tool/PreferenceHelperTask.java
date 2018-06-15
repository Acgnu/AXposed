package org.acgnu.tool;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Toast;
import org.acgnu.xposed.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PreferenceHelperTask extends AsyncTask<Boolean, Void, Map<String, String>> {
    private Context mContext;
    public PreferenceHelperTask(Context context){
        this.mContext = context;
    }
    @Override
    protected Map<String, String> doInBackground(Boolean... type) {
        Map<String, String> result = new HashMap<>();
        try {
            Boolean isImport = type[0];
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(PreferencesUtils.getPrefName(mContext), Context.MODE_WORLD_READABLE);
            if (isImport) {
                String content = FileUtils.readFromSD(FileUtils.EXPORT_DATA_NAME);
                if (TextUtils.isEmpty(content)) {
                    result.put("code", "-1");
                    result.put("msg", mContext.getString(R.string.no_import_data));
                    return result;
                }
                JSONObject prefsJson = new JSONObject(content);
                if (null == prefsJson || prefsJson.length() == 0) {
                    result.put("code", "-2");
                    result.put("msg", mContext.getString(R.string.empty_import_data));
                    return result;
                }
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Iterator<String> iterator = prefsJson.keys();
                while (iterator.hasNext()) {
                    String k = iterator.next();
                    Object v = prefsJson.get(k);
                    if (v instanceof Boolean) {
                        editor.putBoolean(k, (Boolean)v);
                    }else if (v instanceof Integer) {
                        editor.putInt(k, (Integer)v);
                    }else{
                        editor.putString(k, (String) v);
                    }
                }
                editor.commit();
                result.put("code", "0");
                result.put("msg", mContext.getString(R.string.import_success));
                PreferencesUtils.reload();
            } else {
                //读取设置数据
                Map<String, ?> prefs = sharedPreferences.getAll();
                String content = DataUtils.transferMap2Json(prefs).toString();
                FileUtils.savaFileToSD(FileUtils.EXPORT_DATA_NAME, content);
                result.put("code", "0");
                result.put("msg", mContext.getString(R.string.export_success));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            result.put("code", "-3");
            result.put("msg", e.getMessage());
        }
        return result;
    }

    @Override
    protected void onPostExecute(Map<String, String> result) {
        super.onPostExecute(result);
        Toast.makeText(mContext, result.get("msg"), Toast.LENGTH_SHORT).show();
    }
}
