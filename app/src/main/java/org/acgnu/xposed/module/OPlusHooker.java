package org.acgnu.xposed.module;

import android.content.pm.PackageInfo;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import org.acgnu.tool.MyLog;
import org.acgnu.xposed.MyHooker;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class OPlusHooker implements MyHooker {
    private boolean doLog = false;
    private String curNewVersion = "7.0.0";

    @Override
    public String getTargetPackage() {
        return "com.excegroup.community.office";
    }

    @Override
    public void doHookInitZygote(IXposedHookZygoteInit.StartupParam startupParam) throws Throwable {

    }

    @Override
    public void doHookOnLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        MyLog.log("hook on O+", doLog);
//        findAndHookMethod("com.excegroup.community.passphrase.EncodeUtil", loadPackageParam.classLoader, "encodeKey",
//                String.class,
//                String.class,
//                String.class,
//                String.class,
//                new XC_MethodHook() {
//                    @Override
//                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                        MyLog.log(" com.excegroup.community.passphrase.EncodeUtil -> before", doLog);
//                        MyLog.log(String.format("参数:%s, %s, %s, %s", param.args[0], param.args[1], param.args[2], param.args[3]), doLog);
//                    }
//
//                    @Override
//                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                        MyLog.log(" com.excegroup.community.passphrase.EncodeUtil -> after", doLog);
//                        MyLog.log(String.format("返回:%s", param.getResult()), doLog);
//                    }
//                });
        //免更新
        findAndHookMethod("com.excegroup.community.home.HomeActivity", loadPackageParam.classLoader, "checkUpdate",
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        return null;
                    }
                });

        //免认证
        findAndHookMethod("com.excegroup.community.utils.CacheUtils", loadPackageParam.classLoader, "getStatus",
            String.class,
            new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    param.setResult("1");
                }
            });

        //免更新
        findAndHookMethod("android.app.ApplicationPackageManager", loadPackageParam.classLoader, "getPackageInfo",
                String.class,
                int.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        PackageInfo packageInfo =  (PackageInfo) param.getResult();
                        if (null != packageInfo && getTargetPackage().equals(packageInfo.packageName)) {
                            MyLog.log(packageInfo.packageName + "当前版本号" + packageInfo.versionCode + "版本名:" + packageInfo.versionName, doLog);
                            packageInfo.versionCode = Integer.MAX_VALUE;
                            packageInfo.versionName = curNewVersion;
                        }
                    }
                });
    }
}
