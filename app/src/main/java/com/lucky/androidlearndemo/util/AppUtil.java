package com.lucky.androidlearndemo.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.lucky.androidlearndemo.App;

/**
 * 获取App的信息
 * 所有权限、签名、图标、名称、版本号、版本名、包名
 */
public class AppUtil {
    @SuppressLint("StaticFieldLeak")
    static Context context = App.getContext();
    static PackageManager pm = context.getPackageManager();
    static String packageName = context.getPackageName();

    /**
     * 获取程序的权限
     */
    public static String[] getAppPermissions() {
        try {
            PackageInfo packInfo = pm.getPackageInfo(packageName,
                    PackageManager.GET_PERMISSIONS);
            // 获取到所有的权限
            return packInfo.requestedPermissions;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取程序的签名
     * <p>
     * 警告参考
     */
    public static String getAppSignature() {
        try {
            @SuppressLint("PackageManagerGetSignatures") PackageInfo packInfo = pm.getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);
            // 获取到所有的权限
            return packInfo.signatures[0].toCharsString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获得程序图标
     */
    public static Drawable getAppIcon() {
        try {
            ApplicationInfo info = pm.getApplicationInfo(
                    context.getPackageName(), 0);
            return info.loadIcon(pm);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获得程序名称
     */
    public static String getAppName() {
        try {
            ApplicationInfo info = pm.getApplicationInfo(packageName, 0);
            return info.loadLabel(pm).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获得软件版本号
     */
    public static int getAppVersionCode() {
        int versioncode = 0;
        try {
            versioncode = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versioncode;
    }

    /**
     * 获得软件版本名
     */
    public static String getAppVersionName() {
        String versionName = null;
        try {
            versionName = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 得到软件包名
     */
    public static String getAppPackageName() {
        return packageName;
    }
}
