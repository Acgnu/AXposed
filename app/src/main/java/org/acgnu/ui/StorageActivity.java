package org.acgnu.ui;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.support.constraint.solver.widgets.ConstraintAnchor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import org.acgnu.adapter.MyAppAdapter;
import org.acgnu.model.MyAppinfo;
import org.acgnu.xposed.R;

import java.util.*;

public class StorageActivity extends AppCompatActivity {
    private List<MyAppinfo> appdata = new ArrayList<MyAppinfo>();
    private Context context;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_storage);
        new LoadApps().execute();
        listView = (ListView) findViewById(R.id.applist);

    }

//    private void initFruits() {
//        MyAppinfo apple = new MyAppinfo("Apple", "a/b/c");
//        appdata.add(apple);
//        MyAppinfo banana = new MyAppinfo("Banana", "d/d/b");
//        appdata.add(banana);
//        MyAppinfo orange = new MyAppinfo("Orange", "b/w/a");
//        appdata.add(orange);
//    }

    public class LoadApps extends AsyncTask<Void, Void, Void> {
        //        MultiSelectListPreference enabledApps = (MultiSelectListPreference) findPreference("enable_for_apps");
//        MultiSelectListPreference disabledApps = (MultiSelectListPreference) findPreference("disable_for_apps");
        List<CharSequence> appNames = new ArrayList<>();
        List<CharSequence> packageNames = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        @Override
        protected void onPreExecute() {
//            enabledApps.setEnabled(false);
//            disabledApps.setEnabled(false);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            List<String[]> sortedApps = new ArrayList<>();

            Log.d("包数量", packages.size() + "");
            for (ApplicationInfo app : packages) {
                if (isAllowedApp(app)) {
                    sortedApps.add(new String[]{app.packageName, app.loadLabel(pm).toString()});
                }
            }

            Log.d("存入的数量", sortedApps.size() + "");

            Collections.sort(sortedApps, new Comparator<String[]>() {
                @Override
                public int compare(String[] entry1, String[] entry2) {
                    return entry1[1].compareToIgnoreCase(entry2[1]);
                }
            });

            for (int i = 0; i < sortedApps.size(); i++) {
                appNames.add(sortedApps.get(i)[1] + "\n" + "(" + sortedApps.get(i)[0] + ")");
                packageNames.add(sortedApps.get(i)[0]);
                MyAppinfo myappinfo = new MyAppinfo(sortedApps.get(i)[1] + "\n" + "(" + sortedApps.get(i)[0] + ")", "/virtual/path");
                appdata.add(myappinfo);
            }
            Log.d("适配器数量", appdata.size() + "");
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            MyAppAdapter adapter = new MyAppAdapter(StorageActivity.this, R.layout.my_app_list, appdata);
            listView.setAdapter(adapter);
//            CharSequence[] appNamesList = appNames.toArray(new CharSequence[appNames.size()]);
//            CharSequence[] packageNamesList = packageNames.toArray(new CharSequence[packageNames.size()]);

//            enabledApps.setEntries(appNamesList);
//            enabledApps.setEntryValues(packageNamesList);
//            enabledApps.setEnabled(true);
//            disabledApps.setEntries(appNamesList);
//            disabledApps.setEntryValues(packageNamesList);
//            disabledApps.setEnabled(true);

//            Preference.OnPreferenceClickListener listener = new Preference.OnPreferenceClickListener() {
//                @Override
//                public boolean onPreferenceClick(Preference preference) {
//                    ((MultiSelectListPreference) preference).getDialog().getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.FILL_PARENT);
//                    return false;
//                }
//            };

//            enabledApps.setOnPreferenceClickListener(listener);
//            disabledApps.setOnPreferenceClickListener(listener);
        }
    }

    public boolean isAllowedApp(ApplicationInfo appInfo) {
        boolean includeSystemApps = false;
        if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0 && !includeSystemApps) {
            return false;
        }

        if ("com.android.MtpApplication".contains(appInfo.packageName)) {
            return false;
        }
        return true;
    }
}
