package com.linxinzhe.android.xinzhesecurity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.linxinzhe.android.xinzhesecurity.ui.SettingItemView;


public class SettingActivity extends ActionBarActivity {

    private SharedPreferences sp;

    private SettingItemView mUpdateSIV;
    private SettingItemView mIncomingPhoneAddressSIV;

    private SettingItemView mCallBlockSTV;

    private boolean isServiceRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        sp = getSharedPreferences("config", MODE_PRIVATE);

        //设置自动更新
        mUpdateSIV = (SettingItemView) findViewById(R.id.siv_update);
        boolean update = sp.getBoolean("update", false);
        if (update) {
            mUpdateSIV.setChecked(true);
        } else {
            mUpdateSIV.setChecked(false);
        }
        mUpdateSIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sp.edit();
                //自动升级开关
                if (mUpdateSIV.isChecked()) {
                    mUpdateSIV.setChecked(false);
                    editor.putBoolean("update", false);
                } else {
                    mUpdateSIV.setChecked(true);
                    editor.putBoolean("update", true);
                }
                editor.commit();
            }
        });
//        //设置来电显示
//        mIncomingPhoneAddressSIV = (SettingItemView) findViewById(R.id.siv_incoming_phone_address);
//        isServiceRunning = ServiceTools.isExists(SettingActivity.this,"com.linxinzhe.android.xinzhesecurity.service.IncomingPhoneAddressService");
//        if (isServiceRunning){
//            mIncomingPhoneAddressSIV.setChecked(true);
//        }else {
//            mIncomingPhoneAddressSIV.setChecked(false);
//        }
//        mIncomingPhoneAddressSIV.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(SettingActivity.this, IncomingPhoneAddressService.class);
//                if (mIncomingPhoneAddressSIV.isChecked()) {
//                    mIncomingPhoneAddressSIV.setChecked(false);
//                    stopService(intent);
//                } else {
//                    mIncomingPhoneAddressSIV.setChecked(true);
//                    startService(intent);
//                }
//            }
//        });

//        //黑名单拦截设置
//        mCallBlockSTV = (SettingItemView) findViewById(R.id.siv_call_block);
//        isServiceRunning= ServiceTools.isExists(SettingActivity.this,"com.linxinzhe.android.xinzhesecurity.service.BlockCallService");
//        if (isServiceRunning){
//            mCallBlockSTV.setChecked(true);
//        }else {
//            mCallBlockSTV.setChecked(false);
//        }
//        mCallBlockSTV.setOnClickListener(new View.OnClickListener() {
//            Intent  intent = new Intent(SettingActivity.this, BlockCallService.class);
//            @Override
//            public void onClick(View v) {
//                if (mCallBlockSTV.isChecked()) {
//                    // 变为非选中状态
//                    mCallBlockSTV.setChecked(false);
//                    stopService(intent);
//                } else {
//                    // 选择状态
//                    mCallBlockSTV.setChecked(true);
//                    startService(intent);
//                }
//
//            }
//        });
    }

}
