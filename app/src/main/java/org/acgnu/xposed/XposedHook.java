package org.acgnu.xposed;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XposedHook implements IXposedHookLoadPackage, IXposedHookZygoteInit {
    private XSDHooker xsdHooker = new XSDHooker();
    private PVPHooker pvpHooker = new PVPHooker();
    private QQHooker qqHooker = new QQHooker();


    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        qqHooker.handleLoadPackage(loadPackageParam);
        xsdHooker.handleLoadPackage(loadPackageParam);
        pvpHooker.handleLoadPackage(loadPackageParam);
    }

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        qqHooker.initZygote(startupParam);
        xsdHooker.initZygote(startupParam);
    }
}
