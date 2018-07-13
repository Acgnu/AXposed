package org.acgnu.xposed;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import org.acgnu.service.MaskService;
import org.acgnu.tool.MyLog;
import org.acgnu.tool.PreferencesUtils;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

public class PVPHooker implements IXposedHookLoadPackage{
    private String PVP_PACKAGE = "com.tencent.tmgp.sgame";
    private Context mContext;
    private Intent mSvcIntent;

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (loadPackageParam.packageName.equals(PVP_PACKAGE)) {
            if (PreferencesUtils.isPVPOpen()) {
                final Class<?> crashNotifyHandler = findClass("com.tsf4g.apollo.report.CrashNotifyHandler", loadPackageParam.classLoader);
                findAndHookMethod(crashNotifyHandler, "Instance", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult(null);
                    }
                });
            }

            //获取到Context
            findAndHookMethod("com.tencent.tmgp.sgame.SGameActivity", loadPackageParam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (mContext == null) {
                        mContext = (Context) param.thisObject;
                    }
                }
            });

            //如果mask设置为开启，农药启动的时候自动开启mask
            findAndHookMethod("com.tencent.tmgp.sgame.SGameActivity", loadPackageParam.classLoader, "onResume", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (PreferencesUtils.pvpmask() && null != mContext) {
                        if (mSvcIntent == null) {
                            mSvcIntent = new Intent();
                            ComponentName cn = new ComponentName("org.acgnu.xposed", "org.acgnu.service.MaskService");
                            mSvcIntent.setComponent(cn);
                        }
                        mContext.startService(mSvcIntent);
                    }
                }
            });

            //在结束时中止掉service
            findAndHookMethod("com.tencent.tmgp.sgame.SGameActivity", loadPackageParam.classLoader, "onStop", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (null != mContext && null != mSvcIntent) {
                        mContext.stopService(mSvcIntent);
                    }
                }
            });
        }
    }
}
