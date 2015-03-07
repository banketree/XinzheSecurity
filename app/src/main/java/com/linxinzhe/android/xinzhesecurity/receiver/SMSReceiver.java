package com.linxinzhe.android.xinzhesecurity.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

import com.linxinzhe.android.xinzhesecurity.R;
import com.linxinzhe.android.xinzhesecurity.service.GPSService;

public class SMSReceiver extends BroadcastReceiver {

    private static final String TAG = "SMSReceiver";
    private SharedPreferences sp;


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
                if (body.contains("#*location*#")){
                    Log.i(TAG, "得到手机GPS");
                    Intent gpsIntent = new Intent(context, GPSService.class);
                    context.startService(gpsIntent);
                    SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
                    String lastLocation = sp.getString("lastLocation", null);
                    if (TextUtils.isEmpty(lastLocation)) {
                        //得不到位置
                        SmsManager.getDefault().sendTextMessage(sender, null, "获得位置中", null, null);
                    } else {
                        SmsManager.getDefault().sendTextMessage(sender, null, lastLocation, null, null);
                    }
                    abortBroadcast();
                }else if (body.contains("#*alarm*#")){
                    Log.i(TAG, "得到报警信号");
                    MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
                    player.setLooping(false);
                    player.setVolume(1.0f, 1.0f);
                    player.start();
                    abortBroadcast();
                }else if (body.contains("#*wipedata*#")){
                    Log.i(TAG, "得到消除信号");

                    abortBroadcast();
                }else if (body.contains("#*lockscreen*#")){
                    Log.i(TAG, "得到锁屏信号");

                    abortBroadcast();
                }
            }
        }
    }


}
