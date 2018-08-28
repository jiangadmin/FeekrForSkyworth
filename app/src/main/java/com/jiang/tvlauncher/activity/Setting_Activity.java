package com.jiang.tvlauncher.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

import com.jiang.tvlauncher.MyApp;
import com.jiang.tvlauncher.R;
import com.jiang.tvlauncher.dialog.Loading;
import com.jiang.tvlauncher.servlet.Update_Servlet;
import com.jiang.tvlauncher.utils.Tools;

/**
 * @author: jiangadmin
 * @date: 2017/7/3.
 * @Email: www.fangmu@qq.com
 * @Phone: 186 6120 1018
 * TODO: 控制台
 */

public class Setting_Activity extends Base_Activity implements View.OnClickListener {
    private static final String TAG = "Setting_Activity";

    //商店  互联 媒体中心  网络 检测更新  设置
    LinearLayout   setting3, setting4, setting5, setting6;

    public static void start(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, Setting_Activity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initview();
        initeven();

    }

    private void initview() {
        setting3 = findViewById(R.id.setting_3);
        setting4 = findViewById(R.id.setting_4);
        setting5 = findViewById(R.id.setting_5);
        setting6 = findViewById(R.id.setting_6);
    }

    private void initeven() {


        setting3.setOnClickListener(this);
        setting4.setOnClickListener(this);
        setting5.setOnClickListener(this);
        setting6.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            //梯形校正
            case R.id.setting_3:
                startActivity(new Intent(getPackageManager().getLaunchIntentForPackage("com.skyworthdigital.skymediacenter")));
                break;
            //文件管理
            case R.id.setting_4:
                //如果是有线连接
                if (Tools.isLineConnected())
                    //启动到有线连接页面
                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                else
                    //启动到无线连接页面
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                break;
            //检测更新
            case R.id.setting_5:
                Loading.show(this, "检查更新");
                new Update_Servlet(this).execute();
                break;
            //关于本机
            case R.id.setting_6:
                startActivity(new Intent(getPackageManager().getLaunchIntentForPackage("com.skyworthdigital.settings")));
                break;
        }
    }

}
