package com.example.ensamble;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class ReceiveSmsActivity extends Activity {
    ArrayList<String> smsMessagesList = new ArrayList<>();
    ListView messages;
    ArrayAdapter arrayAdapter;
    SmsManager smsManager = SmsManager.getDefault();
    private static ReceiveSmsActivity inst;
    private static final int READ_SMS_PERMISSIONS_REQUEST = 1;

    public static ReceiveSmsActivity instance() { return inst; }

    public void onStart () {
        super.onStart();
        inst=this;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);
        messages = (ListView) findViewById(R.id.messages);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,smsMessagesList);
        messages.setAdapter(arrayAdapter);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) !=
                PackageManager.PERMISSION_GRANTED) {
            getPermissionToReadSMS();
        } else refreshSmsInbox();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getPermissionToReadSMS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_SMS)) {
                Toast.makeText(this, "Revisa los permisos", Toast.LENGTH_SHORT).show();
            } requestPermissions (new String[]{Manifest.permission.READ_SMS},
                    READ_SMS_PERMISSIONS_REQUEST);
        }
    }

    private void refreshSmsInbox() {
        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"),
                null,null,null,null);
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        if (indexBody < 0 || !smsInboxCursor.moveToFirst()) return; arrayAdapter.clear();
        do {
            String str = "SMS de: "+ smsInboxCursor.getString(indexAddress) + "\n" +
                    smsInboxCursor.getString(indexBody) + "\n";
            arrayAdapter.add(str);
        } while (smsInboxCursor.moveToNext());

    }

    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        if (requestCode == READ_SMS_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso concedido", Toast.LENGTH_SHORT).show();
                refreshSmsInbox();
            } else {
                Toast.makeText(this, "Permiso negado", Toast.LENGTH_SHORT).show();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    public void updateInbox(final String smsMessage) {
        arrayAdapter.insert(smsMessage,0);
        arrayAdapter.notifyDataSetChanged();
    }

    public void goToCompose(View view) {
        Intent intent = new Intent(ReceiveSmsActivity.this, SendSmsActivity.class);
        startActivity(intent);
    }

}
