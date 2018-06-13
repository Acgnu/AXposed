package org.acgnu.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import org.acgnu.model.MyAppinfo;
import org.acgnu.xposed.R;

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
            sharedPreferences = context.getSharedPreferences(context.getPackageName() + "_preferences",  Context.MODE_WORLD_READABLE);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final MyAppinfo myApp = (MyAppinfo) getItem(position); // 获取当前项的Appinfo实例
        View listItemLayout = LayoutInflater.from(getContext()).inflate(resourceId, null);  //实例化ListView的项目布局
        final TextView itemTextView = (TextView) listItemLayout.findViewById(R.id.appitem);    //获取该布局内的文本组件
//        ImageView fruitImage = (ImageView) view.findViewById(R.id.fruit_image);//获取该布局内的图片视图
//        fruitImage.setImageResource(fruit.getImageId());//为图片视图设置图片资源
        itemTextView.setText(myApp.getAppname());
        itemTextView.setHint(sharedPreferences.getString(myApp.getPkg(), getContext().getString(R.string.unpoint)));

        listItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final View dialogLayout = view.inflate(view.getContext(), R.layout.app_item_dialog, null);
                AlertDialog.Builder builder= new AlertDialog.Builder(view.getContext());
                builder.setTitle(R.string.set_storage_path);
                if (Build.VERSION.SDK_INT > 21) {
                    builder.setView(dialogLayout); //设置自定义的view
                }
                builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //弹窗的输入框
                        EditText pathEditText = (EditText) dialogLayout.findViewById(R.id.apppath);
                        //存储
                        SharedPreferences.Editor edit = sharedPreferences.edit();
                        edit.putString(myApp.getPkg(), pathEditText.getText().toString().trim());
                        edit.commit();
                    }
                });

                //添加一个取消按钮
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                builder.show();
            }
        });
        return listItemLayout;
    }
}
