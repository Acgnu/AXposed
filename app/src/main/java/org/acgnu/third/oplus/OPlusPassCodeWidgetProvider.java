package org.acgnu.third.oplus;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;
import org.acgnu.xposed.R;

public class OPlusPassCodeWidgetProvider extends AppWidgetProvider {
    private final String BOARDCAST_CLICK_ACTION = "oplus.passcode.widget.action.CLICK";
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
//        Toast.makeText(context, "doUpdate", Toast.LENGTH_SHORT);
//        super.onUpdate(context, appWidgetManager, appWidgetIds);
//        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.oplus_qr_layout);
//        Intent intent = new Intent(BOARDCAST_CLICK_ACTION);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, R.id.iv_qrcode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        remoteViews.setOnClickPendingIntent(R.id.iv_qrcode, pendingIntent);
//        for (int appWidgetId : appWidgetIds) {
//            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
//        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
//        if (BOARDCAST_CLICK_ACTION.equals(intent.getAction())) {
//            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.oplus_qr_layout);
//            remoteViews.setImageViewBitmap(R.id.iv_qrcode, OPlusPassCodeGenerator.instance().generate(context));
//            //获得appwidget管理实例，用于管理appwidget以便进行更新操作
//            AppWidgetManager manger = AppWidgetManager.getInstance(context);
//            // 相当于获得所有本程序创建的appwidget
//            ComponentName thisName = new ComponentName(context,OPlusPassCodeWidgetProvider.class);
//            //更新widget
//            manger.updateAppWidget(thisName,remoteViews);
//        }
    }
}
