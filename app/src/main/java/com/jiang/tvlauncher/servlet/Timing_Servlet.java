package com.jiang.tvlauncher.servlet;

import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.jiang.tvlauncher.MyApp;
import com.jiang.tvlauncher.entity.Const;
import com.jiang.tvlauncher.entity.MonitorResEntity;
import com.jiang.tvlauncher.entity.Save_Key;
import com.jiang.tvlauncher.utils.FileUtils;
import com.jiang.tvlauncher.utils.HttpUtil;
import com.jiang.tvlauncher.utils.SaveUtils;
import com.jiang.tvlauncher.utils.ShellUtils;
import com.jiang.tvlauncher.utils.SystemPropertiesProxy;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by  jiang
 * on 2017/6/19.
 * Email: www.fangmu@qq.com
 * Phone：186 6120 1018
 * Purpose:TODO 定时发送
 * update：
 */
public class Timing_Servlet extends AsyncTask<String, Integer, MonitorResEntity> {

    private static final String TAG = "Timing_Servlet";

    @Override
    protected MonitorResEntity doInBackground(String... infos) {
        Map map = new HashMap();
        if (TextUtils.isEmpty(SaveUtils.getString(Save_Key.ID))) {
            return null;
        }
        //设备类型
        map.put("devType", Const.devType);
        //设备ID
        map.put("devId", SaveUtils.getString(Save_Key.ID));
        //当前网速
        map.put("netSpeed", "1");
        //Rom大小
        map.put("storage", FileUtils.getRomSize());
        //可用内存
        map.put("memoryInfo", SystemPropertiesProxy.getTotalMemory("MemFree"));

        map.put("avaSpace", FileUtils.getFreeDiskSpaceS());

        String res = HttpUtil.doPost(Const.URL + "dev/devRunStateController/monitorRunState.do", map);
        MonitorResEntity entity;
        if (res != null) {
            try {
                entity = new Gson().fromJson(res, MonitorResEntity.class);
            } catch (Exception e) {
                entity = new MonitorResEntity();
                entity.setErrorcode(-2);
                entity.setErrormsg("数据解析失败");
            }

        } else {
            entity = new MonitorResEntity();
            entity.setErrorcode(-1);
            entity.setErrormsg("连接服务器失败");
        }
        return entity;
    }

    @Override
    protected void onPostExecute(MonitorResEntity entity) {
        super.onPostExecute(entity);

        switch (entity.getErrorcode()) {
            case 1000:
                if (entity.getResult().getBussFlag() == 0) {

                    Const.BussFlag = 0;

                    try {
                        ShellUtils.execCommand("input keyevent 3", false);
                    } catch (Exception ex) {
                        Log.e(TAG, "onPostExecute: " + ex.getMessage());

                        Intent backHome = new Intent(Intent.ACTION_MAIN);

                        backHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        backHome.addCategory(Intent.CATEGORY_HOME);

                        MyApp.context.startActivity(backHome);
                    }

                } else if (entity.getResult().getBussFlag() == 1) {
                    Const.BussFlag = 1;
                }

                EventBus.getDefault().post(String.valueOf(entity.getResult().getBussFlag()));
                break;
        }

    }
}
