package com.linxinzhe.android.xinzhesecurity.setup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.linxinzhe.android.xinzhesecurity.R;

public class Setup3LostFoundActivity extends BaseSetupActivity {

    private EditText mSetupPhoneET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3_lost_found);
        mSetupPhoneET= (EditText) findViewById(R.id.et_setup_phone);
        //回显号码
        mSetupPhoneET.setText(sp.getString("safenumber",""));

    }

    public void selectContact(View view) {
        Intent intent = new Intent(this, SelectContactActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    public void goNextSetup() {
        String phone = mSetupPhoneET.getText().toString().trim();
        if (TextUtils.isEmpty(phone)){
            Toast.makeText(this,"请设置安全号码",Toast.LENGTH_LONG).show();
            return;
        }
        SharedPreferences.Editor editor=sp.edit();
        editor.putString("safenumber",phone);
        editor.commit();

        Intent intent = new Intent(this, Setup4LostFoundActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
    }

    @Override
    public void goPrevSetup() {
        Intent intent = new Intent(this, Setup2LostFoundActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_prev_in, R.anim.tran_prev_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data==null){
            return;
        }
        String phone = data.getStringExtra("phone").replace("-", "");
        mSetupPhoneET.setText(phone);
    }
}
