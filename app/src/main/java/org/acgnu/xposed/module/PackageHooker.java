package org.acgnu.xposed.module;

import android.util.Log;
import dalvik.system.DexFile;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import org.acgnu.tool.MyLog;
import org.acgnu.xposed.MyHooker;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;


public class PackageHooker implements MyHooker {
    private XC_LoadPackage.LoadPackageParam loadPackageParam;

    @Override
    public String getTargetPackage() {
        return "com.taobao.idlefish";
    }

    @Override
    public void doHookInitZygote(IXposedHookZygoteInit.StartupParam startupParam) throws Throwable {

    }

    @Override
    public void doHookOnLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (this.loadPackageParam == null) {
            this.loadPackageParam = loadPackageParam;
            try {
                hook();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void hook() throws IOException, ClassNotFoundException {
        DexFile dexFile = new DexFile(loadPackageParam.appInfo.sourceDir);
        Enumeration<String> classNames = dexFile.entries();
        while (classNames.hasMoreElements()) {
            String className = classNames.nextElement();
            if (!className.startsWith("MessageFor")) {
                continue;
            }
            if (isClassNameValid(className)) {
                final Class clazz = Class.forName(className, false, loadPackageParam.classLoader);
                for (Method method: clazz.getDeclaredMethods()) {
                    if (!Modifier.isAbstract(method.getModifiers())) {
                        XposedBridge.hookMethod(method, new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                String methodName = clazz.getName() + "\\" + param.method.getName();
                                MyLog.log("执行 -> " + methodName + ", 结果:" + MyLog.getObjectString(param.getResult()), true);
                            }

                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                String methodName = clazz.getName() + "\\" + param.method.getName();
                                int argIndex = 1;
                                for (Object arg : param.args) {
                                    MyLog.log("执行 -> " + methodName + ", 参数:" + argIndex++ + MyLog.getObjectString(arg), true);
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    public boolean isClassNameValid(String className) {
        return className.startsWith(loadPackageParam.packageName)
                && !className.contains("$")
                && !className.contains("BuildConfig")
                && !className.equals(loadPackageParam.packageName + ".R");
    }


//    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
//        final String targetPackage = "com.xiaojianbang";
//        String targetPackageFull = targetPackage + ".xposeddemo";
//        if (loadPackageParam.packageName.equals(targetPackageFull)) {
//            XposedHelpers.findAndHookMethod(ClassLoader.class, "loadClass", new Object[]{String.class, new XC_MethodHook() {
//                /* access modifiers changed from: protected */
//                public void afterHookedMethod(MethodHookParam param) throws Throwable {
//                    Class clazz = (Class) param.getResult();
//                    String clazzName = clazz.getName();
//                    StringBuilder sb = new StringBuilder();
//                    sb.append("LoadClass: ");
//                    sb.append(clazzName);
//                    Log.d("dajianbang", sb.toString());
//                    if (clazzName.contains(targetPackage)) {
//                        Method[] mds = clazz.getDeclaredMethods();
//                        for (int i = 0; i < mds.length; i++) {
//                            final Method md = mds[i];
//                            int mod = mds[i].getModifiers();
//                            if (!Modifier.isAbstract(mod) && !Modifier.isNative(mod) && !Modifier.isInterface(mod)) {
//                                XposedBridge.hookMethod(mds[i], new XC_MethodHook() {
//                                    /* access modifiers changed from: protected */
//                                    public void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                                        if (md.getName().contains("complexParameterFunc")) {
//                                            for (Object obj : param.args) {
//                                                Log.d("dajianbang", obj.getClass().getName());
//                                            }
//                                        }
//                                        StringBuilder sb = new StringBuilder();
//                                        sb.append("Hook Method: ");
//                                        sb.append(md.toString());
//                                        Log.d("dajianbang", sb.toString());
//                                    }
//                                });
//                            }
//                        }
//                    }
//                }
//            }});
//        }
//    }



}