package com.md.achadoseperdidos.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.md.achadoseperdidos.Activity.Home;
import com.md.achadoseperdidos.Activity.PostDetailActivity;
import com.md.achadoseperdidos.Adapters.PostAdapter;
import com.md.achadoseperdidos.Models.Post;
import com.md.achadoseperdidos.R;
import com.muddzdev.styleabletoast.StyleableToast;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FinishFragment extends Fragment implements PostAdapter.OnPostClickListener {

    private RecyclerView postRecyclerView;
    private PostAdapter postAdapter;
    //private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    //private FirebaseAuth mAuth;
    //private FirebaseUser currentUser;
    private List<Post> postList;

    private SwipeRefreshLayout swipeRefreshLayout;


    public FinishFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // ini
        //mAuth = FirebaseAuth.getInstance();
        //currentUser = mAuth.getCurrentUser();

        //View
        View fragmentView = inflater.inflate(R.layout.fragment_finish, container, false);
        postRecyclerView = fragmentView.findViewById(R.id.postFinish);

        //RecyclerView Data
        postRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        postRecyclerView.setHasFixedSize(true);
        //firebaseDatabase = FirebaseDatabase.getInstance();

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

        // Reverse Layout
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        // Refreshing
        swipeRefreshLayout = fragmentView.findViewById(R.id.swipeContainer2);

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
                            StyleableToast.makeText(getContext(), "Sem conex??o com a internet!", Toast.LENGTH_LONG, R.style.advertenciaToast).show();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }, 300);
            }
        });

        // Set the layout manager to your recyclerview
        postRecyclerView.setLayoutManager(mLayoutManager);

        // menu
        setHasOptionsMenu(true);

        return fragmentView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_main, menu); // Menu para procurar
        MenuItem search = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) search.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                firebaseSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                firebaseSearch(newText);
                return false;
            }
        });

        super.onCreateOptionsMenu(menu,inflater);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void firebaseSearch(String searchText){

        Query firebaseSearchQuery = databaseReference.orderByChild("title").startAt(searchText).endAt(searchText + "\uf8ff");


        firebaseSearchQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList = new ArrayList<>();
                for (DataSnapshot postsnap: dataSnapshot.getChildren()){
                    Post post = postsnap.getValue(Post.class);
                    postList.add(post);
                }

                postAdapter = new PostAdapter(getActivity(), postList);
                postAdapter.OnPostClickListener(FinishFragment.this);
                postRecyclerView.setAdapter(postAdapter);
                postRecyclerView.setHasFixedSize(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // Query ?? a exce????o para apenas pegar items ACHADOS ou PERDIDOS!!
    @Override
    public void onStart(){
        super.onStart();

        databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = databaseReference.orderByChild("category").equalTo("Finalizado");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList = new ArrayList<>();
                for (DataSnapshot postsnap: dataSnapshot.getChildren()){
                    Post post = postsnap.getValue(Post.class);
                    postList.add(post);
                }

                postAdapter = new PostAdapter(getActivity(), postList);
                postAdapter.OnPostClickListener(FinishFragment.this);
                postRecyclerView.setAdapter(postAdapter);
                postRecyclerView.setNestedScrollingEnabled(false);
                postRecyclerView.setHasFixedSize(true);

                //popAddPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
}
