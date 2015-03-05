package com.linxinzhe.android.xinzhesecurity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;


public class HomeActivity extends ActionBarActivity {

    private GridView mListHomeGV;
    private MyAdapter mAdapter;

    private static String[] names={
            "手机防盗","通讯密保","软件管理",
            "内存管理","流量统计","手机杀毒",
            "手机优化","高级工具","设置中心"
    };
    private static int[] icons={
            R.mipmap.ic_screen_lock_portrait_black_36dp,R.mipmap.ic_vpn_key_black_36dp,R.mipmap.ic_dvr_black_36dp,
            R.mipmap.ic_web_black_36dp,R.mipmap.ic_import_export_black_36dp,R.mipmap.ic_security_black_36dp,
            R.mipmap.ic_format_paint_black_36dp,R.mipmap.ic_work_black_36dp,R.mipmap.ic_settings_applications_black_36dp
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mListHomeGV= (GridView) findViewById(R.id.gv_list_home);
        mAdapter=new MyAdapter();
        mListHomeGV.setAdapter(mAdapter);
        mListHomeGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 8:
                        Intent intent=new Intent(HomeActivity.this,SettingActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    private class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return names.length;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(HomeActivity.this, R.layout.list_item_home,null);
            ImageView itemIV = (ImageView) view.findViewById(R.id.iv_item);
            TextView itemTV= (TextView) view.findViewById(R.id.tv_item);
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
