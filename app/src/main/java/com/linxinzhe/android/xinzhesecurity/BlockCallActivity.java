package com.linxinzhe.android.xinzhesecurity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.linxinzhe.android.xinzhesecurity.db.BlockCallDao;
import com.linxinzhe.android.xinzhesecurity.domain.BlockCallInfo;
import com.linxinzhe.android.xinzhesecurity.service.BlockCallService;
import com.linxinzhe.android.xinzhesecurity.utils.ServiceTools;

import java.util.List;


public class BlockCallActivity extends ActionBarActivity {

    public static final String TAG = "CallSmsSafeActivity";

    private ListView lv_callsms_safe;
    private List<BlockCallInfo> infos;
    private CallSmsSafeAdapter adapter;

    private BlockCallDao dao;

    private TextView mOpenReminderTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_block);
        lv_callsms_safe = (ListView) findViewById(R.id.lv_call_block);

        //初始化拦截名单
        dao = new BlockCallDao(BlockCallActivity.this);
        infos = dao.findAll();

        //设置名单显示
        adapter = new CallSmsSafeAdapter();
        lv_callsms_safe.setAdapter(adapter);

        //提示是否开启拦截
        mOpenReminderTV = (TextView) findViewById(R.id.tv_call_block_reminder);
        boolean isServiceRunning = ServiceTools.isExists(BlockCallActivity.this, "com.linxinzhe.android.xinzhesecurity.service.BlockCallService");
        if (isServiceRunning) {
            mOpenReminderTV.setVisibility(View.GONE);
        } else {
            mOpenReminderTV.setVisibility(View.VISIBLE);
        }
    }

    //Adapter用属性
    static class BlockItemHolder {
        TextView phoneTV;
        TextView modeTV;
        Button deleteBTN;
    }

    //修改信息的对话框用的Field
    private EditText mPhoneET;
    private CheckBox mPhoneCB;
    private CheckBox mSmsCB;
    private Button mAddBTN;
    private Button mCancelBTN;

    private class CallSmsSafeAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return infos.size();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view;
            BlockItemHolder holder;
            if (convertView == null) {
                view = View.inflate(getApplicationContext(), R.layout.list_item_block_phonesms, null);
                holder = new BlockItemHolder();
                holder.phoneTV = (TextView) view.findViewById(R.id.tv_block_phone);
                holder.modeTV = (TextView) view.findViewById(R.id.tv_block_mode);
                holder.deleteBTN = (Button) view.findViewById(R.id.btn_delete);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (BlockItemHolder) view.getTag();//5%
            }
            //显示拦截电话
            holder.phoneTV.setText(infos.get(position).getPhone());

            //显示拦截模式
            String mode = infos.get(position).getMode();
            if ("1".equals(mode)) {
                holder.modeTV.setText("来电拦截");
            } else if ("2".equals(mode)) {
                holder.modeTV.setText("短信拦截");
            } else {
                holder.modeTV.setText("全部拦截");
            }

            //删除电话的按钮
            holder.deleteBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(BlockCallActivity.this);
                    builder.setTitle("删除");
                    builder.setMessage("删除后将不再拦截该号码");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //删除数据库的内容,更新界面。
                            dao.delete(infos.get(position).getPhone());
                            infos.remove(position);
                            adapter.notifyDataSetChanged();
                        }
                    });
                    builder.setNegativeButton("取消", null);
                    builder.show();
                }
            });

            //修改拦截信息
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //弹出修改信息的对话框
                    AlertDialog.Builder builder = new AlertDialog.Builder(BlockCallActivity.this);
                    final AlertDialog dialog = builder.create();
                    View contentView = View.inflate(BlockCallActivity.this, R.layout.dialog_add_block_phone, null);
                    //查询原来电话
                    mPhoneET = (EditText) contentView.findViewById(R.id.et_block_phone);
                    mPhoneET.setText(infos.get(position).getPhone());

                    //查询原来拦截模式
                    mPhoneCB = (CheckBox) contentView.findViewById(R.id.cb_phone);
                    mSmsCB = (CheckBox) contentView.findViewById(R.id.cb_sms);
                    String mode = infos.get(position).getMode();
                    if ("1".equals(mode)) {
                        mPhoneCB.setChecked(true);
                        mSmsCB.setChecked(false);
                    } else if ("2".equals(mode)) {
                        mPhoneCB.setChecked(false);
                        mSmsCB.setChecked(true);
                    } else {
                        mPhoneCB.setChecked(true);
                        mSmsCB.setChecked(true);
                    }
                    mCancelBTN = (Button) contentView.findViewById(R.id.btn_cancel);
                    mCancelBTN.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    mAddBTN = (Button) contentView.findViewById(R.id.btn_add);
                    dialog.setView(contentView, 0, 0, 0, 0);
                    dialog.show();

                    //修改数据库内容并更新UI
                    mAddBTN.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String newPhone = mPhoneET.getText().toString().trim();
                            if (TextUtils.isEmpty(newPhone)) {
                                Toast.makeText(getApplicationContext(), "号码不能为空", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            String mode;
                            if (mPhoneCB.isChecked() && mSmsCB.isChecked()) {
                                //全部拦截
                                mode = "3";
                            } else if (mPhoneCB.isChecked()) {
                                //电话拦截
                                mode = "1";
                            } else if (mSmsCB.isChecked()) {
                                //短信拦截
                                mode = "2";
                            } else {
                                Toast.makeText(getApplicationContext(), "请选择拦截模式", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            //数据被更新到数据库，并更新界面
                            String oldPhone = infos.get(position).getPhone();
                            if (newPhone.equals(oldPhone)) {
                                dao.update(newPhone, mode, oldPhone);
                            } else {
                                dao.update(oldPhone, mode);
                            }
                            BlockCallInfo info = new BlockCallInfo();
                            info.setMode(mode);
                            info.setPhone(newPhone);
                            infos.remove(position);
                            infos.add(position, info);
                            adapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    });
                }
            });
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

    private Menu menu = null;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_call_block, menu);
        if (menu != null) {
            this.menu = menu;
            boolean isServiceRunning = ServiceTools.isExists(BlockCallActivity.this, "com.linxinzhe.android.xinzhesecurity.service.BlockCallService");
            if (isServiceRunning) {
                menu.findItem(R.id.open_block).setTitle("关闭拦截");
            } else {
                menu.findItem(R.id.open_block).setTitle("开启拦截");
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

        //修改信息的对话框
        if (id == R.id.add_phone) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final AlertDialog dialog = builder.create();
            View contentView = View.inflate(this, R.layout.dialog_add_block_phone, null);
            mPhoneET = (EditText) contentView.findViewById(R.id.et_block_phone);
            mPhoneCB = (CheckBox) contentView.findViewById(R.id.cb_phone);
            mSmsCB = (CheckBox) contentView.findViewById(R.id.cb_sms);
            mCancelBTN = (Button) contentView.findViewById(R.id.btn_cancel);
            mAddBTN = (Button) contentView.findViewById(R.id.btn_add);
            dialog.setView(contentView, 0, 0, 0, 0);
            dialog.show();

            mCancelBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            mAddBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phone = mPhoneET.getText().toString().trim();
                    if (TextUtils.isEmpty(phone)) {
                        Toast.makeText(getApplicationContext(), "号码不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String mode;
                    if (mPhoneCB.isChecked() && mSmsCB.isChecked()) {
                        //全部拦截
                        mode = "3";
                    } else if (mPhoneCB.isChecked()) {
                        //电话拦截
                        mode = "1";
                    } else if (mSmsCB.isChecked()) {
                        //短信拦截
                        mode = "2";
                    } else {
                        Toast.makeText(getApplicationContext(), "请选择拦截模式", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //数据被加到数据库，并更新界面
                    dao.add(phone, mode);
                    BlockCallInfo info = new BlockCallInfo();
                    info.setMode(mode);
                    info.setPhone(phone);
                    infos.add(0, info);
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            });
            return true;
        } else if (id == R.id.open_block) {
            if (item.getTitle().equals("关闭拦截")) {
                Intent intent = new Intent(BlockCallActivity.this, BlockCallService.class);
                stopService(intent);
                menu.findItem(id).setTitle("开启拦截");
                mOpenReminderTV.setVisibility(View.VISIBLE);
            } else if (item.getTitle().equals("开启拦截")) {
                Intent intent = new Intent(BlockCallActivity.this, BlockCallService.class);
                startService(intent);
                menu.findItem(id).setTitle("关闭拦截");
                mOpenReminderTV.setVisibility(View.GONE);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
