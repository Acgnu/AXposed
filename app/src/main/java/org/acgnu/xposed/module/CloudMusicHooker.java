package org.acgnu.xposed.module;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import org.acgnu.tool.MyLog;
import org.acgnu.tool.XposedUtils;
import org.acgnu.xposed.MyHooker;

import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookConstructor;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

public class CloudMusicHooker implements MyHooker {
    //    private Context mContext;
    private Object strBeforeDec;
    private boolean doLog = false;

    @Override
    public String getTargetPackage() {
        return "com.netease.cloudmusic";
    }

    @Override
    public void doHookInitZygote(IXposedHookZygoteInit.StartupParam startupParam) throws Throwable {

    }

    @Override
    public void doHookOnLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
            MyLog.log("hook on网易云音乐", doLog);
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
////                    MyLog.log(JSON.toJSONString(param));
//                }
//
//                @Override
//                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
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
//                    MyLog.log(strBeforeDec.toString(), param.getResult().toString(), doLog);
//                }
//            });


        findAndHookMethod("com.netease.cloudmusic.module.ad.a", loadPackageParam.classLoader, "a",
                int.class,
                String.class,
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                        MyLog.log("执行了替换的广告请求static方法", doLog);
                        return null;
                    }
                }
        );

        //去广告
        findAndHookMethod("androidx.fragment.app.Fragment", loadPackageParam.classLoader, "setArguments",
                Bundle.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Bundle arg = (Bundle) param.args[0];
                        if (arg == null || !arg.containsKey("adInfo")) {
                            return;
                        }
                        arg.putSerializable("adInfo", null);
                        XposedUtils.showCallStack();
                    }
                });

        //去除首页更新弹窗 (替换检查代码)
        findAndHookMethod("com.netease.cloudmusic.appupdate.a", loadPackageParam.classLoader, "a",
                boolean.class,
                new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    MyLog.log("执行了替换的首页网易云更新检查", doLog);
                    return null;
                }
        });

        //免更新(versioncode)
//        findAndHookMethod("android.app.ApplicationPackageManager", loadPackageParam.classLoader, "getPackageInfo",
//                String.class,
//                Integer.class,
//                new XC_MethodHook() {
//                    @Override
//                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                        PackageInfo packageInfo =  (PackageInfo) param.getResult();
//                        if(getTargetPackage().equals(packageInfo.packageName)){
//                            MyLog.log(packageInfo.packageName + "当前版本号" + packageInfo.versionCode + "版本名:" + packageInfo.versionName, doLog);
//                            packageInfo.versionCode = Integer.MAX_VALUE;
//                        }
//                    }
//                });

        //去除播放列表头部广告
        findAndHookMethod("com.netease.cloudmusic.fragment.PlayListFragment", loadPackageParam.classLoader, "cl", new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                MyLog.log("执行了替换的播放列表顶部广告加载", doLog);
                return null;
            }
        });

        //屏蔽首页banner
//        findAndHookMethod("com.netease.cloudmusic.ui.BannerViewHelper", loadPackageParam.classLoader, "initBanner",
//                boolean.class,
//                Interpolator.class,
//                new XC_MethodHook() {
//                    @Override
//                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                        FrameLayout bannerView = (FrameLayout) getObjectField(param.thisObject, "mMainBannerContainer");
//                        bannerView.setVisibility(View.GONE);
//                    }
//            });

        //屏蔽除了"我的"以外的tab
        findAndHookMethod("com.netease.cloudmusic.activity.r", loadPackageParam.classLoader, "b",
                String[].class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        param.args[0] = new String[]{"我的"};
                    }
                });

        //屏蔽"我的"vip信息
        findAndHookConstructor("com.netease.cloudmusic.module.mymusic.headerentry.MyVipInfoViewHolder", loadPackageParam.classLoader,
                View.class,
                "com.netease.cloudmusic.module.mymusic.k",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        View view = (View) param.args[0];
                        view.setVisibility(View.GONE);
                    }
                });

        //调整屏蔽元素后的布局
        findAndHookConstructor("com.netease.cloudmusic.module.mymusic.headerentry.HeaderEntryListViewHolder", loadPackageParam.classLoader,
                View.class,
                "com.netease.cloudmusic.module.mymusic.k",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        LinearLayout view = (LinearLayout) getObjectField(param.thisObject, "e");
                        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
                        MyLog.log("layoutparam:", layoutParams, doLog);
                        layoutParams.bottomMargin = 50;
                        layoutParams.gravity = Gravity.BOTTOM;
                    }
                });

        //屏蔽小程序标题
        findAndHookConstructor("com.netease.cloudmusic.module.mymusic.SimpleSectionViewHolder", loadPackageParam.classLoader,
                View.class,
                "com.netease.cloudmusic.module.mymusic.k",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        RelativeLayout layout = (RelativeLayout) param.args[0];
                        if (layout.getId() == 2131299470) {
                            layout.setVisibility(View.GONE);
                        }
                    }
                });

        //屏蔽播放列表开头的小程序
        findAndHookConstructor("com.netease.cloudmusic.module.mymusic.miniapp.linear.MiniAppListLinearViewHolder", loadPackageParam.classLoader,
                View.class,
                "com.netease.cloudmusic.module.mymusic.k",
                int.class,
                int.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        View miniAppListView = (View) param.args[0];
                        ViewGroup.LayoutParams layoutParams = miniAppListView.getLayoutParams();
                        layoutParams.height = 0;
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

    //使用网易的解密模块解密字符串并打印
    private void decLog(Class c, String arg) {
        Class[] argCls = new Class[]{String.class};
        Object result = callStaticMethod(c, "c", argCls, arg);
        MyLog.log("解密字符串：" + arg + " --> " + result.toString(), doLog);
    }
}