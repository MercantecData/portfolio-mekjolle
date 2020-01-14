package com.example.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    private float si2local_volume (int volume, float val) {
        if (val == 0.0) {
            return (float) 0.00;
        }
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
        if (val == 0.0) {
            return (float) 0.00;
        }
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

        if (val == 0.0) {
            return (float) 0.00;
        }
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

        if (val == 0.0) {
            return (float) 0.00;
        }
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
