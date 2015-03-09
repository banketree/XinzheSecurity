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

import com.linxinzhe.android.xinzhesecurity.MoreTools.PhoneAddressActivity;


public class MoreToolsActivity extends ActionBarActivity {
    private GridView mListHomeGV;
    private MyAdapter mAdapter;

    private static String[] names = {
            "归属地查询", "手机杀毒",
            "手机优化","通讯密保",
    };
    private static int[] icons = {
            R.mipmap.ic_screen_lock_portrait_black_48dp, R.mipmap.ic_security_black_48dp,
            R.mipmap.ic_format_paint_black_48dp,R.mipmap.ic_vpn_key_black_48dp,
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
                Intent intent=null;
                switch (position) {
                    case 0:
                        intent=new Intent(MoreToolsActivity.this, PhoneAddressActivity.class);
                        startActivity(intent);
                        break;

                    case 1:

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
