package com.example.ensamble;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class SendSmsActivity extends Activity {
    final int SEND_SMS_PERMISSION_REQUEST_CODE=1;
    EditText Phone,Message;
    Button Send;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        Phone=findViewById(R.id.phn);
        Message=findViewById(R.id.msg);
        Send=findViewById(R.id.btn);
        Send.setEnabled(false);

        if (checkPermission(Manifest.permission.SEND_SMS)) {
            Send.setEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.SEND_SMS},SEND_SMS_PERMISSION_REQUEST_CODE);
        }
    }

    public boolean checkPermission(String permission) {
        int check = ContextCompat.checkSelfPermission(this, permission);
        return(check == PackageManager.PERMISSION_GRANTED);
    }

    public void onSend (View view) {
        String Pho=Phone.getText().toString();
        String Msg=Message.getText().toString();

        if (Pho==null || Pho.length()==0 || Msg==null) {
            return;
        }
        if (checkPermission(Manifest.permission.SEND_SMS)) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(Pho,null,Msg,null, null);
            Toast.makeText(this, "Mensaje enviado",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,"Mensaje no enviado",Toast.LENGTH_SHORT).show();
        }
    }

    public void goToInbox(View view) {
        Intent intent = new Intent(SendSmsActivity.this, ReceiveSmsActivity.class);
        startActivity(intent);
    }
}
