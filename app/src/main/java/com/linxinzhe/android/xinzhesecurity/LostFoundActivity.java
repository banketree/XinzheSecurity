package com.linxinzhe.android.xinzhesecurity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.linxinzhe.android.xinzhesecurity.receiver.MyAdminReceiver;
import com.linxinzhe.android.xinzhesecurity.setup.Setup1LostFoundActivity;


public class LostFoundActivity extends ActionBarActivity {

    private SharedPreferences sp;
    private TextView mSafeNumberTV;
    private CheckBox mProtectingCB;

    private Button mOpenAdminBTN;
    private Button mCloseAdminBTN;

    private DevicePolicyManager dpm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        boolean configed = sp.getBoolean("configed", false);
        if (configed) {
            //设置过则直接进入
            setContentView(R.layout.activity_lost_find);
        } else {
            //若没则进入设置向导
            Intent intent=new Intent(this,Setup1LostFoundActivity.class);
            startActivity(intent);
            finish();
        }

        //设置结果的反映
        mSafeNumberTV= (TextView) findViewById(R.id.tv_safenumber);
        String safenumber = sp.getString("safenumber", "");
        mSafeNumberTV.setText(safenumber);
        mProtectingCB= (CheckBox) findViewById(R.id.cb_protecting);
        boolean protecting = sp.getBoolean("protecting", false);
        mProtectingCB.setChecked(protecting);

        //配置远程控制权限
        mOpenAdminBTN= (Button) findViewById(R.id.btn_openAdim);
        mOpenAdminBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                ComponentName mDeviceAdminSample=new ComponentName(LostFoundActivity.this,MyAdminReceiver.class);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"开启后可以远程锁屏和远程销毁数据");
                startActivity(intent);
                v.setVisibility(View.INVISIBLE);
                mCloseAdminBTN.setVisibility(View.VISIBLE);
            }
        });
        mCloseAdminBTN= (Button) findViewById(R.id.btn_closeAdim);
        mCloseAdminBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ComponentName who=new ComponentName(LostFoundActivity.this,MyAdminReceiver.class);
                dpm = (DevicePolicyManager) LostFoundActivity.this.getSystemService(LostFoundActivity.this.DEVICE_POLICY_SERVICE);
                dpm.removeActiveAdmin(who);
                v.setVisibility(View.INVISIBLE);
                mOpenAdminBTN.setVisibility(View.VISIBLE);
                Toast.makeText(LostFoundActivity.this,"远程控制权限已关闭",Toast.LENGTH_LONG).show();
            }
        });
    }

    public void reEnterSetup(View view){
        Intent intent=new Intent(this,Setup1LostFoundActivity.class);
        startActivity(intent);
        finish();
    }

}
