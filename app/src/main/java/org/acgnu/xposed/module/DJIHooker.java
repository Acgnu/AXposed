package org.acgnu.xposed.module;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import org.acgnu.tool.MyLog;
import org.acgnu.xposed.MyHooker;

/**
 * Fucking DJI height limit
 *
 * do not let me get the chance to hack ce model!
 */
public class DJIHooker implements MyHooker {
  private boolean mDoLog = false;
  private Activity mFirstActivity;
  private int mSeekBarId;

  @Override
  public String getTargetPackage() {
    return "dji.go.v5";
  }

  @Override
  public void doHookInitZygote(StartupParam startupParam) throws Throwable {
  }

  @Override
  public void doHookOnLoadPackage(final LoadPackageParam loadPackageParam) throws Throwable {
    MyLog.log("hook on dji", mDoLog);
    findAndHookMethod(Activity.class, "onCreate",
        Bundle.class, new XC_MethodHook() {
          @Override
          protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            if(null == mFirstActivity){
              mFirstActivity = (Activity) param.thisObject;
              mSeekBarId = mFirstActivity.getResources()
                  .getIdentifier("setting_ui_2_item_seek_bar", "id", getTargetPackage());
              MyLog.log("seekBarId" + mSeekBarId, mDoLog);
            }
          }
        });

    findAndHookMethod(SeekBar.class, "setOnSeekBarChangeListener", SeekBar.OnSeekBarChangeListener.class, new XC_MethodHook() {
      @Override
      protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        MyLog.log("hook on seekbar listener", mDoLog);
        final SeekBar seekBar = (SeekBar) param.thisObject;
//        if(! isTargetSeekBar(seekBar)){
//          MyLog.log("not target seekbar on change", doLog);
//          return;
//        }
        if (mSeekBarId != seekBar.getId()) {
          MyLog.log("seek bar id not match", mDoLog);
          return;
        }
        ViewGroup viewGroup = (ViewGroup) seekBar.getParent();
        View childAt0 = viewGroup.getChildAt(0);
        if (!(childAt0 instanceof TextView)) {
          MyLog.log("group node not match", mDoLog);
          return;
        }
        TextView textView = (TextView) childAt0;
        if (!"最大高度".equals(textView.getText())) {
          MyLog.log("seekbar item not match", mDoLog);
          return;
        }
        MyLog.log("find seekbar max=" + seekBar.getMax(), mDoLog);
        if (null != param.args[0]) {
//          MyLog.log("arg0 is not null", doLog);
//          XposedUtils.dumpClass(param.args[0].getClass());
//          MyLog.log("arg0 dump finish", doLog);
          findAndHookMethod(param.args[0].getClass(), "onProgressChanged",
              SeekBar.class,
              int.class,
              boolean.class,
              new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                  //限高 = 滑块值 / 滑块最大值 * 485 + 15
                  MyLog.log("on seek bar change:" + param.args[1], mDoLog);
                  int progress = Integer.parseInt(param.args[1].toString());
                  param.args[1] = progress + 500;
//                  if(seekBar.getMax() > 485){
//                    seekBar.setMax(485);
//                  }
                }
              });
        }
      }
    });
  }
}
