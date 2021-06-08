package com.md.achadoseperdidos.Activity;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.LayoutInflaterCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthWebException;
import com.google.firebase.auth.FirebaseUser;
import com.md.achadoseperdidos.R;
import com.md.achadoseperdidos.databinding.ActivityLoginBinding;
import com.mikepenz.iconics.context.IconicsLayoutInflater2;
import com.muddzdev.styleabletoast.StyleableToast;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    private static final String TAG = "LoginActivity";

    private EditText userMail, userPassword;
    private Button btnLogin;
    private ProgressBar loginProgressBar;
    private FirebaseAuth mAuth;
    private Intent HomeActivity;

    // Google Sign In
    private static final int MY_REQUEST_CODE = 7117; // Qualquer número que você quiser.
    Button btnLogarGoogle;
    List<AuthUI.IdpConfig> providers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate() called with: savedInstanceState = [" + savedInstanceState + "]");
        LayoutInflaterCompat.setFactory2(getLayoutInflater(), new IconicsLayoutInflater2(getDelegate()));
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        //mBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout._activity_layout, null, false);

        userMail = findViewById(R.id.login_mail);
        userPassword = findViewById(R.id.login_password);
        btnLogin = findViewById(R.id.btn_login);
        loginProgressBar = findViewById(R.id.loginProgressBar);
        btnLogarGoogle = findViewById(R.id.login_google);
        HomeActivity = new Intent(this, com.md.achadoseperdidos.Activity.Home.class);

        // Firebase
        mAuth = FirebaseAuth.getInstance();

        loginProgressBar.setVisibility(View.INVISIBLE);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginProgressBar.setVisibility(View.VISIBLE);
                btnLogin.setVisibility(View.INVISIBLE);

                final String mail = userMail.getText().toString();
                final String password = userPassword.getText().toString();

                if(mail.isEmpty() || password.isEmpty()){
                    StyleableToast.makeText(LoginActivity.this, getResources().getString(R.string.fildsLogin), Toast.LENGTH_LONG, R.style.advertenciaToast).show();
                    btnLogin.setVisibility(View.VISIBLE);
                    loginProgressBar.setVisibility(View.INVISIBLE);
                } else {
                    signIn(mail, password);
                    Log.d(TAG, "onClick() returned: " + mail + password);
                }
            }
        });

        //Logar com o Google
        btnLogarGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick() called with: v = [" + v + "]");
                providers = Arrays.asList(new AuthUI.IdpConfig.GoogleBuilder().build());

                showSignInOptions();
            }
        });
    }

    // Google
    private void showSignInOptions() {
        Log.d(TAG, "showSignInOptions() called");
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTheme(R.style.AppTheme)
                        .build(), MY_REQUEST_CODE
        );
    }

    // Google
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
        if (requestCode == MY_REQUEST_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                //Get User
                updateUI();
                //Mostrar email no Toast
                //StyleableToast.makeText(LoginActivity.this, "" + user.getEmail(), Toast.LENGTH_LONG, R.style.sucessoToast).show();

            } else {
                StyleableToast.makeText(LoginActivity.this, "" + response.getError().getMessage(), Toast.LENGTH_LONG, R.style.erroToast).show();
            }
        }
    }

    private void signIn(String mail, String password) {
        Log.d(TAG, "signIn() called with: mail = [" + mail + "], password = [" + password + "]");
        mAuth.signInWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    loginProgressBar.setVisibility(View.INVISIBLE);
                    btnLogin.setVisibility(View.VISIBLE);
                    updateUI();
                } else {
                    // Criação da conta falhou!
                    String erroExcecao;

                    try{
                        Log.e(TAG, "onComplete: ", task.getException());
                        throw task.getException();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        erroExcecao = getResources().getString(R.string.invalidPassEmailLogin);
                    } catch (FirebaseAuthInvalidUserException e) {
                        erroExcecao = getResources().getString(R.string.userInvalidLogin);
                    } catch (FirebaseAuthWebException e) {
                        erroExcecao = getResources().getString(R.string.connectionErrorLogin);
                    } catch (Exception e){
                        erroExcecao = getResources().getString(R.string.errorLogin);
                        e.printStackTrace();
                    }

                    StyleableToast.makeText(LoginActivity.this, erroExcecao, Toast.LENGTH_LONG, R.style.advertenciaToast).show();
                    btnLogin.setVisibility(View.VISIBLE);
                    loginProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void updateUI() {
        Log.d(TAG, "updateUI() called");
        startActivity(HomeActivity);
        finish();
    }

    public void signup(View view) {
        Log.d(TAG, "signup() called with: view = [" + view + "]");
        startActivity(new Intent(this, RegisterActivity.class));
    }

    public void forgot(View view) {
        Log.d(TAG, "forgot() called with: view = [" + view + "]");
        startActivity(new Intent(this, ForgotActivity.class));
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart() called");
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();

        if(user != null) {
            //usuario já está conectado, assim redireciona ele para a tela home
            updateUI();

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected() called with: item = [" + item + "]");
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return true;
    }

    public void btnGoBack(View view) {
        Log.d(TAG, "btnGoBack() called with: view = [" + view + "]");
        Intent intent = new Intent(LoginActivity.this, EnterActivity.class);
        startActivity(intent);
        finishAffinity();
    }
}
