package com.majorproject.secrypt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int RESULT_PICK_CONTACT =1;
    EditText pNumber, message;
    ImageButton contacts;
    Button myMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pNumber = (EditText) findViewById(R.id.editTextPhone);
        message = (EditText) findViewById(R.id.editTextMessage);
        contacts = findViewById(R.id.imageContact);
        myMessages = findViewById(R.id.receivedButton);

        myMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ReceiverActivity.class);
                startActivity(intent);
            }
        });
        
        
        contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent (Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult (in, RESULT_PICK_CONTACT);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_PICK_CONTACT:
                    contactPicked(data);
                    break;
            }
        } else {
            Toast.makeText(this, "Failed To pick contact", Toast.LENGTH_SHORT).show();
        }
    }

    private void contactPicked(Intent data) {
        Cursor cursor = null;

        try {
            String phoneNo = null;
            Uri uri = data.getData ();
            cursor = getContentResolver ().query (uri, null, null,null,null);
            cursor.moveToFirst ();
            int phoneIndex = cursor.getColumnIndex (ContactsContract.CommonDataKinds.Phone.NUMBER);

            phoneNo = cursor.getString (phoneIndex);

            pNumber.setText (phoneNo);


        } catch (Exception e) {
            e.printStackTrace ();
        }
    }

    public void send(View view)
    {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);

        if(permissionCheck == PackageManager.PERMISSION_GRANTED)
            MyMessage();
        else
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.SEND_SMS}, 0);
    }

    public void MyMessage()
    {
        String phoneNumber = pNumber.getText().toString().trim();
        String sendMessage = message.getText().toString().trim();


        if(phoneNumber.equals("") || sendMessage.equals(""))
            Toast.makeText(this,"Enter valid credentials", Toast.LENGTH_LONG).show();
        else{
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, sendMessage, null, null);
            Toast.makeText(this, "Message Sent", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case 0:
                if(grantResults.length >= 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    MyMessage();
                else
                    Toast.makeText(this, "You don't have required permissions", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}