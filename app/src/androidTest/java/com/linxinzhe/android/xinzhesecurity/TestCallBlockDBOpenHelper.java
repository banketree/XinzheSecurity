package com.linxinzhe.android.xinzhesecurity;

import android.test.AndroidTestCase;

import com.linxinzhe.android.xinzhesecurity.db.CallBlockDBOpenHelper;

public class TestCallBlockDBOpenHelper extends AndroidTestCase{

    public void testOncreate() throws Exception{
        CallBlockDBOpenHelper openHelper=new CallBlockDBOpenHelper(getContext());
        openHelper.getWritableDatabase();
    }
}
