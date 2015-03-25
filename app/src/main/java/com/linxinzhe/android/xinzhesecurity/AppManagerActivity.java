package com.linxinzhe.android.xinzhesecurity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.os.StatFs;
import android.support.v7.app.ActionBarActivity;
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
import java.util.List;


public class AppManagerActivity extends ActionBarActivity {

    private TextView mAppCountTV;
    private TextView mAvailableMemoryTV;

    private LinearLayout mLoadingLL;
    private ListView mAppLV;

    private List<AppInfo> appInfos;
    private AppInfo appInfo = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);
        mAppCountTV = (TextView) findViewById(R.id.tv_app_count);
        mAvailableMemoryTV = (TextView) findViewById(R.id.tv_available_memory);
        StatFs statFs = new StatFs(Environment.getDataDirectory().getAbsolutePath());
        long localMemory = statFs.getAvailableBlocks() * statFs.getBlockSize();
        mAvailableMemoryTV.setText("手机剩余空间：" + Formatter.formatFileSize(this, localMemory));

        mLoadingLL = (LinearLayout) findViewById(R.id.ll_loading);
        mLoadingLL.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    appInfos = (new AppInfoTools()).getAppInfos(AppManagerActivity.this);
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAppCountTV.setText("已安装App：" + appInfos.size() + "个");
                        mAppLV.setAdapter(new AppAdapter());
                        mLoadingLL.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }).start();

        mAppLV = (ListView) findViewById(R.id.lv_app);
    }


    private class AppAdapter extends BaseAdapter {

        private ImageView iconIV;
        private TextView nameTV;
        private TextView sizeTV;
        private Button mUnstallBTN;

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
                LayoutInflater inflater = AppManagerActivity.this.getLayoutInflater();
                view = inflater.inflate(R.layout.list_item_app_info, null);
            } else {
                view = convertView;
            }

            iconIV = (ImageView) view.findViewById(R.id.iv_icon);
            nameTV = (TextView) view.findViewById(R.id.tv_name);
            sizeTV = (TextView) view.findViewById(R.id.tv_size);
            mUnstallBTN = (Button) view.findViewById(R.id.btn_uninstall);

            final String packname = appInfo.getPackname();
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PackageManager pm = getPackageManager();
                    Intent intent = pm.getLaunchIntentForPackage(packname);
                    if (intent != null) {
                        startActivity(intent);
                    } else {
                        Toast.makeText(AppManagerActivity.this, "该App是后台服务，无界面", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            iconIV.setImageDrawable(appInfo.getIcon());
            nameTV.setText(appInfo.getName());

            if (appInfo.isUserApp()) {
                sizeTV.setText("用户App：" + Formatter.formatFileSize(AppManagerActivity.this, appInfo.getMemory()));
                mUnstallBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        intent.setAction("android.intent.action.DELETE");
                        intent.addCategory("android.intent.category.DEFAULT");
                        intent.setData(Uri.parse("package:" + packname));
                        startActivityForResult(intent, 0);
                    }
                });
            } else {
                sizeTV.setText("系统App：" + Formatter.formatFileSize(AppManagerActivity.this, appInfo.getMemory()));
                mUnstallBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(AppManagerActivity.this, "系统APP无法卸载", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return view;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mLoadingLL.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    appInfos = (new AppInfoTools()).getAppInfos(AppManagerActivity.this);
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAppCountTV.setText("已安装App：" + appInfos.size() + "个");
                        mAppLV.setAdapter(new AppAdapter());
                        mLoadingLL.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }).start();
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
