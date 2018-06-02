package org.acgnu.ui;

import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import org.acgnu.service.MaskService;
import org.acgnu.tool.PreferencesUtils;
import org.acgnu.xposed.R;

public class MainActivity extends AppCompatActivity {
    private SettingsFragment mSettingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
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
                    PreferencesUtils.destroyInstance();
                    return true;
                }
            };
            Preference pvpmask = findPreference("pvpmask");
            Preference pvpopen = findPreference("pvpopen");
            Preference open = findPreference("open");
            Preference target = findPreference("target");

            pvpmask.setOnPreferenceChangeListener(listener);
            pvpopen.setOnPreferenceChangeListener(listener);
            open.setOnPreferenceChangeListener(listener);
            target.setOnPreferenceChangeListener(listener);
        }
    }
}
