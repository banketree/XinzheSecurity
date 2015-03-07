package com.linxinzhe.android.xinzhesecurity.setup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.linxinzhe.android.xinzhesecurity.R;
import com.linxinzhe.android.xinzhesecurity.ui.SettingItemView;

public class Setup2LostFoundActivity extends BaseSetupActivity {

    private SettingItemView mSimLockSIV;
    private TelephonyManager tm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2_lost_found);
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        mSimLockSIV = (SettingItemView) findViewById(R.id.siv_sim_lock);
        if (TextUtils.isEmpty(sp.getString("sim", null))) {
            mSimLockSIV.setChecked(false);
        } else {
            mSimLockSIV.setChecked(true);
        }
        mSimLockSIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sp.edit();
                if (mSimLockSIV.isChecked()) {
                    mSimLockSIV.setChecked(false);
                    editor.putString("sim", null);
                } else {
                    mSimLockSIV.setChecked(true);
                    String sim = tm.getSimSerialNumber();
                    editor.putString("sim", sim);
                }
                editor.commit();
            }
        });
    }

    @Override
    public void goNextSetup() {
        //绑定SIM卡才能下一步
        String sim = sp.getString("sim", null);
        if (TextUtils.isEmpty(sim)) {
            Toast.makeText(this, "SIM卡没绑定", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(this, Setup3LostFoundActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
    }

    @Override
    public void goPrevSetup() {
        Intent intent = new Intent(this, Setup1LostFoundActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_prev_in, R.anim.tran_prev_out);
    }

}
