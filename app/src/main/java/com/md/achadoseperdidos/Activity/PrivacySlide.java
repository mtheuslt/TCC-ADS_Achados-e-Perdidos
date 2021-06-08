package com.md.achadoseperdidos.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.github.paolorotolo.appintro.ISlidePolicy;
import com.google.firebase.database.annotations.Nullable;
import com.md.achadoseperdidos.R;
import com.muddzdev.styleabletoast.StyleableToast;

public class PrivacySlide extends Fragment implements ISlidePolicy {
    private static final String TAG = "PrivacySlide";
    private TextView textTerms, textPrivacity;
    private CheckBox checkBox;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_privacy, container, false);
        textTerms = view.findViewById(R.id.textTerms);
        textPrivacity = view.findViewById(R.id.textPolicy);

        textTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick() called with: v = [" + v + "]");
                Log.i(TAG, "onClick: textTerms");
                startActivity(new Intent(getActivity(), TermsActivity.class));
            }
        });

        textPrivacity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick() called with: v = [" + v + "]");
                Log.i(TAG, "onClick: textPrivacity");
                startActivity(new Intent(getActivity(), PolicyActivity.class));
            }
        });


        checkBox = view.findViewById(R.id.checkBox);

        return view;
    }

    @Override
    public boolean isPolicyRespected() {
        Log.d(TAG, "isPolicyRespected() called");
        return checkBox.isChecked();
        // If user should be allowed to leave this slide
    }

    @Override
    public void onUserIllegallyRequestedNextPage() {
        Log.d(TAG, "onUserIllegallyRequestedNextPage() called");
        // User illegally requested next slide
        StyleableToast.makeText(getActivity(), getResources().getString(R.string.termsPlease), Toast.LENGTH_LONG, R.style.erroToast).show();
    }
}

