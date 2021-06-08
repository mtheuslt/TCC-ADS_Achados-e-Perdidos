package com.md.achadoseperdidos.Fragments;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.md.achadoseperdidos.Activity.ForgotActivity;
import com.md.achadoseperdidos.Activity.PostDetailActivity;
import com.md.achadoseperdidos.Adapters.PostAdapter;
import com.md.achadoseperdidos.Models.Post;
import com.md.achadoseperdidos.R;
import com.muddzdev.styleabletoast.StyleableToast;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements PostAdapter.OnPostClickListener {

    RecyclerView postRecyclerView;
    PostAdapter postAdapter;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    List<Post> postList;

    TextView userName, userEmail;
    CircleImageView userPhoto;

    SwipeRefreshLayout swipeRefreshLayout;

    public ProfileFragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        // ini
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //View
        View fragmentView = inflater.inflate(R.layout.fragment_profile, container, false);
        postRecyclerView = fragmentView.findViewById(R.id.postProfile);

        // Profile Data
        CircleImageView userPhoto = fragmentView.findViewById(R.id.profileImageUser);
        userName = fragmentView.findViewById(R.id.profileName);
        userEmail = fragmentView.findViewById(R.id.profileEmail);

        Glide.with(this).load(currentUser.getPhotoUrl()).into(userPhoto);
        userName.setText(currentUser.getDisplayName());
        userEmail.setText(currentUser.getEmail());

        //RecyclerView Data
        postRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        postRecyclerView.setHasFixedSize(true);
        firebaseDatabase = FirebaseDatabase.getInstance();

        // Swipe Refresh
        swipeRefreshLayout = fragmentView.findViewById(R.id.swipeContainer4);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isNetworkAvailable() == true){
                            postAdapter.clear();
                            postAdapter.notifyDataSetChanged();
                            onStart();
                            swipeRefreshLayout.setRefreshing(false);
                        } else {
                            StyleableToast.makeText(getContext(), "Sem conexão com a internet!", Toast.LENGTH_LONG, R.style.advertenciaToast).show();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }, 300);
            }
        });

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

        // Ultimo post sempre estará no topo
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        // Set the layout manager to your recyclerview
        postRecyclerView.setLayoutManager(mLayoutManager);

        // menu
        setHasOptionsMenu(true);

        // Profile

        return fragmentView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.profile_menu, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    // Recovering password
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_profile:
                // TODO Something when menu item selected
                Intent settingsActivity = new Intent(getActivity().getApplication(), ForgotActivity.class);
                startActivity(settingsActivity);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Query é a exceção para apenas pegar items ACHADOS ou PERDIDOS!!
    @Override
    public void onStart(){
        super.onStart();

        mAuth = FirebaseAuth.getInstance();
        String currentUserId = mAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = databaseReference.orderByChild("userId").equalTo(currentUserId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList = new ArrayList<>();
                for (DataSnapshot postsnap: dataSnapshot.getChildren()){
                    Post post = postsnap.getValue(Post.class);
                    postList.add(post);
                }

                postAdapter = new PostAdapter(getActivity(), postList);
                postAdapter.OnPostClickListener(ProfileFragment.this);
                postRecyclerView.setAdapter(postAdapter);
                postRecyclerView.setNestedScrollingEnabled(false);
                postRecyclerView.setHasFixedSize(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onPostClick(int position) {
        Intent postDetailActivity = new Intent(getActivity(), PostDetailActivity.class);
        //getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();

        //int position = getAdapterPosition();

        postDetailActivity.putExtra("title", postList.get(position).getTitle());
        postDetailActivity.putExtra("postImage", postList.get(position).getPicture());
        postDetailActivity.putExtra("description", postList.get(position).getDescription());
        postDetailActivity.putExtra("contact", postList.get(position).getContact());
        postDetailActivity.putExtra("location", postList.get(position).getLocation());
        postDetailActivity.putExtra("email", postList.get(position).getEmail());
        postDetailActivity.putExtra("postKey", postList.get(position).getPostKey());
        postDetailActivity.putExtra("userPhoto", postList.get(position).getUserPhoto());
        postDetailActivity.putExtra("userName", postList.get(position).getUserName());

        long timestamp = (long) postList.get(position).getTimeStamp();
        postDetailActivity.putExtra("postDate", timestamp);
        startActivity(postDetailActivity);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}

