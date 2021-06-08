package com.md.achadoseperdidos.Activity;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.md.achadoseperdidos.R;
import com.muddzdev.styleabletoast.StyleableToast;

public class ForgotActivity extends AppCompatActivity {

    EditText userEmail;
    Button userPass;

    FirebaseAuth firebaseAuth;
    private static final String TAG = "ForgotActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() called with: savedInstanceState = [" + savedInstanceState + "]");
        setContentView(R.layout.activity_forgot);

        userEmail = findViewById(R.id.et_email_address);
        userPass = findViewById(R.id.btn_recovery);

        firebaseAuth = FirebaseAuth.getInstance();

        userPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: userPass");
                String email = userEmail.getText().toString();

                if (email.equals("")) {
                    StyleableToast.makeText(ForgotActivity.this, getResources().getString(R.string.emailCamp), Toast.LENGTH_LONG, R.style.advertenciaToast).show();
                } else {
                    firebaseAuth.sendPasswordResetEmail(userEmail.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(ForgotActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        StyleableToast.makeText(ForgotActivity.this, getResources().getString(R.string.forgotSucess), Toast.LENGTH_LONG, R.style.sucessoToast).show();
                                    } else {
                                        Log.i(TAG, "onComplete: sendPasswordResetEmail");
                                        StyleableToast.makeText(ForgotActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG, R.style.erroToast).show();
                                    }
                                }
                            });
                }

            }
        });

    }
    public void btnVoltar(View view) {
        Log.d(TAG, "btnVoltar() called with: view = [" + view + "]");
        super.onBackPressed();
    }
}
