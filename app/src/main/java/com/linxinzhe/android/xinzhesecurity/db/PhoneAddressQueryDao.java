package com.linxinzhe.android.xinzhesecurity.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class PhoneAddressQueryDao {

    private static String path = "data/data/com.linxinzhe.android.xinzhesecurity/files/phone_address_mi.db";

    /**
     * 查询归属地数据库
     *
     * @param phone
     * @return 没查到则是空字符串
     */
    public static String query(String phone) {
        String address = "数据库暂无此号";
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        //手机号
        if (phone.length() < 7) {
        } else if (phone.matches("^1[34568]\\d{9}$")) {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT location FROM data2 WHERE id = (SELECT outkey FROM data1 WHERE id = ?)", new String[]{phone.substring(0, 7)});
            while (cursor.moveToNext()) {
                address = "归属地：" + cursor.getString(0);
            }
            cursor.close();
        } else {
            if (phone.length() > 10 && phone.startsWith("0")) {
                // 010-12345678
                Cursor cursor = sqLiteDatabase.rawQuery("SELECT location FROM data2 WHERE area = ?", new String[]{phone.substring(1, 3)});

                while (cursor.moveToNext()) {
                    String location = cursor.getString(0);
                    address = "归属地：" + location.substring(0, location.length() - 2);
                }
                cursor.close();

                // 0855-12345678
                cursor = sqLiteDatabase.rawQuery("SELECT location FROM data2 WHERE area = ?", new String[]{phone.substring(1, 4)});
                while (cursor.moveToNext()) {
                    String location = cursor.getString(0);
                    address = "归属地：" + location.substring(0, location.length() - 2);
                }
            }
        }
        return address;
    }
}
