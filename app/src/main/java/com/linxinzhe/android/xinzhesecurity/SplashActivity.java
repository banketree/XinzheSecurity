package com.linxinzhe.android.xinzhesecurity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.linxinzhe.android.xinzhesecurity.utils.StreamTools;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class SplashActivity extends Activity {

    private static final String TAG = "SplashActivity";
    private static final int SHOW_UPDATE_DIALOG = 0;
    private static final int ENTER_HOME = 1;
    private static final int URL_ERROR = 2;
    private static final int NETWORK_ERROR = 3;
    private static final int JSON_ERROR = 4;

    private SharedPreferences sp;

    private TextView mSplashVersionTV;

    private TextView mUpdateProgressTV;
    private ProgressBar mUpdateProgressPB;
    private Button mCancelBTN;

    private String apkFile;
    private Button mOpenUpdatePackageBTN;

    private String description;
    private String apkurl;
    //static暂未实现
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_UPDATE_DIALOG:
                    Log.i(TAG, "显示升级的对话框");
                    showUpdateDialog();
                    break;
                case ENTER_HOME:
                    enterHome();
                    break;
                case URL_ERROR:
                    enterHome();
                    Log.d(TAG, "URL错误");
                    break;
                case NETWORK_ERROR:
                    enterHome();
                    Log.d(TAG, "网络错误");
                    Toast.makeText(SplashActivity.this, "网路异常，无法获取更新", Toast.LENGTH_SHORT).show();
                    break;
                case JSON_ERROR:
                    enterHome();
                    Log.d(TAG, "JSON解析错误");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //显示当前版本号
        mSplashVersionTV = (TextView) findViewById(R.id.tv_splash_version);
        mSplashVersionTV.setText("版本：" + getVersionName());

        //检查设置中心是否启动更新
        sp = getSharedPreferences("config", MODE_PRIVATE);
        if (sp.getBoolean("update", false)) {
            //检查升级
            checkUpdate();
        } else {
            //为了用户体验，缓冲再进入
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    enterHome();
                }
            }, 1 * 1000);
        }
        mUpdateProgressTV = (TextView) findViewById(R.id.tv_update_progress);
        mUpdateProgressPB = (ProgressBar) findViewById(R.id.pb_update_progress);
        //若用户不幸取消安装则提供一个按钮找回安装路径
        mOpenUpdatePackageBTN = (Button) findViewById(R.id.btn_open_update_package);
        mOpenUpdatePackageBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setDataAndType(Uri.fromFile(new File(apkFile)), "application/vnd.android.package-archive");
                startActivity(intent);
            }
        });
        mCancelBTN = (Button) findViewById(R.id.btn_cancel);
        mCancelBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterHome();
            }
        });

        //初始化归属地数据库
        initDatabase("antivirus_kingsoft.db");
        initDatabase("phone_address_mi.db");
    }

    private void initDatabase(String dbPathStr) {
        InputStream is = null;
        FileOutputStream fos = null;
        File dbPath = new File(getFilesDir(), dbPathStr);
        if (dbPath.exists() && dbPath.length() > 0) {
            Log.d(TAG, "无需初始化数据库");
            return;
        } else {
            try {
                is = getAssets().open(dbPathStr);
                fos = new FileOutputStream(dbPath);
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                is.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkUpdate() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();

                Message msg = Message.obtain();
                try {
                    //联网获得版本信息
                    URL apkUrl = new URL(getString(R.string.serverurl));
                    HttpURLConnection connection = (HttpURLConnection) apkUrl.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5 * 1000);
                    connection.setReadTimeout(3 * 1000);
                    int responseCode = connection.getResponseCode();
                    if (responseCode / 100 == 2) {
                        InputStream is = connection.getInputStream();
                        String jsonStr = StreamTools.readFromStream(is);
                        Log.i(TAG, "获取版本更新信息成功" + jsonStr);
                        JSONObject json = new JSONObject(jsonStr);
                        String version = (String) json.get("version");
                        description = (String) json.get("description");
                        apkurl = (String) json.get("apkurl");

                        //检查是否有新版本
                        if (getVersionName().equals(version)) {
                            msg.what = ENTER_HOME;
                        } else {
                            msg.what = SHOW_UPDATE_DIALOG;
                        }
                    }
                } catch (MalformedURLException e) {
                    msg.what = URL_ERROR;
                    e.printStackTrace();
                } catch (IOException e) {
                    msg.what = NETWORK_ERROR;
                    e.printStackTrace();
                } catch (JSONException e) {
                    msg.what = JSON_ERROR;
                    e.printStackTrace();
                } finally {
                    long endTime = System.currentTimeMillis();
                    long durationTime = endTime - startTime;
                    if (durationTime < 1 * 1000) {
                        try {
                            Thread.sleep(1 * 1000 - durationTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }

    private void showUpdateDialog() {
        //弹出对话框，左取消，右升级
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请升级");
        builder.setMessage(description);
        //对话框被取消后也进入主页面
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                enterHome();
            }
        });

        builder.setNegativeButton("下次再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                enterHome();
            }
        });

        builder.setPositiveButton("立刻升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //要有SD卡存在才，下载Apk并替换安装
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    FinalHttp finalHttp = new FinalHttp();
                    apkFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + (new File(apkurl)).getName();
                    finalHttp.download(apkurl, apkFile, new AjaxCallBack<File>() {
                        @Override
                        public void onSuccess(File file) {
                            super.onSuccess(file);
                            //下载成功则安装APK
                            Intent intent = new Intent();
                            intent.setAction("android.intent.action.VIEW");
                            intent.addCategory("android.intent.category.DEFAULT");
                            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                            startActivity(intent);
                            mOpenUpdatePackageBTN.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onFailure(Throwable t, int errorNo, String strMsg) {
                            t.printStackTrace();
                            //下载失败提示用户
                            Toast.makeText(getApplicationContext(), "下载失败，请检查网路", Toast.LENGTH_LONG).show();
                            super.onFailure(t, errorNo, strMsg);
                        }

                        @Override
                        public void onLoading(long count, long current) {
                            super.onLoading(count, current);
                            int progress = (int) (current * 100 / count);
                            mUpdateProgressTV.setVisibility(View.VISIBLE);
                            mUpdateProgressPB.setVisibility(View.VISIBLE);
                            mCancelBTN.setVisibility(View.VISIBLE);
                            mUpdateProgressPB.setProgress(progress);
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "请插入SD卡再进行升级", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.show();
    }

    private void enterHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }


    /**
     * 获得App版本名称
     *
     * @return
     */
    private String getVersionName() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }
}
