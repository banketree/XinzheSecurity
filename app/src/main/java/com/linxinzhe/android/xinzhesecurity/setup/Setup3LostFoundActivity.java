package com.linxinzhe.android.xinzhesecurity.setup;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.linxinzhe.android.xinzhesecurity.AntitheftActivity;
import com.linxinzhe.android.xinzhesecurity.R;
import com.linxinzhe.android.xinzhesecurity.receiver.MyAdminReceiver;

public class Setup3LostFoundActivity extends BaseSetupActivity {

    private EditText mLockPswET;

    private CheckBox mProtectingCB;

    private DevicePolicyManager dpm;
    private Button mOpenAdminBTN;
    private Button mCloseAdminBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3_lost_found);

        mLockPswET = (EditText) findViewById(R.id.et_lock_psw);
        mLockPswET.setText(sp.getString("lockpsw", ""));

        //防盗保护是否开启
        mProtectingCB = (CheckBox) findViewById(R.id.cb_protecting);
        mProtectingCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mProtectingCB.setText("开启防盗保护");
                } else {
                    mProtectingCB.setText("未开启防盗保护");
                }
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("protecting", isChecked);
                editor.commit();
            }
        });
        if (sp.getBoolean("protecting", false)) {
            mProtectingCB.setText("开启防盗保护");
            mProtectingCB.setChecked(true);
        } else {
            mProtectingCB.setText("未开启防盗保护");
            mProtectingCB.setChecked(false);
        }

        //配置远程控制权限
        mOpenAdminBTN = (Button) findViewById(R.id.btn_openAdim);
        mOpenAdminBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                ComponentName mDeviceAdminSample = new ComponentName(Setup3LostFoundActivity.this, MyAdminReceiver.class);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "开启后可以远程锁屏和远程销毁数据");
                startActivity(intent);
                mOpenAdminBTN.setVisibility(View.INVISIBLE);
                mCloseAdminBTN.setVisibility(View.VISIBLE);
            }
        });
        mCloseAdminBTN = (Button) findViewById(R.id.btn_closeAdim);
        mCloseAdminBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ComponentName who = new ComponentName(Setup3LostFoundActivity.this, MyAdminReceiver.class);
                dpm = (DevicePolicyManager) Setup3LostFoundActivity.this.getSystemService(Setup3LostFoundActivity.this.DEVICE_POLICY_SERVICE);
                dpm.removeActiveAdmin(who);
                v.setVisibility(View.INVISIBLE);
                mOpenAdminBTN.setVisibility(View.VISIBLE);
                Toast.makeText(Setup3LostFoundActivity.this, "远程控制权限已关闭", Toast.LENGTH_LONG).show();
            }
        });


    }

    @Override
    public void goNextSetup() {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("configed", true);
        editor.commit();

        String lockPsw = mLockPswET.getText().toString().trim();
        if (TextUtils.isEmpty(lockPsw)) {
            Toast.makeText(this, "请设置锁屏密码", Toast.LENGTH_LONG).show();
            return;
        }
        SharedPreferences.Editor editor2 = sp.edit();
        editor2.putString("lockpsw", lockPsw);
        editor2.commit();

        Intent intent = new Intent(this, AntitheftActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
    }

    @Override
    public void goPrevSetup() {
        Intent intent = new Intent(this, Setup2LostFoundActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_prev_in, R.anim.tran_prev_out);
    }
}
