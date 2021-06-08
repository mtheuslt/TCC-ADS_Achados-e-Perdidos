package com.md.achadoseperdidos.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.md.achadoseperdidos.Activity.Home;
import com.md.achadoseperdidos.Models.Post;
import com.md.achadoseperdidos.R;
import com.md.achadoseperdidos.Utils.SharedPref;
import com.muddzdev.styleabletoast.StyleableToast;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    private Context mContext;
    private static final String TAG = "PostAdapter";
    //private Post post;
    private List<Post> mData;
    private String currentUserId;
    private OnPostClickListener mListener;
    SharedPref sharedPref;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    public interface OnPostClickListener {
        void onPostClick(int position);
    }

    public void OnPostClickListener(OnPostClickListener listener){
        mListener = listener;
    }

    public PostAdapter(Context mContext, List<Post> mData) {
        Log.d(TAG, "PostAdapter() called with: mContext = [" + mContext + "], mData = [" + mData + "]");
        this.mContext = mContext;
        this.mData = mData;
    }
    /* Within the RecyclerView.Adapter class */

    // Clean all elements of the recycler
    public void clear() {
        Log.d(TAG, "clear() called");
        mData.clear();
        notifyDataSetChanged();
    }


    // Add a list of items -- change to type used
    public void addAll(List<Post> list) {
        mData.addAll(list);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder() called with: parent = [" + parent + "], viewType = [" + viewType + "]");
        View row = LayoutInflater.from(mContext).inflate(R.layout.row_post_item, parent, false);
        return new MyViewHolder(row);

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder() called with: holder = [" + holder + "], position = [" + position + "]");

        holder.tvTitle.setText(mData.get(position).getTitle());
        //holder.tvFoundOrLost.setText(mData.get(position).getCategory());

        String deviceLocale = Locale.getDefault().getLanguage();

        if (mData.get(position).getCategory().equals("Achado") && deviceLocale == "en"){
            holder.tvFoundOrLost.setText("Found");
        } else if (mData.get(position).getCategory().equals("Perdido") && deviceLocale == "en") {
            holder.tvFoundOrLost.setText("Lost");
        } else if (mData.get(position).getCategory().equals("Finalizado") && deviceLocale == "en") {
            holder.tvFoundOrLost.setText("Complete");
        } else {
            holder.tvFoundOrLost.setText(mData.get(position).getCategory());
        }
        
        holder.tvDesc.setText(mData.get(position).getDescription());
        Glide.with(mContext).load(mData.get(position).getPicture()).into(holder.imgPost);
        Glide.with(mContext).load(mData.get(position).getUserPhoto()).into(holder.imgPostProfile);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        final Boolean finish = mData.get(position).getCategory().equals("Finalizado");


        if (currentUserId.equals(mData.get(position).getUserId()) && finish == false) {
            holder.txtOptionDigit.setVisibility(View.VISIBLE);
            holder.txtOptionDelete.setVisibility(View.GONE);
            holder.txtOptionDelete.setVisibility(View.INVISIBLE);
            holder.txtOptionReport.setVisibility(View.GONE);
            holder.txtOptionReport.setVisibility(View.INVISIBLE);
        } else if (currentUserId.equals(mData.get(position).getUserId()) && finish == true) {
            holder.txtOptionDigit.setVisibility(View.INVISIBLE);
            holder.txtOptionDigit.setVisibility(View.GONE);
            holder.txtOptionDelete.setVisibility(View.VISIBLE);
            holder.txtOptionReport.setVisibility(View.GONE);
            holder.txtOptionReport.setVisibility(View.INVISIBLE);
        } else {
            holder.txtOptionReport.setVisibility(View.VISIBLE);
            holder.txtOptionDigit.setVisibility(View.GONE);
            holder.txtOptionDigit.setVisibility(View.INVISIBLE);
            holder.txtOptionDelete.setVisibility(View.GONE);
            holder.txtOptionDelete.setVisibility(View.INVISIBLE);
        }

        holder.txtOptionDigit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick() called with: v = [" + v + "]");
                Log.i(TAG, "onClick: txtOptionDigit - popupMenu");

                PopupMenu popupMenu = new PopupMenu(mContext, holder.txtOptionDigit);
                popupMenu.inflate(R.menu.option_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_item_complete:
                                AlertDialog.Builder msgBox2 = new AlertDialog.Builder(mContext, R.style.AlertDialog);
                                msgBox2.setTitle(mContext.getResources().getString(R.string.finishPost));
                                msgBox2.setIcon(mContext.getDrawable(R.drawable.ic_check_black));
                                msgBox2.setMessage(mContext.getResources().getString(R.string.finishOptionPostAdapter));

                                msgBox2.setPositiveButton(mContext.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // finish sucess
                                        StyleableToast.makeText(mContext, mContext.getResources().getString(R.string.toastFinishSucess), Toast.LENGTH_LONG, R.style.sucessoToast).show();

                                        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference();
                                        final Query queryComplete = ref2.child("Posts").orderByChild("postKey").equalTo(mData.get(position).getPostKey());

                                        queryComplete.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot completeSnapshot : dataSnapshot.getChildren()) {
                                                    completeSnapshot.getRef().child("category").setValue("Finalizado");
                                                }

                                                updateList(position);

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                    }
                                });
                                msgBox2.setNegativeButton(mContext.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        StyleableToast.makeText(mContext, mContext.getResources().getString(R.string.optionNoFinish), Toast.LENGTH_LONG, R.style.advertenciaToast).show();
                                    }
                                });

                                msgBox2.show();

                                break;
                            case R.id.menu_item_editar:

                                holder.popUpdatePost.show();

                                // add data
                                holder.popupTitle.setText(mData.get(position).getTitle());
                                holder.popupContact.setText(mData.get(position).getContact());
                                holder.popupDescription.setText(mData.get(position).getDescription());
                                holder.popupSearchMap.setText(mData.get(position).getLocation());
                                Glide.with(mContext).load(mData.get(position).getPicture()).into(holder.popupPostImag);
                                Glide.with(mContext).load(mData.get(position).getUserPhoto()).into(holder.popupUserImage);
                                holder.popupPostImag.setScaleType(ImageView.ScaleType.CENTER_CROP);

                                holder.popupAddBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        holder.popupAddBtn.setVisibility(View.INVISIBLE);
                                        holder.popupClickProgress.setVisibility(View.VISIBLE);

                                        if (!holder.popupTitle.getText().toString().isEmpty() && !holder.popupDescription.getText().toString().isEmpty() && !holder.popupContact.getText().toString().isEmpty() &&
                                                !(holder.popupContact.length() < 11) && !holder.popupSearchMap.getText().toString().isEmpty()) {
                                            // todos os campos estão preenchidos
                                            // TODO: UPDATE OBJETO
                                            // adiciona o post ao firebase database
                                            //addPost(post);

                                        } else {
                                            if (holder.popupContact.length() < 11) {
                                                StyleableToast.makeText(mContext, mContext.getResources().getString(R.string.contactValidHome), Toast.LENGTH_LONG, R.style.advertenciaToast).show();
                                                holder.popupAddBtn.setVisibility(View.VISIBLE);
                                                holder.popupClickProgress.setVisibility(View.INVISIBLE);
                                            } else {
                                                StyleableToast.makeText(mContext, mContext.getResources().getString(R.string.fildsValidHome), Toast.LENGTH_LONG, R.style.advertenciaToast).show();
                                                holder.popupAddBtn.setVisibility(View.VISIBLE);
                                                holder.popupClickProgress.setVisibility(View.INVISIBLE);
                                            }
                                        }
                                    }
                                });

                                //Toast.makeText(mContext, "Editar", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.menu_item_delete:

                                //Deletar UM Post feito pelo usuário
                                AlertDialog.Builder msgBox = new AlertDialog.Builder(mContext, R.style.AlertDialog);
                                msgBox.setTitle(mContext.getResources().getString(R.string.deletePostAdapter));
                                msgBox.setIcon(mContext.getDrawable(R.drawable.ic_delete_black));
                                msgBox.setMessage(mContext.getResources().getString(R.string.deleteOptionPostAdapter));
                                msgBox.setPositiveButton(mContext.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // delete sucess
                                        StyleableToast.makeText(mContext, mContext.getResources().getString(R.string.toastDeleteSucess), Toast.LENGTH_LONG, R.style.sucessoToast).show();

                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                        Query queryDelete = ref.child("Posts").orderByChild("postKey").equalTo(mData.get(position).getPostKey());

                                        queryDelete.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot deleteSnapshot : dataSnapshot.getChildren()) {
                                                    deleteSnapshot.getRef().removeValue();
                                                }
                                                removePost(position);
                                                notifyDataSetChanged();// notify the changed
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }

                                        });
                                    }
                                });

                                msgBox.setNegativeButton(mContext.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        StyleableToast.makeText(mContext, mContext.getResources().getString(R.string.optionNo), Toast.LENGTH_LONG, R.style.advertenciaToast).show();
                                    }
                                });

                                msgBox.show();

                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        holder.txtOptionDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu2 = new PopupMenu(mContext, holder.txtOptionDelete);
                popupMenu2.inflate(R.menu.option_delete_menu);
                popupMenu2.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_item_delete2:
                                //Deletar UM Post feito pelo usuário
                                AlertDialog.Builder msgBox = new AlertDialog.Builder(mContext, R.style.AlertDialog);
                                msgBox.setTitle(mContext.getResources().getString(R.string.deletePostAdapter));
                                msgBox.setIcon(mContext.getDrawable(R.drawable.ic_delete_black));
                                msgBox.setMessage(mContext.getResources().getString(R.string.deleteOptionPostAdapter));
                                msgBox.setPositiveButton(mContext.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // delete sucess
                                        StyleableToast.makeText(mContext, mContext.getResources().getString(R.string.toastDeleteSucess), Toast.LENGTH_LONG, R.style.sucessoToast).show();

                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                        Query queryDelete = ref.child("Posts").orderByChild("postKey").equalTo(mData.get(position).getPostKey());

                                        queryDelete.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot deleteSnapshot : dataSnapshot.getChildren()) {
                                                    deleteSnapshot.getRef().removeValue();
                                                }
                                                removePost(position);
                                                notifyDataSetChanged();// notify the changed
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }

                                        });
                                    }
                                });

                                msgBox.setNegativeButton(mContext.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        StyleableToast.makeText(mContext, mContext.getResources().getString(R.string.optionNo), Toast.LENGTH_LONG, R.style.advertenciaToast).show();
                                    }
                                });

                                msgBox.show();

                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                popupMenu2.show();
            }
        });

        holder.txtOptionReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu3 = new PopupMenu(mContext, holder.txtOptionDelete);
                popupMenu3.inflate(R.menu.option_report_menu);
                popupMenu3.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_item_report:
                                AlertDialog.Builder msgBox3 = new AlertDialog.Builder(mContext, R.style.AlertDialog);
                                msgBox3.setTitle(mContext.getResources().getString(R.string.reportMenuPostAdapter));
                                msgBox3.setIcon(mContext.getDrawable(R.drawable.ic_report));
                                msgBox3.setMessage(mContext.getResources().getString(R.string.reportOptionPostAdapter));
                                msgBox3.setPositiveButton(mContext.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        String idPost = mData.get(position).getPostKey();
                                        String nomeDoUser = mData.get(position).getUserName();
                                        String idUser = mData.get(position).getUserId();
                                        currentUser = mAuth.getCurrentUser();

                                        BackgroundMail.newBuilder(mContext)
                                                .withUsername("m.araujo2830@student.sbccd.edu")
                                                .withPassword("N2w5p7k4")
                                                .withMailto("m.theus_@live.com")
                                                .withType(BackgroundMail.TYPE_PLAIN)
                                                .withSubject("Post Reportado")
                                                .withBody( " ID do Post: " + idPost + "\n Nome do Usuário Reportado: " + nomeDoUser + "\n Nome do Usuário que Reportou: "
                                                        + currentUser.getDisplayName() + "\n ID do Usuário Reportado: " + idUser + "\n ID do Usuário que Reportou: " + currentUser.getUid())
                                                .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                                                    @Override
                                                    public void onSuccess() {
                                                        StyleableToast.makeText(mContext, mContext.getResources().getString(R.string.optionYesReport), Toast.LENGTH_LONG, R.style.sucessoToast).show();
                                                    }
                                                })
                                                .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                                                    @Override
                                                    public void onFail() {
                                                        StyleableToast.makeText(mContext, mContext.getResources().getString(R.string.optionNoReport), Toast.LENGTH_LONG, R.style.erroToast).show();
                                                    }
                                                })
                                                .send();
                                    }
                                });

                                msgBox3.setNegativeButton(mContext.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        StyleableToast.makeText(mContext, mContext.getResources().getString(R.string.optionNoReport), Toast.LENGTH_LONG, R.style.advertenciaToast).show();
                                    }
                                });

                                msgBox3.show();

                                break;

                            default:
                                break;
                        }
                        return false;
                    }
                });

                popupMenu3.show();
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
    }


    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount() called");
        return mData.size();
    }

    public void removePost(int position) {
        Log.d(TAG, "removePost() called with: position = [" + position + "]");
        mData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mData.size());
    }

    public void updateList(int position) {
        Log.d(TAG, "updateList() called with: position = [" + position + "]");
        mData.remove(position);
        notifyDataSetChanged();
    }

    public void addItem (int position, Post post) {
        Log.d(TAG, "addItem() called with: position = [" + position + "], post = [" + post + "]");
        mData.set(position, post);
        notifyDataSetChanged();
    }

    public void setLocale(String lang){
        Log.d(TAG, "setLocale() called with: lang = [" + lang + "]");
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        mContext.getResources().updateConfiguration(config, mContext.getResources().getDisplayMetrics());
        //save data
        SharedPreferences.Editor editor = mContext.getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();

    }

    public void loadLocale(){
        Log.d(TAG, "loadLocale() called");
        SharedPreferences preferences = mContext.getSharedPreferences("Settings", MODE_PRIVATE);
        String lang = preferences.getString("My_Lang", "");
        setLocale(lang);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvFoundOrLost;
        TextView tvDesc;
        ImageView imgPost;
        TextView txtOptionDigit;
        TextView txtOptionDelete;
        TextView txtOptionReport;
        CircleImageView imgPostProfile;
        FloatingActionButton fab;
        View postView;
        SwipeRefreshLayout swipeContainer;

        // update
        ImageView popupPostImag, popupAddBtn;
        CircleImageView popupUserImage;
        TextView popupTitle, popupDescription, popupContact;
        AutoCompleteTextView popupSearchMap;
        Spinner popupSpinner;
        ProgressBar popupClickProgress;
        Dialog popUpdatePost;


        //String currentUserId;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "MyViewHolder() called with: itemView = [" + itemView + "]");

            loadLocale();
            sharedPref = new SharedPref(mContext);
            if (sharedPref.loadNightModeState() == true){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }

            tvTitle = itemView.findViewById(R.id.row_post_title);
            tvFoundOrLost = itemView.findViewById(R.id.row_post_category);
            tvDesc = itemView.findViewById(R.id.blog_desc);
            imgPost = itemView.findViewById(R.id.row_post_img);
            imgPostProfile = itemView.findViewById(R.id.row_post_profile_img);
            txtOptionDigit = itemView.findViewById(R.id.txtOptionDigit);
            txtOptionDelete = itemView.findViewById(R.id.txtOptionDelete);
            txtOptionReport = itemView.findViewById(R.id.txtOptionComplaint);
            fab = itemView.findViewById(R.id.fab);
            postView = itemView.findViewById(R.id.main_blog_post);
            swipeContainer = itemView.findViewById(R.id.swipeContainer);

            // ini (transparent update post)
            popUpdatePost = new Dialog(mContext);
            popUpdatePost.setContentView(R.layout.popup_add_post);
            popUpdatePost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popUpdatePost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
            popUpdatePost.getWindow().getAttributes().gravity = Gravity.TOP;

            // ini popup update widgets
            popupUserImage = popUpdatePost.findViewById(R.id.popup_user_image);
            popupPostImag = popUpdatePost.findViewById(R.id.popup_img);
            popupTitle = popUpdatePost.findViewById(R.id.popup_title);
            popupContact = popUpdatePost.findViewById(R.id.popup_contact);
            popupDescription = popUpdatePost.findViewById(R.id.popup_description);
            popupSpinner = popUpdatePost.findViewById(R.id.my_spinner);
            popupAddBtn = popUpdatePost.findViewById(R.id.popup_update);
            popupClickProgress = popUpdatePost.findViewById(R.id.popup_progressBar);

            // Mostra opções de lugares no edittext (Campinas)
            popupSearchMap = popUpdatePost.findViewById(R.id.popup_search_map);

            String [] PLACES = mContext.getResources().getStringArray(R.array.Places);
            popupSearchMap.setAdapter(new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, PLACES));

            tvTitle.setSingleLine(true);
            tvDesc.setMaxLines(2);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mListener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            mListener.onPostClick(position);
                        }
                    }
                }
            });

        }
    }
}

