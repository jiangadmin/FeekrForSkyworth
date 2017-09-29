package com.jiang.tvlauncher.servlet;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.jiang.tvlauncher.MyAppliaction;
import com.jiang.tvlauncher.activity.Home_Activity;
import com.jiang.tvlauncher.dialog.Loading;
import com.jiang.tvlauncher.entity.Const;
import com.jiang.tvlauncher.entity.Point;
import com.jiang.tvlauncher.entity.Save_Key;
import com.jiang.tvlauncher.entity.TurnOnEntity;
import com.jiang.tvlauncher.server.TimingService;
import com.jiang.tvlauncher.utils.HttpUtil;
import com.jiang.tvlauncher.utils.LogUtil;
import com.jiang.tvlauncher.utils.SaveUtils;
import com.jiang.tvlauncher.utils.Tools;
import com.jiang.tvlauncher.utils.Wifi_APManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by  jiang
 * on 2017/6/19.
 * Email: www.fangmu@qq.com
 * Phone：186 6120 1018
 * Purpose:TODO 开机发送
 * update：
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

        map.put("serialNum", MyAppliaction.ID);
        map.put("turnType", MyAppliaction.turnType);
        map.put("modelNum", MyAppliaction.modelNum);

        map.put("systemVersion", Build.VERSION.INCREMENTAL);
        map.put("androidVersion", Build.VERSION.RELEASE);

        String res = HttpUtil.doPost(Const.URL + "dev/devTurnOffController/turnOn.do", map);

        TurnOnEntity entity;
        if (res != null) {
            try {
                entity = new Gson().fromJson(res, TurnOnEntity.class);
            } catch (Exception e) {
                entity = new TurnOnEntity();
                entity.setErrorcode(-2);
                entity.setErrormsg("数据解析失败");
                LogUtil.e(TAG, e.getMessage());
            }
        } else {
            entity = new TurnOnEntity();
            entity.setErrorcode(-1);
            entity.setErrormsg("连接服务器失败");
        }

        return entity;
    }

    @Override
    protected void onPostExecute(TurnOnEntity entity) {
        super.onPostExecute(entity);
        Const.Nets = false;
        Loading.dismiss();

        LogUtil.e(TAG, "=======================================================================================");
        LogUtil.e(TAG, entity.getErrormsg());
//        Toast.makeText(context, "开机请求返回："+entity.getErrormsg(), Toast.LENGTH_SHORT).show();
        LogUtil.e(TAG, "=======================================================================================");

        if (entity.getErrorcode() == 1000) {
            MyAppliaction.TurnOnS = true;

            Const.ID = entity.getResult().getDevInfo().getId();

            //存储ID
            SaveUtils.setString(Save_Key.ID, String.valueOf(entity.getResult().getDevInfo().getId()));

            //存储密码
            SaveUtils.setString(Save_Key.Password, entity.getResult().getShadowcnf().getShadowPwd());

            //判断是否是有线连接
            if (Tools.isLineConnected()) {
                LogUtil.e(TAG, "SSID:" + entity.getResult().getShadowcnf().getWifi() + "  PassWord:" + entity.getResult().getShadowcnf().getWifiPassword());
                //设置热点名  热点密码
                new Wifi_APManager(context).closeWifiAp();
                new Wifi_APManager(context).createAp(entity.getResult().getShadowcnf().getWifi(), entity.getResult().getShadowcnf().getWifiPassword());
            }

            //更改开机动画
            if (!TextUtils.isEmpty(entity.getResult().getLaunch().getMediaUrl())) {
                LogUtil.e(TAG, entity.getResult().getLaunch().getMediaUrl());
                SaveUtils.setString(Save_Key.BootAn, entity.getResult().getLaunch().getMediaUrl());
            }

            //方案类型（1=开机，2=屏保，3=互动）
            if (entity.getResult().getLaunch().getLaunchType() == 1) {
                //非空判断
                if (!TextUtils.isEmpty(entity.getResult().getLaunch().getMediaUrl())) {

                    //图片
                    if (entity.getResult().getLaunch().getMediaType() == 1) {
                        SaveUtils.setBoolean(Save_Key.NewImage, true);
                        SaveUtils.setBoolean(Save_Key.NewVideo, false);
                        SaveUtils.setString(Save_Key.NewImageUrl, entity.getResult().getLaunch().getMediaUrl());
                    }

                    //视频
                    if (entity.getResult().getLaunch().getMediaType() == 2) {
                        SaveUtils.setBoolean(Save_Key.NewVideo, true);
                        SaveUtils.setBoolean(Save_Key.NewImage, false);
                        SaveUtils.setString(Save_Key.NewVideoUrl, entity.getResult().getLaunch().getMediaUrl());
                    }
                }
            }

            String s = entity.getResult().getDevInfo().getZoomVal();
            LogUtil.e(TAG, "梯形数据:" + s);

            try {
                //初始化设备名称
                MyAppliaction.apiManager.set("setDeviceName", entity.getResult().getDevInfo().getModelNum(), null, null, null);

                //初始化投影方式
                MyAppliaction.apiManager.set("setProjectionMode", String.valueOf(entity.getResult().getShadowcnf().getProjectMode()), null, null, null);

                //初始化上电开机
                if (entity.getResult().getShadowcnf().getPowerTurn() == 1)
                    MyAppliaction.apiManager.set("setPowerOnStart", "true", null, null, null);
                else
                    MyAppliaction.apiManager.set("setPowerOnStart", "false", null, null, null);
                //初始化梯形数据

                Point point = new Gson().fromJson(s, Point.class);
                for (int i = 0; i < point.getPoint().size(); i++) {
                    MyAppliaction.apiManager.set("setKeyStoneByPoint", point.getPoint().get(i).getIdx(), point.getPoint().get(i).getCurrent_x(), point.getPoint().get(i).getCurrent_y(), null);
                }
            } catch (Exception e) {

                LogUtil.e(TAG, e.getMessage());
            }

            //存储间隔时间
            SaveUtils.setInt(Save_Key.Timming, entity.getResult().getShadowcnf().getMonitRate());

            //启动定时服务
            context.startService(new Intent(context, TimingService.class));

            //获取开屏
            new FindLanunch_Servlet().execute();

            if (MyAppliaction.activity != null && MyAppliaction.activity.getClass() == Home_Activity.class) {
                ((Home_Activity) MyAppliaction.activity).update();
            }

//            new FindChannelList_Servlet(new Home_Activity()).execute();

        } else if (entity.getErrorcode() == -2) {
            LogUtil.e(TAG, entity.getErrormsg());

        } else {
            timeCount.start();
            LogUtil.e(TAG, "失败了" + entity.getErrormsg());
        }
    }

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
            //再次启动
            new TurnOn_servlet(context).execute();
        }

        @Override
        public void onTick(long millisUntilFinished) {//计时过程显示

        }
    }
}
