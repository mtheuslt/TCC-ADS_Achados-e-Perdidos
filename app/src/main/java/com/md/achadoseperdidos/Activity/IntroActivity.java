package com.md.achadoseperdidos.Activity;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.google.firebase.database.annotations.Nullable;

import com.md.achadoseperdidos.R;

public class IntroActivity extends AppIntro {

    private static final String TAG = "IntroActivity";

    @SuppressLint("ResourceAsColor")
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate() called with: savedInstanceState = [" + savedInstanceState + "]");
        super.onCreate(savedInstanceState);
        // Note here that we DO NOT use setContentView();

        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
        addSlide(AppIntroFragment.newInstance(getResources().getString(R.string.titleSlide1), getResources().getString(R.string.descriptionSlide1), R.drawable.lost_slide1,
                ContextCompat.getColor(getApplicationContext(), R.color.blueSlide)));

        addSlide(AppIntroFragment.newInstance(getResources().getString(R.string.titleSlide2), getResources().getString(R.string.descriptionSlide2), R.drawable.acess_slide,
                ContextCompat.getColor(getApplicationContext(), R.color.blueSlide)));

        //addSlide(SampleSlide.newInstance(R.layout.activity_privacy));
        addSlide(new PrivacySlide());

        addSlide(AppIntroFragment.newInstance(getResources().getString(R.string.titleSlide4), getResources().getString(R.string.descriptionSlide4), R.drawable.checkmark,
                ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark)));

        // Ask for CAMERA permission on the second slide
        askForPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);

        //@SuppressLint("ResourceType") ConstraintLayout activityPolicy = this.findViewById(R.layout.activity_privacy);
        //setContentView(R.layout.activity_privacy);

        //textView.setText("Leia a nossa Politica de Privacidade. Toque em concordar para aceitar os Termos de Serviço.!");

        // OPTIONAL METHODS
        // Override bar/separator color.
        // Hide Skip/Done button.
        showSkipButton(false);
        showStatusBar(false);
        setDepthAnimation();

        setBarColor(R.color.colorPrimary);
        setSeparatorColor(R.color.colorPrimaryDark);

        // usar para a politica de privacidade
        //setProgressButtonEnabled(false);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        Log.d(TAG, "onSkipPressed() called with: currentFragment = [" + currentFragment + "]");
        startActivity(new Intent(this, EnterActivity.class));
        finish();
        // Do something when users tap on Skip button.
        //StyleableToast.makeText(IntroActivity.this, "Introdução do Aplicativou terminou", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        Log.d(TAG, "onDonePressed() called with: currentFragment = [" + currentFragment + "]");
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        //StyleableToast.makeText(IntroActivity.this, "Introdução do Aplicativou terminou", Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, EnterActivity.class));
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        Log.d(TAG, "onSlideChanged() called with: oldFragment = [" + oldFragment + "], newFragment = [" + newFragment + "]");
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}


