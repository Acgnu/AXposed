package org.acgnu.xposed;

import android.text.TextUtils;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import org.acgnu.xposed.module.CloudMusicHooker;
import org.acgnu.xposed.module.IdelFishHooker;

import java.util.ArrayList;
import java.util.List;

public class XposedHook implements IXposedHookLoadPackage, IXposedHookZygoteInit {
    private static List<MyHooker> myHookers;

    static {
        myHookers = new ArrayList<>();
        myHookers.add(new IdelFishHooker());
        myHookers.add(new CloudMusicHooker());
//        myHookers.add(new PVPHooker());
//        myHookers.add(new QQHooker());
//        myHookers.add(new XSDHooker());
//        myHookers.add(new PackageHooker());
    }

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        for (MyHooker myHooker : myHookers) {
            if (TextUtils.isEmpty(myHooker.getTargetPackage())) {
                myHooker.doHookOnLoadPackage(loadPackageParam);
            } else if(loadPackageParam.packageName.equals(myHooker.getTargetPackage())){
                myHooker.doHookOnLoadPackage(loadPackageParam);
            }
        }
    }

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        for (MyHooker myHooker : myHookers) {
            myHooker.doHookInitZygote(startupParam);
        }
    }
}
