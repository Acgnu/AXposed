package org.acgnu.xposed.module;

import android.content.Context;
import android.content.Intent;
import android.content.res.XModuleResources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import org.acgnu.tool.PreferencesUtils;
import org.acgnu.xposed.MyHooker;
import org.acgnu.xposed.R;

import java.lang.reflect.Field;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static org.acgnu.tool.XposedUtils.findFieldByClassAndTypeAndName;

public class QQHooker implements MyHooker {
    private String mRecentUserUin = null;
    private Context qqContext = null;
    public static XModuleResources sModRes;

    @Override
    public String getTargetPackage() {
        return "com.tencent.mobileqq";
    }

    @Override
    public void doHookInitZygote(IXposedHookZygoteInit.StartupParam startupParam) throws Throwable {
        sModRes = XModuleResources.createInstance(startupParam.modulePath, null);
    }

    @Override
    public void doHookOnLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        //得到QQ的Context
        findAndHookMethod("com.tencent.mobileqq.activity.SplashActivity", loadPackageParam.classLoader, "doOnCreate", Bundle.class, new
                XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (null == qqContext) {
                            qqContext = (Context) param.thisObject;
                        }
                    }
                }
        );

        //拦截所有QQ消息中的文本消息，包括QQ表情
//            findAndHookMethod("com.tencent.mobileqq.data.MessageForText", loadPackageParam.classLoader, "doParse", new XC_MethodHook() {
//                @Override
//                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                    if (PreferencesUtils.isOpen()) {
//                        //聊天窗口中目标QQ号
//                        String frienduin = (String)getObjectField(param.thisObject, "frienduin");
//
//                        //指定的要拦截的QQ号
//                        if (isMatchTargetQQ(frienduin)) {
//                            CharSequence replaceSb = RandomUtil.randomLink();
//                            setObjectField(param.thisObject, "sb", replaceSb);
//                        }
//                    }
//                }
//            });

        //hook最近会话的联系人
        findAndHookMethod("com.tencent.mobileqq.activity.recent.RecentEfficientItemBuilder", loadPackageParam.classLoader, "a",
                int.class,
                Object.class,
                "com.tencent.mobileqq.activity.recent.RecentFaceDecoder",
                View.class,
                ViewGroup.class,
                Context.class,
                View.OnClickListener.class,
                View.OnLongClickListener.class,
                "com.tencent.mobileqq.activity.recent.cur.DragFrameLayout.OnDragModeChangedListener",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        if (PreferencesUtils.isOpen()) {
                            //进入个人会话
                            if (param.args[1].getClass().getSimpleName().equals("RecentItemChatMsgData")) {
                                //获取封装了用户信息的成员变量
                                Field mRecentUserField = findFieldByClassAndTypeAndName(param.args[1].getClass(), findClass("com.tencent.mobileqq.data.RecentUser", loadPackageParam.classLoader), "a");
                                //获取用户信息对象
                                Object mRecentUser = mRecentUserField.get(param.args[1]);
                                if (mRecentUser != null) {
                                    //获取用户QQ号
                                    Field mUinField = findFieldByClassAndTypeAndName(mRecentUser.getClass(), String.class, "uin");
                                    if (PreferencesUtils.isMatchTargetQQ(mUinField.get(mRecentUser).toString())){
                                        mRecentUserUin = mUinField.get(mRecentUser).toString();
                                        //修改目标显示的最新消息内容
                                        Field lastmsg = findFieldByClassAndTypeAndName(param.args[1].getClass(), CharSequence.class, "b");
                                        CharSequence cs = "[应用] app-debug.apk";
                                        lastmsg.set(param.args[1], cs);

                                        //修改目标显示昵称
                                        Field nickname = findFieldByClassAndTypeAndName(param.args[1].getClass(), String.class, "b");
                                        String nk = "文件传输助手";
                                        nickname.set(param.args[1], nk);
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                        if (!TextUtils.isEmpty(mRecentUserUin)) {
                            View v = (View)param.getResult();
                            v.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //hook掉目标点击事件
                                    Toast.makeText(qqContext, "设备未连接", Toast.LENGTH_SHORT).show();
                                }
                            });
                            mRecentUserUin = null;
                        }
                    }
                });

        //hook掉最近聊天列表的头像
        findAndHookMethod("com.tencent.mobileqq.activity.recent.RecentEfficientItemBuilder", loadPackageParam.classLoader, "a",
                View.class,
                "com.tencent.mobileqq.activity.recent.RecentBaseData",
                Context.class,
                Drawable.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        if (!TextUtils.isEmpty(mRecentUserUin)){//&& null != qqContext) {
                            param.args[3] = sModRes.getDrawable(R.drawable.feb);//ContextCompat.getDrawable(qqContext, R.drawable.feb);
                        }
                    }
                });


        //拦截进入聊天会话界面
        findAndHookMethod("com.tencent.mobileqq.activity.BaseChatPie", loadPackageParam.classLoader, "k", Intent.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (PreferencesUtils.isOpen()) {
                    Intent intent = (Intent) param.args[0];
//                        System.out.println(JSON.toJSONString(intent));
                    String uin = intent.getStringExtra("uin");
                    if (PreferencesUtils.isMatchTargetQQ(uin)) {
                        param.args[0] = null;
                    }
                }
            }
        });
    }
}
