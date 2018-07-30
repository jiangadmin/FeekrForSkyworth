package com.jiang.tvlauncher;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.ctvdevicemanger.aidl.IctvDeviceManager;
import com.jiang.tvlauncher.entity.Point;
import com.jiang.tvlauncher.entity.Save_Key;
import com.jiang.tvlauncher.servlet.TurnOn_servlet;
import com.jiang.tvlauncher.utils.LogUtil;
import com.jiang.tvlauncher.utils.SaveUtils;
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

public class MyAppliaction extends Application {

    private static final String TAG = "MyAppliaction";
    public static boolean LogShow = true;
    public static Context context;

    public static IctvDeviceManager apiManager;

    public static boolean IsLineNet = true;//是否是有线网络
    public static String modelNum = "Z5极光";
    public static String ID = "";
    public static String Temp = "FFFFFF";
    public static String WindSpeed = "FFFFFF";
    public static String turnType = "2";//开机类型 1 通电开机 2 手动开机
    Point point;
    public static boolean TurnOnS = false;

    public static Activity activity;

    @Override
    public void onCreate() {
        super.onCreate();
//        startService(new Intent(this, TimingService.class));
        context = this;

        //崩溃检测
        CrashReport.initCrashReport(getApplicationContext(), "b9c56f18c1", false);

        LogUtil.e(TAG, "有线连接：" + Tools.isLineConnected());
        Tools.setScreenOffTime(24 * 60 * 60 * 1000);
        LogUtil.e(TAG, "休眠时间：" + Tools.getScreenOffTime());

        SaveUtils.setBoolean(Save_Key.FristTurnOn, true);

        LogUtil.e(TAG, "准备连接AIDL");
        ComponentName componentName = new ComponentName("com.ctvdevicemanger.aidl", "com.ctvdevicemanger.aidl.IctvDeviceManager");
        bindService(new Intent().setComponent(componentName), serviceConnection, Context.BIND_AUTO_CREATE);

    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        //绑定上服务的时候
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            LogUtil.e(TAG, "连接AIDL成功");

            //得到远程服务
            apiManager = IctvDeviceManager.Stub.asInterface(iBinder);

            if (TurnOnS) {
                return;
            }
            try {
                //电视/机顶盒机型号
                ID = apiManager.getSTBData("Model", null);
                //平台 ROM 固件版本号（客户-版型-机型-软件版本( 如TL-CV338H_A-SmartTV-V002)）
                ID = apiManager.getSTBData("SoftwareVersion", null);
                //电视/机顶盒有线 MAC
                ID = apiManager.getSTBData("MAC", null);
                //获取序列号
                ID = apiManager.getSTBData("SN", null);
                //广电串号（需保存在不可擦写区域）
                ID = apiManager.getSTBData("GDID", null);
                //客 户 - 版 型 - 机 型 ( 如CYF-CV338H_A-SmartTV)
                ID = apiManager.getSTBData("sw", null);
                //芯片平台，如 mtk、rk
                ID = apiManager.getSTBData("IC", null);
                //Android 版本，如 android5.1
                ID = apiManager.getSTBData("OsVersion", null);
                //主板生产厂商名字
                ID = apiManager.getSTBData("BoardName", null);
                //电视/机顶盒认证结果
                ID = apiManager.getSTBData("apprresult", null);
                //预留
                ID = apiManager.getSTBData("Reserved", null);
//
                if (!TurnOnS) {
                    new TurnOn_servlet(context).execute();
                }

            } catch (RemoteException e) {
                e.printStackTrace();
                LogUtil.e(TAG, "连接失败" + e.getMessage());
            }
        }

        //断开服务的时候
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            LogUtil.e(TAG, "断开AIDL连接");
        }
    };

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
