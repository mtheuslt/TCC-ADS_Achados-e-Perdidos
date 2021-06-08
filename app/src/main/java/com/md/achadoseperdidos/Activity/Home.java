package com.md.achadoseperdidos.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.md.achadoseperdidos.Adapters.PostAdapter;
import com.md.achadoseperdidos.Fragments.FinishFragment;
import com.md.achadoseperdidos.Fragments.FoundFragment;
import com.md.achadoseperdidos.Fragments.HomeFragment;
import com.md.achadoseperdidos.Fragments.LostFragment;
import com.md.achadoseperdidos.Fragments.ProfileFragment;
import com.md.achadoseperdidos.Utils.MaskEditUtil;
import com.md.achadoseperdidos.Models.Post;
import com.md.achadoseperdidos.R;
import com.md.achadoseperdidos.Utils.SharedPref;
import com.muddzdev.styleabletoast.StyleableToast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, PostAdapter.OnPostClickListener {

    private static final String TAG = "Home";
    private static final int PReqCode = 2;
    private static final int REQUESCODE = 2;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;
    Dialog popAddPost;
    ImageView popupPostImag, popupAddBtn;
    CircleImageView popupUserImage;
    TextView popupTitle, popupDescription, popupContact;
    AutoCompleteTextView popupSearchMap;
    Spinner popupSpinner;
    ProgressBar popupClickProgress;
    private static Uri pickedImgUri;
    private SharedPref sharedPref;


    @SuppressWarnings("FieldCanBeLocal")
    // NavigationView
    private BottomNavigationView mainBottomNav;
    private HomeFragment homeFragment;
    private FoundFragment foundFragment;
    private LostFragment lostFragment;
    private ProfileFragment profileFragment;
    private FinishFragment finishFragment;
    private List<Post> mData;
    private PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() called with: savedInstanceState = [" + savedInstanceState + "]");
        loadLocale();
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // notify
        postAdapter = new PostAdapter(this, mData);
        //listView3.setAdapter(adapter);

        // set title
        getSupportActionBar().setTitle(getResources().getString(R.string.homeMenu));

        // ini
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference("Posts");
        mData = new ArrayList<>();

        // ini popup
        iniPopup();
        setupPopupImageClick();

        //Nav Bar (Home, Found and Lost Buttons Main)
        mainBottomNav = findViewById(R.id.mainBottomNav);

        // FRAGMENTS
        homeFragment = new HomeFragment();
        foundFragment = new FoundFragment();
        lostFragment = new LostFragment();
        profileFragment = new ProfileFragment();
        finishFragment = new FinishFragment();

        mainBottomNav.setSelectedItemId(R.id.bottom_action_home);

        mainBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.d(TAG, "onNavigationItemSelected() called with: item = [" + item + "]");
                switch (item.getItemId()){
                    case R.id.bottom_action_home:
                        getSupportActionBar().setTitle(getResources().getString(R.string.homeMenu));
                        getSupportFragmentManager().beginTransaction().setTransition( FragmentTransaction.TRANSIT_FRAGMENT_OPEN ).
                                replace(R.id.container, homeFragment).addToBackStack("Home").commit();
                        Log.i(TAG, "onNavigationItemSelected: bottom_action_home");
                        return true;

                    case R.id.bottom_action_found:
                        getSupportActionBar().setTitle(getResources().getString(R.string.foundMenu));
                        getSupportFragmentManager().beginTransaction().setTransition( FragmentTransaction.TRANSIT_FRAGMENT_OPEN ).
                                replace(R.id.container, foundFragment).addToBackStack("Achado").commit();
                        Log.i(TAG, "onNavigationItemSelected: bottom_action_found");
                        return true;

                    case R.id.bottom_action_lost:
                        getSupportActionBar().setTitle(getResources().getString(R.string.lostMenu));
                        getSupportFragmentManager().beginTransaction().setTransition( FragmentTransaction.TRANSIT_FRAGMENT_OPEN ).
                                replace(R.id.container, lostFragment).addToBackStack("Perdido").commit();
                        Log.i(TAG, "onNavigationItemSelected: bottom_action_lost");
                        return true;

                    case R.id.bottom_action_complete:
                        getSupportActionBar().setTitle(getResources().getString(R.string.finishMenu));
                        getSupportFragmentManager().beginTransaction().setTransition( FragmentTransaction.TRANSIT_FRAGMENT_OPEN ).
                                replace(R.id.container, finishFragment).addToBackStack("Finalizado").commit();
                        Log.i(TAG, "onNavigationItemSelected: bottom_action_complete");
                        return true;

                    case R.id.bottom_action_profile:
                        getSupportActionBar().setTitle(getResources().getString(R.string.profileMenu));
                        getSupportFragmentManager().beginTransaction().setTransition( FragmentTransaction.TRANSIT_FRAGMENT_OPEN ).
                                replace(R.id.container, profileFragment).addToBackStack("Perfil").commit();
                        Log.i(TAG, "onNavigationItemSelected: bottom_action_profile");
                        return true;

                    default:
                        return false;
                }
            }
        });


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popAddPost.show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        updateNavHeader();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();

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

    private void setupPopupImageClick() {
        Log.d(TAG, "setupPopupImageClick() called");
        popupPostImag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir a galeria e checar se tem acesso as fotos
                checkAndRequestForPermission();


            }
        });
    }

    private void checkAndRequestForPermission() {
        Log.d(TAG, "checkAndRequestForPermission() called");

        if (ContextCompat.checkSelfPermission(Home.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Home.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                StyleableToast.makeText(Home.this, getResources().getString(R.string.permissionHome), Toast.LENGTH_LONG, R.style.advertenciaToast).show();

            } else {
                ActivityCompat.requestPermissions(Home.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, PReqCode);

            }

        } else
            // tudo ocorreu bem
            openImageIntent();
    }

    // Abre as opções de galeria e camera
    private void openImageIntent() {
        // TODO: abre a view galeria e espera o usuario escolher uma imagem !
        // TODO: abre a view camera e espera o usuario tirar uma foto

        Log.d(TAG, "openImageIntent() called");

        File outputFile = null;
        try {
            outputFile = File.createTempFile("tmp", ".jpg", getCacheDir());
        } catch (IOException pE) {
            pE.printStackTrace();
        }
        pickedImgUri = Uri.fromFile(outputFile);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for(ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_PICK);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, getResources().getString(R.string.selectHome));

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivityForResult(chooserIntent, REQUESCODE);

        Log.d(TAG, "openImageIntent() returned: " + REQUESCODE);
    }

    // Quando o usuario escolhe uma imagem ou tira uma foto
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUESCODE) {
                Bitmap bmp = null;
                if (data.hasExtra("data")) {
                    Bundle extras = data.getExtras();
                    bmp = (Bitmap) extras.get("data");
                } else {
                    AssetFileDescriptor fd = null;
                    try {
                        fd = getContentResolver().openAssetFileDescriptor(data.getData(), "r");
                    } catch (FileNotFoundException pE) {
                        pE.printStackTrace();
                    }
                    bmp = BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor());
                }
                try {
                    FileOutputStream out = new FileOutputStream(new File(pickedImgUri.getPath()));
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                popupPostImag.setImageURI(pickedImgUri);
                popupPostImag.setScaleType(ImageView.ScaleType.CENTER_CROP);

            }
        }
    }


    private void iniPopup() {

        Log.d(TAG, "iniPopup() called");

        popAddPost = new Dialog(this);
        popAddPost.setContentView(R.layout.popup_add_post);
        popAddPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popAddPost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
        popAddPost.getWindow().getAttributes().gravity = Gravity.TOP;

        // ini popup widgets
        popupUserImage = popAddPost.findViewById(R.id.popup_user_image);
        popupPostImag = popAddPost.findViewById(R.id.popup_img);
        popupTitle = popAddPost.findViewById(R.id.popup_title);
        popupContact = popAddPost.findViewById(R.id.popup_contact);
        popupDescription = popAddPost.findViewById(R.id.popup_description);
        popupSpinner = popAddPost.findViewById(R.id.my_spinner);
        popupAddBtn = popAddPost.findViewById(R.id.popup_update);
        popupClickProgress = popAddPost.findViewById(R.id.popup_progressBar);

        // Pega a imagem de usuario e coloca no add post
        Glide.with(Home.this).load(currentUser.getPhotoUrl()).into(popupUserImage);

        // Coloca uma imagem padrão no popupPostImag
        //popupPostImag.setImageDrawable(getResources().getDrawable(R.drawable.add_image));

        popupContact.addTextChangedListener(MaskEditUtil.mask((EditText) popupContact, "(##) #####-####"));

        // Mostra opções no edittext de procurar (Api Google Maps)
        popupSearchMap = popAddPost.findViewById(R.id.popup_search_map);

        String [] PLACES = getResources().getStringArray(R.array.Places);
        popupSearchMap.setAdapter(new ArrayAdapter<>(Home.this, android.R.layout.simple_list_item_1, PLACES));

        //popupContact = Double.parseDouble(getText().toString());

        // Add post click Listener
        popupAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupAddBtn.setVisibility(View.INVISIBLE);
                popupClickProgress.setVisibility(View.VISIBLE);

                // testar os campos
                if (!popupTitle.getText().toString().isEmpty() && !popupDescription.getText().toString().isEmpty() && !popupContact.getText().toString().isEmpty() &&
                        !(popupContact.length() < 11) && !popupSearchMap.getText().toString().isEmpty() && pickedImgUri!= null) {
                    // todos os campos estão preenchidos
                    // TODO: Criação do Objeto Post e Adiciona no Firebase Database
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("blog_images");

                    final StorageReference imageFilePath = storageReference.child(pickedImgUri.getLastPathSegment());

                    imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageDownloadLink = uri.toString();
                                    String spinnerText = popupSpinner.getSelectedItem().toString();
                                    // Criação do post Object
                                    Post post = new Post(currentUser.getDisplayName(), popupTitle.getText().toString(), popupDescription.getText().toString(), spinnerText,
                                            popupContact.getText().toString(), popupSearchMap.getText().toString(), currentUser.getEmail(), imageDownloadLink, currentUser.getUid(),
                                            currentUser.getPhotoUrl().toString());

                                    // adiciona o post ao firebase database
                                    addPost(post);
                                    //postAdapter.insertPost(0, post);
                                    //int position = 0;
                                    //postAdapter.addPostSize(position, post);
                                    //postAdapter.notifyDataSetChanged();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // alguma falha com o upload da imagem aconteceu
                                    StyleableToast.makeText(Home.this, e.getMessage(), Toast.LENGTH_LONG, R.style.erroToast).show();
                                    popupClickProgress.setVisibility(View.INVISIBLE);
                                    popupAddBtn.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    });
                /*} else if (!(popupContact.getText().toString().length() < 11)){
                    StyleableToast.makeText(Home.this, "Número de contato não é valido!", Toast.LENGTH_LONG, R.style.advertenciaToast).show();
                    popupAddBtn.setVisibility(View.VISIBLE);
                    popupClickProgress.setVisibility(View.INVISIBLE);*/
                } else {
                    if (popupContact.length() < 11){
                        StyleableToast.makeText(Home.this, getResources().getString(R.string.contactValidHome), Toast.LENGTH_LONG, R.style.advertenciaToast).show();
                        popupAddBtn.setVisibility(View.VISIBLE);
                        popupClickProgress.setVisibility(View.INVISIBLE);
                    } else {
                        StyleableToast.makeText(Home.this, getResources().getString(R.string.fildsValidHome), Toast.LENGTH_LONG, R.style.advertenciaToast).show();
                        popupAddBtn.setVisibility(View.VISIBLE);
                        popupClickProgress.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }

    private void addPost(Post post) {
        Log.d(TAG, "addPost() called with: post = [" + post + "]");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Posts").push();

        // pega o ID unico do post e  atualiza a chave(key) do post
        String key = myRef.getKey();
        post.setPostKey(key);

        // adiciona o conteudo do post no firebase database e remove os dados no popup
        myRef.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                StyleableToast.makeText(Home.this, getResources().getString(R.string.itemHome), Toast.LENGTH_LONG, R.style.sucessoToast).show();
                popupAddBtn.setVisibility(View.VISIBLE);
                popupClickProgress.setVisibility(View.INVISIBLE);
                popupDescription.setText("");
                popupTitle.setText("");
                popupContact.setText("");
                popupSearchMap.setText("");
                Drawable drawable = ContextCompat.getDrawable(Home.this, R.drawable.ic_image_add);
                popupPostImag.setImageDrawable(drawable);
                popupPostImag.setScaleType(ImageView.ScaleType.FIT_CENTER);
                pickedImgUri.equals(null);

                popAddPost.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed() called");
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialog);
            builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Home.super.onBackPressed();
                }
            });
            builder.setMessage(getResources().getString(R.string.closeHome));
            builder.setNegativeButton(getResources().getString(R.string.no), null);
            builder.show();
            //additional code
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    /*@Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("SAIR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Home.super.onBackPressed();
            }
        });
        builder.setMessage("Quer sair do aplicativo?");
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }*/

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        Log.d(TAG, "onNavigationItemSelected() called with: item = [" + item + "]");
        int id = item.getItemId();

        if (id == R.id.nav_home) {

            getSupportActionBar().setTitle(getResources().getString(R.string.homeMenu));
            mainBottomNav.setSelectedItemId(R.id.bottom_action_home);
            getSupportFragmentManager().beginTransaction().setTransition( FragmentTransaction.TRANSIT_FRAGMENT_OPEN ).
                    replace(R.id.container, homeFragment).addToBackStack("Home").commit();

        } else if (id == R.id.nav_profile) {

            getSupportActionBar().setTitle(getResources().getString(R.string.profileMenu));
            mainBottomNav.setSelectedItemId(R.id.bottom_action_profile);
            getSupportFragmentManager().beginTransaction().setTransition( FragmentTransaction.TRANSIT_FRAGMENT_OPEN ).
                    replace(R.id.container, profileFragment).addToBackStack("Perfil").commit();

        } else if (id == R.id.nav_settings) {

            //getSupportActionBar().setTitle("Configuração");
            Intent settingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(settingsActivity);

        } else if (id == R.id.nav_about) {

            //getSupportActionBar().setTitle("Sobre");
            Intent aboutActivity = new Intent(this, AboutActivity.class);
            startActivity(aboutActivity);
            //getSupportFragmentManager().beginTransaction().replace(R.id.container, new AboutFragment()).commit();
            //getSupportFragmentManager().beginTransaction().replace(R.id.container, new AboutFragment()).commit();

        } else if (id == R.id.nav_signout){

            FirebaseAuth.getInstance().signOut();
            Intent loginActivity = new Intent(this, LoginActivity.class);
            startActivity(loginActivity);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void updateNavHeader(){
        Log.d(TAG, "updateNavHeader() called");
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.nav_username);
        TextView navUserMail = headerView.findViewById(R.id.nav_user_mail);
        CircleImageView navUserPhot = headerView.findViewById(R.id.nav_user_photo);

        navUserMail.setText(currentUser.getEmail());
        navUsername.setText(currentUser.getDisplayName());
        navUserPhot.setImageURI(currentUser.getPhotoUrl());

        // Utilizando Glide para carregar a imagem
        // Importado no gradle
        Glide.with(this).load(currentUser.getPhotoUrl()).into(navUserPhot);
    }

    @Override
    public void onPostClick(int position) {
        Log.d(TAG, "onPostClick() called with: position = [" + position + "]");
        Intent postDetailActivity = new Intent(Home.this, PostDetailActivity.class);
        //getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();

        //int position = getAdapterPosition();

        postDetailActivity.putExtra("title", mData.get(position).getTitle());
        postDetailActivity.putExtra("postImage", mData.get(position).getPicture());
        postDetailActivity.putExtra("description", mData.get(position).getDescription());
        postDetailActivity.putExtra("contact", mData.get(position).getContact());
        postDetailActivity.putExtra("location", mData.get(position).getLocation());
        postDetailActivity.putExtra("email", mData.get(position).getEmail());
        postDetailActivity.putExtra("postKey", mData.get(position).getPostKey());
        postDetailActivity.putExtra("userPhoto", mData.get(position).getUserPhoto());
        postDetailActivity.putExtra("userName", mData.get(position).getUserName());

        long timestamp = (long) mData.get(position).getTimeStamp();
        postDetailActivity.putExtra("postDate", timestamp);
        startActivity(postDetailActivity);
    }
}
