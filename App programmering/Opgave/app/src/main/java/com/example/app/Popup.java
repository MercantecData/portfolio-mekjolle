package com.example.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class Popup extends Activity {
    private Spinner SpinnerVolume;
    private Spinner SpinnerTime;
    private ArrayAdapter adapterVolume;
    private ArrayAdapter adapterTime;
    private TextView TextViewFull;
    private int Volume = 0;
    private int Time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popupwindow);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        Volume = getIntent().getIntExtra("Volume", 0);
        Time = getIntent().getIntExtra("Time", 0);

        getWindow().setLayout((int)(width*.7),(int) (height*0.94));
        getWindow().setGravity(Gravity.RIGHT| Gravity.BOTTOM);

        SpinnerVolume = findViewById(R.id.spinnerVolume);
        SpinnerTime = findViewById(R.id.spinnerTime);

        TextViewFull = findViewById(R.id.textViewFull);

        adapterVolume = ArrayAdapter.createFromResource(this, R.array.Volume, R.layout.spinner_item);
        adapterVolume.setDropDownViewResource(R.layout.spinner_dropdown_item);
        adapterTime = ArrayAdapter.createFromResource(this, R.array.Time, R.layout.spinner_item);
        adapterTime.setDropDownViewResource(R.layout.spinner_dropdown_item);

        SpinnerVolume.setAdapter(adapterVolume);
        SpinnerVolume.setSelection(Volume);
        SpinnerVolume.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getBaseContext(), parent.getItemIdAtPosition(position)+" selected", Toast.LENGTH_LONG).show();
                Volume = position;
                Intent returnIntent = new Intent();
                returnIntent.putExtra("Volume", Volume );
                returnIntent.putExtra("Time", Time );
                setResult(Activity.RESULT_OK,returnIntent);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        SpinnerTime.setAdapter(adapterTime);
        SpinnerTime.setSelection(Time);
        SpinnerTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getBaseContext(), parent.getItemIdAtPosition(position)+" selected", Toast.LENGTH_LONG).show();
                Time = position;
                Intent returnIntent = new Intent();
                returnIntent.putExtra("Volume", Volume );
                returnIntent.putExtra("Time", Time );
                setResult(Activity.RESULT_OK,returnIntent);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        TextViewFull.setOnTouchListener(new OnSwipeTouchListener(Popup.this) {
            public void onSwipeRight() {
                finish();
            }

            public void onSwipeBottom()
            {
                finish();
            }
        });
    }
}
