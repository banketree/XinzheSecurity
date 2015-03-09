package com.linxinzhe.android.xinzhesecurity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;

import com.linxinzhe.android.xinzhesecurity.service.IncomingPhoneAddressService;
import com.linxinzhe.android.xinzhesecurity.ui.SettingItemView;
import com.linxinzhe.android.xinzhesecurity.utils.ServiceTools;


public class SettingActivity extends ActionBarActivity {

    private SharedPreferences sp;

    private SettingItemView mUpdateSIV;
    private SettingItemView mIncomingPhoneAddressSIV;


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
        //设置来电显示
        mIncomingPhoneAddressSIV = (SettingItemView) findViewById(R.id.siv_incoming_phone_address);
        boolean isServiceRunning= ServiceTools.isExists(SettingActivity.this,"com.linxinzhe.android.xinzhesecurity.service.IncomingPhoneAddressService");
        if (isServiceRunning){
            mIncomingPhoneAddressSIV.setChecked(true);
        }else {
            mIncomingPhoneAddressSIV.setChecked(false);
        }
        mIncomingPhoneAddressSIV.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, IncomingPhoneAddressService.class);
                if (mIncomingPhoneAddressSIV.isChecked()) {
                    mIncomingPhoneAddressSIV.setChecked(false);
                    stopService(intent);
                } else {
                    mIncomingPhoneAddressSIV.setChecked(true);
                    startService(intent);
                }
            }
        });
    }

}
