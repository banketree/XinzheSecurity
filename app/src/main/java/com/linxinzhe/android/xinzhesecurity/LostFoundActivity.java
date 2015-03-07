package com.linxinzhe.android.xinzhesecurity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;

import com.linxinzhe.android.xinzhesecurity.setup.Setup1LostFoundActivity;


public class LostFoundActivity extends ActionBarActivity {

    private SharedPreferences sp;

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
    }

    public void reEnterSetup(View view){
        Intent intent=new Intent(this,Setup1LostFoundActivity.class);
        startActivity(intent);
        finish();
    }
}
