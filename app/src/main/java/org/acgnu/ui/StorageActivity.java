package org.acgnu.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import org.acgnu.adapter.MyAppAdapter;
import org.acgnu.model.MyAppinfo;
import org.acgnu.tool.PreferencesUtils;
import org.acgnu.xposed.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StorageActivity extends AppCompatActivity {
    private List<MyAppinfo> appdata = new ArrayList<MyAppinfo>();
    private Context context;
    private SharedPreferences sharedPreferences;
    private ProgressDialog loadingDialog;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);
        ActionBar mActionBar=getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        context = this;
        sharedPreferences = context.getSharedPreferences(PreferencesUtils.getPrefName(this),  Context.MODE_WORLD_READABLE);
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
                    MyAppinfo myappinfo = new MyAppinfo(app.loadLabel(pm).toString(), app.packageName, app.loadIcon(pm), sharedPreferences.getString(app.packageName, getString(R.string.unpoint)));
                    appdata.add(myappinfo);
                }
            }

            Collections.sort(appdata, new Comparator<MyAppinfo>() {
                @Override
                public int compare(MyAppinfo entry1, MyAppinfo entry2) {
                    return entry1.getAppname().compareToIgnoreCase(entry2.getAppname());
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            MyAppAdapter adapter = new MyAppAdapter(StorageActivity.this, R.layout.my_app_list, appdata);
            listView.setAdapter(adapter);
            loadingDialog.dismiss();
        }
    }

    public boolean isAllowedApp(ApplicationInfo appInfo) {
        if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
            return false;
        }

        if ("com.android.MtpApplication".contains(appInfo.packageName)
                || "org.acgnu.xposed".contains(appInfo.packageName)) {
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
