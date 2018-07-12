package org.acgnu.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import org.acgnu.adapter.MyAppAdapter;
import org.acgnu.xposed.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StorageActivity extends AppCompatActivity {
    private List<ApplicationInfo> appdata = new ArrayList<>();
    private Context context;
    private ProgressDialog loadingDialog;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);
        ActionBar mActionBar=getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        context = this;
        listView = (ListView) findViewById(R.id.applist);
        new LoadApps().execute();
    }

    public class LoadApps extends AsyncTask<Void, Void, Void> {
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        @Override
        protected void onPreExecute() {
            loadingDialog = ProgressDialog.show(context, null, getString(R.string.loading), true, false);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            for (ApplicationInfo app : packages) {
                if (isAllowedApp(app)) {
                    appdata.add(app);
                }
            }

            Collections.sort(appdata, new Comparator<ApplicationInfo>() {
                @Override
                public int compare(ApplicationInfo entry1, ApplicationInfo entry2) {
                    return entry1.loadLabel(pm).toString().compareToIgnoreCase(entry2.loadLabel(pm).toString());
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            MyAppAdapter adapter = new MyAppAdapter(StorageActivity.this, R.layout.app_list_item, appdata);
            listView.setAdapter(adapter);
            loadingDialog.dismiss();
        }
    }

    public boolean isAllowedApp(ApplicationInfo appInfo) {
//        if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
//            return false;
//        }

        if ("org.acgnu.xposed".contains(appInfo.packageName)
                || appInfo.packageName.contains("android")
                || appInfo.packageName.contains("qualcomm")
                || appInfo.packageName.contains("google")
                || appInfo.packageName.contains("oneplus")
                || appInfo.packageName.contains("com.qti")
                || appInfo.packageName.contains("com.oem")
                || appInfo.packageName.contains("com.oppo")
                || appInfo.packageName.contains("com.dsi.ant.server")
                || appInfo.packageName.contains("com.example")
                || appInfo.packageName.contains("org.codeaurora")) {
            return false;
        }
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
