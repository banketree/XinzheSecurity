package com.linxinzhe.android.xinzhesecurity.setup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.linxinzhe.android.xinzhesecurity.LostFoundActivity;
import com.linxinzhe.android.xinzhesecurity.R;

public class Setup4LostFoundActivity extends BaseSetupActivity {

    private CheckBox mProtectingCB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4_lost_found);
        mProtectingCB= (CheckBox) findViewById(R.id.cb_protecting);
        mProtectingCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    mProtectingCB.setText("开启防盗保护");
                }else{
                    mProtectingCB.setText("未开启防盗保护");
                }
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("protecting",isChecked);
                editor.commit();
            }
        });
        if(sp.getBoolean("protecting",false)){
            mProtectingCB.setText("开启防盗保护");
            mProtectingCB.setChecked(true);
        }else {
            mProtectingCB.setText("未开启防盗保护");
            mProtectingCB.setChecked(false);
        }
    }

    @Override
    public void goNextSetup() {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("configed", true);
        editor.commit();

        Intent intent = new Intent(this, LostFoundActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
    }

    @Override
    public void goPrevSetup() {
        Intent intent = new Intent(this, Setup3LostFoundActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_prev_in, R.anim.tran_prev_out);
    }
}
