package com.md.achadoseperdidos.Activity;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.md.achadoseperdidos.R;
import com.ortiz.touchview.TouchImageView;
import com.squareup.picasso.Picasso;


import java.util.Calendar;
import java.util.Locale;

public class PostDetailActivity extends AppCompatActivity {

    ImageView imgPost,imgUserPost;
    TextView txtPostDesc, txtPostDateName, txtPostTitle, txtPostContact, txtPostEmail, txtPostLocation;
    private static final String TAG = "PostDetailActivity";
    ImageView btnPostCelphone, btnPostEmail;
    String PostKey;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        Log.d(TAG, "onCreate() called with: savedInstanceState = [" + savedInstanceState + "]");

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        // ini Views
        imgPost = findViewById(R.id.post_detail_img);
        imgUserPost = findViewById(R.id.post_detail_user_img);

        // set title
        getSupportActionBar().setTitle(getResources().getString(R.string.details));

        txtPostTitle = findViewById(R.id.post_detail_title);
        txtPostDesc = findViewById(R.id.post_detail_desc);
        txtPostDateName = findViewById(R.id.post_detail_date);
        txtPostContact = findViewById(R.id.post_detail_contact);
        txtPostEmail = findViewById(R.id.post_detail_email);
        txtPostLocation = findViewById(R.id.post_detail_location);
        //btnPostCelphone = findViewById(R.id.post_button_celphone);
        //btnPostEmail = findViewById(R.id.post_button_email);

        // icones no txtview
        txtPostContact.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_phone, 0, 0,  0);
        txtPostEmail.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_emai, 0, 0, 0);
        txtPostLocation.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_place, 0, 0, 0);

        // padding nos icones
        txtPostContact.setCompoundDrawablePadding( txtPostContact.getResources().getDimensionPixelOffset(R.dimen.fab_margin)); // fab_margin = 16dp
        txtPostEmail.setCompoundDrawablePadding( txtPostContact.getResources().getDimensionPixelOffset(R.dimen.fab_margin));
        txtPostLocation.setCompoundDrawablePadding( txtPostContact.getResources().getDimensionPixelOffset(R.dimen.fab_margin));

        // colocar todas as informações com o padrão do PostAdapter
        imgPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // visualizar a imagem do post melhor
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(PostDetailActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_custom_layout, null);
                TouchImageView photoView = mView.findViewById(R.id.photoView);
                String postImage = getIntent().getExtras().getString("postImage");
                Glide.with(PostDetailActivity.this).load(postImage).into(photoView);
                photoView.setImageResource(R.drawable.ic_background_image);
                mBuilder.setView(mView);
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });

        String postImage = getIntent().getExtras().getString("postImage");
        Glide.with(this).load(postImage).into(imgPost);

        String postTitle = getIntent().getExtras().getString("title");
        txtPostTitle.setText(postTitle);

        String postDescription = getIntent().getExtras().getString("description");
        txtPostDesc.setText(postDescription);

        final String postContact = getIntent().getExtras().getString("contact");
        txtPostContact.setText(postContact);

        final String postEmail = getIntent().getExtras().getString("email");
        txtPostEmail.setText(postEmail);

        final String postLocation = getIntent().getExtras().getString("location");
        txtPostLocation.setText(postLocation);

        //entrar no google maps
        /*txtPostLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri mapUri = Uri.parse("geo:0,0?q=" + Uri.encode(postLocation));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });*/

        txtPostLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick() called with: v = [" + v + "]");
                Log.i(TAG, "onClick: txtPostLocation");
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://maps.google.co.in/maps?q=" + postLocation));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        // mandar email
        /*btnPostEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", postEmail, null));
                startActivity(new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", postEmail, null)));
            }
        });*/

        txtPostEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick() called with: v = [" + v + "]");
                Log.i(TAG, "onClick: txtPostEmail");
                startActivity(new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", postEmail, null)));
            }
        });

        // Ligar para o número
        /*btnPostCelphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", postContact, null)));
            }
        });*/

        txtPostContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick() called with: v = [" + v + "]");
                Log.i(TAG, "onClick: txtPostContact");
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", postContact, null)));
            }
        });

        String userpostImage = getIntent().getExtras().getString("userPhoto");
        Glide.with(this).load(userpostImage).into(imgUserPost);

        // post id
        PostKey = getIntent().getExtras().getString("postKey");

        String date = timestampToString(getIntent().getExtras().getLong("postDate"));
        txtPostDateName.setText(date);

    }

    private String timestampToString(long time) {
        Log.d(TAG, "timestampToString() called with: time = [" + time + "]");

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(time);
        String postName = getIntent().getExtras().getString("userName");
        String dateFormat = DateFormat.format("dd MMMM yyyy, hh:mm:ss", calendar).toString();
        String date = dateFormat +  " | por " + postName;
        return date;
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

}
