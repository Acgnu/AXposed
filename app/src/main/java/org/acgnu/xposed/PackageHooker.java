package org.acgnu.xposed;

import dalvik.system.DexFile;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;


/**
 * Created by mbpeele on 2/24/16.
 */
public class PackageHooker {

    private final XC_LoadPackage.LoadPackageParam loadPackageParam;

    public PackageHooker(XC_LoadPackage.LoadPackageParam param) {
        loadPackageParam = param;
        try {
            hook();
        } catch (Exception e) {
            e.printStackTrace();
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
                                log("HOOKED: " + clazz.getName() + "\\" + param.method.getName());
                            }
                        });
                    }
                }
            }
        }
    }


    public void log(Object str) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        XposedBridge.log("[" + df.format(new Date()) + "]:  " + str.toString());
    }

    public boolean isClassNameValid(String className) {
        return className.startsWith(loadPackageParam.packageName)
                && !className.contains("$")
                && !className.contains("BuildConfig")
                && !className.equals(loadPackageParam.packageName + ".R");
    }


    private void dumpClass(Class actions) {
        XposedBridge.log("Dump class " + actions.getName());

        XposedBridge.log("Methods");
        Method[] m = actions.getDeclaredMethods();
        for (int i = 0; i < m.length; i++) {
            XposedBridge.log(m[i].toString());
        }
        XposedBridge.log("Fields");
        Field[] f = actions.getDeclaredFields();
        for (int j = 0; j < f.length; j++) {
            XposedBridge.log(f[j].toString());
        }
        XposedBridge.log("Classes");
        Class[] c = actions.getDeclaredClasses();
        for (int k = 0; k < c.length; k++) {
            XposedBridge.log(c[k].toString());
        }
    }
}