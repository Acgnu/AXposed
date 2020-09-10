package org.acgnu.xposed.module;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import org.acgnu.tool.MyLog;
import org.acgnu.xposed.MyHooker;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class OPlusHooker implements MyHooker {
    private boolean doLog = false;

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
        findAndHookMethod("com.excegroup.community.home.HomeActivity", loadPackageParam.classLoader, "checkUpdate",
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        return null;
                    }
                });
    }
}
