package com.linxinzhe.android.xinzhesecurity.setup;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.linxinzhe.android.xinzhesecurity.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SelectContactActivity extends ActionBarActivity {

    private ListView mContactsLV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);
        //获取联系人
        mContactsLV = (ListView) findViewById(R.id.list_select_contact);
        final List<Map<String, String>> contactData = getContactInfo();
        mContactsLV.setAdapter(new SimpleAdapter(this, contactData, R.layout.contact_item_view, new String[]{"name", "phone"}, new int[]{R.id.tv_name, R.id.tv_phone}));

        //
        mContactsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String phone = contactData.get(position).get("phone");
                Intent phoneData = new Intent();
                phoneData.putExtra("phone", phone);
                setResult(RESULT_OK, phoneData);

                finish();
            }
        });
    }


    /**
     * 读取手机联系人
     *
     * @return 联系人键值对
     */
    private List<Map<String, String>> getContactInfo() {

        List<Map<String, String>> contactList = new ArrayList<>();

        ContentResolver resolver = getContentResolver();
        Uri uriRaw = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri uriData = Uri.parse("content://com.android.contacts/data");
        Cursor cursor = resolver.query(uriRaw, new String[]{"contact_id"}, null, null, null);
        while (cursor.moveToNext()) {
            String contact_id = cursor.getString(0);
            if (contact_id != null) {
                //查询出一个联系人
                Map<String, String> contactMap = new HashMap<>();

                Cursor dataCursor = resolver.query(uriData, new String[]{"data1", "mimetype"}, "contact_id=?", new String[]{contact_id}, null);
                while (dataCursor.moveToNext()) {
                    String data1 = dataCursor.getString(0);
                    String mimetype = dataCursor.getString(1);
                    if ("vnd.android.cursor.item/name".equals(mimetype)) {
                        //联系人姓名
                        contactMap.put("name", data1);
                    } else if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
                        //电话号码
                        contactMap.put("phone", data1);
                    }
                }
                contactList.add(contactMap);
                dataCursor.close();
            }
        }
        cursor.close();
        return contactList;
    }


}
