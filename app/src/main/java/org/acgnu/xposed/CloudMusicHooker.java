package org.acgnu.xposed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class CloudMusicHooker{
    private static String PACKAGE = "com.netease.cloudmusic";
//    private Context mContext;
//    private boolean showFlag = false;
//    private Object strBeforeDec;

    public static void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (loadPackageParam.packageName.equals(PACKAGE)) {
//            MyLog.log("网易云 音乐 就绪");
//            Class<?> ContextClass = findClass("android.content.ContextWrapper", loadPackageParam.classLoader);
//            findAndHookMethod(ContextClass, "getApplicationContext", new XC_MethodHook() {
//                @Override
//                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                    mContext = (Context) param.getResult();
//                }
//            });


            //开始测试（此处HOOK云音乐获取内置存储地址的函数）
//            findAndHookMethod("com.netease.cloudmusic.utils.NeteaseMusicUtils", loadPackageParam.classLoader, "c", boolean.class, new XC_MethodHook() {
//                @Override
//                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                    showFlag = false;
////                    MyLog.log(JSON.toJSONString(param));
//                }
//
//                @Override
//                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                    showFlag = true;
//                    Class<?> a_auu_a = findClass("a.auu.a", loadPackageParam.classLoader);
//                    decLog(a_auu_a, "JAoqHB8f");    //adInfo
//                }
//            });

            //挂钩解密函数，打印其解密前和解密后的字符串
//            findAndHookMethod("a.auu.a", loadPackageParam.classLoader, "c", String.class,  new XC_MethodHook() {
//                @Override
//                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                    strBeforeDec = param.args[0];
//                }
//
//                @Override
//                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                    if (showFlag) {
//                        MyLog.log(strBeforeDec.toString(), param.getResult().toString());
//                    }
//                }
//            });

            //去除网易云音乐广告
            findAndHookMethod("com.netease.cloudmusic.fragment.ax", loadPackageParam.classLoader, "onCreateView",
                LayoutInflater.class,
                ViewGroup.class,
                Bundle.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Bundle args = (Bundle) callMethod(param.thisObject, "getArguments");
                        args.putSerializable("adInfo", null);
                    }
            });


            //适用于多dex情况下找不到对应类的时候使用，本人手机上没有出现找不到的情况
//            findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
//                @Override
//                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                    ClassLoader cl = ((Context)param.args[0]).getClassLoader();
//                    Class<?> hookclass = null;
//                    try {
//                        hookclass = cl.loadClass("com.netease.cloudmusic.fragment.ax");
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        MyLog.log("寻找com.netease.cloudmusic.fragment.ax报错");
//                        return;
//                    }
//                    MyLog.log("寻找com.netease.cloudmusic.fragment.ax成功");
//                    findAndHookMethod(hookclass, "onCreateView",
//                            LayoutInflater.class,
//                            ViewGroup.class,
//                            Bundle.class,
//                            new XC_MethodHook(){
//                                @Override
//                                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                                    Bundle args = (Bundle) callMethod(param.thisObject, "getArguments");
//                                    args.putSerializable("adInfo", null);
//                                }
//                            });
//                }
//            });
        }
    }

    //使用网易的解密模块解密字符串并打印
//    private void decLog(Class c, String arg) {
//        Class[] argCls = new Class[]{String.class};
//        Object result = callStaticMethod(c, "c", argCls, arg);
//        MyLog.log("解密字符串：" + arg + " --> " + result.toString());
//    }
}