package org.acgnu.xposed.module;

import android.os.Build;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import org.acgnu.tool.MyLog;
import org.acgnu.xposed.MyHooker;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class IdelFishHooker implements MyHooker {
    private boolean doLog = true;

    @Override
    public String getTargetPackage() {
        return "com.taobao.idlefish";
    }

    @Override
    public void doHookInitZygote(IXposedHookZygoteInit.StartupParam startupParam) throws Throwable {

    }

    @Override
    public void doHookOnLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        MyLog.log("hook on 闲鱼", doLog);
//        findAndHookMethod("com.idlefish.flutterbridge.flutterboost.IdleFishFlutterActivity", loadPackageParam.classLoader, "getContainerUrlParams",
//                new XC_MethodHook() {
//                    @Override
//                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                        MyLog.log("IdleFishFlutterActivity.getContainerUrlParams:", param.getResult());
//                        XposedUtils.showCallStack();
//                    }
//                });

        //过滤搜索结果页的广告和推荐
        findAndHookMethod("mtopsdk.mtop.domain.MtopResponse", loadPackageParam.classLoader, "setBytedata",
                byte[].class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        MyLog.log("MtopResponse.setBytedata", doLog);
                        if(null == param.args[0] || ((byte[]) param.args[0]).length == 0){
                            MyLog.log("设置bytedate参数不符合", doLog);
                            return;
                        }
                        String content = new String((byte[]) param.args[0]);
                        JSONObject jsonContent = new JSONObject(content);
                        JSONObject jsonData = jsonContent.optJSONObject("data");
                        if (null == jsonData || !jsonData.has("resultList")) {
                            MyLog.log("设置bytedate参数不包含resultlist", doLog);
                            return;
                        }
                        JSONArray resultList = jsonData.getJSONArray("resultList");
                        List<String> filterList = Arrays.asList("ad", "Bagtag", "聚合卡片");
                        MyLog.log("==========设置bytedate参数集=============", doLog);
                        for (int i = 0; i < resultList.length(); i++) {
                            JSONObject listItem = resultList.getJSONObject(i);
                            MyLog.log(listItem.toString(), doLog);
                            String bizType = listItem.getJSONObject("data").getJSONObject("item").getJSONObject("main").getJSONObject("clickParam").getJSONObject("args").getString("biz_type");
                            if (filterList.contains(bizType)) {
                                MyLog.log("匹配到广告/细选等:" + listItem.toString(), doLog);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    MyLog.log("广告已移除:" + listItem.toString(), doLog);
                                    resultList.remove(i);
                                    i--;
                                }
                            }
                        }
                        param.args[0] = jsonContent.toString().getBytes();
                    }
                });

        //去除更新检查
        findAndHookMethod("com.taobao.idlefish.upgrade.traceable.Upgrade", loadPackageParam.classLoader, "b",
                "com.taobao.idlefish.upgrade.traceable.UpgradeHandler",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        param.args[0] = null;
                    }
                }
        );

//        findAndHookMethod("com.taobao.idlefish.dynamicso.interrupter.impl.SearchResultInterrupter", loadPackageParam.classLoader, "checkInterrupt",
//                Context.class,
//                String.class,
//                "com.taobao.idlefish.protocol.nav.IRouteRequest",
//                new XC_MethodHook() {
//                    @Override
//                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                        MyLog.log("SearchResultInterrupter.checkInterrupt1:", param.args[1]);
//                        MyLog.log("SearchResultInterrupter.checkInterrupt2:", param.args[2]);
//                        XposedUtils.showCallStack();
//                    }
//                });
    }
}
