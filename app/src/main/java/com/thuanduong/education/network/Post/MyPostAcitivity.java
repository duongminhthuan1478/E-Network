package com.thuanduong.education.network.Post;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.thuanduong.education.network.Model.Post;
import com.thuanduong.education.network.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

public class MyPostAcitivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mPostRef, mUserRef, mLikeRef;

    private String mCurrentUserID;

    private Toolbar mToolbar;
    private RecyclerView mMyPostRecyclerView;
    private FirebaseRecyclerAdapter<Post, MyPostViewHolder> mMyPostFirebaseRecyclerAdapter;

    // Biến kiểm tra người dùng đã like post hay chưa
    boolean likeChecker = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post_acitivity);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserID = mAuth.getCurrentUser().getUid();
        mPostRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mLikeRef = FirebaseDatabase.getInstance().getReference().child("Likes");


        /** Ánh xạ và recyclerview */
        findID();

        actionBar();

        displayAllPost();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mMyPostFirebaseRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMyPostFirebaseRecyclerAdapter.stopListening();
    }

    private void displayAllPost() {
        Query queryMyPost = mPostRef.orderByChild("uid")
                .startAt(mCurrentUserID).endAt(mCurrentUserID + "\uf8ff");
        FirebaseRecyclerOptions<Post> options =
                new FirebaseRecyclerOptions.Builder<Post>()
                        .setQuery(queryMyPost, Post.class)
                        .build();

        mMyPostFirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post, MyPostViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyPostViewHolder holder, int position,
                    @NonNull Post model) {
                // key (tên node bai post) hiện tại
                final String postKey = getRef(position).getKey();

                holder.setFullName(model.getFullName());
                holder.setTime(model.getTime());
                holder.setDate(model.getDate());
                holder.setDescription(model.getDescription());
                holder.setPostImage(model.getPostimage());
                holder.setProfileImage(model.getProfileimage());

                /** Xử lý đếm like và thay đổi trạng thái like-dislike khi người dùng click*/
                holder.setLikeButtonStatus(postKey);


                // Send người dùng đến ClickPostActivity tại vị trí click
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MyPostAcitivity.this, ClickPostActivity.class);
                        // Gửi tất cả dữ liệu (image , description) cùng key để ClickPostActivity  có thể nhận
                        intent.putExtra("PostKey", postKey);
                        startActivity(intent);
                    }
                });

                // Click Like Imagebutton
                holder.likePostButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Khi click like , checker = true
                        likeChecker = true;
                        mLikeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                // Ref đến tên bài post
                                if(likeChecker == true){
                                    /** postKey: vị trí bài post cụ thể, nếu có child của người dùng hiện tại rồi
                                     * có nghĩa rằng người dùng đã like , -> removalue
                                     * nếu chưa có thì set value(mCurrentUserID) = true (dã like)*/
                                    if(dataSnapshot.child(postKey).hasChild(mCurrentUserID)){
                                        mLikeRef.child(postKey).child(mCurrentUserID).removeValue();
                                        likeChecker = false;
                                    } else {
                                        mLikeRef.child(postKey).child(mCurrentUserID).setValue(true);
                                        likeChecker = false;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                });

                // Click Post ImageButton
                holder.commentPostButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intentComment = new Intent(MyPostAcitivity.this, CommentsActivity.class);
                        // Gửi tất cả dữ liệu (image , description) cùng key để ClickPostActivity  có thể nhận
                        intentComment.putExtra("PostKey", postKey);
                        startActivity(intentComment);
                    }
                });

            }

            @NonNull
            @Override
            public MyPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.all_post_layout_item, parent, false);
                return new MyPostViewHolder(view);
            }
        };
        mMyPostRecyclerView.setAdapter(mMyPostFirebaseRecyclerAdapter);

    }

    public static class MyPostViewHolder extends RecyclerView.ViewHolder{
        View mView;

        ImageButton likePostButton, commentPostButton;
        TextView numberOfLike;
        int countLikes;
        String currentUserID;
        DatabaseReference likeRef;

        public MyPostViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            likePostButton = mView.findViewById(R.id.like_image_button);
            commentPostButton = mView.findViewById(R.id.comment_image_button);
            numberOfLike = mView.findViewById(R.id.number_of_like_text);

            likeRef = FirebaseDatabase.getInstance().getReference().child("Likes");
            currentUserID  = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        public void setLikeButtonStatus(final String postKEy){
            likeRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Nếu bài post (key) có user đó rồi, đếm ra hiển thị
                    if(dataSnapshot.child(postKEy).hasChild(currentUserID)){
                        /**ChildrendCount số node tương ứng */
                        countLikes = (int) dataSnapshot.child(postKEy).getChildrenCount();
                        likePostButton.setImageResource(R.drawable.like);
                        numberOfLike.setText(String.valueOf(countLikes));
                    }else {
                        countLikes = (int) dataSnapshot.child(postKEy).getChildrenCount();
                        likePostButton.setImageResource(R.drawable.dislike);
                        numberOfLike.setText(String.valueOf(countLikes));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        public void setFullName(String name) {
            TextView userName = mView.findViewById(R.id.post_full_name);
            userName.setText(name);
        }
        public void setProfileImage(String profileimage) {
            CircleImageView image = mView.findViewById(R.id.post_profile_image);
            Picasso.get().load(profileimage).into(image);
        }
        public void setTime(String time) {
            TextView posttime = mView.findViewById(R.id.post_time);
            posttime.setText("   " + time);
        }
        public void setDate(String date) {
            TextView postDate = mView.findViewById(R.id.post_date);
            postDate.setText(date);
        }
        public void setDescription(String description) {
            TextView postDescript =  mView.findViewById(R.id.post_description);
            postDescript.setText(description);
        }
        public void setPostImage(String postImage) {
            ImageView post_image = mView.findViewById(R.id.post_image);
            Picasso.get().load(postImage).into(post_image);
        }
    }

    private void findID(){
        mToolbar = (Toolbar) findViewById(R.id.my_post_toolbar);
        mMyPostRecyclerView = (RecyclerView) findViewById(R.id.my_all_post_recyclerview);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mMyPostRecyclerView.setHasFixedSize(true);
        mMyPostRecyclerView.setLayoutManager(linearLayoutManager);

    }

    private void actionBar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Post");
    }
}
