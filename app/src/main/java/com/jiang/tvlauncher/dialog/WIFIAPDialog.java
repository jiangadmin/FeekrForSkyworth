package com.jiang.tvlauncher.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiang.tvlauncher.R;

/**
 * Created by  jiang
 * on 2017/9/29.
 * Email: www.fangmu@qq.com
 * Phone：186 6120 1018
 * Purpose:TODO Wifi热点
 * update：
 */

public class WIFIAPDialog extends Dialog {

    ImageView imageView;
    TextView ssid,pwd;

    public WIFIAPDialog(@NonNull Context context) {
        super(context, R.style.MyDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_wifiap);

        imageView = (ImageView) findViewById(R.id.qrcode);
        ssid = (TextView) findViewById(R.id.ssid);
        pwd = (TextView) findViewById(R.id.wifipwd);
    }
}
