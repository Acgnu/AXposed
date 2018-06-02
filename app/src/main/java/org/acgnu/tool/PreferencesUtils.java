package org.acgnu.tool;


import android.text.TextUtils;
import de.robv.android.xposed.XSharedPreferences;
import org.acgnu.xposed.PackageHooker;

public class PreferencesUtils {
    private static XSharedPreferences instance = null;
    private static String[] targetQQs = new String[0];

    private static XSharedPreferences getInstance() {
        if (instance == null) {
            instance = new XSharedPreferences(PackageHooker.class.getPackage().getName());
            instance.makeWorldReadable();
            String targetQQStr = targetQQ();
            if (!TextUtils.isEmpty(targetQQStr)) {
                targetQQs = targetQQStr.split(",");
            }
        } else {
            instance.reload();
        }
        return instance;
    }

    public static boolean isOpen() {
        return getInstance().getBoolean("open", true);
    }

    public static boolean isPVPOpen() {
        return getInstance().getBoolean("pvpopen", true);
    }

    public static boolean pvpmask() {
        return getInstance().getBoolean("pvpmask", true);
    }

    public static String targetQQ() {
        return getInstance().getString("target", "");
    }

    public static void destroyInstance(){
        instance = null;
    }

    /**
     * 返回qq号是否在目标中
     * @param toCompare 待查询的qq号
     * @return true 是目标
     */
    public static boolean isMatchTargetQQ(String toCompare){
        for (String targetQQ : targetQQs) {
            if (toCompare.equals(targetQQ)) {
                return true;
            }
        }
        return false;
    }
}


