package com.md.achadoseperdidos.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.md.achadoseperdidos.R;
import com.md.achadoseperdidos.databinding.ActivityRegisterBinding;
import com.mikepenz.iconics.context.IconicsLayoutInflater2;
import com.muddzdev.styleabletoast.StyleableToast;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {
    ActivityRegisterBinding binding;
    private static final String TAG = "RegisterActivity";

    CircleImageView ImgUserPhoto;
    static  int PReqCode = 1;
    static  int REQUESCODE = 1;
    private Uri pickedImgUri = null;

    private EditText userEmail, userPassword, userName;
    private ProgressBar loadingProgress;
    private Button regBtn;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory2(getLayoutInflater(), new IconicsLayoutInflater2(getDelegate()));
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        Log.d(TAG, "onCreate() called with: savedInstanceState = [" + savedInstanceState + "]");

        // Views
        userEmail = findViewById(R.id.regMail);
        userPassword = findViewById(R.id.regPassword);
        userName = findViewById(R.id.regName);
        loadingProgress = findViewById(R.id.regProgressBar);
        regBtn = findViewById(R.id.regBtn);

        // Autenticação do Firebase
        mAuth = FirebaseAuth.getInstance();

        // Progress Bar Invisible
        loadingProgress.setVisibility(View.INVISIBLE);

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick() called with: v = [" + v + "]");
                Log.i(TAG, "onClick: regBtn");
                regBtn.setVisibility(View.INVISIBLE);
                loadingProgress.setVisibility(View.VISIBLE);
                final String email = userEmail.getText().toString();
                final String password = userPassword.getText().toString();
                final String name = userName.getText().toString();


                if (email.isEmpty() || name.isEmpty() || password.isEmpty() || pickedImgUri == null) {
                    // Mostra mensagem de erro!
                    StyleableToast.makeText(RegisterActivity.this, getResources().getString(R.string.validRegister), Toast.LENGTH_LONG, R.style.advertenciaToast).show();
                    regBtn.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                } else {
                    // se todos os campos estiverem preenchidos
                    // metodo CreateUserAccount irá tentar criar o usuario se o e-mail for valido.
                    CreateUserAccount(email,name,password);
                }
            }
        });

        // View Foto
        ImgUserPhoto = findViewById(R.id.regUserPhoto);
        //ImgUserPhoto = findViewById(R.id.regUserPhoto);

        // Colocar a foto no registro.
        ImgUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick() called with: v = [" + v + "]");
                Log.i(TAG, "onClick: ImgUserPhoto");
                if (Build.VERSION.SDK_INT >= 22) {
                    checkAndRequestForPermission();
                } else {
                    openGallery();
                }
            }
        });
    }

    private void CreateUserAccount(String email, final String name, final String password) {
        Log.d(TAG, "CreateUserAccount() called with: email = [" + email + "], name = [" + name + "], password = [" + password + "]");

        // Este método cria uma conta de usuario com o email e a senha especificada.
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            // Conta do úsuario criada com sucesso
                            //StyleableToast.makeText(RegisterActivity.this, "Usuário cadastrado com sucesso!", Toast.LENGTH_LONG, R.style.sucessoToast).show();

                            // Atualização do nome e da foto do úsuario
                            updateUserInfo(name ,pickedImgUri,mAuth.getCurrentUser());

                        } else {
                            // Criação da conta falhou!
                            String erroExcecao;

                            try{
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e){
                                erroExcecao = getResources().getString(R.string.passwordStrong);
                            } catch (FirebaseAuthInvalidCredentialsException e){
                                erroExcecao = getResources().getString(R.string.emailInvalid);
                            } catch (FirebaseAuthUserCollisionException e){
                                erroExcecao = getResources().getString(R.string.emailRegistered);
                            } catch (Exception e){
                                erroExcecao = getResources().getString(R.string.errorRegister);
                                e.printStackTrace();
                            }
                            StyleableToast.makeText(RegisterActivity.this, erroExcecao, Toast.LENGTH_LONG, R.style.erroToast).show();
                            regBtn.setVisibility(View.VISIBLE);
                            loadingProgress.setVisibility(View.INVISIBLE);

                        }
                    }
                });
    }

    // Atualização da foto e do nome do úsuario
    private void updateUserInfo(final String name, Uri pickedImgUri, final FirebaseUser currentUser) {
        Log.d(TAG, "updateUserInfo() called with: name = [" + name + "], pickedImgUri = [" + pickedImgUri + "], currentUser = [" + currentUser + "]");
        // Firebase Storage - Upload da photo no firebase e depois pegar a URL

        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
        final StorageReference imageFilePath = mStorage.child(pickedImgUri.getLastPathSegment());

        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Upload da imagem aconteceu com sucesso
                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // URI contem a URL da imagem do úsuario.
                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .setPhotoUri(uri)
                                .build();

                        currentUser.updateProfile(profileUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        // informações do úsuario atualizadas com sucesso
                                        StyleableToast.makeText(RegisterActivity.this, getResources().getString(R.string.userRegisterSucess), Toast.LENGTH_LONG, R.style.sucessoToast).show();
                                        updateUI();

                                    }
                                });
                    }
                });

            }
        });
    }


    private void updateUI() {
        Log.d(TAG, "updateUI() called");
        Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(loginActivity);
        finish();
    }

    private void openGallery() {
        Log.d(TAG, "openGallery() called");
        // TODO: abre a view galeria e espera o usuario escolher uma imagem !
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.imagePick)), REQUESCODE);
        //Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        //galleryIntent.setType("image/*");
       // startActivityForResult(galleryIntent, REQUESCODE);
    }

    // Checa a permissão de acesso para a galeria!
    private void checkAndRequestForPermission() {
        Log.d(TAG, "checkAndRequestForPermission() called");

        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                StyleableToast.makeText(RegisterActivity.this, getResources().getString(R.string.permissionHome), Toast.LENGTH_LONG, R.style.advertenciaToast).show();

            } else {
                ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PReqCode);
            }

        } else
            openGallery();
    }


    public void login(View view) {
        Log.d(TAG, "login() called with: view = [" + view + "]");
        startActivity(new Intent(this, LoginActivity.class));
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

    // Botão para testar o Plugin Crashlytics
    /*public void forceCrash(View view) {
        //throw new RuntimeException("This is a crash");
    }*/

    public void btnGoBack(View view) {
        Log.d(TAG, "btnGoBack() called with: view = [" + view + "]");
        Intent intent = new Intent(RegisterActivity.this, EnterActivity.class);
        startActivity(intent);
        finishAffinity();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data != null ) {
            // O usuario escolheu com sucesso uma imagem
            pickedImgUri = data.getData();
            ImgUserPhoto.setImageURI(pickedImgUri);

        }
    }
}
