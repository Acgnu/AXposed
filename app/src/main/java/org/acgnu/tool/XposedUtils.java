package org.acgnu.tool;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static de.robv.android.xposed.XposedHelpers.*;


public class XposedUtils {
    public static final String CLASS_PACKAGE_PARSER_PACKAGE = "android.content.pm.PackageParser.Package";
    public static final String[] MTP_APPS = {"com.android.MtpApplication", "com.samsung.android.MtpApplication"};
    public static final String PERM_WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";
    public static final String PERM_ACCESS_ALL_EXTERNAL_STORAGE = "android.permission.ACCESS_ALL_EXTERNAL_STORAGE";
    public static final String PERM_WRITE_MEDIA_STORAGE = "android.permission.WRITE_MEDIA_STORAGE";

    public static Object findResultByMethodNameAndReturnTypeAndParams(Object targetObject, String methodName, String returnType, Object... params) throws InvocationTargetException, IllegalAccessException {
        return findMethodByNameAndReturnType(targetObject.getClass(), methodName, returnType, getParameterTypes(params)).invoke(targetObject, params);
    }

    public static Method findMethodByNameAndReturnType(Class<?> targetObject, String methodName, String returnType, Class<?>... params) {
        for (Method method : targetObject.getDeclaredMethods()) {
            if (method.getReturnType().getName().equals(returnType) && method.getName().equals(methodName)) {
                Class[] parameterTypes = method.getParameterTypes();
                if (params.length != parameterTypes.length) {
                    continue;
                }
                for (int i = 0; i < params.length; i++) {
                    if (params[i] != parameterTypes[i]) {
                        break;
                    }
                }
                method.setAccessible(true);
                return method;
            }
        }
        throw new NoSuchMethodError();
    }

    public static Field findFieldByClassAndTypeAndName(Class<?> targetObject, Class<?> fieldType, String fieldName) {
        Class clazz = targetObject;
        do {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.getType() == fieldType && field.getName().equals(fieldName)) {
                    field.setAccessible(true);
                    return field;
                }
            }
            clazz = clazz.getSuperclass();
        } while (clazz != null);
        throw new NoSuchFieldError("Field of type " + fieldType.getName() + " in class " + targetObject.getName());
    }

    public void showFields(Field[] fields, XC_MethodHook.MethodHookParam param){
        for (Field field : fields) {
            try {
                String fieldType = field.getType().getSimpleName();
                MyLog.log(fieldType + ":" + field.getName(), true);
                Object filedV = findFieldIfExists(param.thisObject.getClass(), field.getName());
                if (null != filedV) {
                    Object objV = getObjectField(param.thisObject, field.getName());
                    if (null != objV) {
                        if ("String".equals(fieldType)) {
                            String stringV = (String)objV;
                            MyLog.log("当前field:" + field.getName() + ", value:" + stringV, true);
                        }else if ("CharSequence".equals(fieldType)){
                            CharSequence charSequence = (CharSequence) objV;
                            MyLog.log("当前field:" + field.getName() + ", value:" + charSequence.toString(), true);
                        }else if ("long".equals(fieldType)){
                            long longv = (long) objV;
                            MyLog.log("当前field:" + field.getName() + ", value:" + longv, true);
                        }else{
                            MyLog.log("当前field:" + field.getName() + ", value:" + objV.toString(), true);
                        }
                    }else{
//                        Log.log("当前field:" + field.getName() + ", value:null");
                    }
                }
            } catch (Exception e){
                MyLog.log(e.getMessage(), true);
            }
        }
    }

    public static String appendFileSeparator(String path) {
        if (!path.endsWith(File.separator)) {
            path += File.separator;
        }
        return path;
    }

    public static void showCallStack(){
        Throwable ex = new Throwable();
        StackTraceElement[] stackElements = ex.getStackTrace();
        if (stackElements != null) {
            MyLog.log("调用堆栈:--------------------", true);
            for (int i = 0; i < stackElements.length; i++) {
                MyLog.log(stackElements[i].getClassName() + " -> " +
                        stackElements[i].getMethodName() + "()"
                , true);
            }
        }
    }

    public static void dumpClass(Class actions) {
        MyLog.log("Dump class " + actions.getName(), true);

        MyLog.log("Methods", true);
        Method[] m = actions.getDeclaredMethods();
        for (int i = 0; i < m.length; i++) {
            MyLog.log(m[i].toString(), true);
        }
        MyLog.log("Fields", true);
        Field[] f = actions.getDeclaredFields();
        for (int j = 0; j < f.length; j++) {
            MyLog.log(f[j].toString(), true);
        }
        MyLog.log("Classes", true);
        Class[] c = actions.getDeclaredClasses();
        for (int k = 0; k < c.length; k++) {
            MyLog.log(c[k].toString(), true);
        }
    }
}
