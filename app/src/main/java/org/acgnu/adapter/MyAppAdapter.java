package org.acgnu.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import org.acgnu.model.MyAppinfo;
import org.acgnu.tool.PreferencesUtils;
import org.acgnu.xposed.R;

import java.io.File;
import java.util.List;

/**
 * 显示所有已安装应用的Listview Adapter
 */
public class MyAppAdapter extends ArrayAdapter {
    private final int resourceId;
    private SharedPreferences sharedPreferences;

    public MyAppAdapter(Context context, int textViewResourceId, List<MyAppinfo> objects){
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        if (null == sharedPreferences) {
            sharedPreferences = context.getSharedPreferences(PreferencesUtils.getPrefName(getContext()),  Context.MODE_WORLD_READABLE);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final MyAppinfo myApp = (MyAppinfo) getItem(position); // 获取当前项的Appinfo实例
        View listItemLayout = LayoutInflater.from(getContext()).inflate(resourceId, null);  //实例化ListView的项目布局
        final TextView itemTextView = (TextView) listItemLayout.findViewById(R.id.appitem);    //程序名称
        final TextView itemPathView = (TextView) listItemLayout.findViewById(R.id.app_hint);
        ImageView appIconView = (ImageView) listItemLayout.findViewById(R.id.app_icon);
        appIconView.setImageDrawable(myApp.getIcon());
        itemTextView.setText(myApp.getAppname());
        itemPathView.setText(myApp.getStoragepath());

        listItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final View dialogLayout = view.inflate(getContext(), R.layout.app_item_dialog, null);
                EditText popupEditText = (EditText) dialogLayout.findViewById(R.id.apppath);
                AlertDialog.Builder builder= new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.set_storage_path);
                if (Build.VERSION.SDK_INT > 21) {
                    builder.setView(dialogLayout); //设置自定义的view

                    //为弹出的edittext赋已存在的值
                    String currentAppCusPath = sharedPreferences.getString(myApp.getPkg(), "");
                    if (!TextUtils.isEmpty(currentAppCusPath)) {
                        popupEditText.setText(currentAppCusPath.substring(currentAppCusPath.lastIndexOf("/") + 1, currentAppCusPath.length()));
//                        popupEditText.setFocusable(true);
//                        popupEditText.setFocusableInTouchMode(true);
//                        ((Activity)getContext()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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
                        edit.putString(myApp.getPkg(), storageRootPath.toString());
                        edit.commit();

                        //显示到UI
                        myApp.setStoragepath(storageRootPath.toString());

                        //重载配置
                        PreferencesUtils.reload();
                    }
                });

                //添加一个取消按钮
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                builder.setNeutralButton(R.string.reset_path, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //存储
                        SharedPreferences.Editor edit = sharedPreferences.edit();
                        edit.remove(myApp.getPkg());
                        edit.commit();

                        //显示到UI
                        itemPathView.setText(getContext().getString(R.string.unpoint));

                        //重载配置
                        PreferencesUtils.reload();
                    }
                });

                builder.show();
            }
        });
        return listItemLayout;
    }
}
