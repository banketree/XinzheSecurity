package com.linxinzhe.android.xinzhesecurity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.linxinzhe.android.xinzhesecurity.db.PhoneAddressQueryDao;
import com.linxinzhe.android.xinzhesecurity.service.IncomingPhoneAddressService;
import com.linxinzhe.android.xinzhesecurity.utils.ServiceTools;

public class PhoneAddressActivity extends ActionBarActivity {

    private EditText mPhoneET;
    private Button mQueryBTN;
    private TextView mAddressTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_address);
        //输入时即使查询
        mPhoneET = (EditText) findViewById(R.id.et_phone);
        mPhoneET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s!=null) {
                    String address=PhoneAddressQueryDao.query(s.toString());
                    mAddressTV.setText(address);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mQueryBTN = (Button) findViewById(R.id.btn_query);
        mQueryBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = mPhoneET.getText().toString().trim();
                if (!TextUtils.isEmpty(phone)) {
                    String address = PhoneAddressQueryDao.query(phone);
                    mAddressTV.setText("归属地：" + address);
                } else {
                    Toast.makeText(PhoneAddressActivity.this, "号码不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mAddressTV = (TextView) findViewById(R.id.tv_phone_address);
    }


    private Menu menu = null;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_phone_address, menu);
        if (menu!=null){
            this.menu=menu;
            boolean isServiceRunning= ServiceTools.isExists(PhoneAddressActivity.this, "com.linxinzhe.android.xinzhesecurity.service.IncomingPhoneAddressService");
            if (isServiceRunning){
                menu.findItem(R.id.open_incoming_phone_address).setTitle("关闭来电自动查询");
            }else {
                menu.findItem(R.id.open_incoming_phone_address).setTitle("开启来电自动查询");
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.open_incoming_phone_address) {
            if (item.getTitle().equals("关闭来电自动查询")) {
                Intent intent = new Intent(PhoneAddressActivity.this, IncomingPhoneAddressService.class);
                stopService(intent);
                menu.findItem(id).setTitle("开启来电自动查询");
            }else if (item.getTitle().equals("开启来电自动查询")){
                Intent intent = new Intent(PhoneAddressActivity.this, IncomingPhoneAddressService.class);
                startService(intent);
                menu.findItem(id).setTitle("关闭来电自动查询");
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
