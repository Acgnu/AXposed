package org.acgnu.xposed;

import android.text.TextUtils;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import org.acgnu.xposed.module.CloudMusicHooker;
import org.acgnu.xposed.module.IdelFishHooker;
import org.acgnu.xposed.module.OPlusHooker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class XposedHook implements IXposedHookLoadPackage, IXposedHookZygoteInit {
    private static List<MyHooker> myHookers = Arrays.asList(
            new IdelFishHooker(),
            new CloudMusicHooker(),
            new OPlusHooker()
    );

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
