package org.acgnu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import org.acgnu.model.MyAppinfo;
import org.acgnu.xposed.R;

import java.util.List;

public class MyAppAdapter extends ArrayAdapter {
    private final int resourceId;

    public MyAppAdapter(Context context, int textViewResourceId, List<MyAppinfo> objects){
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyAppinfo myApp = (MyAppinfo) getItem(position); // 获取当前项的Fruit实例
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);//实例化一个对象
//        ImageView fruitImage = (ImageView) view.findViewById(R.id.fruit_image);//获取该布局内的图片视图
        TextView appItem = (TextView) view.findViewById(R.id.appitem);//获取该布局内的文本视图
//        fruitImage.setImageResource(fruit.getImageId());//为图片视图设置图片资源
        appItem.setText(myApp.getAppname());//为文本视图设置文本内容
        appItem.setHint(myApp.getStoragepath());
        return view;
    }
}
