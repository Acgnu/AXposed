package org.acgnu.ui;

import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import org.acgnu.service.MaskService;
import org.acgnu.tool.FileUtils;
import org.acgnu.tool.PreferencesUtils;
import org.acgnu.xposed.R;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private SettingsFragment mSettingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        JSON.parse("{}");
        if (savedInstanceState == null) {
            mSettingsFragment = new SettingsFragment();
            replaceFragment(R.id.settings_container, mSettingsFragment);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void replaceFragment(int viewId, android.app.Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(viewId, fragment).commit();
    }

    /**
     * A placeholder fragment containing generateKey settings view.
     */
    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getPreferenceManager().setSharedPreferencesMode(MODE_WORLD_READABLE);
            addPreferencesFromResource(R.xml.pref_setting);
            Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newVal) {
                    if(preference.getKey().equals("pvpmask")){
                        boolean state = (boolean) newVal;
                        Intent svc = new Intent(getActivity(), MaskService.class);
                        getActivity().stopService(svc);
                        if (state) {
                            getActivity().startService(svc);
                        }
                    }
                    PreferencesUtils.reload();
                    return true;
                }
            };
            Preference pvpmask = findPreference("pvpmask");
            Preference pvpopen = findPreference("pvpopen");
            Preference open = findPreference("open");
            Preference target = findPreference("target");
            Preference storageopen = findPreference("storageopen");

            pvpmask.setOnPreferenceChangeListener(listener);
            pvpopen.setOnPreferenceChangeListener(listener);
            open.setOnPreferenceChangeListener(listener);
            target.setOnPreferenceChangeListener(listener);
            storageopen.setOnPreferenceChangeListener(listener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //创建弹出式菜单对象
       PopupMenu popup = new PopupMenu(this, findViewById(R.id.menu_import_setting));
       //获取菜单填充器
       MenuInflater inflater = popup.getMenuInflater();
       //填充菜单
       inflater.inflate(R.menu.import_exprot_popup_menu, popup.getMenu());
       //绑定菜单项的点击事件
       popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
           @Override
           public boolean onMenuItemClick(MenuItem item) {
               switch (item.getItemId()) {
                   case R.id.popup_menu_import:
                       exchangePreference(true);
                       return true;
                   case R.id.popup_menu_export:
                       exchangePreference(false);
                       return true;
                   default:
                       return false;
               }
           }
       });
       popup.show();
       return super.onOptionsItemSelected(item);
    }

    /**
     * 导入/导出设置
     * @Param flag true 导入 / false 导出
     */
    private void exchangePreference(boolean flag){
        new AsyncTask<Boolean, Void, Integer>(){
            @Override
            protected Integer doInBackground(Boolean... type) {
                Boolean isImport = type[0];
                SharedPreferences sharedPreferences = getSharedPreferences(PreferencesUtils.getPrefName(getApplicationContext()), Context.MODE_WORLD_READABLE);
                if (isImport) {
                    //读取设置数据
                    Map<String, ?> prefs = sharedPreferences.getAll();
                    String content = JSON.toJSONString(prefs);
                    FileUtils.savaFileToSD(FileUtils.EXPORT_DATA_NAME, content);
                    return 1;   //导出成功
                } else {
                    String content = FileUtils.readFromSD(FileUtils.EXPORT_DATA_NAME);
                    if (!TextUtils.isEmpty(content)) {
                        return -1;  //没有可导入的数据
                    }
                    Map<String, Object> prefs = JSON.toJavaObject(JSON.parseObject(content), Map.class);
                    if (null == prefs) {
                        return -2;  //可导入的数据为空
                    }
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    for (Map.Entry<String, Object> entry : prefs.entrySet()) {
                        if (entry.getValue() instanceof Boolean) {
                            editor.putBoolean(entry.getKey(), (Boolean)entry.getValue());
                        }else if (entry.getValue() instanceof Integer) {
                            editor.putInt(entry.getKey(), (Integer)entry.getValue());
                        }else{
                            editor.putString(entry.getKey(), (String) entry.getValue());
                        }
                    }
                    editor.commit();
                }
                return 2;   //导入成功
            }

            @Override
            protected void onPostExecute(Integer result) {
                super.onPostExecute(result);
                String msg = null;
                switch (result) {
                    case 1: msg = "导出成功"; break;
                    case 2: msg = "导入成功"; break;
                    case -1: msg = "没有可导入的数据"; break;
                    case -2: msg = "可导入的数据为空"; break;
                    default: msg = "未知";
                }
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        }.execute(flag);
    }
}
