package com.jiang.tvlauncher.servlet;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.jiang.tvlauncher.MyApp;
import com.jiang.tvlauncher.dialog.Loading;
import com.jiang.tvlauncher.entity.Const;
import com.jiang.tvlauncher.entity.Save_Key;
import com.jiang.tvlauncher.entity.TurnOnEntity;
import com.jiang.tvlauncher.server.TimingService;
import com.jiang.tvlauncher.utils.HttpUtil;
import com.jiang.tvlauncher.utils.LogUtil;
import com.jiang.tvlauncher.utils.SaveUtils;
import com.jiang.tvlauncher.utils.SystemPropertiesProxy;
import com.jiang.tvlauncher.utils.Tools;
import com.jiang.tvlauncher.utils.WifiApUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: jiangadmin
 * @date: 2017/6/19.
 * @Email: www.fangmu@qq.com
 * @Phone: 186 6120 1018
 * TODO: 开机发送
 */

public class TurnOn_servlet extends AsyncTask<String, Integer, TurnOnEntity> {
    private static final String TAG = "TurnOn_servlet";
    Context context;

    TimeCount timeCount;

    public TurnOn_servlet(Context context) {
        this.context = context;
        timeCount = new TimeCount(3000, 1000);
    }

    @Override
    protected TurnOnEntity doInBackground(String... strings) {
        Map map = new HashMap();
        //设备类型
        map.put("devType", Const.devType);
        //设备SN
        map.put("serialNum", MyApp.getSerialNum());
        //开机类型
        map.put("turnType", MyApp.turnType);

        map.put("modelNum", SystemPropertiesProxy.getString(context, "ro.product.model"));

        map.put("systemVersion", SystemPropertiesProxy.getString(context, "persist.sys.hwconfig.soft_ver"));
        map.put("androidVersion", SystemPropertiesProxy.getString(context, "ro.build.version.release"));

        String res = HttpUtil.doPost(Const.URL + "dev/devTurnOffController/turnOn.do", map);

        TurnOnEntity entity;
        if (TextUtils.isEmpty(res)) {
            entity = new TurnOnEntity();
            entity.setErrorcode(-1);
            entity.setErrormsg("连接服务器失败");
        } else {
            try {
                entity = new Gson().fromJson(res, TurnOnEntity.class);
            } catch (Exception e) {
                entity = new TurnOnEntity();
                entity.setErrorcode(-2);
                entity.setErrormsg("数据解析失败");
                LogUtil.e(TAG, e.getMessage());
            }
        }

        LogUtil.e(TAG, "=======================================================================================");
        if (entity != null && entity.getErrormsg() != null)
            LogUtil.e(TAG, entity.getErrormsg());
//        Toast.makeText(context, "开机请求返回："+entity.getErrormsg(), Toast.LENGTH_SHORT).show();
        LogUtil.e(TAG, "=======================================================================================");

        if (entity.getErrorcode() == 1000) {
            MyApp.TurnOnS = true;

            //归零
            num = 0;

            Const.ID = entity.getResult().getDevInfo().getId();

            //存储ID
            SaveUtils.setString(Save_Key.ID, String.valueOf(entity.getResult().getDevInfo().getId()));

            //存储密码
            if (entity.getResult().getShadowcnf() != null && entity.getResult().getShadowcnf().getShadowPwd() != null) {
                SaveUtils.setString(Save_Key.Password, entity.getResult().getShadowcnf().getShadowPwd());
            } else {
                SaveUtils.setString(Save_Key.Password, "");
            }
            //更改开机动画
            if (entity.getResult().getLaunch() != null)
                if (!TextUtils.isEmpty(entity.getResult().getLaunch().getMediaUrl())) {
                    LogUtil.e(TAG, entity.getResult().getLaunch().getMediaUrl());
                    SaveUtils.setString(Save_Key.BootAn, entity.getResult().getLaunch().getMediaUrl());
                }

            //方案类型（1=开机，2=屏保，3=互动）
            if (entity.getResult().getLaunch() != null)
                if (entity.getResult().getLaunch().getLaunchType() == 1) {
                    //非空判断
                    if (!TextUtils.isEmpty(entity.getResult().getLaunch().getMediaUrl())) {

                        SaveUtils.setBoolean(Save_Key.NewImage, entity.getResult().getLaunch().getMediaType() == 1);
                        SaveUtils.setBoolean(Save_Key.NewVideo, entity.getResult().getLaunch().getMediaType() == 2);

                    }
                }

            //存储间隔时间
            if (entity.getResult().getShadowcnf() != null)
                SaveUtils.setInt(Save_Key.Timming, entity.getResult().getShadowcnf().getMonitRate());

            //启动定时服务
            context.startService(new Intent(context, TimingService.class));

            //判断是否是有线连接 & 服务启用同步数据
            if (Tools.isLineConnected() && entity.getResult().getShadowcnf() != null
                    && entity.getResult().getShadowcnf().getHotPointFlag() == 1) {
                if (entity.getResult().getShadowcnf().getHotPoint() == 1
                        && entity.getResult().getShadowcnf().getWifi() != null
                        && entity.getResult().getShadowcnf().getWifiPassword() != null) {                //开启热点

                    //获取热点名称&热点密码
                    String SSID = entity.getResult().getShadowcnf().getWifi();
                    String APPWD = entity.getResult().getShadowcnf().getWifiPassword();

                    //存储热点名称&密码
                    SaveUtils.setString(Save_Key.WiFiName, SSID);
                    SaveUtils.setString(Save_Key.WiFiPwd, APPWD);

                    LogUtil.e(TAG, "SSID:" + SSID + "  PassWord:" + APPWD);

                    //开启热点
                    WifiApUtils.getInstance(context).openWifiAp(SSID, APPWD);

                } else if (entity.getResult().getShadowcnf().getHotPoint() == 0) {
                    //关闭热点
                    WifiApUtils.getInstance(context).closeWifiAp();

                }
            }


        } else if (entity.getErrorcode() == -2) {
            LogUtil.e(TAG, entity.getErrormsg());

        } else {
            timeCount.start();
            LogUtil.e(TAG, "失败了" + entity.getErrormsg());
        }

        return entity;
    }

    @Override
    protected void onPostExecute(TurnOnEntity entity) {
        super.onPostExecute(entity);
        Const.Nets = false;
        Loading.dismiss();

        switch (entity.getErrorcode()) {
            case 1000:
                EventBus.getDefault().post("update");
                break;
        }
    }

    public static int num = 0;

    /**
     * 计时器
     */
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }

        //倒计时完成
        @Override
        public void onFinish() {
            num++;
            //再次启动
            new TurnOn_servlet(context).execute();

        }

        @Override
        public void onTick(long millisUntilFinished) {//计时过程显示

        }
    }
}
