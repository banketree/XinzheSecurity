package com.linxinzhe.android.xinzhesecurity.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.linxinzhe.android.xinzhesecurity.domain.CallBlockInfo;

import java.util.ArrayList;
import java.util.List;

public class CallBlockDao {

    private static final String TAG = "CallBlockDao";
    private CallBlockDBOpenHelper helper;

    /**
     * 构造方法
     *
     * @param context 上下文
     */
    public CallBlockDao(Context context) {
        helper = new CallBlockDBOpenHelper(context);
    }

    /**
     * 查询黑名单号码是是否存在
     *
     * @param phone
     * @return
     */
    public boolean find(String phone) {
        boolean result = false;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM blockphone WHERE phone=?", new String[]{phone});
        if (cursor.moveToNext()) {
            result = true;
        }
        cursor.close();
        db.close();
        return result;
    }

    /**
     * 查询黑名单号码的拦截模式
     *
     * @param phone
     * @return 返回号码的拦截模式，不是黑名单号码返回null
     */
    public String findMode(String phone) {
        if (phone.length() > 11 && phone.matches("^[\\+]86\\d+")) {
            phone = phone.substring(3, phone.length());
        }
        Log.i(TAG, phone);
        String result = null;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT mode FROM blockphone WHERE phone=?", new String[]{phone});
        if (cursor.moveToNext()) {
            result = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return result;
    }

    /**
     * 查询全部黑名单号码
     *
     * @return
     */
    public List<CallBlockInfo> findAll() {
        List<CallBlockInfo> result = new ArrayList<CallBlockInfo>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT phone,mode FROM blockphone ORDER BY _id desc", null);
        while (cursor.moveToNext()) {
            CallBlockInfo info = new CallBlockInfo();
            String phone = cursor.getString(0);
            String mode = cursor.getString(1);
            info.setMode(mode);
            info.setPhone(phone);
            result.add(info);
        }
        cursor.close();
        db.close();
        return result;
    }


    /**
     * 添加黑名单号码
     *
     * @param phone 黑名单号码
     * @param mode  拦截模式 1.电话拦截 2.短信拦截 3.全部拦截
     */
    public void add(String phone, String mode) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("phone", phone);
        values.put("mode", mode);
        db.insert("blockphone", null, values);
        db.close();
    }

    /**
     * 修改黑名单号码的拦截模式
     *
     * @param phone   要修改的黑名单号码
     * @param newmode 新的拦截模式
     */
    public void update(String phone, String newmode) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("mode", newmode);
        db.update("blockphone", values, "phone=?", new String[]{phone});
        db.close();
    }

    /**
     * 删除黑名单号码
     *
     * @param phone 要删除的黑名单号码
     */
    public void delete(String phone) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("blockphone", "phone=?", new String[]{phone});
        db.close();
    }
}
