package com.linxinzhe.android.xinzhesecurity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

import com.linxinzhe.android.xinzhesecurity.db.CallBlockDao;
import com.linxinzhe.android.xinzhesecurity.domain.CallBlockInfo;
import com.linxinzhe.android.xinzhesecurity.service.BlockCallService;
import com.linxinzhe.android.xinzhesecurity.utils.ServiceTools;

import java.util.List;


public class BlockCallActivity extends ActionBarActivity {

    public static final String TAG = "CallSmsSafeActivity";
    private ListView lv_callsms_safe;
    private List<CallBlockInfo> infos;
    private CallBlockDao dao;
    private CallSmsSafeAdapter adapter;

    private TextView mOpenReminderTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_block);
        lv_callsms_safe = (ListView) findViewById(R.id.lv_call_block);
        dao = new CallBlockDao(BlockCallActivity.this);
        infos = dao.findAll();
        adapter = new CallSmsSafeAdapter();
        lv_callsms_safe.setAdapter(adapter);

        mOpenReminderTV = (TextView) findViewById(R.id.tv_call_block_reminder);
        boolean isServiceRunning = ServiceTools.isExists(BlockCallActivity.this, "com.linxinzhe.android.xinzhesecurity.service.BlockCallService");
        if (isServiceRunning) {
            mOpenReminderTV.setVisibility(View.GONE);
        } else {
            mOpenReminderTV.setVisibility(View.VISIBLE);
        }
    }

    private class CallSmsSafeAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return infos.size();
        }

        //有多少个条目被显示，这个方法就会被调用多少次
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder holder;
            //1.减少内存中view对象创建的个数
            if (convertView == null) {
                Log.i(TAG, "创建新的view对象：" + position);
                //把一个布局文件转化成  view对象。
                view = View.inflate(getApplicationContext(), R.layout.list_item_block_phonesms, null);
                //2.减少子孩子查询的次数  内存中对象的地址。
                holder = new ViewHolder();
                holder.tv_number = (TextView) view.findViewById(R.id.tv_block_phone);
                holder.tv_mode = (TextView) view.findViewById(R.id.tv_block_mode);
                holder.btn_delete = (Button) view.findViewById(R.id.btn_delete);
                //当孩子生出来的时候找到他们的引用，存放在记事本，放在父亲的口袋
                view.setTag(holder);
            } else {
                Log.i(TAG, "复用历史缓存的view对象：" + position);
                view = convertView;
                holder = (ViewHolder) view.getTag();//5%
            }
            holder.tv_number.setText(infos.get(position).getPhone());
            String mode = infos.get(position).getMode();
            if ("1".equals(mode)) {
                holder.tv_mode.setText("来电拦截");
            } else if ("2".equals(mode)) {
                holder.tv_mode.setText("短信拦截");
            } else {
                holder.tv_mode.setText("全部拦截");
            }
            holder.btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(BlockCallActivity.this);
                    builder.setTitle("删除");
                    builder.setMessage("删除后将不再拦截该号码");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //删除数据库的内容
                            dao.delete(infos.get(position).getPhone());
                            //更新界面。
                            infos.remove(position);
                            //通知listview数据适配器更新
                            adapter.notifyDataSetChanged();
                        }
                    });
                    builder.setNegativeButton("取消", null);
                    builder.show();
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

    /**
     * view对象的容器
     * 记录孩子的内存地址。
     * 相当于一个记事本
     */
    static class ViewHolder {
        TextView tv_number;
        TextView tv_mode;
        Button btn_delete;
    }


    private EditText et_block_phone;
    private CheckBox cb_phone;
    private CheckBox cb_sms;
    private Button bt_ok;
    private Button bt_cancel;

    public void addBlackNumber(View view) {

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.add_phone) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final AlertDialog dialog = builder.create();
            View contentView = View.inflate(this, R.layout.dialog_add_block_phone, null);
            et_block_phone = (EditText) contentView.findViewById(R.id.et_block_phone);
            cb_phone = (CheckBox) contentView.findViewById(R.id.cb_phone);
            cb_sms = (CheckBox) contentView.findViewById(R.id.cb_sms);
            bt_cancel = (Button) contentView.findViewById(R.id.cancel);
            bt_ok = (Button) contentView.findViewById(R.id.ok);
            dialog.setView(contentView, 0, 0, 0, 0);
            dialog.show();
            bt_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            bt_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String blacknumber = et_block_phone.getText().toString().trim();
                    if (TextUtils.isEmpty(blacknumber)) {
                        Toast.makeText(getApplicationContext(), "号码不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String mode;
                    if (cb_phone.isChecked() && cb_sms.isChecked()) {
                        //全部拦截
                        mode = "3";
                    } else if (cb_phone.isChecked()) {
                        //电话拦截
                        mode = "1";
                    } else if (cb_sms.isChecked()) {
                        //短信拦截
                        mode = "2";
                    } else {
                        Toast.makeText(getApplicationContext(), "请选择拦截模式", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //数据被加到数据库
                    dao.add(blacknumber, mode);
                    //更新listview集合里面的内容。
                    CallBlockInfo info = new CallBlockInfo();
                    info.setMode(mode);
                    info.setPhone(blacknumber);
                    infos.add(0, info);
                    //通知listview数据适配器数据更新了。
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
