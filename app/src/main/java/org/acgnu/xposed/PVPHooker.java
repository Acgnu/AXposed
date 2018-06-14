package org.acgnu.xposed;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import org.acgnu.tool.PreferencesUtils;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

public class PVPHooker implements IXposedHookLoadPackage{
    private String PVP_PACKAGE = "com.tencent.tmgp.sgame";
//    private Activity pvpActivity = null;
    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (loadPackageParam.packageName.equals(PVP_PACKAGE)) {
            if (PreferencesUtils.isPVPOpen()) {
                final Class<?> crashNotifyHandler = findClass("com.tsf4g.apollo.report.CrashNotifyHandler", loadPackageParam.classLoader);
                findAndHookMethod(crashNotifyHandler, "Instance", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        param.setResult(null);
                    }
                });
            }

            //如果mask是开启状态，农药启动的时候自动开启mask
//            findAndHookMethod("com.tencent.tmgp.sgame.SGameActivity", loadPackageParam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
//                @Override
//                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                    if (pvpActivity == null) {
//                        pvpActivity = (Activity) param.thisObject;
//                    }
//                    if (PreferencesUtils.pvpmask()) {
//                        Intent mask = new Intent(pvpActivity, "");
//                        pvpActivity.startService(mask);
//                    }
//                }
//            });
        }
    }
}
