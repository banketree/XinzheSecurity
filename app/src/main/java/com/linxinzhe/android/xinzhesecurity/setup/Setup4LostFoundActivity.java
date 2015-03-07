package com.linxinzhe.android.xinzhesecurity.setup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.linxinzhe.android.xinzhesecurity.LostFoundActivity;
import com.linxinzhe.android.xinzhesecurity.R;

public class Setup4LostFoundActivity extends BaseSetupActivity {

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4_lost_found);
        sp=getSharedPreferences("config",MODE_PRIVATE);
    }

    @Override
    public void goNextSetup() {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("configed",true);
        editor.commit();

        Intent intent=new Intent(this,LostFoundActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_in,R.anim.tran_out);
    }

    @Override
    public void goPrevSetup() {
        Intent intent=new Intent(this,Setup3LostFoundActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_prev_in,R.anim.tran_prev_out);
    }
}
