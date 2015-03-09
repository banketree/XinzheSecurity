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
    private LocationManager lmGPS;
    private LocationManager lmNET;
    private MyGPSLocationListener listenerGPS;
    private MyNETLocationListener listenerNET;

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
        lmGPS = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        lmNET = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listenerGPS = new MyGPSLocationListener();
        listenerNET = new MyNETLocationListener();
        //给定位提供者设置限制条件
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        lmGPS.getBestProvider(criteria, true);
        lmGPS.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60 * 1000, 50, listenerGPS);
        lmNET.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60 * 1000, 50, listenerNET);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lmGPS.removeUpdates(listenerGPS);
        listenerGPS = null;
        lmNET.removeUpdates(listenerNET);
        listenerNET = null;
    }

    private class MyNETLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            String longitude = "经度:" + location.getLongitude() + "\n";
            String latitude = "纬度:" + location.getLatitude() + "\n";
            String accuracy = "精度:" + location.getAccuracy() + "\n";

            //标准GPS转换成火星坐标
            try {
                InputStream is = getAssets().open("axisoffset.dat");
                ModifyOffset offset = ModifyOffset.getInstance(is);
                PointDouble pointDouble = offset.s2c(new PointDouble(location.getLongitude(), location.getLatitude()));
                longitude = "经度:" + pointDouble.x + "\n";
                latitude = "纬度:" + pointDouble.y + "\n";
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //发短信给安全号码
            SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("lastLocationNET", longitude + latitude + accuracy);
            Log.i(TAG, longitude + latitude + accuracy);
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

    private class MyGPSLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            String longitude = "经度:" + location.getLongitude() + "\n";
            String latitude = "纬度:" + location.getLatitude() + "\n";
            String accuracy = "精度:" + location.getAccuracy() + "\n";

            //标准GPS转换成火星坐标
            try {
                InputStream is = getAssets().open("axisoffset.dat");
                ModifyOffset offset = ModifyOffset.getInstance(is);
                PointDouble pointDouble = offset.s2c(new PointDouble(location.getLongitude(), location.getLatitude()));
                longitude = "经度:" + pointDouble.x + "\n";
                latitude = "纬度:" + pointDouble.y + "\n";
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //发短信给安全号码
            SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("lastLocationGPS", longitude + latitude + accuracy);
            Log.i(TAG, longitude + latitude + accuracy);
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
