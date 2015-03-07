package com.linxinzhe.android.xinzhesecurity;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;

import com.linxinzhe.android.xinzhesecurity.ui.SettingItemView;


public class SettingActivity extends ActionBarActivity {

    private SettingItemView mUpdateSIV;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        sp = getSharedPreferences("config", MODE_PRIVATE);
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
    }

}
