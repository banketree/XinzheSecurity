package com.linxinzhe.android.xinzhesecurity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
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

import com.linxinzhe.android.xinzhesecurity.utils.MD5Tools;


public class HomeActivity extends ActionBarActivity {

    private SharedPreferences sp;

    private GridView mListHomeGV;
    private MyAdapter mAdapter;

    private static String[] names = {
            "手机防盗", "手机杀毒",
            "骚扰拦截", "APP管理",
            "来电查询","百宝箱"
    };
    private static int[] icons = {
            R.mipmap.ic_screen_lock_portrait_black_48dp, R.mipmap.ic_security_black_48dp,
            R.mipmap.ic_phone_missed_black_48dp, R.mipmap.ic_dvr_black_48dp,
            R.mipmap.ic_call_black_48dp,R.mipmap.ic_work_black_48dp,
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
                Intent intent = null;
                switch (position) {
                    //手机防盗
                    case 0:
                        showLostFindDialog();
                        break;
                    //手机杀毒
                    case 1:
                        intent = new Intent(HomeActivity.this, AntivirusActivity.class);
                        startActivity(intent);
                        break;
                    //拦截骚扰
                    case 2:
                        intent = new Intent(HomeActivity.this, BlockCallActivity.class);
                        startActivity(intent);
                        break;
                    //APP管理
                    case 3:
                        intent = new Intent(HomeActivity.this, AppManagerActivity.class);
                        startActivity(intent);
                        break;
                    //来电查询
                    case 4:
                        intent = new Intent(HomeActivity.this, PhoneAddressActivity.class);
                        startActivity(intent);
                        break;
                    //高级工具
                    case 5:
                        intent = new Intent(HomeActivity.this, MoreToolsActivity.class);
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
                    editor.putString("password", MD5Tools.encrypt(password));
                    editor.commit();
                    dialog.dismiss();
                    //进入手机防盗
                    Intent intent = new Intent(HomeActivity.this, LostFoundActivity.class);
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
                if (MD5Tools.encrypt(password).equals(savePassword)) {
                    //进入手机防盗页面
                    dialog.dismiss();
                    Intent intent = new Intent(HomeActivity.this, LostFoundActivity.class);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
