package com.linxinzhe.android.xinzhesecurity.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

import com.linxinzhe.android.xinzhesecurity.R;
import com.linxinzhe.android.xinzhesecurity.service.GPSService;

public class SMSReceiver extends BroadcastReceiver {

    private static final String TAG = "SMSReceiver";
    private SharedPreferences sp;

    private DevicePolicyManager dpm;

    public SMSReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        Object[] objs = (Object[]) intent.getExtras().get("pdus");
        for (Object o : objs) {
            SmsMessage sms = SmsMessage.createFromPdu((byte[]) o);
            String sender = sms.getOriginatingAddress();
            String body = sms.getMessageBody();

            //判断是否是安全号码
            String safenumber = sp.getString("safenumber", "");
            if (sender.contains(safenumber)) {
                if (body.contains("dingwei#")) {
                    Log.i(TAG, "得到手机GPS");
                    Intent gpsIntent = new Intent(context, GPSService.class);
                    context.startService(gpsIntent);
                    SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
                    String lastLocationGPS = sp.getString("lastLocationGPS", "");
                    String lastLocationNET = sp.getString("lastLocationNET", "");
                    if (TextUtils.isEmpty(lastLocationGPS) && TextUtils.isEmpty(lastLocationNET)) {
                        //得不到位置
                        SmsManager.getDefault().sendTextMessage(sender, null, "获得位置中", null, null);
                    } else {
                        SmsManager.getDefault().sendTextMessage(sender, null, lastLocationGPS + lastLocationNET, null, null);
                    }
                    abortBroadcast();
                } else if (body.contains("jingbao#")) {
                    Log.i(TAG, "得到报警信号");
                    MediaPlayer player = MediaPlayer.create(context, R.raw.alert);
                    player.setLooping(true);
                    player.setVolume(1.0f, 1.0f);
                    player.start();
                    abortBroadcast();
                } else if (body.contains("shanchu#")) {
                    //太危险，先注释掉
//                    dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
//                    dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
//                    dpm.wipeData(0);
                    abortBroadcast();
                    Log.i(TAG, "得到消除信号");
                } else if (body.contains("suoding#")) {
                    Log.i(TAG, "得到锁屏信号");
                    dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                    ComponentName who = new ComponentName(context, MyAdminReceiver.class);
                    if (dpm.isAdminActive(who)) {
                        dpm.lockNow();
                        dpm.resetPassword(sp.getString("lockpsw", "9999"), 0);
                        abortBroadcast();
                    }
                }
                deleteSMS(context);
            }
        }
    }

    private void deleteSMS(Context context) {

        long id = getThreadId(context);
        Uri mUri = Uri.parse("content://sms/conversations/" + id);
        context.getContentResolver().delete(mUri, null, null);
    }

    private long getThreadId(Context context) {

        long threadId = 0;
        String SMS_READ_COLUMN = "read";
        String WHERE_CONDITION = SMS_READ_COLUMN + " = 0";
        String SORT_ORDER = "date DESC";
        int count = 0;
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/inbox"), new String[]{"_id", "thread_id", "address", "person", "date", "body"}, WHERE_CONDITION, null, SORT_ORDER);
        if (cursor != null) {
            try {
                count = cursor.getCount();
                if (count > 0) {
                    cursor.moveToFirst();
                    threadId = cursor.getLong(1);
                }
            } finally {
                cursor.close();
            }
        }
        Log.i("threadId", String.valueOf(threadId));
        return threadId;
    }

}
