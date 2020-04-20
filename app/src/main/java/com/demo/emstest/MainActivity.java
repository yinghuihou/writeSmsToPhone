package com.demo.emstest;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnSetting;
    private Button btnSmsSend;
    private Button btnSmsCount;

    private TextView smsCount;
    private TextView smsTip;
    private EditText sendSmsCount;

    private String smsContent = "Pengajuan pinjaman Anda sedang dalam proses pemeriksaan. Kami akan menghubungi Anda untuk validasi data, harap pastikan Anda dapat dihubungi.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSmsSend = findViewById(R.id.btn_send_sms);
        btnSetting = findViewById(R.id.btn_setting);
        btnSmsCount = findViewById(R.id.btn_read_sms);
        smsCount = findViewById(R.id.tv_sms_count);
        sendSmsCount = findViewById(R.id.et_sms);
        smsTip = findViewById(R.id.tv_sms_tip);

        initView();
    }

    private void initView() {
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingSms();
            }
        });

        btnSmsCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smsCount.setText("目前共有 " + getSmsInPhone() + " 条短信");
            }
        });
        btnSmsSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String count = sendSmsCount.getText().toString();
                try {
                    int smsCount = Integer.valueOf(count);
                    for (int i = 0; i < smsCount; i++) {
                        Log.e("xxx", "插入第" + i + "条");
                        smsTip.setText("已经插入了" + (i + 1) + "条");
                        Thread.sleep(10);
                        ContentResolver resolver = getContentResolver();
                        Uri url = Uri.parse("content://sms/");
                        ContentValues values = new ContentValues();
                        values.put("address", "10086");
                        values.put("type", 1);
                        values.put("date", System.currentTimeMillis());
                        values.put("body", smsContent + "第" + (i + 1) + "条信息");
                        resolver.insert(url, values);
                    }
                } catch (Exception e) {
                    Log.e("xxx", e.getMessage());
                }
            }
        });
    }

    private void settingSms() {
        Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, "com.demo.emstest");
        startActivity(intent);
    }

    public String getSmsInPhone() {
        final String SMS_URI_ALL = "content://sms/"; // 所有短信
        int m = 0;
        try {
            Uri uri = Uri.parse(SMS_URI_ALL);
            String[] projection = new String[]{"_id", "address", "person",
                    "body", "date", "type",};
            Cursor cur = getContentResolver().query(uri, projection, null,
                    null, "date desc"); // 获取手机内部短信
            if (cur.moveToFirst()) {
                do {
                    m++;
                } while (cur.moveToNext());

                if (!cur.isClosed()) {
                    cur.close();
                    cur = null;
                }
            }
        } catch (SQLiteException ex) {
            Log.d("SQLiteException", ex.getMessage());
        }

        return m + "";
    }
}
