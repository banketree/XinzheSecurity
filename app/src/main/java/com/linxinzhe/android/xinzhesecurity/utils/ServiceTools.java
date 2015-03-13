package com.linxinzhe.android.xinzhesecurity.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

public class ServiceTools {

    public static boolean isExists(Context context, String serviceName) {

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServicesInfos = am.getRunningServices(200);
        for (ActivityManager.RunningServiceInfo info : runningServicesInfos) {
            String name = info.service.getClassName();
            if (serviceName.equals(name)) {
                return true;
            }
        }
        return false;
    }
}
