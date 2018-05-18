package com.jiang.tvlauncher.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.jiang.tvlauncher.MyAppliaction;
import com.jiang.tvlauncher.R;
import com.jiang.tvlauncher.entity.Save_Key;
import com.jiang.tvlauncher.servlet.DownUtil;
import com.jiang.tvlauncher.utils.ImageUtils;
import com.jiang.tvlauncher.utils.LogUtil;
import com.jiang.tvlauncher.utils.SaveUtils;
import com.jiang.tvlauncher.utils.Tools;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

/**
 * @author: jiangadmin
 * @date: 2017/8/22.
 * @Email: www.fangmu@qq.com
 * @Phone: 186 6120 1018
 * TODO: 图片展示
 */

public class Image_Activity extends Base_Activity {
    private static final String TAG = "Image_Activity";
    private static final String URL = "url";

    ImageView imageView;

    String imageurl, imagename;

    public static void start(Context context, String url) {
        Intent intent = new Intent();
        intent.setClass(context, Image_Activity.class);
        intent.putExtra(URL, url);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyAppliaction.activity = this;
        setContentView(R.layout.activity_image);
        imageView =  findViewById(R.id.imageView);
        LogUtil.e(TAG, "图片展示");

        String imgf = Environment.getExternalStorageDirectory().getPath() + "/feekr/Download/" + SaveUtils.getString(Save_Key.NewImageName);
        LogUtil.e(TAG, "地址：" + imgf);

        //如果有网络
        if (Tools.isNetworkConnected()) {
            imageurl = getIntent().getStringExtra(URL);
            imagename = Tools.getFileNameWithSuffix(getIntent().getStringExtra(URL));
            //加载网络图片
            ImageLoader.getInstance().displayImage(imageurl, imageView);
            SaveUtils.setString(Save_Key.NewImageName, imagename);
            //下载网络图片
            new DownUtil(this).downLoad(imageurl, imagename, false);
        } else {
            //判断是否有记录
            if (!TextUtils.isEmpty(SaveUtils.getString(Save_Key.NewImageName))) {
                imageView.setImageBitmap(ImageUtils.getBitmap(new File(imgf)));
            } else {
                Toast.makeText(this, "网络异常，请联系服务人员", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
