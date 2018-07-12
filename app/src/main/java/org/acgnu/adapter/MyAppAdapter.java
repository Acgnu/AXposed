package org.acgnu.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import org.acgnu.tool.PreferencesUtils;
import org.acgnu.xposed.R;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 显示所有已安装应用的Listview Adapter
 */
public class MyAppAdapter extends ArrayAdapter {
    private final int resourceId;
    private SharedPreferences sharedPreferences;
    private PackageManager mPm;

    public MyAppAdapter(Context context, int textViewResourceId, List<ApplicationInfo> objects){
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        if (null == sharedPreferences) {
            sharedPreferences = context.getSharedPreferences(PreferencesUtils.getPrefName(getContext()),  Context.MODE_WORLD_READABLE);
        }
        if (null == mPm) {
            mPm = context.getPackageManager();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ApplicationInfo myApp = (ApplicationInfo) getItem(position); // 获取当前项的Appinfo实例
        View listItemLayout = LayoutInflater.from(getContext()).inflate(resourceId, null);  //实例化ListView的项目布局
        TextView itemTextView = (TextView) listItemLayout.findViewById(R.id.app_name);    //程序名称
        final TextView itemPathView = (TextView) listItemLayout.findViewById(R.id.app_storage);
        ImageView appIconView = (ImageView) listItemLayout.findViewById(R.id.app_icon);
        appIconView.setImageDrawable(myApp.loadIcon(mPm));
        itemTextView.setText(myApp.loadLabel(mPm).toString());
        itemPathView.setText(sharedPreferences.getString(myApp.packageName, getContext().getString(R.string.unpoint)));

        listItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final View dialogLayout = view.inflate(getContext(), R.layout.app_item_dialog, null);
                final EditText popupEditText = (EditText) dialogLayout.findViewById(R.id.apppath);

                AlertDialog.Builder builder= new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.set_storage_path);
                if (Build.VERSION.SDK_INT > 21) {
                    builder.setView(dialogLayout); //设置自定义的view

                    //为弹出的edittext赋已存在的值
                    String currentAppCusPath = sharedPreferences.getString(myApp.packageName, "");
                    if (!TextUtils.isEmpty(currentAppCusPath)) {
                        popupEditText.setText(currentAppCusPath.substring(currentAppCusPath.lastIndexOf(File.separator) + 1, currentAppCusPath.length()));
                    }
                }

                builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //弹窗的输入框
                        EditText pathEditText = (EditText) dialogLayout.findViewById(R.id.apppath);
                        if (TextUtils.isEmpty(pathEditText.getText().toString().trim())) {
                            return;
                        }
                        //存储
                        StringBuilder storageRootPath = new StringBuilder(Environment.getExternalStorageDirectory().getAbsolutePath());
                        storageRootPath.delete(0, 1).append(File.separator).append(pathEditText.getText().toString().trim());
                        SharedPreferences.Editor edit = sharedPreferences.edit();
                        edit.putString(myApp.packageName, storageRootPath.toString());
                        edit.commit();

                        //显示到UI
//                        myApp.setStoragepath(storageRootPath.toString());
                        itemPathView.setText(storageRootPath.toString());

                        //重载配置
                        PreferencesUtils.reload();
                    }
                });

                //取消按钮
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                //重置按钮
                builder.setNeutralButton(R.string.reset_path, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //存储
                        SharedPreferences.Editor edit = sharedPreferences.edit();
                        edit.remove(myApp.packageName);
                        edit.commit();

                        //显示到UI
                        itemPathView.setText(getContext().getString(R.string.unpoint));

                        //重载配置
                        PreferencesUtils.reload();
                    }
                });
                builder.show();
                //延迟后弹出软键盘
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(popupEditText, InputMethodManager.SHOW_IMPLICIT);
                    }
                }, 100);
            }
        });
        return listItemLayout;
    }
}
