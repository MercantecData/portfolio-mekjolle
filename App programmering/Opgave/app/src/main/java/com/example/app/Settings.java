package com.example.app;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class Settings extends AppCompatActivity {

    public static final String MIME_VIGO6 = "application/x.proces-data.vigo6_nfc";
    private boolean convert = false;
    private Button ButtonInitialize;
    private EditText SerialNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        lockScreenOrientation();

        SerialNumber = findViewById(R.id.editTextSerialnumberSettings);
        ButtonInitialize = findViewById(R.id.buttonInitialize);
        ButtonInitialize.setOnClickListener(ConvertListener);
    }


    private View.OnClickListener ConvertListener = new View.OnClickListener() {
        public void onClick(View v) {

            String editTextSerialNumberSettingsData;
            editTextSerialNumberSettingsData = SerialNumber.getText().toString();

            if (editTextSerialNumberSettingsData.length() == 8) {
                convert = true;
                Intent validDataIntent = new Intent();
                validDataIntent.putExtra("MaxFlowData", 0.0277777777777778f);
                validDataIntent.putExtra("PulseData", 1000.00f);
                validDataIntent.putExtra("Current4mAData", 0.00f);
                validDataIntent.putExtra("Current20mAData", 0.0277777777777778f);
                validDataIntent.putExtra("ActualFlowData", 0.0096305555555556f);
                validDataIntent.putExtra("SerialNumberData", editTextSerialNumberSettingsData);
                validDataIntent.putExtra("BusAddressData", 125);
                setResult(Activity.RESULT_OK,validDataIntent);
                finish();
            }
            else {
                Toast.makeText(Settings.this, "The Serial Number must contain exactly 8 characters!", Toast.LENGTH_LONG).show();
            }

        }
    };


    private void lockScreenOrientation() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

}
