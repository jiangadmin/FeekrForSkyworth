package com.jiang.tvlauncher.entity;

import android.os.Environment;

import com.jiang.tvlauncher.BuildConfig;

/**
 * Created by  jiang
 * on 2017/6/19.
 * Email: www.fangmu@qq.com
 * Phone：186 6120 1018
 * Purpose:TODO 常量
 * update：
 */
public class Const {
    /**
     * 存储位置
     */
    public static String FilePath = Environment.getExternalStorageDirectory().getPath() + "/feekr/Download/";

    public static String URL = BuildConfig.NetUrl;
//    public static String URL = " http://testapi.feekrs.com/";

    public static int Timing = 30;
    public static int ID;

    public static int ShowType = 1; //1:图片  2 ：视频
    public static boolean Nets = true; //网络状态  控制发请求

    public static int seconds = 300;      //间隔时间

    public static int BussFlag = 1;      //账户状态

    public static int FindChannelList = 0;

    public static boolean IsGetVip = false;  //本次开机时候获取过Vip

    //设备类型  1 极米投影  2 创维盒子
    public static String devType = "2";

    public static String 包 = "baobaobao";
    public static String TvViedoDow = "TvViedoDow";//定制版腾讯视频下载地址

    public static String 云视听 = "KtcpVideo";//云视听
    public static String 云视听Url = "KtcpVideoUrl";//下载地址

    public static String TvViedo = "com.ktcp.tvvideo";//定制版腾讯视频
    public static String TencentViedo = "com.ktcp.video";//腾讯视频ktcp

    public static String ktcp_vuid = "";//腾讯视频ID
    public static String ktcp_vtoken = "";//腾讯视频ID
    public static String ktcp_accessToken = "";//腾讯视频ID

}
