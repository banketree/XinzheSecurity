package com.linxinzhe.android.xinzhesecurity;

import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.UserHandle;
import android.support.v7.app.ActionBarActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;


public class CleanActivity extends ActionBarActivity {

    private static final String TAG = "CleanActivity";
    private PackageManager pm;

    private TextView mTotalCacheTV;
    private TextView mScaningTV;
    private LinearLayout mScanningListLL;

    private Menu cleanMenu;
    private MenuItem cleanMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean);
        mTotalCacheTV = (TextView) findViewById(R.id.tv_cache_size);
        mScaningTV = (TextView) findViewById(R.id.tv_scanning);
        mScanningListLL = (LinearLayout) findViewById(R.id.ll_scanning_list);
        scanCache();
    }

    private long totalCache;

    private void scanCache() {
        pm = getPackageManager();
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
                Method getPackageSizeInfoMethod = null;
                Method[] methods = PackageManager.class.getMethods();
                for (Method method : methods) {
                    if ("getPackageSizeInfo".equals(method.getName())) {
                        getPackageSizeInfoMethod = method;
                    }
                }

                for (PackageInfo packageInfo : installedPackages) {
                    try {
                        Method myUserId = UserHandle.class.getDeclaredMethod("myUserId");
                        int userID = (Integer) myUserId.invoke(pm, null);
                        getPackageSizeInfoMethod.invoke(pm, packageInfo.packageName, userID, new CacheDataObserver());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
//                cleanMenuItem.setVisible(true);
            }
        }).start();
    }

    private class CacheDataObserver extends IPackageStatsObserver.Stub {

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
            final long cache = pStats.cacheSize;
            long code = pStats.codeSize;
            long data = pStats.dataSize;
            Log.d(TAG, "cache:" + cache);
            final String packname = pStats.packageName;
            final ApplicationInfo appInfo;
            try {
                appInfo = pm.getApplicationInfo(packname, 0);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mScaningTV.setText("正在扫描：" + appInfo.loadLabel(pm));
                        if (cache > 0) {
                            View view = View.inflate(CleanActivity.this, R.layout.list_item_app_info, null);
                            ImageView iconTV = (ImageView) view.findViewById(R.id.iv_icon);
                            iconTV.setImageDrawable(appInfo.loadIcon(pm));
                            TextView nameTV = (TextView) view.findViewById(R.id.tv_name);
                            nameTV.setText(appInfo.loadLabel(pm));
                            TextView sizeTV = (TextView) view.findViewById(R.id.tv_size);
                            sizeTV.setText("垃圾大小：" + Formatter.formatFileSize(CleanActivity.this, cache));
                            totalCache += cache;
                            mTotalCacheTV.setText("总垃圾：" + Formatter.formatFileSize(CleanActivity.this, totalCache));
                            Button deleteBTN = (Button) view.findViewById(R.id.btn_uninstall);
                            deleteBTN.setText("清理");
                            deleteBTN.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        Method method = PackageManager.class.getMethod("deleteApplicationCacheFiles", String.class, IPackageDataObserver.class);
                                        method.invoke(pm, packname, new MyPackageDataObserver());
                                    } catch (NoSuchMethodException e) {
                                        e.printStackTrace();
                                    } catch (InvocationTargetException e) {
                                        e.printStackTrace();
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            mScanningListLL.addView(view, 0);
                        }
                    }

                    ;
                });

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private class MyPackageDataObserver extends IPackageDataObserver.Stub {

        @Override
        public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_clean, menu);
//        this.cleanMenu=menu;
//        cleanMenuItem=menu.findItem(R.id.clean);
//        cleanMenuItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.clean) {
            //清理全部缓存
            Method method = null;
            try {
                method = PackageManager.class.getMethod("freeStorageAndNotify", long.class, IPackageDataObserver.class);
                method.invoke(pm, Integer.MAX_VALUE, new MyPackageDataObserver());
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
