package com.example.yj.eyeshield;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.widget.SeekBar;


public class MainActivity extends AppCompatActivity implements Preference.OnPreferenceClickListener, SeekBar.OnSeekBarChangeListener {


    static MainActivity a;
    static Intent serviceIntent;
    static int screenWidth, screenHeight;
    static SwitchPreference switchPreference;
    static boolean activated;
    static int alpha, red, green, blue;
    SeekBar setAlpha, setRed, setGreen, setBlue;

    Handler mHanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    activated = true;
                    //System.out.println("" + activated);
                    break;
                case 1:
                    activated = false;
                    //System.out.println("" + activated);
                    break;
                default:
                    // System.out.println("" + activated);
                    break;
            }
            checkStatus(activated);
        }
    };


    public void initSetColor(int malpha, int mred, int mgreen, int mblue) {
        alpha = malpha;
        red = mred;
        green = mgreen;
        blue = mblue;
    }

    public void checkStatus(boolean flag) {
        if (!flag && (serviceIntent != null)) {
            stopService(serviceIntent);
            serviceIntent = null;
            SurfaceFilterService.setFilterOn(false);
        } else if (flag && (serviceIntent == null)) {
            serviceIntent = new Intent(MainActivity.this, SurfaceFilterService.class);
            startService(serviceIntent);
            SurfaceFilterService.setFilterOn(true);
            //onBackPressed();
        }
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        a = this;
        Display display = getWindowManager().getDefaultDisplay();
        screenWidth = display.getWidth();
        screenHeight = display.getHeight();

        getFragmentManager().beginTransaction().replace(R.id.set, new PrefsFragment()).commit();
        mHanlder.sendEmptyMessage(2);

        setAlpha = (SeekBar) findViewById(R.id.setAlpha);
        setRed = (SeekBar) findViewById(R.id.setRed);
        setGreen = (SeekBar) findViewById(R.id.setGreen);
        setBlue = (SeekBar) findViewById(R.id.setBlue);
        setAlpha.setOnSeekBarChangeListener(this);
        setRed.setOnSeekBarChangeListener(this);
        setGreen.setOnSeekBarChangeListener(this);
        setBlue.setOnSeekBarChangeListener(this);
        initSetColor(setAlpha.getProgress(), setRed.getProgress(), setGreen.getProgress(), setBlue.getProgress());
    }


    @Override
    protected void onResume() {
        super.onResume();
        //FilterService.setFilterOn(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //FilterService.setFilterOn(true);
    }


    @Override
    public boolean onPreferenceClick(Preference preference) {

        if (preference.getKey().equals("setFilter")) {
            if (((SwitchPreference) preference).isChecked()) {

                mHanlder.sendEmptyMessage(0);
                //System.out.println("" + 0);
            } else {

                mHanlder.sendEmptyMessage(1);
                //System.out.println("" + 1);
            }
        }
        return true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.setAlpha:
                alpha = progress;
                break;
            case R.id.setRed:
                red = progress;
                break;
            case R.id.setGreen:
                green = progress;
                break;
            case R.id.setBlue:
                blue = progress;
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


    public static class PrefsFragment extends PreferenceFragment {

        static SwitchPreference mSwitchPreference;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference);
            switchPreference = (SwitchPreference) getPreferenceManager().findPreference("setFilter");
            switchPreference.setOnPreferenceClickListener(a);
            if (switchPreference.isChecked()) {
                activated = true;
            } else {
                activated = false;
            }
        }
    }

}
