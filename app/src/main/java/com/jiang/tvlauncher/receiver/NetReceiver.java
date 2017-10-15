package com.jiang.tvlauncher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.jiang.tvlauncher.MyAppliaction;
import com.jiang.tvlauncher.dialog.NetDialog;
import com.jiang.tvlauncher.utils.LogUtil;
import com.jiang.tvlauncher.utils.Tools;

/**
 * Created by  jiang
 * on 2017/7/15.
 * Email: www.fangmu@qq.com
 * Phone：186 6120 1018
 * Purpose:TODO 网络广播
 * update：
 */
public class NetReceiver extends BroadcastReceiver {
    private static final String TAG = "NetReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
                if (networkInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
                    LogUtil.e(TAG, "有线网络");
                    MyAppliaction.IsLineNet = true;
                    if (Tools.isNetworkConnected())
                        NetDialog.dismiss();

                }
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    LogUtil.e(TAG, "无线网络");
                    MyAppliaction.IsLineNet = false;
                    if (Tools.isNetworkConnected())
                        NetDialog.dismiss();
                }
            } else {
                LogUtil.e(TAG, "网络断开");
                NetDialog.showW();
            }
        }
    }
}
