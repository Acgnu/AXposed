package org.acgnu.xposed;

import android.content.Context;
import android.content.Intent;
import android.content.res.XModuleResources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import org.acgnu.tool.MyLog;
import org.acgnu.tool.PreferencesUtils;

import java.lang.reflect.Field;

import static de.robv.android.xposed.XposedHelpers.*;
import static org.acgnu.tool.XposedUtils.findFieldByClassAndTypeAndName;

public class CloudMusicHooker implements IXposedHookLoadPackage {
    private String PACKAGE = "com.netease.cloudmusic";
    private Context mContext;
    private boolean showFlag = false;

//    String a = "{\"com.netease.cloudmusic\":\"/storage/emulated/0/test\"}";

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (loadPackageParam.packageName.equals(PACKAGE)) {
            MyLog.log("网易云 音乐 就绪");
            Class<?> ContextClass = findClass("android.content.ContextWrapper", loadPackageParam.classLoader);
            findAndHookMethod(ContextClass, "getApplicationContext", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    mContext = (Context) param.getResult();
                }
            });


            //开始测试
            findAndHookMethod("com.netease.cloudmusic.utils.NeteaseMusicUtils", loadPackageParam.classLoader, "c", boolean.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    showFlag = false;
                    super.afterHookedMethod(param);
//                    MyLog.log(JSON.toJSONString(param));
                }

                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    showFlag = true;
                    Class<?> a_auu_a = findClass("a.auu.a", loadPackageParam.classLoader);
                    decLog(a_auu_a, "KAEWHA0VEA==");
                    decLog(a_auu_a, "a0RDWg8WFTESDQYfAwggFgUTDQwSJBpQQAUWATYLHxQMAxEnAggODRUMIw8XDgoUFyQcBxQKDBE2CgUBBVgSMB0GLlcsJ25HSlJXWgYyQEk=");
                    decLog(a_auu_a, "KAEWHA0=");
                    decLog(a_auu_a, "JB0GEQ==");
                    decLog(a_auu_a, "KgwB");
                    decLog(a_auu_a, "NgsABwsV");
                    decLog(a_auu_a, "ZQ==");
                    decLog(a_auu_a, "ag==");
                    decLog(a_auu_a, "ai8NFgsfHSFBBxMNEVs=");
                    decLog(a_auu_a, "aggKHhwDWwEBAAcUFRoxHQ==");
                    decLog(a_auu_a, "ai8NFgsfHSFB");
                    decLog(a_auu_a, "IgsXNwEEETcAAh40HwErGhBDSko=");
                    decLog(a_auu_a, "IgsXNwEEETcAAh40HwErGhBDTUo=");
                    decLog(a_auu_a, "IgsXNwEEETcAAh40HwErGhBDTEo=");
                    decLog(a_auu_a, "IgsXNwEEETcAAh40HwErGhBDT0o=");
                    super.beforeHookedMethod(param);
                }
            });

            findAndHookMethod("com.netease.cloudmusic.log.a", loadPackageParam.classLoader, "a", String.class, String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (showFlag) {
//                        Object arg0 = param.args[0];  //调取日志的包名
                        Object arg1 = param.args[1];           //日志内容
//                        MyLog.log(arg0.toString());
                        MyLog.log(arg1.toString());
                    }
                    super.beforeHookedMethod(param);
                }
            });
        }
    }

    private void decLog(Class c, String arg) {
        Class[] argCls = new Class[]{String.class};
        Object result = callStaticMethod(c, "c", argCls, arg);
        MyLog.log("解密字符串：" + arg + " --> " + result.toString());
    }
}