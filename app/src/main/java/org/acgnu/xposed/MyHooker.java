package org.acgnu.xposed;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public interface MyHooker{
    String getTargetPackage();

    void doHookInitZygote(IXposedHookZygoteInit.StartupParam startupParam) throws Throwable;

    void doHookOnLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable;
}
