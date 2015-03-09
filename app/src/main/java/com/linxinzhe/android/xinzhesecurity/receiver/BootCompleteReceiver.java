package com.linxinzhe.android.xinzhesecurity.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class BootCompleteReceiver extends BroadcastReceiver {

    private SharedPreferences sp;
    private TelephonyManager tm;

    public BootCompleteReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //检查是否开启保护
        boolean protecting = sp.getBoolean("protecting", false);
        if (protecting) {
            String savedSim = sp.getString("sim", null);
            String realSim = tm.getSimSerialNumber() ;
            Log.i("config", "sim卡比较即将执行");
            if (savedSim.equals(realSim)) {
                Log.i("config", "sim卡相同");
            } else {
                Log.i("config", "sim卡变更");
                String safenumber = sp.getString("safenumber", "");
                SmsManager.getDefault().sendTextMessage(safenumber, null, "sim变更了", null, null);
            }
            Log.i("config", "sim卡比较执行结束");
        }
    }
}
