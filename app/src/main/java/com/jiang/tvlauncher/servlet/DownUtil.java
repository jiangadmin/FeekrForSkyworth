package com.jiang.tvlauncher.servlet;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.os.SystemProperties;
import android.view.KeyEvent;
import android.widget.Toast;

import com.jiang.tvlauncher.dialog.Loading;
import com.jiang.tvlauncher.utils.LogUtil;
import com.jiang.tvlauncher.utils.ShellUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jiangyao
 * on 2017/9/7.
 * Email: www.fangmu@qq.com
 * Phone：186 6120 1018
 * Purpose:TODO 下载
 * update：
 */

public class DownUtil {
    private static final String TAG = "DownUtil";
    Activity activity;

    ProgressDialog pd;

    public DownUtil(Activity activity) {
        this.activity = activity;
    }

    public void downLoad(final String path, final String fileName, final boolean showpd) {
        // 进度条对话框
        pd = new ProgressDialog(activity);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("下载中，精彩马上呈现，请稍后...");
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);
        // 监听返回键--防止下载的时候点击返回
        pd.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//                    Toast.makeText(activity, "正在下载请稍后", Toast.LENGTH_SHORT).show();
                    return true;
                } else {
                    return false;
                }
            }
        });
        // Sdcard不可用
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(activity, "SD卡不可用~", Toast.LENGTH_SHORT).show();
            Loading.dismiss();

        } else {
            if (showpd)
                pd.show();
            //下载的子线程
            new Thread() {
                @Override
                public void run() {
                    try {
                        // 在子线程中下载APK文件
                        File file = getFileFromServer(path, fileName, pd);

                        //判断文件是否下载成功
                        if (file != null && file.exists()) {
                            sleep(1000);
                            LogUtil.e(TAG, "文件下载完成" + fileName);
                            // 结束掉进度条对话框
                            if (showpd)
                                pd.dismiss();

                            if (fileName.contains(".apk")) {
                                //静默安装
                                ShellUtils.installSilent(file.getPath());
                            } else if (fileName.contains(".zip")) {
                                LogUtil.e(TAG, "下载ZIP");

                                //设置系统开机广告
                                String bootanim = file.getPath().replace(fileName, "").replace("0","legacy");
                                bootanim = bootanim.substring(0, bootanim.length() - 1);
                                SystemProperties.set("persist.sys.bootanima.path", bootanim);
                                LogUtil.e(TAG, "走开机动画" + bootanim);
                                SystemProperties.set("persist.sys.bootanimation", "1");

                            }
                        }
                    } catch (Exception e) {
                        LogUtil.e(TAG, "文件下载失败了" + e.getMessage());

                        Loading.dismiss();
                        if (showpd)
                            pd.dismiss();
                        e.printStackTrace();
                    }
                }

            }.start();
        }
    }

    /**
     * 从服务器下载apk
     */
    public static File getFileFromServer(String path, String fileName, ProgressDialog pd) throws Exception {

        File file = null;
        // 如果相等的话表示当前的sdcard挂载在手机上并且是可用的
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            // 获取到文件的大小
            if (pd != null)
                pd.setMax(conn.getContentLength() / 1024);

            InputStream is = null;
            FileOutputStream fos = null;
            BufferedInputStream bis = null;
            try {
                is = conn.getInputStream();
                file = new File(Environment.getExternalStorageDirectory().getPath() + "/feekr/Download/", fileName);
                //判断文件夹是否被创建
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                fos = new FileOutputStream(file);
                bis = new BufferedInputStream(is);

                byte[] buffer = new byte[1024];
                int len;
                int total = 0;
                while ((len = bis.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    total += len;
                    // 获取当前下载量
                    if (pd != null)
                        pd.setProgress(total / 1024);
                }

            } catch (Exception ex) {
                LogUtil.e(TAG, "文件下载失败了" + ex.getMessage());
            } finally {
                if (fos != null) {
                    fos.close();
                }

                if (bis != null) {
                    bis.close();
                }

                if (is != null) {
                    is.close();
                }
            }
        } else {
            Loading.dismiss();
        }

        return file;
    }
}
