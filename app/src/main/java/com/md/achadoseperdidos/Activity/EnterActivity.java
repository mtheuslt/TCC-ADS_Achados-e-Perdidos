package com.md.achadoseperdidos.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.md.achadoseperdidos.R;
import com.md.achadoseperdidos.Utils.SharedPref;
import com.md.achadoseperdidos.databinding.ActivityEnterBinding;

import java.util.Locale;

import io.fabric.sdk.android.Fabric;

@SuppressWarnings("FieldCanBeLocal")
public class EnterActivity extends AppCompatActivity {
    ActivityEnterBinding binding;

    private FirebaseAuth mAuth;
    private Intent HomeActivity;
    private SharedPref sharedPref;
    private static final String TAG = "EnterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate() called with: savedInstanceState = [" + savedInstanceState + "]");
        loadLocale();
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        super.onCreate(savedInstanceState);

        //  Declare a new thread to do a preference check
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Initialize SharedPreferences
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                //  Create a new boolean and preference and set it to true
                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

                //  If the activity has never started before...
                if (isFirstStart) {
                    Log.i(TAG, "run: FirstStart");
                    //  Launch app intro
                    final Intent i = new Intent(EnterActivity.this, IntroActivity.class);

                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            startActivity(i);
                        }
                    });

                    //  Make a new preferences editor
                    SharedPreferences.Editor e = getPrefs.edit();

                    //  Edit preference to make it false because we don't want this to run again
                    e.putBoolean("firstStart", false);

                    //  Apply changes
                    e.apply();
                }
            }
        });

        // Start the thread
        t.start();

        SharedPreferences sp = this.getSharedPreferences("pref", Context.MODE_PRIVATE);
        if (!sp.getBoolean("first", false)) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("first", true);
            editor.apply();
            Intent intent = new Intent(this, IntroActivity.class); // Call the AppIntro java class
            startActivity(intent);
        }


        Fabric.with(this, new Crashlytics());
        binding = DataBindingUtil.setContentView(this, R.layout.activity_enter);

        // Firebase
        mAuth = FirebaseAuth.getInstance();

        HomeActivity = new Intent(this, com.md.achadoseperdidos.Activity.Home.class);
    }

    private void updateUI() {
        Log.d(TAG, "updateUI() called");
        startActivity(HomeActivity);
        finish();
    }

    public void  login(View view)
    {
        Log.d(TAG, "login() called with: view = [" + view + "]");
        startActivity(new Intent(this,LoginActivity.class));
    }


    public void  getStarted(View view)
    {
        Log.d(TAG, "getStarted() called with: view = [" + view + "]");
        startActivity(new Intent(this, RegisterActivity.class));
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart() called");
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();

        if(user != null) {
            Log.d(TAG, "User not null");
            //usuario já está conectado, assim redireciona ele para a tela home
            updateUI();

        }
    }

    public void setLocale(String lang){
        Log.d(TAG, "setLocale() called with: lang = [" + lang + "]");
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        //save data
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();

    }

    public void loadLocale(){
        Log.d(TAG, "loadLocale() called");
        SharedPreferences preferences = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String lang = preferences.getString("My_Lang", "");
        setLocale(lang);
    }

}
