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
import org.acgnu.tool.MyLog;
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

    public class LoadApps extends AsyncTask<Void, Void, Void> {
        List<CharSequence> appNames = new ArrayList<>();
        List<CharSequence> packageNames = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            List<String[]> sortedApps = new ArrayList<>();

            for (ApplicationInfo app : packages) {
                if (isAllowedApp(app)) {
                    sortedApps.add(new String[]{app.packageName, app.loadLabel(pm).toString()});
                }
            }

            Collections.sort(sortedApps, new Comparator<String[]>() {
                @Override
                public int compare(String[] entry1, String[] entry2) {
                    return entry1[1].compareToIgnoreCase(entry2[1]);
                }
            });

            for (int i = 0; i < sortedApps.size(); i++) {
                appNames.add(sortedApps.get(i)[1] + "\n" + "(" + sortedApps.get(i)[0] + ")");
                packageNames.add(sortedApps.get(i)[0]);
                MyAppinfo myappinfo = new MyAppinfo(sortedApps.get(i)[1], sortedApps.get(i)[0], null);
                appdata.add(myappinfo);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            MyAppAdapter adapter = new MyAppAdapter(StorageActivity.this, R.layout.my_app_list, appdata);
            listView.setAdapter(adapter);
        }
    }

    public boolean isAllowedApp(ApplicationInfo appInfo) {
        if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
            return false;
        }

        if ("com.android.MtpApplication".contains(appInfo.packageName)) {
            return false;
        }
        return true;
    }
}
