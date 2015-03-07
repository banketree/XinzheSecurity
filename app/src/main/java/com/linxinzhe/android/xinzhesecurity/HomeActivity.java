package com.linxinzhe.android.xinzhesecurity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.linxinzhe.android.xinzhesecurity.utils.MD5Utils;


public class HomeActivity extends ActionBarActivity {

    private SharedPreferences sp;

    private GridView mListHomeGV;
    private MyAdapter mAdapter;

    private static String[] names = {
            "手机防盗", "通讯密保", "软件管理",
            "内存管理", "流量统计", "手机杀毒",
            "手机优化", "高级工具", "设置中心"
    };
    private static int[] icons = {
            R.mipmap.ic_screen_lock_portrait_black_36dp, R.mipmap.ic_vpn_key_black_36dp, R.mipmap.ic_dvr_black_36dp,
            R.mipmap.ic_web_black_36dp, R.mipmap.ic_import_export_black_36dp, R.mipmap.ic_security_black_36dp,
            R.mipmap.ic_format_paint_black_36dp, R.mipmap.ic_work_black_36dp, R.mipmap.ic_settings_applications_black_36dp
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sp = getSharedPreferences("config", MODE_PRIVATE);

        mListHomeGV = (GridView) findViewById(R.id.gv_list_home);
        mAdapter = new MyAdapter();
        mListHomeGV.setAdapter(mAdapter);
        mListHomeGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        showLostFindDialog();
                        break;
                    case 8:
                        Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    private void showLostFindDialog() {
        if (isSetupPwd()) {
            showPwdEnterDialog();
        } else {
            showSetupPwdDialog();
        }
    }

    private EditText mSetupPwdET;
    private EditText mSetupConfirmPwdEI;
    private Button mOkBTN;
    private Button mCancelBTN;
    private AlertDialog dialog;

    /**
     * 设置密码的对话框
     */
    private void showSetupPwdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        //自定义对话框
        View view = View.inflate(HomeActivity.this, R.layout.dialog_setup_password, null);
        mSetupPwdET = (EditText) view.findViewById(R.id.et_setup_pwd);
        mSetupConfirmPwdEI = (EditText) view.findViewById(R.id.et_setup_confirm_pwd);
        mOkBTN = (Button) view.findViewById(R.id.btn_ok);
        mOkBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //检查两次输入密码是否一致
                String password = mSetupPwdET.getText().toString().trim();
                String confirmPassword = mSetupConfirmPwdEI.getText().toString().trim();
                //验证密码格式
                if (TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
                    Toast.makeText(HomeActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                //一致则保存
                if (password.equals(confirmPassword)) {
                    //保存密码
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("password", MD5Utils.encrypt(password));
                    editor.commit();
                    dialog.dismiss();
                    //进入手机防盗
                    Intent intent=new Intent(HomeActivity.this,LostFoundActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(HomeActivity.this, "密码不一致", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mCancelBTN = (Button) view.findViewById(R.id.btn_cancel);
        mCancelBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        builder.setView(view);
        dialog = builder.show();
    }

    /**
     * 输入密码进入的对话框
     */
    private void showPwdEnterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        //自定义对话框
        View view = View.inflate(HomeActivity.this, R.layout.dialog_enter_password, null);
        mSetupPwdET = (EditText) view.findViewById(R.id.et_setup_pwd);
        mOkBTN = (Button) view.findViewById(R.id.btn_ok);
        mOkBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = mSetupPwdET.getText().toString().trim();
                String savePassword = sp.getString("password", "");
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(HomeActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                //验证密码
                if (MD5Utils.encrypt(password).equals(savePassword)) {
                    //进入手机防盗页面
                    dialog.dismiss();
                    Intent intent=new Intent(HomeActivity.this,LostFoundActivity.class);
                    startActivity(intent);
                } else {
                    mSetupPwdET.setText("");
                    Toast.makeText(HomeActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mCancelBTN = (Button) view.findViewById(R.id.btn_cancel);
        mCancelBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        builder.setView(view);
        dialog = builder.show();
    }

    private boolean isSetupPwd() {
        String password = sp.getString("password", null);
        return !TextUtils.isEmpty(password);
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return names.length;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(HomeActivity.this, R.layout.list_item_home, null);
            ImageView itemIV = (ImageView) view.findViewById(R.id.iv_item);
            TextView itemTV = (TextView) view.findViewById(R.id.tv_item);
            itemTV.setText(names[position]);
            itemIV.setImageResource(icons[position]);
            return view;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }

}
