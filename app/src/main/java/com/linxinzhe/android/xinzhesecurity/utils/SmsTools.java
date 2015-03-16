package com.linxinzhe.android.xinzhesecurity.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;
import android.widget.Toast;

public class SmsTools {

    public interface BackUpCallBack {
        public void beforeBackup(int max);

        public void onSmsBackup(int progress);

    }

    public static void backupSms(Context context) throws Exception {
        ContentResolver resolver = context.getContentResolver();
        File file = new File(Environment.getExternalStorageDirectory(),
                "backupSms.xml");
        FileOutputStream fos = new FileOutputStream(file);
        XmlSerializer serializer = Xml.newSerializer();
        serializer.setOutput(fos, "utf-8");
        serializer.startDocument("utf-8", true);
        serializer.startTag(null, "smss");
        Uri uri = Uri.parse("content://sms/");
        Cursor cursor = resolver.query(uri, new String[]{"body", "address",
                "type", "date"}, null, null, null);
        int max = cursor.getCount();
        serializer.attribute(null, "max", max + "");
        int process = 0;
        while (cursor.moveToNext()) {
//            Thread.sleep(500);
            String body = cursor.getString(0);
            String address = cursor.getString(1);
            String type = cursor.getString(2);
            String date = cursor.getString(3);
            serializer.startTag(null, "sms");

            serializer.startTag(null, "body");
            serializer.text(body);
            serializer.endTag(null, "body");

            serializer.startTag(null, "address");
            serializer.text(address);
            serializer.endTag(null, "address");

            serializer.startTag(null, "type");
            serializer.text(type);
            serializer.endTag(null, "type");

            serializer.startTag(null, "date");
            serializer.text(date);
            serializer.endTag(null, "date");

            serializer.endTag(null, "sms");
            process++;
            // pb.setProgress(process);
            // pd.setProgress(process);
        }
        cursor.close();
        serializer.endTag(null, "smss");
        serializer.endDocument();
        fos.close();
    }

    //做了接口回调功能，方便以后修改
    public static void backupSms(Context context, BackUpCallBack callBack)
            throws Exception {
        ContentResolver resolver = context.getContentResolver();
        File file = new File(Environment.getExternalStorageDirectory(),
                "backupSms.xml");
        FileOutputStream fos = new FileOutputStream(file);
        XmlSerializer serializer = Xml.newSerializer();
        serializer.setOutput(fos, "utf-8");
        serializer.startDocument("utf-8", true);
        serializer.startTag(null, "smss");
        Uri uri = Uri.parse("content://sms/");
        Cursor cursor = resolver.query(uri, new String[]{"body", "address",
                "type", "date"}, null, null, null);
        int max = cursor.getCount();
        // pb.setMax(max);
        // pd.setMax(max);
        callBack.beforeBackup(max);
        serializer.attribute(null, "max", max + "");
        int process = 0;
        while (cursor.moveToNext()) {
            Thread.sleep(500);
            String body = cursor.getString(0);
            String address = cursor.getString(1);
            String type = cursor.getString(2);
            String date = cursor.getString(3);
            serializer.startTag(null, "sms");

            serializer.startTag(null, "body");
            serializer.text(body);
            serializer.endTag(null, "body");

            serializer.startTag(null, "address");
            serializer.text(address);
            serializer.endTag(null, "address");

            serializer.startTag(null, "type");
            serializer.text(type);
            serializer.endTag(null, "type");

            serializer.startTag(null, "date");
            serializer.text(date);
            serializer.endTag(null, "date");

            serializer.endTag(null, "sms");
            process++;
            // pb.setProgress(process);
            // pd.setProgress(process);
            callBack.onSmsBackup(process);
        }
        cursor.close();
        serializer.endTag(null, "smss");
        serializer.endDocument();
        fos.close();
    }

    public static void restoreSms(final Context context, final boolean flag) throws IOException, XmlPullParserException {

        Uri uri = Uri.parse("content://sms/");
        if (flag) {
            context.getContentResolver().delete(uri, null, null);
        }
        ContentValues smsContent = new ContentValues();
        XmlPullParser parser = Xml.newPullParser();
        InputStream is = null;
        is = new FileInputStream(new File(Environment.getExternalStorageDirectory(), "backupSms.xml"));
        parser.setInput(is, "utf-8");
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    switch (parser.getName()) {
                        case "smss":
                        case "sms":
                            break;
                        case "body":
                            smsContent.put("body", parser.nextText());
                            break;
                        case "date":
                            smsContent.put("date", parser.nextText());
                            break;
                        case "type":
                            smsContent.put("type", parser.nextText());
                            break;
                        case "address":
                            smsContent.put("address", parser.nextText());
                            break;
                    }
                    break;
                case XmlPullParser.END_TAG:
                    switch (parser.getName()) {
                        case "sms":
                            context.getContentResolver().insert(uri, smsContent);
                            break;
                    }
                    break;
            }
            eventType = parser.next();
        }


    }
}