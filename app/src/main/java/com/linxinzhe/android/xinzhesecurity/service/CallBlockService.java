package com.linxinzhe.android.xinzhesecurity.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.linxinzhe.android.xinzhesecurity.db.CallBlockDao;

import java.lang.reflect.Method;

public class CallBlockService extends Service {
    public static final String TAG = "CallSmsSafeService";
    private InnerSmsReceiver receiver;
    private CallBlockDao dao;
    private TelephonyManager tm;
    private MyListener listener;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class InnerSmsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "内部广播接受者， 短信到来了");
            //检查发件人是否是黑名单号码，设置短信拦截全部拦截。
            Object[] objs = (Object[]) intent.getExtras().get("pdus");
            for(Object obj:objs){
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
                //得到短信发件人
                String sender = smsMessage.getOriginatingAddress();
                Log.i(TAG,sender);
                String result = dao.findMode(sender);
                if("2".equals(result)||"3".equals(result)){
                    Log.i(TAG,"拦截短信");
                    abortBroadcast();
//                    deleteSMS(context);
                }
            }
        }
    }

    @Override
    public void onCreate() {
        dao = new CallBlockDao(this);
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener = new MyListener();
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        receiver = new InnerSmsReceiver();
        IntentFilter filter =  new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        registerReceiver(receiver,filter);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        receiver = null;
        tm.listen(listener, PhoneStateListener.LISTEN_NONE);
        super.onDestroy();
    }

    private class MyListener extends PhoneStateListener{

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING://响铃状态
                    String result = dao.findMode(incomingNumber);
                    if("1".equals(result)||"3".equals(result)){
                        Log.i(TAG,"挂断电话");
                        endCall();
                    }
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    public void endCall() {
        //IBinder iBinder = ServiceManager.getService(TELEPHONY_SERVICE);
        try {
            //加载servicemanager的字节码
            Class clazz = CallBlockService.class.getClassLoader().loadClass("android.os.ServiceManager");
            Method method = clazz.getDeclaredMethod("getService", String.class);
            IBinder ibinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
            ITelephony.Stub.asInterface(ibinder).endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void deleteSMS(Context context) {

        long id = getThreadId(context);
        Uri mUri=Uri.parse("content://sms/conversations/" + id);
        context.getContentResolver().delete(mUri, null, null);
    }

    private long getThreadId(Context context) {

        long threadId = 0;
        String SMS_READ_COLUMN = "read";
        String WHERE_CONDITION = SMS_READ_COLUMN + " = 0";
        String SORT_ORDER = "date DESC";
        int count = 0;
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/inbox"),new String[] { "_id", "thread_id", "address", "person", "date", "body" },WHERE_CONDITION,null,SORT_ORDER);
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
