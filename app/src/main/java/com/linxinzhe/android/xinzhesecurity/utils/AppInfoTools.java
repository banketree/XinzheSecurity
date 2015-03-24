package com.linxinzhe.android.xinzhesecurity.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.os.RemoteException;

import com.linxinzhe.android.xinzhesecurity.domain.AppInfo;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by linxinzhe on 2015/3/10.
 */
public class AppInfoTools {

//    private long totalsize ;

    public List<AppInfo> getAppInfos(Context context) throws RemoteException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, PackageManager.NameNotFoundException {
        PackageManager pm = context.getPackageManager();
        //所有的安装在系统上的应用程序包信息。
        List<PackageInfo> packInfos = pm.getInstalledPackages(0);
        List<AppInfo> appInfos = new ArrayList<AppInfo>();

        ApplicationInfo applicationInfo = null;
        File file = null;
        for (PackageInfo packInfo : packInfos) {
            AppInfo appInfo = new AppInfo();
            //packInfo  相当于一个应用程序apk包的清单文件
            String packname = packInfo.packageName;
            Drawable icon = packInfo.applicationInfo.loadIcon(pm);
            String name = packInfo.applicationInfo.loadLabel(pm).toString();

//            Method getPackageSizeInfo = pm.getClass().getDeclaredMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
//            getPackageSizeInfo.invoke(pm, packname,new PkgSizeObserver());
//            long memory =totalsize;

            applicationInfo = pm.getApplicationInfo(packname, 0);
            file = new File(applicationInfo.sourceDir);
            long memory = file.length();

            int flags = packInfo.applicationInfo.flags;//应用程序信息的标记 相当于用户提交的答卷
            if ((flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                //用户程序
                appInfo.setUserApp(true);
            } else {
                //系统程序
                appInfo.setUserApp(false);
            }
            if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0) {
                //手机的内存
                appInfo.setInRom(true);
            } else {
                //手机外存储设备
                appInfo.setInRom(false);
            }
            appInfo.setPackname(packname);
            appInfo.setIcon(icon);
            appInfo.setName(name);
            appInfo.setMemory(memory);
            if (appInfo.isUserApp()) {
                appInfos.add(0, appInfo);
            } else {
                appInfos.add(appInfo);
            }
        }

        return appInfos;
    }

    public List<AppInfo> getAppInfosAndTraffic(Context context) throws RemoteException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, PackageManager.NameNotFoundException {
        PackageManager pm = context.getPackageManager();
        //所有的安装在系统上的应用程序包信息。
        List<PackageInfo> packInfos = pm.getInstalledPackages(0);
        List<AppInfo> appInfos = new ArrayList<AppInfo>();

        ApplicationInfo applicationInfo = null;
        File file = null;
        for (PackageInfo packInfo : packInfos) {
            AppInfo appInfo = new AppInfo();
            //packInfo  相当于一个应用程序apk包的清单文件
            String packname = packInfo.packageName;
            Drawable icon = packInfo.applicationInfo.loadIcon(pm);
            String name = packInfo.applicationInfo.loadLabel(pm).toString();

//            Method getPackageSizeInfo = pm.getClass().getDeclaredMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
//            getPackageSizeInfo.invoke(pm, packname,new PkgSizeObserver());
//            long memory =totalsize;

            applicationInfo = pm.getApplicationInfo(packname, 0);
            file = new File(applicationInfo.sourceDir);
            long memory = file.length();

            int flags = packInfo.applicationInfo.flags;//应用程序信息的标记 相当于用户提交的答卷
            if ((flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                //用户程序
                appInfo.setUserApp(true);
            } else {
                //系统程序
                appInfo.setUserApp(false);
            }
            if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0) {
                //手机的内存
                appInfo.setInRom(true);
            } else {
                //手机外存储设备
                appInfo.setInRom(false);
            }
            appInfo.setPackname(packname);
            appInfo.setIcon(icon);
            appInfo.setName(name);
            appInfo.setMemory(memory);
            if (appInfo.isUserApp()) {
                appInfos.add(0, appInfo);
            } else {
                appInfos.add(appInfo);
            }

            int uid = applicationInfo.uid;
            long rxBytes = TrafficStats.getUidRxBytes(uid);//但app下载
            long txBytes = TrafficStats.getUidTxBytes(uid);//单app上传
            appInfo.setRxBytes(rxBytes);
            appInfo.setTxBytes(txBytes);
        }

        return appInfos;
    }
//    //aidl文件形成的Bindler机制服务类
//    private class PkgSizeObserver extends IPackageStatsObserver.Stub {
//        /**
//         * 回调函数，
//         *
//         * @param pStatus   ,返回数据封装在PackageStats对象中
//         * @param succeeded 代表回调成功
//         */
//        @Override
//        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
//                throws RemoteException {
////            cachesize = pStats.cacheSize; //缓存大小
////            datasize = pStats.dataSize;  //数据大小
////            codesize = pStats.codeSize;  //应用程序大小
//            totalsize = pStats.cacheSize + pStats.dataSize + pStats.codeSize;
//
//        }
//    }
}
