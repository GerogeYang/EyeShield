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


public class MainActivity extends AppCompatActivity implements Preference.OnPreferenceClickListener, SeekBarPreference.OnSeekBarPrefsChangeListener {


    static MainActivity a;
    static Intent serviceIntent;
    static int screenWidth, screenHeight;
    static SwitchPreference switchPreference;
    static SeekBarPreference setAlphaPreference, setRedPreference, setGreenPreference, setBluePreference;
    static boolean activated;
    static int alpha, red, green, blue;


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


    public void initSetColor() {

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


        getFragmentManager().beginTransaction().replace(R.id.set, new SetFragment()).commit();
        getFragmentManager().beginTransaction().replace(R.id.setAlpha, new SetAlphaFragement()).commit();
        getFragmentManager().beginTransaction().replace(R.id.setRed, new SetRedFragement()).commit();
        getFragmentManager().beginTransaction().replace(R.id.setGreen, new SetGreenFragement()).commit();
        getFragmentManager().beginTransaction().replace(R.id.setBlue, new SetBlueFragement()).commit();


        mHanlder.sendEmptyMessage(2);
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
        System.out.println("YJJJ");
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

    /*   @Override
       public boolean onPreferenceChange(Preference preference, Object newValue) {

           System.out.println("YJ2");
           SeekBarPreference seekBarPreference = (SeekBarPreference) preference;
           switch (seekBarPreference.getKey()) {
               case "setalpha":
                   alpha = seekBarPreference.getMprogress();
                   break;
               case "setred":
                   red = (int)newValue;
                   break;
               case "setgreen":
                   green = seekBarPreference.getMprogress();
                   break;
               case "setblue":
                   blue = seekBarPreference.getMprogress();
                   break;
           }

           return true;
       }
   */
    @Override
    public void onStopTrackingTouch(String key, SeekBar seekBar) {

    }

    @Override
    public void onStartTrackingTouch(String key, SeekBar seekBar) {

    }

    @Override
    public void onProgressChanged(String key, SeekBar seekBar, int progress, boolean fromUser) {
        switch (key) {
            case "setalpha":
                alpha = progress;
                break;
            case "setred":
                red = progress;
                break;
            case "setgreen":
                green = progress;
                break;
            case "setblue":
                blue = progress;
                break;
        }
    }


    public static class SetFragment extends PreferenceFragment {

        static SwitchPreference mSwitchPreference;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.setpreference);
            switchPreference = (SwitchPreference) getPreferenceManager().findPreference("setFilter");
            switchPreference.setOnPreferenceClickListener(a);
            if (switchPreference.isChecked()) {
                activated = true;
            } else {
                activated = false;
            }
        }
    }

    public static class SetAlphaFragement extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.setalphapreference);
            setAlphaPreference = (SeekBarPreference) findPreference("setalpha");
            setAlphaPreference.setMax(255);
            setAlphaPreference.setDefaultProgressValue(100);
            setAlphaPreference.setOnSeekBarPrefsChangeListener(a);
            alpha = setAlphaPreference.getProgress();
        }
    }

    public static class SetRedFragement extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.setredpreference);
            setRedPreference = (SeekBarPreference) findPreference("setred");
            setRedPreference.setMax(255);
            setRedPreference.setDefaultProgressValue(100);
            setRedPreference.setOnSeekBarPrefsChangeListener(a);
            red = setRedPreference.getProgress();
        }
    }

    public static class SetGreenFragement extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.setgreenpreference);
            setGreenPreference = (SeekBarPreference) findPreference("setgreen");
            setGreenPreference.setMax(255);
            setGreenPreference.setDefaultProgressValue(100);
            setGreenPreference.setOnSeekBarPrefsChangeListener(a);
            green = setGreenPreference.getProgress();
        }
    }

    public static class SetBlueFragement extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.setbluepreference);
            setBluePreference = (SeekBarPreference) findPreference("setblue");
            setBluePreference.setMax(255);
            setBluePreference.setDefaultProgressValue(100);
            setBluePreference.setOnSeekBarPrefsChangeListener(a);
            blue = setBluePreference.getProgress();
        }
    }
}
