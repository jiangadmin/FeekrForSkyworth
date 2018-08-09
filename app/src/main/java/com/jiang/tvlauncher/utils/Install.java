package com.jiang.tvlauncher.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.jiang.tvlauncher.server.IPackageInstallObserver;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author: jiangyao
 * @date: 2018/8/7
 * @Email: www.fangmu@qq.com
 * @Phone: 186 6120 1018
 * TODO:
 */
public class Install {
    private static final String TAG = "Install";

    public final static int INSTALL_SUCCEEDED = 1;
    public static final int INSTALL_REPLACE_EXISTING = 0x00000002;
    public static final int INSTALL_FAILED_DUPLICATE_PERMISSION = -112;
    public static final int INSTALL_FAILED_OTHER = -1000000;


    public static boolean installPackageIce(Context context, String filePath) {
        int installFlags = 0;
        installFlags |= INSTALL_REPLACE_EXISTING;

        final Uri apkURI = Uri.fromFile(new File(filePath));
        PackageInstallObserver obs = new PackageInstallObserver();
        PackageManager pm = context.getPackageManager();
        try {
            Method method = PackageManager.class.getDeclaredMethod("installPackage", Uri.class,
                    IPackageInstallObserver.class, int.class, String.class);
            method.setAccessible(true);
            method.invoke(pm, apkURI, obs, installFlags, null);
            synchronized (obs) {
                while (!obs.finished) {
                    try {
                        obs.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                final boolean ret = (obs.result == INSTALL_SUCCEEDED);
                if (!ret) {
                    LogUtil.e(TAG, "installPackageIce install failed. file: " + filePath + " resultCode: " + obs.result);
                }
                return ret;
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    static class PackageInstallObserver extends IPackageInstallObserver.Stub {
        boolean finished;
        int result;
        public void packageInstalled(String name, int status) {
            synchronized( this) {
                finished = true;
                result = status;
                notifyAll();
            }
        }
    }
}
