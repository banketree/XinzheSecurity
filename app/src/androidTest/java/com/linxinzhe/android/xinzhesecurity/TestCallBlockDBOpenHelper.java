package com.linxinzhe.android.xinzhesecurity;

import android.test.AndroidTestCase;

import com.linxinzhe.android.xinzhesecurity.db.BlockCallDBOpenHelper;

public class TestCallBlockDBOpenHelper extends AndroidTestCase{

    public void testOncreate() throws Exception{
        BlockCallDBOpenHelper openHelper=new BlockCallDBOpenHelper(getContext());
        openHelper.getWritableDatabase();
    }
}
