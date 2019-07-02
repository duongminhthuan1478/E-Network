package com.thuanduong.education.network;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.thuanduong.education.network.Model.FindFriend;
import com.thuanduong.education.network.Ultil.ShowToast;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendActivity extends AppCompatActivity {

    private FirebaseRecyclerAdapter<FindFriend, FindFriendViewHoder> mFindFriendFirebaseRecyclerViewAdapter;
    private DatabaseReference mAllUserDatabaseRef;
    private RecyclerView mSearchResultRecyclerView;

    private Toolbar mToolbar;
    private ImageButton mSearchImgButton;
    private EditText mSearchInputEdt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);
        actionBar();
        findID();
        mAllUserDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mSearchResultRecyclerView = (RecyclerView) findViewById(R.id.search_result_recyclerview);
        mSearchResultRecyclerView.setHasFixedSize(true);
        mSearchResultRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mSearchImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchStringInput = mSearchInputEdt.getText().toString();
                searchPeople(searchStringInput);
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        mFindFriendFirebaseRecyclerViewAdapter.stopListening();
    }

    private void searchPeople(String searchStringInput) {
        ShowToast.showToast(this,"Searching....");

        Query query = mAllUserDatabaseRef.orderByChild("fullname").startAt(searchStringInput).endAt(searchStringInput + "\uf8ff");
        FirebaseRecyclerOptions<FindFriend> options =
                new FirebaseRecyclerOptions.Builder<FindFriend>()
                        .setQuery(query, FindFriend.class)
                        .build();

        mFindFriendFirebaseRecyclerViewAdapter = new FirebaseRecyclerAdapter<FindFriend, FindFriendViewHoder>(options) {


            @Override
            protected void onBindViewHolder(@NonNull FindFriendViewHoder holder, final int position,
                    @NonNull FindFriend model) {
                holder.setProfileimage(model.getProfileimage());
                holder.setFullname(model.getFullname());
                holder.setStatus(model.getStatus());

                /** Khi click vào vị trí cụ thể user của  result find friends
                 * visit_user_id: nhận key được lưu bằng (uid) của người dùng và gửi sang PersonProfileActivity
                 * để vào chính node của người dùng đó , lấy thông tin user ra
                 * */
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override

                    public void onClick(View v) {
                        String visit_user_id = getRef(position).getKey();

                        Intent profileIntent = new Intent(FindFriendActivity.this, PersonProfileActivity.class);
                        profileIntent.putExtra("visit_user_id", visit_user_id);
                        startActivity(profileIntent);
                    }
                });
            }

            @NonNull
            @Override
            public FindFriendViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.all_user_display_item, parent, false);
                return new FindFriendViewHoder(view);
            }
        };

        mSearchResultRecyclerView.setAdapter(mFindFriendFirebaseRecyclerViewAdapter);
        mFindFriendFirebaseRecyclerViewAdapter.startListening();
    }
    public static class FindFriendViewHoder extends RecyclerView.ViewHolder{

        View mView;

        public FindFriendViewHoder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setProfileimage(String profileimage) {
            CircleImageView imageView = mView.findViewById(R.id.all_user_profile_image);
            Picasso.get().load(profileimage).into(imageView);
        }
        public void setFullname(String fullname) {
            TextView myName = mView.findViewById(R.id.all_user_profile_fullname);
            myName.setText(fullname);
        }
        public void setStatus(String status) {
            TextView mystatus = mView.findViewById(R.id.all_user_profile_status);
            mystatus.setText(status);
        }
    }

    private void findID() {
        mSearchImgButton = (ImageButton) findViewById(R.id.search_people_friend_button);
        mSearchInputEdt = (EditText) findViewById(R.id.search_box_friend_edt);

    }

    private void actionBar() {
        mToolbar = (Toolbar) findViewById(R.id.find_friends_appbar_layout);
        setSupportActionBar(mToolbar);
        // Hiển thị dấu mũi tên quay lại
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Find Friends");

    }
}
