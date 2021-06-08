package com.md.achadoseperdidos.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.marcoscg.licenser.Library;
import com.marcoscg.licenser.License;
import com.marcoscg.licenser.LicenserDialog;
import com.md.achadoseperdidos.R;
import com.md.achadoseperdidos.Utils.SharedPref;
import com.muddzdev.styleabletoast.StyleableToast;

import java.util.Calendar;
import java.util.Locale;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutActivity extends AppCompatActivity {

    private static final String TAG = "AboutActivity";
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadLocale();
        super.onCreate(savedInstanceState);
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        //setContentView(R.layout.activity_about);

        Log.d(TAG, "onCreate: Entrando na Activity");

        if(getSupportActionBar() != null){
            Log.i(TAG, "onCreate: ActionBar");
            getSupportActionBar().setTitle(getResources().getString(R.string.about));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Element adsElement = new Element();
        adsElement.setTitle(getResources().getString(R.string.announceAbout));

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.mipmap.ic_icon_round)
                .setDescription(getResources().getString(R.string.descriptionAbout))
                .addItem(new Element().setTitle(getResources().getString(R.string.versionAbout)))
                .addGroup(getResources().getString(R.string.conectAbout))
                .addEmail("m.theus.lt@gmail.com")
                .addFacebook("mtheusltt")
                .addYoutube("My Youtube")
                .addTwitter("My Twitter")
                .addPlayStore("My PlayStore")
                .addItem(termsService())
                .addItem(privacityPolicy())
                .addItem(License())
                .addItem(createCopyright())
                .create();

        setContentView(aboutPage);
    }

    private Element License() {
        Log.i(TAG, "License: Information");
        Element license = new Element();
        @SuppressLint({"StringFormatInvalid", "LocalSuppress"}) final String copyrightString = String.format(Locale.getDefault(), getResources().getString(R.string.license), Calendar.getInstance().get(Calendar.YEAR));
        license.setTitle(copyrightString);
        license.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LicenserDialog(AboutActivity.this)
                        .setTitle(getResources().getString(R.string.license))
                        .setCustomNoticeTitle(getResources().getString(R.string.noticesAbout))
                        .setBackgroundColor(Color.WHITE) // Optional
                        .setLibrary(new Library(getResources().getString(R.string.librariesAbout),
                                "https://developer.android.com/topic/libraries/support-library/index.html",
                                License.APACHE))
                        .setLibrary(new Library(getResources().getString(R.string.libraries2About),
                                "https://github.com/mtheuslt?tab=stars",
                                License.APACHE))
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // TODO: 11/05/2019
                            }
                        })
                        .show();
                // Toast.makeText(AboutActivity.this, copyrightString, Toast.LENGTH_LONG).show();
            }
        });
        return license;
    }

    @Override
    public boolean onSupportNavigateUp() {
        Log.d(TAG, "onSupportNavigateUp() called");
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed() called");
        super.onBackPressed();
    }

    private Element termsService() {
        Log.d(TAG, "termsService() called");
        Element terms = new Element();
        @SuppressLint({"StringFormatInvalid", "LocalSuppress"}) final String copyrightString = String.format(Locale.getDefault(), getResources().getString(R.string.termsBar), Calendar.getInstance().get(Calendar.YEAR));
        terms.setTitle(copyrightString);
        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent termsActivity = new Intent(AboutActivity.this, TermsActivity.class);
                startActivity(termsActivity);
                // Toast.makeText(AboutActivity.this, copyrightString, Toast.LENGTH_LONG).show();
            }
        });
        return terms;
    }

    private Element privacityPolicy() {
        Log.d(TAG, "privacityPolicy() called");
        Element police = new Element();
        @SuppressLint({"StringFormatInvalid", "LocalSuppress"}) final String copyrightString = String.format(Locale.getDefault(), getResources().getString(R.string.privacyBar), Calendar.getInstance().get(Calendar.YEAR));
        police.setTitle(copyrightString);
        police.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent policyActivity = new Intent(AboutActivity.this, PolicyActivity.class);
                startActivity(policyActivity);
                //StyleableToast.makeText(AboutActivity.this, copyrightString, Toast.LENGTH_LONG, R.style.advertenciaToast).show();
                // Toast.makeText(AboutActivity.this, copyrightString, Toast.LENGTH_LONG).show();
            }
        });
        return police;
    }

    private Element createCopyright() {
        Log.d(TAG, "createCopyright() called");
        Element copyright = new Element();
        final String copyrightString = String.format(Locale.getDefault(), "Copyright %d by Matheus e Gabriel", Calendar.getInstance().get(Calendar.YEAR));
        copyright.setTitle(copyrightString);
        copyright.setIconDrawable(R.mipmap.ic_icon_round);
        copyright.setGravity(Gravity.CENTER);
        copyright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StyleableToast.makeText(AboutActivity.this, copyrightString, Toast.LENGTH_LONG, R.style.advertenciaToast).show();
                // Toast.makeText(AboutActivity.this, copyrightString, Toast.LENGTH_LONG).show();
            }
        });
        return copyright;
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
