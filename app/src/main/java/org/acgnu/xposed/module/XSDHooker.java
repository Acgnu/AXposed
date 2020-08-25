package org.acgnu.xposed.module;

import android.os.Build;
import android.os.Environment;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodHook.MethodHookParam;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import org.acgnu.tool.PreferencesUtils;
import org.acgnu.tool.XposedUtils;
import org.acgnu.xposed.MyHooker;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class XSDHooker implements MyHooker {
    //    public XSharedPreferences prefs;
    public String internalSd;
    private String CURRENT_PKG = "";
    public XC_MethodHook getExternalStorageDirectoryHook;
    public XC_MethodHook getExternalFilesDirHook;
    public XC_MethodHook getObbDirHook;
    public XC_MethodHook getExternalStoragePublicDirectoryHook;
    public XC_MethodHook getExternalFilesDirsHook;
    public XC_MethodHook getObbDirsHook;
    //    public XC_MethodHook externalSdCardAccessHook; // 4.4 - 5.0
    public XC_MethodHook externalSdCardAccessHook2; // 6.0 and up

    boolean detectedSdPath = false;

    @Override
    public String getTargetPackage() {
        return null;
    }

    @Override
    public void doHookInitZygote(IXposedHookZygoteInit.StartupParam startupParam) throws Throwable {
//        prefs = new XSharedPreferences(XSDHooker.class.getPackage().getName());
//        prefs.makeWorldReadable();
        getExternalStorageDirectoryHook = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                changeDirPath(param);
            }
        };

        getExternalFilesDirHook = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                changeDirPath(param);
            }
        };

        getObbDirHook = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                changeDirPath(param);
            }
        };

        getExternalStoragePublicDirectoryHook = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                changeDirPath(param);
            }
        };

        getExternalFilesDirsHook = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                changeDirsPath(param);
            }
        };

        getObbDirsHook = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                changeDirsPath(param);
            }
        };

