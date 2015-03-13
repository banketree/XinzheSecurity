package com.linxinzhe.android.xinzhesecurity.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

/**
 * Created by linxinzhe on 2015/3/11.
 */
public class Md5AntivirusDao {

    public static boolean isVirus(Context context, String md5) {
        boolean isVirusExist = false;

        File file = new File(context.getFilesDir(), "antivirus_kingsoft.db");
        SQLiteDatabase database = SQLiteDatabase.openDatabase(file.toString(), null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = database.rawQuery("SELECT * FROM datable WHERE md5=?", new String[]{md5});
        if (cursor.moveToNext()) {
            isVirusExist = true;
        }
        cursor.close();
        database.close();
        return isVirusExist;
    }
}
