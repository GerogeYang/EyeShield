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


public class MainActivity extends AppCompatActivity implements Preference.OnPreferenceClickListener {


    static MainActivity a;
    static Intent serviceIntent;
    static int screenWidth, screenHeight;
    static SwitchPreference switchPreference;
    static boolean activated;

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
        } else if (flag && (serviceIntent == null)) {
            serviceIntent = new Intent(MainActivity.this, FilterService.class);
            startService(serviceIntent);
            onBackPressed();
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
    }


    @Override
    protected void onResume() {
        super.onResume();
        FilterService.setFilterOn(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FilterService.setFilterOn(true);
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
            ;
        }
    }

}
