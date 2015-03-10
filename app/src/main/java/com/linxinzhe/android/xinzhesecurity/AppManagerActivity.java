package com.linxinzhe.android.xinzhesecurity;

import android.os.Environment;
import android.os.StatFs;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.linxinzhe.android.xinzhesecurity.domain.AppInfo;
import com.linxinzhe.android.xinzhesecurity.utils.AppInfoTools;

import java.util.List;


public class AppManagerActivity extends ActionBarActivity {

    private TextView mAvailableMemoryTV;

    private LinearLayout mLoadingLL;
    private ListView mAppLV;

    private List<AppInfo> appInfos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);
        mAvailableMemoryTV= (TextView) findViewById(R.id.tv_available_memory);
        StatFs statFs=new StatFs(Environment.getDataDirectory().getAbsolutePath());
        int localMemory = statFs.getAvailableBlocks() * statFs.getBlockSize();
        mAvailableMemoryTV.setText("手机剩余空间："+Formatter.formatFileSize(this, localMemory));

        mAppLV= (ListView) findViewById(R.id.lv_app);
        mLoadingLL= (LinearLayout) findViewById(R.id.ll_loading);
        mLoadingLL.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                appInfos= AppInfoTools.getAppInfos(AppManagerActivity.this);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAppLV.setAdapter(new AppAdapter());
                        mLoadingLL.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }).start();
    }




    private class AppAdapter extends BaseAdapter{

        private ImageView iconIV;
        private TextView nameTV;

        @Override
        public int getCount() {
            return appInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView==null){
                LayoutInflater inflater = AppManagerActivity.this.getLayoutInflater();
                view = inflater.inflate(R.layout.list_item_app_info, null);
            }else {
                view=convertView;
            }
            iconIV= (ImageView) view.findViewById(R.id.iv_icon);
            nameTV= (TextView) view.findViewById(R.id.tv_name);
            AppInfo appInfo=appInfos.get(position);
            iconIV.setImageDrawable(appInfo.getIcon());
            nameTV.setText(appInfo.getName());
            return view;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_app_manager, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
