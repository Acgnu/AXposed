package org.acgnu.xposed.module;

import dalvik.system.DexFile;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
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



}