package com.linxinzhe.android.xinzhesecurity.setup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.linxinzhe.android.xinzhesecurity.R;
import com.linxinzhe.android.xinzhesecurity.ui.SettingItemView;

public class Setup2LostFoundActivity extends BaseSetupActivity {

    private SettingItemView mSimLockSIV;
    private TelephonyManager tm;

    private EditText mSetupPhoneET;

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

        //原3
        mSetupPhoneET = (EditText) findViewById(R.id.et_setup_phone);
        //回显号码
        mSetupPhoneET.setText(sp.getString("safenumber", ""));
    }

    @Override
    public void goNextSetup() {
        //绑定SIM卡才能下一步
        String sim = sp.getString("sim", null);
        if (TextUtils.isEmpty(sim)) {
            Toast.makeText(this, "SIM卡没绑定", Toast.LENGTH_LONG).show();
            return;
        }

        //原3
        String phone = mSetupPhoneET.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "请设置安全号码", Toast.LENGTH_LONG).show();
            return;
        }
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("safenumber", phone);
        editor.commit();

        Intent intent = new Intent(this, Setup3LostFoundActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
    }

    @Override
    public void goPrevSetup() {
        Intent intent = new Intent(this, Setup1LostFoundActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_prev_in, R.anim.tran_prev_out);
    }

    //原3
    public void selectContact(View view) {
        Intent intent = new Intent(this, SelectContactActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        String phone = data.getStringExtra("phone").replace("-", "");
        mSetupPhoneET.setText(phone);
    }
}
