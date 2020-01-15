package com.example.app;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView TextViewUnit1;
    private TextView TextViewUnit2;
    private TextView TextViewUnit3;
    private TextView TextViewUnit4;
    private TextView TextViewUnit5;
    private TextView TextViewHitBox;
    private Button buttonwrite;
    private Button buttonread;
    private EditText editTextMaxflowrate;
    private EditText editTextBusaddress;
    private EditText editTextPulseoutput;
    private EditText editTextCurrentoutput4mA;
    private EditText editTextCurrentoutput20mA;
    private EditText editTextSerialnumber;
    private EditText editTextActualflow;
    private int TimeUnitIndex = -1;
    private int VolumeUnitIndex = -1;
    private int iBus = 0;
    private float fMax = 0.0f;
    private float iPulse = 0;
    private float fCurrent4mA = 0.0f;
    private float fCurrent20mA = 0.0f;
    private float fActual = 0.0f;
    private int startupcounter = 0;
    private boolean editflag = false;
    IntentFilter[] intentFiltersArray;
    String [] [] techListsArray;
    PendingIntent pendingIntent;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

            editTextMaxflowrate.clearFocus();
            editTextPulseoutput.clearFocus();
            editTextCurrentoutput4mA.clearFocus();
            editTextCurrentoutput20mA.clearFocus();
            editTextActualflow.clearFocus();
            editTextBusaddress.clearFocus();
            editTextSerialnumber.clearFocus();
        }
        return super.dispatchTouchEvent(ev);
    }


    public boolean onCreateOptionsMenu(Menu manu){
        getMenuInflater().inflate(R.menu.main, manu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id==R.id.id_exit) {

            return true;
        }
        if(id==R.id.id_setting) {

            Intent intentSettings = new Intent(MainActivity.this,Settings.class);
            startActivityForResult(intentSettings , 40);
        }
        return true;
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextViewUnit1 = (TextView) findViewById(R.id.textViewUnit1);
        TextViewUnit2 = (TextView) findViewById(R.id.textViewUnit2);
        TextViewUnit3 = (TextView) findViewById(R.id.textViewUnit3);
        TextViewUnit4 = (TextView) findViewById(R.id.textviewUnit4);
        TextViewUnit5 = (TextView) findViewById(R.id.textViewUnit5);
        TextViewHitBox = (TextView) findViewById(R.id.textViewHitBox);
        editTextMaxflowrate = (EditText) findViewById(R.id.editTextMaxflowrate);
        editTextBusaddress = (EditText) findViewById(R.id.editTextBusaddress);
        editTextPulseoutput = (EditText) findViewById(R.id.editTextPulseoutput);
        editTextCurrentoutput4mA = (EditText) findViewById(R.id.editTextCurrentoutput4mA);
        editTextCurrentoutput20mA = (EditText) findViewById(R.id.editTextCurrentoutput20mA);
        editTextSerialnumber = (EditText) findViewById(R.id.editTextSerialnumber);
        editTextActualflow = (EditText) findViewById(R.id.editTextActualFlow);
        buttonwrite = (Button) findViewById(R.id.buttonwrite);
        buttonread = (Button) findViewById(R.id.buttonread);
        editTextSerialnumber.setKeyListener(null);
        editTextMaxflowrate.setKeyListener(null);
        editTextActualflow.setKeyListener(null);
    }



    private float si2local_volume (int volume, float val) {
        //if (val == 0.0) {
        //    return (float) 0.00;
        //}
        float f = val;
        switch (volume)
        {
            case 0: //ltr
                f = f / 0.001f;
                break;
            case 1: //hl
                f = f / 0.1f;
                break;
            case 2: //m³
                f = f / 1.0f;
                break;
            case 3: //USG
                f = f / 0.00378541178f;
                break;
            case 4: //UKG
                f = f / 0.00454609188f;
                break;
            default: //default "ltr"
                f = f / 0.001f;
                break;
        }
        return f;
    }

    private float volume_2localsi (int volume, float val) {
        //if (val == 0.0) {
        //    return (float) 0.00;
        //}
        float f = val;
        switch (volume)
        {
            case 0: //ltr
                f = f * 0.001f;
                break;
            case 1: //hl
                f = f * 0.1f;
                break;
            case 2: //m³
                f = f * 1.0f;
                break;
            case 3: //USG
                f = f * 0.00378541178f;
                break;
            case 4: //UKG
                f = f * 0.00454609188f;
                break;
            default: //default "ltr"
                f = f * 0.001f;
                break;
        }
        return f;
    }

    private float si2local_time (int time, float val){

        //if (val == 0.0) {
        //    return (float) 0.00;
        //}
        float f = val;
        switch (time)
        {
            case 0: // sec
                f = f / 1.0f;
                break;
            case 1: //min
                f = f / 60.0f;
                break;
            case 2: //hour
                f = f / 3600.0f;
                break;
            default: //default "sec"
                f = f / 1.0f;
                break;
        }
        return f;
    }

    private float time_2localsi (int time, float val){
        //if (val == 0.0) {
        //    return (float) 0.00;
        //}
        float f = val;
        switch (time)
        {
            case 0: // sec
                f = f * 1.0f;
                break;
            case 1: //min
                f = f * 60.0f;
                break;
            case 2: //hour
                f = f * 3600.0f;
                break;
            default: //default "sec"
                f = f * 1.0f;
                break;
        }
        return f;
    }
}
