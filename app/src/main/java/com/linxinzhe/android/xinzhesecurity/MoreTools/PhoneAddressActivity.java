package com.linxinzhe.android.xinzhesecurity.MoreTools;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.linxinzhe.android.xinzhesecurity.R;
import com.linxinzhe.android.xinzhesecurity.db.PhoneAddressQueryDao;

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


}
