package com.linxinzhe.android.xinzhesecurity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.linxinzhe.android.xinzhesecurity.utils.SmsTools;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;


public class MoreToolsActivity extends ActionBarActivity {
    private static final int BACKUP_SUCCESS = 0;
    private GridView mListHomeGV;
    private MyAdapter mAdapter;

    private static String[] names = {
            "短信备份", "短信还原",
    };
    private static int[] icons = {
            R.mipmap.ic_cloud_upload_black_48dp, R.mipmap.ic_cloud_download_black_48dp,
    };

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==BACKUP_SUCCESS){
                Toast.makeText(MoreToolsActivity.this, (CharSequence) msg.obj, Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_tools);
        mListHomeGV = (GridView) findViewById(R.id.gv_list_home);
        mAdapter = new MyAdapter();
        mListHomeGV.setAdapter(mAdapter);
        mListHomeGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = null;
                switch (position) {
                    case 0:
                        try {
                            SmsTools.backupSms(MoreToolsActivity.this);
                            Toast.makeText(MoreToolsActivity.this, "短信备份成功！", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(MoreToolsActivity.this, "短信备份失败！", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 1:
                        AlertDialog.Builder builder = new AlertDialog.Builder(MoreToolsActivity.this);
                        builder.setTitle("警告");
                        builder.setMessage("还原将把短信恢复到您上次备份时的情况（若短信较多，请耐心等待）");
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                File backUpSmsFile = new File(Environment.getExternalStorageDirectory(), "backupSms.xml");
                                if (backUpSmsFile.exists() && backUpSmsFile.length() > 0) {
                                    final ProgressDialog tmpPD = new ProgressDialog(MoreToolsActivity.this);
                                    tmpPD.setTitle("还原中");
                                    tmpPD.setMessage("若短信较多，请稍安勿躁！");
                                    tmpPD.setIndeterminate(true);
                                    tmpPD.setCancelable(false);
                                    tmpPD.show();
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                SmsTools.restoreSms(MoreToolsActivity.this, true);
                                                Message msg=new Message();
                                                msg.what=BACKUP_SUCCESS;
                                                msg.obj="还原成功！";
                                                mHandler.sendMessage(msg);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                                Toast.makeText(MoreToolsActivity.this, "还原失败！", Toast.LENGTH_SHORT).show();
                                            } catch (XmlPullParserException e) {
                                                e.printStackTrace();
                                                Toast.makeText(MoreToolsActivity.this, "还原失败！", Toast.LENGTH_SHORT).show();
                                            }
                                            tmpPD.dismiss();
                                        }
                                    }).start();
                                } else {
                                    Toast.makeText(MoreToolsActivity.this, "您从未备份过！", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        builder.setNegativeButton("取消", null);
                        builder.show();
                        break;
                }
            }
        });
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return names.length;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(MoreToolsActivity.this, R.layout.list_item_home, null);
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
