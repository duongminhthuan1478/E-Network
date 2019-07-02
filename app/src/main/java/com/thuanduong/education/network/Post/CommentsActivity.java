package com.thuanduong.education.network.Post;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.thuanduong.education.network.Model.Comment;
import com.thuanduong.education.network.R;
import com.thuanduong.education.network.Ultil.ShowToast;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class CommentsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabaseRef, mPostDatabaseRef;

    private String mCurrentUserID;

    private ImageButton mSendPostCommentBtn;
    private EditText mCommentInputEdt;
    private RecyclerView mCommentRecyclerview;

    private FirebaseRecyclerAdapter<Comment, CommentsViewHolder> mCommentFirebaseRecyclerViewAdapter;

    /** Lưu giữ key(tên node) hiện tại của bài post*/
    String postKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        findID();

        /** Nhận key(tên node) hiện tại của bài post*/
        postKey = getIntent().getExtras().get("PostKey").toString();

        mAuth = FirebaseAuth.getInstance();
        mCurrentUserID = mAuth.getCurrentUser().getUid();
        mUserDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mPostDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(postKey).child("Comments");


        //Click send Comment Btn
        mSendPostCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserDatabaseRef.child(mCurrentUserID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            String userName = dataSnapshot.child("username").getValue().toString();
                            String profileImage = dataSnapshot.child("profileimage").getValue().toString();
                            validateCommentInput(userName, profileImage);

                            mCommentInputEdt.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        readDataCommentRecyclerView();

    }

    @Override
    protected void onStop() {
        super.onStop();
        mCommentFirebaseRecyclerViewAdapter.stopListening();
    }

    private void readDataCommentRecyclerView(){
        Query query = mPostDatabaseRef;
        FirebaseRecyclerOptions<Comment> options =
                new FirebaseRecyclerOptions.Builder<Comment>()
                        .setQuery(query, Comment.class)
                        .build();
        mCommentFirebaseRecyclerViewAdapter = new FirebaseRecyclerAdapter<Comment, CommentsViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull CommentsViewHolder holder, int position,
                    @NonNull Comment model) {
                holder.setComment(model.getComment());
                holder.setDate(model.getDate());
                holder.setTime(model.getTime());
                holder.setUsername(model.getUsername());
                holder.setProfileimage(model.getProfileimage());

            }

            @NonNull
            @Override
            public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_comment_item, parent, false);
                return new CommentsViewHolder(view);
            }
        };
        mCommentRecyclerview.setAdapter(mCommentFirebaseRecyclerViewAdapter);
        mCommentFirebaseRecyclerViewAdapter.startListening();

    }

    public static class CommentsViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public CommentsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setComment(String comment) {
            TextView tvComment = mView.findViewById(R.id.comment_text);
            tvComment.setText(comment);
        }
        public void setDate(String date) {
            TextView tvDate = mView.findViewById(R.id.comment_date);
            tvDate.setText("Date: " + date);
        }
        public void setTime(String time) {
            TextView tvTime = mView.findViewById(R.id.comment_time);
            tvTime.setText("   at: " + time);
        }
        public void setUsername(String username) {
            TextView tvUserName = mView.findViewById(R.id.comment_user_name);
            tvUserName.setText(username);
        }
        public void setProfileimage(String profileimage) {
            CircleImageView image = mView.findViewById(R.id.comment_profile);
            Picasso.get().load(profileimage).placeholder(R.drawable.profile).into(image);
        }

    }

    private void validateCommentInput(String userName, String profileImage) {
        String commentText = mCommentInputEdt.getText().toString();
        if(TextUtils.isEmpty(commentText)){
            ShowToast.showToast(CommentsActivity.this, "Write something...");
        }
        else {
            Calendar calForDate =  Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            String saveCurrentDate = currentDate.format(calForDate.getTime());

            // Time
            Calendar calForTime =  Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
            String saveCurrentTime = currentTime.format(calForTime.getTime());

            /** Tạo một biến để lấy thời gian, ngày hiện tại khi người dùng đăng ảnh để tránh tên hình ảnh bị
             * trùng lặp tên, biến này cũng được dùng để lưu bài post */
            //mPostRandomName = mSaveCurrentDate + mSaveCurrentTime;

            HashMap commentMap = new HashMap();
            commentMap.put("uid", mCurrentUserID);
            commentMap.put("comment", commentText);
            commentMap.put("date", saveCurrentDate);
            commentMap.put("time", saveCurrentTime);
            commentMap.put("username", userName);
            commentMap.put("profileimage", profileImage);
            // push() gửi lên dưới dạng push id
            mPostDatabaseRef.push().updateChildren(commentMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        ShowToast.showToast(getApplicationContext(),"Ok thanh cong");
                    }
                }
            });
        }
    }

    private void findID() {
        mCommentRecyclerview = (RecyclerView) findViewById(R.id.comment_recyclerview);
        mCommentRecyclerview.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        // new comment on the top
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mCommentRecyclerview.setLayoutManager(linearLayoutManager);

        mCommentInputEdt = (EditText) findViewById(R.id.comment_input_edittext);
        mSendPostCommentBtn = (ImageButton) findViewById(R.id.send_comment_image_btn);
    }
}
