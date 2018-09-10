package com.jiang.tvlauncher;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.SystemProperties;
import android.support.multidex.MultiDex;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.ctvdevicemanger.aidl.IctvDeviceManager;
import com.jiang.tvlauncher.entity.Save_Key;
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

    public static boolean IsLineNet = true;//是否是有线网络
    public static String serialNum;
    public static String turnType = "2";//开机类型 1 通电开机 2 手动开机
    public static boolean TurnOnS = false;

    public static Activity activity;

    private NotificationManager manager;

    public static String getSerialNum() {
        return SystemPropertiesProxy.getString(context, "ro.serialno");
    }


    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        context = this;
        Toast.makeText(this, "TvLauncher onCreate", Toast.LENGTH_SHORT).show();

        //崩溃检测
        CrashReport.initCrashReport(getApplicationContext(), "b9c56f18c1", false);

        LogUtil.e(TAG, "有线连接：" + Tools.isLineConnected());
        Tools.setScreenOffTime(24 * 60 * 60 * 1000);
        LogUtil.e(TAG, "休眠时间：" + Tools.getScreenOffTime());

        SaveUtils.setBoolean(Save_Key.FristTurnOn, true);

        LogUtil.e(TAG, "SN:" + SystemProperties.get("ro.serialno"));
        LogUtil.e(TAG, "机器型号:" + SystemProperties.get("ro.product.model"));
        LogUtil.e(TAG, "系统版本:" + SystemProperties.get("persist.sys.hwconfig.soft_ver"));
        LogUtil.e(TAG, "Android版本:" + SystemProperties.get("ro.build.version.release"));

        LogUtil.e(TAG, "内存总大小：" + SystemPropertiesProxy.getTotalMemory("MemTotal"));
        LogUtil.e(TAG, "内存可用：" + SystemPropertiesProxy.getTotalMemory("MemFree"));

        LogUtil.e(TAG, "机器型号:" + SystemPropertiesProxy.getString(this, "ro.product.model"));
        LogUtil.e(TAG, "系统版本:" + SystemPropertiesProxy.getString(this, "persist.sys.hwconfig.soft_ver"));
        LogUtil.e(TAG, "Android版本:" + SystemPropertiesProxy.getString(this, "ro.build.version.release"));

        new TurnOn_servlet(this).execute();

        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.feekr)
                .setTicker("feekr正在运行中").setContentInfo("feekr正在运行中")
                .setContentTitle("feekr正在运行中").setContentText("feekr正在运行中")
                .setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL)
                .build();
        manager.notify(1, notification);

    }

}