//        externalSdCardAccessHook = new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param)
//                    throws Throwable {
//                prefs.reload();
//                String permission = (String) param.args[1];
//                boolean externalSdCardFullAccess = prefs.getBoolean(
//                        "external_sdcard_full_access", true);
//                if (!externalSdCardFullAccess) {
//                    return;
//                }
//                if (XposedUtils.PERM_WRITE_EXTERNAL_STORAGE
//                        .equals(permission)
//                        || XposedUtils.PERM_ACCESS_ALL_EXTERNAL_STORAGE
//                        .equals(permission)) {
//                    Class<?> process = XposedHelpers.findClass(
//                            "android.os.Process", null);
//                    int gid = (Integer) XposedHelpers.callStaticMethod(process,
//                            "getGidForName", "media_rw");
//                    Object permissions = null;
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        permissions = XposedHelpers.getObjectField(
//                                param.thisObject, "mPermissions");
//                    } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
//                        Object settings = XposedHelpers.getObjectField(
//                                param.thisObject, "mSettings");
//                        permissions = XposedHelpers.getObjectField(settings,
//                                "mPermissions");
//                    }
//                    Object bp = XposedHelpers.callMethod(permissions, "get",
//                            permission);
//                    int[] bpGids = (int[]) XposedHelpers.getObjectField(bp,
//                            "gids");
//                    XposedHelpers.setObjectField(bp, "gids",
//                            appendInt(bpGids, gid));
//                }
//            }
//        };


        externalSdCardAccessHook2 = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                prefs.reload();
//                boolean externalSdCardFullAccess = prefs.getBoolean("external_sdcard_full_access", true);
//                if (!externalSdCardFullAccess) {
//                    return;
//                }
                Object extras = XposedHelpers.getObjectField(param.args[0], "mExtras");
                Object ps = XposedHelpers.callMethod(extras, "getPermissionsState");
                Object settings = XposedHelpers.getObjectField(param.thisObject, "mSettings");
                Object permissions = XposedHelpers.getObjectField(settings, "mPermissions");
                boolean hasPermission = (boolean) XposedHelpers.callMethod(ps, "hasInstallPermission", XposedUtils.PERM_WRITE_MEDIA_STORAGE);
                if (!hasPermission) {
                    Object permWriteMediaStorage = XposedHelpers.callMethod(permissions, "get", XposedUtils.PERM_WRITE_MEDIA_STORAGE);
                    XposedHelpers.callMethod(ps, "grantInstallPermission", permWriteMediaStorage);
                }
            }
        };
    }

    @Override
    public void doHookOnLoadPackage(final LoadPackageParam loadPackageParam) throws Throwable {
        if ("android".equals(loadPackageParam.packageName) && "android".equals(loadPackageParam.processName)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                XposedHelpers.findAndHookMethod(XposedHelpers.findClass("com.android.server.pm.PackageManagerService",loadPackageParam.classLoader),
                        "grantPermissionsLPw",
                        XposedUtils.CLASS_PACKAGE_PARSER_PACKAGE, boolean.class, String.class,
                        externalSdCardAccessHook2);
            }
//            else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP || Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP_MR1) {
//                XposedHelpers.findAndHookMethod(XposedHelpers.findClass("com.android.server.SystemConfig", lpparam.classLoader),
//                        "readPermission",
//                        XmlPullParser.class, String.class,
//                        externalSdCardAccessHook);
//            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
//                XposedHelpers.findAndHookMethod(XposedHelpers.findClass("com.android.server.pm.PackageManagerService",lpparam.classLoader),
//                        "readPermission",
//                        XmlPullParser.class, String.class,
//                        externalSdCardAccessHook);
//            }
        }

        if (!detectedSdPath) {
            try {
                //解决 Path requests must specify a user by using UserEnvironment 报错
                Class<?> environmentcls = Class.forName("android.os.Environment");
                Method setUserRequiredM = environmentcls.getMethod("setUserRequired", boolean.class);
                setUserRequiredM.invoke(null, false);

                File internalSdPath = Environment.getExternalStorageDirectory();
                internalSd = internalSdPath.getPath();
                detectedSdPath = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!isEnabledApp(loadPackageParam)) {
            return;
        }

        CURRENT_PKG = loadPackageParam.packageName;

        XposedHelpers.findAndHookMethod(Environment.class, "getExternalStorageDirectory", getExternalStorageDirectoryHook);
        XposedHelpers.findAndHookMethod(XposedHelpers.findClass("android.app.ContextImpl", loadPackageParam.classLoader), "getExternalFilesDir", String.class, getExternalFilesDirHook);
        XposedHelpers.findAndHookMethod(XposedHelpers.findClass("android.app.ContextImpl", loadPackageParam.classLoader), "getObbDir", getObbDirHook);
        XposedHelpers.findAndHookMethod(Environment.class, "getExternalStoragePublicDirectory", String.class, getExternalStoragePublicDirectoryHook);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            XposedHelpers.findAndHookMethod(XposedHelpers.findClass("android.app.ContextImpl", loadPackageParam.classLoader), "getExternalFilesDirs", String.class, getExternalFilesDirsHook);
            XposedHelpers.findAndHookMethod(XposedHelpers.findClass("android.app.ContextImpl", loadPackageParam.classLoader), "getObbDirs", getObbDirsHook);
        }
    }

    public boolean isEnabledApp(LoadPackageParam lpparam) {
        boolean isEnabledApp = true;
//        prefs.reload();
        boolean enabledModule = PreferencesUtils.isStorageOpen();
        if (!enabledModule) {
            return false;
        }
        if (!PreferencesUtils.hasCustomerPath(lpparam.packageName)) {
            return false;
        }
//        String packageName = lpparam.packageName;
//        boolean enabledForAllApps = prefs.getBoolean("enable_for_all_apps", false);
//        if (enabledForAllApps) {
//            Set<String> disabledApps = prefs.getStringSet("disable_for_apps", new HashSet<String>());
//            if (!disabledApps.isEmpty()) {
//                isEnabledApp = !disabledApps.contains(packageName);
//            }
//        } else {
//            Set<String> enabledApps = prefs.getStringSet("enable_for_apps", new HashSet<String>());
//            if (!enabledApps.isEmpty()) {
//                isEnabledApp = enabledApps.contains(packageName);
//            } else {
//                isEnabledApp = !isEnabledApp;
//            }
//        }
        return isEnabledApp;
    }

    public void changeDirPath(MethodHookParam param) {
        File oldDirPath = (File) param.getResult();
        if (oldDirPath == null) {
            return;
        }
        String customInternalSd = getCustomInternalSd();
        if (customInternalSd.isEmpty()) {
            return;
        }
        String internalSd = getInternalSd();
        if (internalSd.isEmpty()) {
            return;
        }

//        String dir = XposedUtils.appendFileSeparator(oldDirPath.getPath());
        String dir = XposedUtils.appendFileSeparator(customInternalSd);
//        String newDir = dir.replaceFirst(internalSd, customInternalSd);
//        File newDirPath = new File(newDir);
        File newDirPath = new File(dir);
        if (!newDirPath.exists()) {
            newDirPath.mkdirs();
        }
        param.setResult(newDirPath);
    }

    public void changeDirsPath(MethodHookParam param) {
//        File[] oldDirPaths = (File[]) param.getResult();
        ArrayList<File> newDirPaths = new ArrayList<File>();
//        for (File oldDirPath : oldDirPaths) {
//            if (oldDirPath != null) {
//                newDirPaths.add(oldDirPath);
//            }
//        }

        String customInternalSd = getCustomInternalSd();
        if (customInternalSd.isEmpty()) {
            return;
        }

        String internalSd = getInternalSd();
        if (internalSd.isEmpty()) {
            return;
        }

//        String dir = XposedUtils.appendFileSeparator(oldDirPaths[0].getPath());
//        String newDir = dir.replaceFirst(internalSd, customInternalSd);
//        File newDirPath = new File(newDir);

        String dir = XposedUtils.appendFileSeparator(customInternalSd);
        File newDirPath = new File(dir);

        if (!newDirPaths.contains(newDirPath)) {
            newDirPaths.add(newDirPath);
        }
        if (!newDirPath.exists()) {
            newDirPath.mkdirs();
        }

        File[] appendedDirPaths = newDirPaths.toArray(new File[newDirPaths.size()]);
        param.setResult(appendedDirPaths);
    }

    public String getCustomInternalSd() {
//        prefs.reload();
        String customInternalSd = PreferencesUtils.getCustomerPath(CURRENT_PKG, getInternalSd());
        customInternalSd = XposedUtils.appendFileSeparator(customInternalSd);
        return customInternalSd;
    }

    public String getInternalSd() {
        internalSd = XposedUtils.appendFileSeparator(internalSd);
        return internalSd;
    }

//    public boolean isAllowedApp(ApplicationInfo appInfo) {
//        prefs.reload();
//        boolean includeSystemApps = prefs.getBoolean("include_system_apps",
//                false);
//        if (appInfo == null) {
//            return includeSystemApps;
//        } else {
//            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0
//                    && !includeSystemApps) {
//                return false;
//            }
//            if (Arrays.asList(XposedUtils.MTP_APPS).contains(appInfo.packageName)) {
//                return false;
//            }
//        }
//        return true;
//    }

//    public int[] appendInt(int[] cur, int val) {
//        if (cur == null) {
//            return new int[]{val};
//        }
//        final int N = cur.length;
//        for (int i = 0; i < N; i++) {
//            if (cur[i] == val) {
//                return cur;
//            }
//        }
//        int[] ret = new int[N + 1];
//        System.arraycopy(cur, 0, ret, 0, N);
//        ret[N] = val;
//        return ret;
//    }
}
