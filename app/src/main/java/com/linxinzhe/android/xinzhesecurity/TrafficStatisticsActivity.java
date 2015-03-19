package com.linxinzhe.android.xinzhesecurity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.linxinzhe.android.xinzhesecurity.domain.AppInfo;
import com.linxinzhe.android.xinzhesecurity.utils.AppInfoTools;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class TrafficStatisticsActivity extends ActionBarActivity {

    private LinearLayout mLoadingLL;

    private TextView m2gTrafficTV;
    private TextView mTotalTrafficTV;

    private ListView mAppTrafficLL;

    private List<AppInfo> appInfos;
    private AppInfo appInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic_statistics);
        mLoadingLL = (LinearLayout) findViewById(R.id.ll_loading);
        mLoadingLL.setVisibility(View.VISIBLE);
        m2gTrafficTV = (TextView) findViewById(R.id.tv_2g_traffic);
        mTotalTrafficTV = (TextView) findViewById(R.id.tv_total_traffic);
        mAppTrafficLL = (ListView) findViewById(R.id.lv_app_traffic);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    appInfos = (new AppInfoTools()).getAppInfosAndTraffic(TrafficStatisticsActivity.this);
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                Collections.sort(appInfos, new Comparator() {

                    @Override
                    public int compare(Object lhs, Object rhs) {
                        AppInfo app1 = (AppInfo) lhs;
                        long totalTraffic1 = app1.getRxBytes() + app1.getTxBytes();
                        AppInfo app2 = (AppInfo) rhs;
                        long totalTraffic2 = app2.getRxBytes() + app2.getTxBytes();
                        if (totalTraffic1 < totalTraffic2) {
                            return 1;
                        } else if (totalTraffic1 == totalTraffic2) {
                            return 0;
                        } else {
                            return -1;
                        }
                    }
                });

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        long traffic2g = TrafficStats.getMobileRxBytes() + TrafficStats.getMobileTxBytes();
                        long trafficTotal = TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes();
                        m2gTrafficTV.setText("2G/3G总流量：" + Formatter.formatFileSize(TrafficStatisticsActivity.this, traffic2g));
                        mTotalTrafficTV.setText("总流量：" + Formatter.formatFileSize(TrafficStatisticsActivity.this, trafficTotal));
                        mAppTrafficLL.setAdapter(new AppAdapter());
                        mLoadingLL.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }).start();
    }


    private class AppAdapter extends BaseAdapter {

        private ImageView iconIV;
        private TextView nameTV;
        private TextView trafficTV;

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
        public View getView(final int position, View convertView, ViewGroup parent) {
            appInfo = appInfos.get(position);
            View view;
            if (convertView == null) {
                LayoutInflater inflater = TrafficStatisticsActivity.this.getLayoutInflater();
                view = inflater.inflate(R.layout.list_item_app_traffic, null);
            } else {
                view = convertView;
            }

            iconIV = (ImageView) view.findViewById(R.id.iv_icon);
            nameTV = (TextView) view.findViewById(R.id.tv_name);
            trafficTV = (TextView) view.findViewById(R.id.tv_traffic);
            iconIV.setImageDrawable(appInfo.getIcon());
            nameTV.setText(appInfo.getName());
            long appTraffic = appInfo.getRxBytes() + appInfo.getTxBytes();
            trafficTV.setText("消耗：" + Formatter.formatFileSize(TrafficStatisticsActivity.this, appTraffic));
            return view;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_traffic_statistics, menu);
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
