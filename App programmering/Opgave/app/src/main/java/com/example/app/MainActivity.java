package com.example.app;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcF;
import android.os.Parcelable;
import android.preference.PreferenceManager;
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
import android.widget.Toast;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.AttributedString;
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
    NfcAdapter nfcAdapter;
    private boolean nfcwriteenable = false;
    private boolean nfcreadenable = false;
    IntentFilter[] intentFiltersArray;
    String [] [] techListsArray;
    PendingIntent pendingIntent;
    public static final String MIME_VIGO6 = "application/x.proces-data.vigo6_nfc";
    private int ikkeaktivfarve = 0xc6918b8b;
    private int aktivfarve = 0xc672c127;
    private boolean editflag = false;

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


    public void handleIntent(Intent intent) {
        String action = intent.getAction();
        byte[] payloadout = new byte[0];
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            if (nfcAdapter != null && nfcAdapter.isEnabled() && !nfcwriteenable && !nfcreadenable) {
                Toast.makeText(this, "NFC available!", Toast.LENGTH_SHORT).show();
            } else {
                if (nfcreadenable) {
                    Toast.makeText(this, "NFC data read!", Toast.LENGTH_LONG).show();
                }
                if (nfcwriteenable)
                {
                    Toast.makeText(this, "NFC data written!", Toast.LENGTH_LONG).show();
                }
            }


            if (nfcreadenable && !nfcwriteenable) {
                if (intent.getType() != null && intent.getType().equals(MIME_VIGO6)) {
                    // Read the first record which contains the NFC data
                    Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                    NdefRecord relayRecord = ((NdefMessage) rawMsgs[0]).getRecords()[0];
                    byte[] payload = relayRecord.getPayload();

                    short stringLenght = Dataconverter.msgToShort(payload, 28);

                    ByteBuffer stringbuffer = ByteBuffer.wrap(payload, 30, stringLenght);
                    byte[] tmp = new byte[stringLenght];
                    stringbuffer.get(tmp);
                    String serialNumber = new String(tmp);

                    iBus = payload[50]; //to do "range"
                    fMax = Dataconverter.msgToFloat(payload, 52);
                    iPulse = Dataconverter.msgToFloat(payload, 56);
                    fCurrent4mA = Dataconverter.msgToFloat(payload, 60);
                    fCurrent20mA = Dataconverter.msgToFloat(payload, 64);
                    fActual = Dataconverter.msgToFloat(payload, 68);

                    try {
                        editTextSerialnumber.setText(serialNumber);
                        editTextBusaddress.setText(Integer.toString(iBus));
                        editTextMaxflowrate.setText(String.format(Locale.US, "%.2f" ,fMax));
                        editTextPulseoutput.setText(String.format(Locale.US, "%.2f" ,iPulse));
                        editTextCurrentoutput4mA.setText(String.format(Locale.US, "%.2f" ,fCurrent4mA));
                        editTextCurrentoutput20mA.setText(String.format(Locale.US, "%.2f" , fCurrent20mA));
                        editTextActualflow.setText(String.format(Locale.US, "%.2f", (fActual)));
                    } catch (Exception e) {
                    }
                    nfcreadenable = false;
                    nfcwriteenable = false;
                    buttonread.setBackgroundColor(ikkeaktivfarve);
                }
            }
        }
        if (nfcwriteenable) {
            Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            byte[] convertinformations = new  byte[296];

            String GUIDstr = new String("PD_18232-03");
            Dataconverter.shortToMsg((short)GUIDstr.length(), convertinformations, 0);
            ByteBuffer.wrap(convertinformations, 2 , GUIDstr.length()).put(GUIDstr.getBytes());


            Dataconverter.shortToMsg((short)editTextSerialnumber.getText().length(), convertinformations, 28);
            ByteBuffer.wrap(convertinformations, 30 , editTextSerialnumber.getText().length()).put(editTextSerialnumber.getText().toString().getBytes());
            Dataconverter.byteToMsg(Byte.parseByte(editTextBusaddress.getText().toString()), convertinformations, 50);
            Dataconverter.floatToMsg(fMax, convertinformations, 52);
            Dataconverter.floatToMsg(iPulse, convertinformations, 56);
            Dataconverter.floatToMsg(fCurrent4mA, convertinformations, 60);
            Dataconverter.floatToMsg(fCurrent20mA, convertinformations, 64);
            Dataconverter.floatToMsg(fActual, convertinformations, 68);

            NdefMessage msg = new NdefMessage(new NdefRecord[] {createMimeRecord(MIME_VIGO6, convertinformations)});
            writeTag(tagFromIntent, msg);
            nfcwriteenable = false;
            nfcreadenable = false;
            buttonwrite.setBackgroundColor(ikkeaktivfarve);
        }

    }

    private NdefRecord createMimeRecord(String mimeType, byte[] payload) {
        byte[] mimeBytes = mimeType.getBytes(Charset.forName("UTF-8"));
        NdefRecord mimeRecord = new
                NdefRecord(NdefRecord.TNF_MIME_MEDIA,
                mimeBytes, new byte[0], payload);
        return mimeRecord;
    }
    private void intentfiltersetup(){
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType(MIME_VIGO6);  /* Handles all MIME based dispatches.
                                       You should specify only the ones that you need. */
        }
        catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }

        IntentFilter emptytag = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);

        intentFiltersArray = new IntentFilter[] {ndef, emptytag, };

        techListsArray = new String[][] { new String[] { NfcF.class.getName() }, new String[] {MifareUltralight.class.getName()} };

    }

    public void writeTag(Tag tag, NdefMessage message)  {
        if (tag != null) {
            try {
                Ndef ndefTag = Ndef.get(tag);
                if (ndefTag == null) {
                    // Let's try to format the Tag in NDEF
                    NdefFormatable nForm = NdefFormatable.get(tag);
                    if (nForm != null) {
                        nForm.connect();
                        nForm.format(message);
                        nForm.close();
                    }
                }
                else {
                    ndefTag.connect();

                    ndefTag.writeNdefMessage(message);
                    ndefTag.close();
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lockScreenOrientation();

        TextViewUnit1 = findViewById(R.id.textViewUnit1);
        TextViewUnit2 = findViewById(R.id.textViewUnit2);
        TextViewUnit3 = findViewById(R.id.textViewUnit3);
        TextViewUnit4 = findViewById(R.id.textviewUnit4);
        TextViewUnit5 = findViewById(R.id.textViewUnit5);
        TextViewHitBox = findViewById(R.id.textViewHitBox);
        editTextMaxflowrate = findViewById(R.id.editTextMaxflowrate);
        editTextBusaddress = findViewById(R.id.editTextBusaddress);
        editTextPulseoutput = findViewById(R.id.editTextPulseoutput);
        editTextCurrentoutput4mA = findViewById(R.id.editTextCurrentoutput4mA);
        editTextCurrentoutput20mA = findViewById(R.id.editTextCurrentoutput20mA);
        editTextSerialnumber = findViewById(R.id.editTextSerialnumber);
        editTextActualflow = findViewById(R.id.editTextActualFlow);
        buttonwrite = findViewById(R.id.buttonwrite);
        buttonread = findViewById(R.id.buttonread);

        //Gøre felterne ikke klikbare
        editTextSerialnumber.setKeyListener(null);
        editTextMaxflowrate.setKeyListener(null);
        editTextActualflow.setKeyListener(null);

        //Gøre deres underline transparrant
        editTextSerialnumber.setBackgroundResource(android.R.color.transparent);
        editTextMaxflowrate.setBackgroundResource(android.R.color.transparent);
        editTextActualflow.setBackgroundResource(android.R.color.transparent);

        pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        intentfiltersetup();

        TextViewHitBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editflag) {
                    return;
                }
                Intent popup = new Intent(MainActivity.this, Popup.class);
                popup.putExtra("Volume", VolumeUnitIndex);
                popup.putExtra("Time", TimeUnitIndex);
                startActivityForResult(popup, 38);
            }
        });

        editTextMaxflowrate.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    float volume = 0.0f;
                    float time = 0.0f;

                    if(TextUtils.isEmpty(editTextMaxflowrate.getText().toString()))
                    {
                        fMax = 0.0f;
                    }
                    else
                    {
                        float local = Float.parseFloat(editTextMaxflowrate.getText().toString());

                        if (local == 0)
                        {
                            fMax = 0.0f;
                        }
                        else
                        {
                            volume = volume_2localsi(VolumeUnitIndex, local);
                            time = time_2localsi(TimeUnitIndex, local);

                            fMax = local * (volume / time);
                        }
                    }
                    new java.util.Timer().schedule(
                            new java.util.TimerTask() {
                                @Override
                                public void run() {
                                    editflag = false;
                                }
                            },
                            200
                    );
                } else {
                    editflag = true;
                }
                editTextMaxflowrate.clearFocus();
                onResume();
            }
        });

        editTextPulseoutput.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    float volume = 0.0f;

                    if (TextUtils.isEmpty(editTextPulseoutput.getText().toString())) {
                        Toast.makeText(MainActivity.this, "Provide valid data to field before writing", Toast.LENGTH_LONG).show();
                        iPulse = 0;
                    } else {
                        float local = Float.parseFloat(editTextPulseoutput.getText().toString());

                        if (local == 0) {
                            iPulse = 0;
                        } else {
                            volume = volume_2localsi(VolumeUnitIndex, 1);

                            iPulse = (local / volume);

                        }
                    }
                    new java.util.Timer().schedule(
                            new java.util.TimerTask() {
                                @Override
                                public void run() {
                                    editflag = false;
                                }
                            },
                            200
                    );
                } else {
                    editflag = true;
                }
                onResume();
            }
        });
        editTextCurrentoutput4mA.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    float volume = 0.0f;
                    float time = 0.0f;

                    if (TextUtils.isEmpty(editTextCurrentoutput4mA.getText().toString()))
                    {
                        Toast.makeText(MainActivity.this, "Provide valid data to field before writing", Toast.LENGTH_LONG).show();
                        fCurrent4mA = 0.0f;
                    }
                    else
                    {
                        float local = Float.parseFloat(editTextCurrentoutput4mA.getText().toString());

                        if (local == 0)
                        {
                            fCurrent4mA = 0.0f;
                        }
                        else
                        {
                            volume = volume_2localsi(VolumeUnitIndex, local);
                            time = time_2localsi(TimeUnitIndex, local);

                            fCurrent4mA = local * (volume / time);
                        }
                    }
                    new java.util.Timer().schedule(
                            new java.util.TimerTask() {
                                @Override
                                public void run() {
                                    editflag = false;
                                }
                            },
                            200
                    );
                } else {
                    editflag = true;
                }
                onResume();
            }
        });
        editTextCurrentoutput20mA.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    float volume = 0.0f;
                    float time = 0.0f;

                    if(TextUtils.isEmpty(editTextCurrentoutput20mA.getText().toString()))
                    {
                        Toast.makeText(MainActivity.this, "Provide valid data to field before writing", Toast.LENGTH_LONG).show();
                        fCurrent20mA = 0.0f;
                    }
                    else
                    {
                        float local = Float.parseFloat(editTextCurrentoutput20mA.getText().toString());

                        if (local == 0)
                        {
                            fCurrent20mA = 0.0f;
                        }
                        else {
                            volume = volume_2localsi(VolumeUnitIndex, local);
                            time = time_2localsi(TimeUnitIndex, local);
                            fCurrent20mA = local * (volume / time);
                        }
                    }
                    new java.util.Timer().schedule(
                            new java.util.TimerTask() {
                                @Override
                                public void run() {
                                    editflag = false;
                                }
                            },
                            200
                    );
                } else {
                    editflag = true;
                }
                onResume();
            }
        });

        editTextActualflow.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    float volume = 0.0f;
                    float time = 0.0f;

                    if(TextUtils.isEmpty(editTextActualflow.getText().toString()))
                    {
                        fActual = 0.0f;
                    }
                    else
                    {
                        float local = Float.parseFloat(editTextActualflow.getText().toString());

                        if (local == 0)
                        {
                            fActual = 0.0f;
                        }
                        else
                        {
                            volume = volume_2localsi(VolumeUnitIndex, local);
                            time = time_2localsi(TimeUnitIndex, local);

                            fActual = local * (volume / time);
                        }
                    }
                    new java.util.Timer().schedule(
                            new java.util.TimerTask() {
                                @Override
                                public void run() {
                                    editflag = false;
                                }
                            },
                            200
                    );
                } else {
                    editflag = true;
                }
                editTextActualflow.clearFocus();
                onResume();
            }
        });

        editTextBusaddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if(TextUtils.isEmpty(editTextBusaddress.getText().toString()))
                    {
                        Toast.makeText(MainActivity.this, "Provide valid data to field before writing", Toast.LENGTH_LONG).show();
                        iBus = 0;
                    }

                    new java.util.Timer().schedule(
                            new java.util.TimerTask() {
                                @Override
                                public void run() {
                                    editflag = false;
                                }
                            },
                            200
                    );
                } else {
                    editflag = true;
                }
            }
        });

        editTextSerialnumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                editTextSerialnumber.clearFocus();
            }
        });

        buttonwrite.setOnClickListener(writeListener);
        buttonread.setOnClickListener(readListener);

    }

    private void lockScreenOrientation() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    private View.OnClickListener writeListener = new View.OnClickListener() {
        public void onClick(View v) {

            if (!TextUtils.isEmpty(editTextSerialnumber.getText().toString()))
            {
                if (!nfcwriteenable)
                {
                    if (nfcreadenable)
                    {
                        nfcreadenable = false;
                        buttonread.setBackgroundColor(ikkeaktivfarve);
                        nfcwriteenable = true;
                        buttonwrite.setBackgroundColor(aktivfarve);
                    }
                    else
                    {
                        nfcwriteenable = true;
                        buttonwrite.setBackgroundColor(aktivfarve);
                    }
                }
                else
                {
                    nfcwriteenable = false;
                    buttonwrite.setBackgroundColor(ikkeaktivfarve);
                }
            }
            else
            {
                Toast.makeText(MainActivity.this, "Initialize flow meter before writing!", Toast.LENGTH_LONG).show();
            }

        }
    };

    private View.OnClickListener readListener = new View.OnClickListener() {
        public void onClick(View v) {

            if (!nfcreadenable) {
                if (nfcwriteenable)
                {
                    nfcwriteenable = false;
                    buttonwrite.setBackgroundColor(ikkeaktivfarve);
                    nfcreadenable = true;
                    buttonread.setBackgroundColor(aktivfarve);
                }
                else
                {
                    nfcreadenable = true;
                    buttonread.setBackgroundColor(aktivfarve);
                }
            }
            else
            {
                nfcreadenable = false;
                buttonread.setBackgroundColor(ikkeaktivfarve);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 38) {
            if (resultCode == RESULT_OK) {
                VolumeUnitIndex = data.getIntExtra("Volume" , 0);
                TimeUnitIndex = data.getIntExtra("Time" , 0);

            }
        }
        if (requestCode == 40) {
            if (resultCode == RESULT_OK) {


                Intent getvalidDataIntent = data;

                float Rightvalue = 0.00f;

                String SerialNumber = data.getStringExtra("SerialNumberData");
                editTextSerialnumber.setText(SerialNumber);

                Integer getBus = data.getIntExtra("BusAddressData", 0);
                iBus = getBus;
                editTextBusaddress.setText(Integer.toString(iBus));

                Float getMax = data.getFloatExtra("MaxFlowData", 0.0f);
                fMax = getMax;
                Rightvalue = time_2localsi(TimeUnitIndex, fMax);
                editTextMaxflowrate.setText(Float.toString(Rightvalue));

                Float getiPulse = data.getFloatExtra("PulseData" , 0.0f);
                iPulse = getiPulse;
                editTextPulseoutput.setText(Float.toString(getiPulse));

                Float get4mA = data.getFloatExtra("Current4mAData" , 0.0f);
                fCurrent4mA = get4mA;
                Rightvalue = time_2localsi(TimeUnitIndex, fCurrent4mA);
                editTextCurrentoutput4mA.setText(Float.toString(Rightvalue));

                Float get20mA = data.getFloatExtra("Current20mAData" , 0.0f);
                fCurrent20mA = get20mA;
                Rightvalue = time_2localsi(TimeUnitIndex, fCurrent20mA);
                editTextCurrentoutput20mA.setText(Float.toString(Rightvalue));

                Float getActual = data.getFloatExtra("ActualFlowData" , 0.0f);
                fActual = getActual;
                Rightvalue = time_2localsi(TimeUnitIndex, fActual);
                editTextActualflow.setText(Float.toString(Rightvalue));
            }

        }
    }


    public void onPause() {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);

        SharedPreferences saved_values = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = saved_values.edit();
        editor.putInt("SaveVolume", VolumeUnitIndex);
        editor.putInt("SaveTime", TimeUnitIndex);
        editor.commit();
    }

    public void onResume() {
        super.onResume();
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray);

        SharedPreferences saved_values = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (VolumeUnitIndex == -1) {
            VolumeUnitIndex = saved_values.getInt("SaveVolume", 0);
        }
        if (TimeUnitIndex == -1) {
            TimeUnitIndex = saved_values.getInt("SaveTime", 0);

        }

        Resources res = getResources();
        String[] VolumeArray = res.getStringArray(R.array.Volume);
        String[] TimeArray = res.getStringArray(R.array.Time);
        TextViewUnit1.setText(VolumeArray[VolumeUnitIndex] + "/" + TimeArray[TimeUnitIndex]);
        TextViewUnit2.setText("pulses" + "/" + VolumeArray[VolumeUnitIndex]);
        TextViewUnit3.setText(VolumeArray[VolumeUnitIndex] + "/" + TimeArray[TimeUnitIndex]);
        TextViewUnit4.setText(VolumeArray[VolumeUnitIndex] + "/" + TimeArray[TimeUnitIndex]);
        TextViewUnit5.setText(VolumeArray[VolumeUnitIndex] + "/" + TimeArray[TimeUnitIndex]);

        startupcounter ++;

        if (startupcounter > 1) {

            float volume = 0.00f;
            float time = 0.00f;

            if (TextUtils.isEmpty(editTextMaxflowrate.getText().toString()))
            {}
            else
            {
                volume = si2local_volume(VolumeUnitIndex, fMax);
                time = si2local_time(TimeUnitIndex, fMax);

                if (fMax == 0) {
                    editTextMaxflowrate.setText(Float.toString(0.00f));
                }
                else
                {
                    editTextMaxflowrate.setText(String.format(Locale.US, "%.2f",(fMax / time) * volume));
                }
            }

            if (TextUtils.isEmpty(editTextPulseoutput.getText().toString()))
            {}
            else
            {
                volume = si2local_volume(VolumeUnitIndex, 1);

                if (iPulse == 0) {
                    editTextPulseoutput.setText(Float.toString(0.00f));
                }
                else
                {
                    editTextPulseoutput.setText(String.format(Locale.US, "%.2f",(iPulse / volume)));
                }

            }

            if (TextUtils.isEmpty(editTextCurrentoutput4mA.getText().toString()))
            {}
            else
            {
                volume = si2local_volume(VolumeUnitIndex, fCurrent4mA);
                time = si2local_time(TimeUnitIndex, fCurrent4mA);

                if (fCurrent4mA == 0) {
                    editTextCurrentoutput4mA.setText(Float.toString(0.00f));
                }
                else
                {
                    editTextCurrentoutput4mA.setText(String.format(Locale.US, "%.2f",(fCurrent4mA / time) * volume));
                }

            }

            if (TextUtils.isEmpty(editTextCurrentoutput20mA.getText().toString()))
            {}
            else {
                volume = si2local_volume(VolumeUnitIndex, fCurrent20mA);
                time = si2local_time(TimeUnitIndex, fCurrent20mA);

                if (fCurrent20mA == 0) {
                    editTextCurrentoutput20mA.setText(Float.toString(0.00f));
                }
                else
                {
                    editTextCurrentoutput20mA.setText(String.format(Locale.US, "%.2f",(fCurrent20mA / time) * volume));
                }
            }

            if (TextUtils.isEmpty(editTextActualflow.getText().toString()))
            {}
            else {
                volume = si2local_volume(VolumeUnitIndex, fActual);
                time = si2local_time(TimeUnitIndex, fActual);

                if (fActual == 0)
                {
                    editTextActualflow.setText(Float.toString(0.00f));
                }
                else
                {
                    editTextActualflow.setText(String.format(Locale.US,"%.2f" , (fActual / time) * volume));
                }
            }
        }
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

    public void onNewIntent(Intent intent) {
        //Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        //do something with tagFromIntent
        handleIntent(intent);
    }
}

