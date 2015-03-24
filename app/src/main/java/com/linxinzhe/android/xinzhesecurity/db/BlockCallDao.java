package com.linxinzhe.android.xinzhesecurity.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.linxinzhe.android.xinzhesecurity.domain.BlockCallInfo;

import java.util.ArrayList;
import java.util.List;

public class BlockCallDao {

    private static final String TAG = "BlockCallDao";
    private BlockCallDBOpenHelper helper;

    public BlockCallDao(Context context) {
        helper = new BlockCallDBOpenHelper(context);
    }

    /**
     * 初始化黑名单用
     *
     * @return
     */
    public List<BlockCallInfo> findAll() {
        List<BlockCallInfo> result = new ArrayList<BlockCallInfo>();
        SQLiteDatabase db = helper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("SELECT phone,mode FROM blockphone ORDER BY _id desc", null);
            BlockCallInfo info;
            while (cursor.moveToNext()) {
                info = new BlockCallInfo();
                info.setPhone(cursor.getString(0));
                info.setMode(cursor.getString(1));
                result.add(info);
            }
            cursor.close();
            db.close();
        }
        return result;
    }

    public BlockCallInfo findInfo(String phone, String mode) {
        BlockCallInfo info = null;
        SQLiteDatabase db = helper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("SELECT phone,mode FROM blockphone WHERE phone=?,mode=?", new String[]{phone, mode});
            while (cursor.moveToNext()) {
                info = new BlockCallInfo();
                info.setPhone(cursor.getString(0));
                info.setMode(cursor.getString(1));
            }
            cursor.close();
            db.close();
        }
        return info;
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
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("SELECT * FROM blockphone WHERE phone=?", new String[]{phone});
            if (cursor.moveToNext()) {
                result = true;
            }
            cursor.close();
            db.close();
        }
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
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("SELECT mode FROM blockphone WHERE phone=?", new String[]{phone});
            if (cursor.moveToNext()) {
                result = cursor.getString(0);
            }
            cursor.close();
            db.close();
        }
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
        if (db.isOpen()) {
            db.execSQL("INSERT INTO blockphone VALUES (null,?,?)", new Object[]{phone, mode});
            db.close();
        } else {
            db.close();
        }
    }

    /**
     * 修改黑名单号码的拦截模式
     *
     * @param phone   要修改的黑名单号码
     * @param newmode 新的拦截模式
     */
    public void update(String phone, String newmode) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL("UPDATE blockphone SET mode=? WHERE phone=?", new String[]{newmode, phone});
            db.close();
        }
    }

    public void update(String newPhone, String newmode, String phone) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            if (phone == newPhone) {
                db.execSQL("UPDATE blockphone SET mode=? WHERE phone=?", new String[]{newmode, phone});
            } else {
                db.execSQL("UPDATE blockphone SET phone=?,mode=?, WHERE phone=?", new String[]{newPhone, newmode, phone});
            }
            db.close();
        }
    }

    /**
     * 删除黑名单号码
     *
     * @param phone 黑名单号码
     */
    public void delete(String phone) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL("DELETE FROM blockphone WHERE phone=?", new Object[]{phone});
            db.close();
        }
    }
}
