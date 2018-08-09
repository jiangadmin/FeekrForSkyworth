package com.jiang.tvlauncher;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.ctvdevicemanger.aidl.IctvDeviceManager;
import com.jiang.tvlauncher.entity.Save_Key;
import com.jiang.tvlauncher.servlet.Timing_Servlet;
import com.jiang.tvlauncher.servlet.TurnOn_servlet;
import com.jiang.tvlauncher.utils.LogUtil;
import com.jiang.tvlauncher.utils.SaveUtils;
import com.jiang.tvlauncher.utils.SystemPropertiesProxy;
import com.jiang.tvlauncher.utils.Tools;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.List;

/**
 * Created by  jiang
 * on 2017/7/3.
 * Email: www.fangmu@qq.com
 * Phone：186 6120 1018
 * Purpose:TODO
 * update：
 */

public class MyApp extends Application {

    private static final String TAG = "MyAppliaction";
    public static boolean LogShow = true;
    public static Context context;

    public static IctvDeviceManager apiManager;

    public static boolean IsLineNet = true;//是否是有线网络
    public static String modelNum = "Z5极光";
    public static String serialNum;
    public static String turnType = "2";//开机类型 1 通电开机 2 手动开机
    public static String ID;
    public static boolean TurnOnS = false;

    public static Activity activity;

    public static String getSerialNum() {
        return serialNum;
    }

    public static void setSerialNum(String serialNum) {
        MyApp.serialNum = serialNum;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        //崩溃检测
        CrashReport.initCrashReport(getApplicationContext(), "b9c56f18c1", false);

        LogUtil.e(TAG, "有线连接：" + Tools.isLineConnected());
        Tools.setScreenOffTime(24 * 60 * 60 * 1000);
        LogUtil.e(TAG, "休眠时间：" + Tools.getScreenOffTime());

        SaveUtils.setBoolean(Save_Key.FristTurnOn, true);

        setSerialNum(SystemPropertiesProxy.getString(this, "ro.serialno"));

        LogUtil.e(TAG, "机器型号:" + SystemPropertiesProxy.getString(this, "ro.product.model"));
        LogUtil.e(TAG, "系统版本:" + SystemPropertiesProxy.getString(this, "persist.sys.hwconfig.soft_ver"));
        LogUtil.e(TAG, "Android版本:" + SystemPropertiesProxy.getString(this, "ro.build.version.release"));

        new TurnOn_servlet(this).execute();
        new Timing_Servlet().execute();

    }

    /**
     * 当前应用是否处于前台
     *
     * @return
     */
    public static boolean isForeground() {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processes = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : processes) {
            if (processInfo.processName.equals(context.getPackageName())) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return true;
                }
            }
        }
        return false;
    }
}
