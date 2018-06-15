package org.acgnu.ui;

import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import org.acgnu.service.MaskService;
import org.acgnu.tool.PreferenceHelperTask;
import org.acgnu.tool.PreferencesUtils;
import org.acgnu.xposed.R;

public class MainActivity extends AppCompatActivity {
    private SettingsFragment mSettingsFragment;
    private PopupMenu popup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        if (null == popup) {
            //创建弹出式菜单对象
            popup = new PopupMenu(this, findViewById(R.id.menu_import_setting));
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
                            new PreferenceHelperTask(getApplicationContext()).execute(true);
                            return true;
                        case R.id.popup_menu_export:
                            new PreferenceHelperTask(getApplicationContext()).execute(false);
                            return true;
                        default:
                            return false;
                    }
                }
            });
        }
       popup.show();
       return super.onOptionsItemSelected(item);
    }
}
