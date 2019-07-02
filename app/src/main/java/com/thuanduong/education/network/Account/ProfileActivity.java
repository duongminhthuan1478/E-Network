package com.thuanduong.education.network.Account;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.thuanduong.education.network.FriendActivity;
import com.thuanduong.education.network.Post.MyPostAcitivity;
import com.thuanduong.education.network.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mProfileUserDatabaseRef, mFriendDatabaseRef, mPostDatabaseRef;
    private String mCurrentUserID;

    /**  tổng số friend và post*/
    private Button mMyPostsButton, mMyFriendsButton;


    private TextView mUserName, mFullName, mUserStatus, mUserCountry, mUserGender, mUserRelation, mUserDOB;
    private CircleImageView mUserProfileImage;

    // Chứa tổng số Friend, Post của người dùng hiện tại
     int countFriends = 0, countPosts = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        findID();

        mAuth = FirebaseAuth.getInstance();
        mCurrentUserID = mAuth.getCurrentUser().getUid();
        mFriendDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        mPostDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        mProfileUserDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUserID);
        mProfileUserDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    String myUserName = dataSnapshot.child("username").getValue().toString();
                    String myFullName = dataSnapshot.child("fullname").getValue().toString();
                    String myStatus = dataSnapshot.child("status").getValue().toString();
                    String myDOB = dataSnapshot.child("dob").getValue().toString();
                    String myCountry = dataSnapshot.child("country").getValue().toString();
                    String myGender = dataSnapshot.child("gender").getValue().toString();
                    String myRelation = dataSnapshot.child("relationshipstatus").getValue().toString();
                    Picasso.get().load(myProfileImage).placeholder(R.drawable.profile).into(mUserProfileImage);


                    mUserName.setText("@" + myUserName);
                    mFullName.setText(myFullName);
                    mUserStatus.setText(myStatus);
                    mUserGender.setText("Gender: " + myGender);
                    mUserCountry.setText("Country: " + myCountry);
                    mUserDOB.setText("DOB: " + myDOB);
                    mUserRelation.setText("Relationship " + myRelation);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mMyFriendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToFriendActivity();
            }
        });

        mMyPostsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToMyPostActivity();
            }
        });

        /** Vào child friend -> người dùng online và count số bạn ra */
        mFriendDatabaseRef.child(mCurrentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    countFriends = (int) dataSnapshot.getChildrenCount();
                    mMyFriendsButton.setText(countFriends + " Friends");
                } else {
                    mMyFriendsButton.setText("No Friend");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /** Tìm đến uid và check xem đúng người dùng online thì lấy số post ra */
        mPostDatabaseRef.orderByChild("uid").startAt(mCurrentUserID)
                .endAt(mCurrentUserID + "\uf8ff")
                .addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            countPosts = (int) dataSnapshot.getChildrenCount();
                            mMyPostsButton.setText(countPosts + " Posts");

                        } else {
                            mMyPostsButton.setText("0 Post");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void findID() {
        mUserName = (TextView) findViewById(R.id.my_profile_username);
        mFullName = (TextView) findViewById(R.id.my_profile_fullname);
        mUserStatus = (TextView) findViewById(R.id.my_profile_status);
        mUserCountry = (TextView) findViewById(R.id.my_profile_country);
        mUserGender = (TextView) findViewById(R.id.my_profile_gender);
        mUserDOB = (TextView) findViewById(R.id.my_profile_dob);
        mUserRelation = (TextView) findViewById(R.id.my_profile_relationship_status);
        mUserProfileImage = (CircleImageView) findViewById(R.id.my_profile_circle_image);
        mMyFriendsButton = (Button) findViewById(R.id.my_friend_button);
        mMyPostsButton = (Button) findViewById(R.id.my_post_button);
    }

    private void sendUserToFriendActivity() {
        Intent Intent = new Intent(ProfileActivity.this, FriendActivity.class);
        startActivity(Intent);
    }
    private void sendUserToMyPostActivity() {
        Intent Intent = new Intent(ProfileActivity.this, MyPostAcitivity.class);
        startActivity(Intent);
    }

}
