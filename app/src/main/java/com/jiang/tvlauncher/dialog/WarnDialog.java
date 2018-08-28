package com.jiang.tvlauncher.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.jiang.tvlauncher.MyApp;
import com.jiang.tvlauncher.R;
import com.jiang.tvlauncher.utils.LogUtil;
import com.jiang.tvlauncher.utils.Tools;

/**
 * @author: jiangadmin
 * @date: 2017/8/29.
 * @Email: www.fangmu@qq.com
 * @Phone: 186 6120 1018
 * TODO: 警告弹框
 */

public class WarnDialog {
    private static final String TAG = "WarnDialog";
    private static WarningDialog netWarningDialog;

    private static WindowManager wm = null;
    private static LinearLayout linearLayout = null;

    public static void createFloatView() {

        LayoutInflater inflater = (LayoutInflater) MyApp.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        linearLayout = (LinearLayout) inflater.inflate(R.layout.dialog_warning, null);

        if (wm == null) {

            wm = (WindowManager) MyApp.context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

            WindowManager.LayoutParams params = new WindowManager.LayoutParams();

            // 设置window type
            params.type = WindowManager.LayoutParams.TYPE_PHONE;
            /*
             * 如果设置为params.type = WindowManager.LayoutParams.TYPE_PHONE; 那么优先级会降低一些,
             * 即拉下通知栏不可见
             */
            params.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明

            // 设置悬浮窗的长得宽
            params.width = Tools.dp2px(MyApp.context, 270);
            params.height = Tools.dp2px(MyApp.context, 480);

            wm.addView(linearLayout, params);
        }

    }

    public static void remove() {
        try {
            if (wm != null && linearLayout != null) {
                wm.removeView(linearLayout);
//            wm.removeViewImmediate(linearLayout);

            }
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage());
        }

    }

    /**
     * 显示警告框
     */
    public static void showW(Activity activity) {
        if (netWarningDialog == null) {
            netWarningDialog = new WarningDialog(activity);
        }

        try {
            netWarningDialog.setCancelable(false);
            netWarningDialog.setCanceledOnTouchOutside(false);
            netWarningDialog.show();
        } catch (RuntimeException e) {
            LogUtil.e(TAG, e.getMessage());
        }
    }

    /**
     * 关闭
     */
    public static void dismiss() {

        try {
            //关闭警告框
            if (netWarningDialog != null) {
                netWarningDialog.dismiss();
                netWarningDialog = null;
            }
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage());
        } finally {
            netWarningDialog = null;
        }
    }

    /**
     * 警告框
     */
    public static class WarningDialog extends Dialog {
        public WarningDialog(@NonNull Context context) {
            super(context, R.style.MyDialog);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.dialog_warning);
        }
    }
}
