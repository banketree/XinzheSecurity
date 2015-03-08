package com.linxinzhe.android.xinzhesecurity.setup;

import android.content.Intent;
import android.os.Bundle;

import com.linxinzhe.android.xinzhesecurity.R;


public class Setup1LostFoundActivity extends BaseSetupActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup1_lost_found);
    }

    @Override
    public void goNextSetup() {
        Intent intent = new Intent(this, Setup2LostFoundActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
    }

    @Override
    public void goPrevSetup() {

    }
}
