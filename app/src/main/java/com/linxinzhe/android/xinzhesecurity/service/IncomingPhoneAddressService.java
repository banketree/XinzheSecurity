package com.linxinzhe.android.xinzhesecurity.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.linxinzhe.android.xinzhesecurity.R;
import com.linxinzhe.android.xinzhesecurity.db.PhoneAddressQueryDao;

public class IncomingPhoneAddressService extends Service {

    private WindowManager wm;
    private View view;

    private TelephonyManager tm;
    private MyListenerPhone listenerPhone;

    private OutgoingPhoneReceiver outgoingPhoneReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public void onCreate() {
        super.onCreate();
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        // 监听来电
        listenerPhone = new MyListenerPhone();
        tm.listen(listenerPhone, PhoneStateListener.LISTEN_CALL_STATE);

        //注册去电广播
        outgoingPhoneReceiver = new OutgoingPhoneReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        registerReceiver(outgoingPhoneReceiver, filter);

        //显示自定义的来电去电Toast
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
    }



    public void phoneAddressToast(String address) {
        view =   View.inflate(this, R.layout.address_toast, null);
        TextView textview  = (TextView) view.findViewById(R.id.tv_phone_address);

        textview.setText(address);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();

        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;

        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        wm.addView(view, params);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 取消监听来电
        tm.listen(listenerPhone, PhoneStateListener.LISTEN_NONE);
        listenerPhone = null;

        //取消去电广播
        unregisterReceiver(outgoingPhoneReceiver);
        outgoingPhoneReceiver = null;

    }

    private class MyListenerPhone extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:// 来电铃声响起，则查询数据库的操作
                    String address = PhoneAddressQueryDao.query(incomingNumber);
                    phoneAddressToast(address);
                    break;
                case TelephonyManager.CALL_STATE_IDLE://电话的空闲状态：挂电话、来电拒绝，则清除
                    if(view != null ){
                        wm.removeView(view);
                    }
                    break;
                default:
                    break;
            }
        }

    }

    private class OutgoingPhoneReceiver extends BroadcastReceiver {
        public OutgoingPhoneReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String phone = getResultData();
            String address = PhoneAddressQueryDao.query(phone);
            Toast.makeText(context,address,Toast.LENGTH_LONG).show();

        }
    }

}
