package com.linxinzhe.android.xinzhesecurity.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.linxinzhe.android.xinzhesecurity.utils.ModifyOffset;
import com.linxinzhe.android.xinzhesecurity.utils.PointDouble;

import java.io.IOException;
import java.io.InputStream;

public class GPSService extends Service {
    private static final String TAG = "GPSService";
    private LocationManager lm;
    private MyLocationListener listener;

    public GPSService() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //A-GPS定位
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();
        //给定位提供者设置限制条件
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        lm.getBestProvider(criteria, true);
        lm.requestLocationUpdates("gps", 60 * 1000, 50, listener);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lm.removeUpdates(listener);
        listener = null;
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            String longitude = "longitude:" + location.getLongitude() + "\n";
            String latitude = "latitude:" + location.getLatitude() + "\n";
            String accuracy = "accuracy:" + location.getAccuracy() + "\n";

            //标准GPS转换成火星坐标
            try {
                InputStream is = getAssets().open("axisoffset.dat");
                ModifyOffset offset = ModifyOffset.getInstance(is);
                PointDouble pointDouble = offset.s2c(new PointDouble(location.getLongitude(), location.getLatitude()));
                longitude = "longitude:" + pointDouble.x + "\n";
                latitude = "latitude:" + pointDouble.y + "\n";
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //发短信给安全号码
            SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("lastLocation", longitude + latitude + accuracy);
            Log.i(TAG, longitude + latitude + accuracy );
            editor.commit();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}
