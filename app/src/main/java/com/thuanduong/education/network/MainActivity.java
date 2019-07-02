package com.thuanduong.education.network;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.thuanduong.education.network.Account.LoginActivity;
import com.thuanduong.education.network.Account.ProfileActivity;
import com.thuanduong.education.network.Account.SettingActivity;
import com.thuanduong.education.network.Account.SetupActivity;
import com.thuanduong.education.network.Friends_RequestFriend.FriendsActivity;
import com.thuanduong.education.network.Model.Post;
import com.thuanduong.education.network.Post.ClickPostActivity;
import com.thuanduong.education.network.Post.CommentsActivity;
import com.thuanduong.education.network.Post.PostActivity;
import com.thuanduong.education.network.Ultil.ShowToast;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    /** Hai biến thể hiện trạng thái khi user onl/off */
    private static final String USER_ONLINE = "ONLINE";
    private static final String USER_OFFLINE = "OFFLINE";

    /** Khai báo các View trong main activity */
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;

    /** Khai báo thanh xổ suống menu (xổ ra Navigation View) */
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private RecyclerView mPostList;
    private Toolbar mToolbar;
    private CircleImageView mNavProfileImage;
    private TextView mNavProfileFullNameText;
    private ImageButton mAddNewPostImgButton;

    /** Xác nhận người dùng **/
    private FirebaseAuth mFirebaseAuth;
    //create database
    private FirebaseDatabase mFirebaseDatabase;
    // Đối tượng tham chiếu đến một phần cụ thể của Database , trong trường hợp này là User
    private DatabaseReference mUserDatabaseRef, mPostDatabaseRef, mLikeDatabaseRef;

    private String mCurrentUserID;
    private  FirebaseRecyclerAdapter<Post, PostViewHolder> mPostFirebaseRecyclerAdapter;

    // Biến kiểm tra người dùng đã like post hay chưa
    boolean likeChecker = false;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawable_layout);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mAddNewPostImgButton = (ImageButton) findViewById(R.id.add_new_post_img_button);



        mPostList = (RecyclerView) findViewById(R.id.all_user_post_recyclerview);
        mPostList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        // setReverseLayout khi một post mới đăng lên , post cũ sẽ xuống bottom cho post mới lên top
        linearLayoutManager.setReverseLayout(true);
        // true để ghim nội dung của khung nhìn xuống cạnh dưới cùng, sai để ghim nội dung của khung nhìn lên cạnh trên cùng
        linearLayoutManager.setStackFromEnd(true);
        mPostList.setLayoutManager(linearLayoutManager);




        // Hiển thị layout header cho Navigation bằng code
        // hoặc có thể dùng lệnh trong XML 'app:headerLayout="@layout/nav_drawer_header"
        View navigationView = mNavigationView.inflateHeaderView(R.layout.navigation_header);
        mNavProfileImage = (CircleImageView) navigationView.findViewById(R.id.nav_profile_img);
        mNavProfileFullNameText = (TextView) navigationView.findViewById(R.id.nav_user_full_name);


       // Click Item cho Navigation View (thanh  vuốt điều hướng bên trái)
        mNavigationView.setNavigationItemSelectedListener(new NavigationView
                .OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                userMenuSelector(item);
                return false;
            }
        });
        /** thiết lập toolbar và hiển thị title , Cài đặt thanh menu để đổ xuống navigation view*/
        actionBar();

        /** Khởi tạo Firebase Auth để xác thực người dùng */
        mFirebaseAuth = FirebaseAuth.getInstance();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        // Node cha chứa các Users
        mUserDatabaseRef = mFirebaseDatabase.getReference().child("Users");
        mPostDatabaseRef = mFirebaseDatabase.getReference().child("Posts");
        mLikeDatabaseRef = mFirebaseDatabase.getReference().child("Likes");

        // tham chiếu đến con của node Users với các user cụ thể với ID
        // để lấy hình ảnh và username từ firebase hiển thị lên navigation header
        mCurrentUserID = mFirebaseAuth.getCurrentUser().getUid();
        mUserDatabaseRef.child(mCurrentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    /** Add validation , VD : trong trường hợp nếu người dùng remove app, khi tải lại ứng dụng và run
                     * AndroidManifest sẽ buộc chạy Main activity đầu tiên , trong TH chạy main có nghĩa là người dùng chưa
                     * Login vào để FirebaseAuth xác nhận tài khoản, vì vậy phải rào điều kiện dùng lệnh if kiểm tra
                     *  các node con VD dataSnapshot.hasChild("fullname") nếu có mới bắt đầu hiển thị ,
                     *  tất nhiên phải login vào thì node mới có ((xác thực mới đọc được dữ liệu)) . Vì vậy trong AndroidManifest.xml ,
                     /  *  copy phần <intent-filter> của MainActivity vào Login để chạy LoginActivity trước sau đó mới chạy Main
                     *
                     */
                    if(dataSnapshot.hasChild("fullname")){
                        String fullName = dataSnapshot.child("fullname").getValue().toString();
                        mNavProfileFullNameText.setText(fullName);
                    }
                    if(dataSnapshot.hasChild("profileimage")){
                        String imageUrl = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.get()
                                .load(imageUrl)
                                .placeholder(R.drawable.profile)
                                .into(mNavProfileImage);
                    }
                }
                else {
                     ShowToast.showToast(MainActivity.this, "Profile name do not exists..");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mAddNewPostImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToPostActivity();
            }
        });

        // sau khi add một post từ (mAddNewPostImgButton) cho hiển thị lên màn hình với recyccleview
        displayAllUserPosts();



    } // onCreate();


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        if(currentUser == null){
            sendUserToLoginActivity();
        } else {
            // Sau khi xác thực kiểm tra xem user đã tồn tại trong database chưa
            checkUserExistence();
        }
        mPostFirebaseRecyclerAdapter.notifyDataSetChanged();
        mPostFirebaseRecyclerAdapter.startListening();

    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        updateUserState(USER_OFFLINE);
//    }
//
    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateUserState(USER_OFFLINE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /** Xử lý clicl vào đổ ra Navigation view*/
        if(mActionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** Check người dùng đang online hay offline*/
    private void updateUserState(String state){
        String saveCurrentDate, saveCurrentTime;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMMM dd");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm");
        saveCurrentTime = currentTime.format(calForTime.getTime());

        Map currentUserState = new HashMap();
        currentUserState.put("time", saveCurrentTime);
        currentUserState.put("date", saveCurrentDate);
        currentUserState.put("type", state);

        mUserDatabaseRef.child(mCurrentUserID).child("userState").updateChildren(currentUserState);

    }

    private void userMenuSelector(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                ShowToast.showToast(this, "Home");
                break;

            case R.id.navigation_profile:
                sendUserToProfileActivity();
                break;

            case R.id.navigation_post:
                sendUserToPostActivity();
                break;

            case R.id.navigation_friends:
                //sendUserToFriendActivity();
                sendUserToFriendsActivity();
                break;

            case R.id.navigation_find_friends:
                sendUserToFindFriendActivity();
                break;

            case R.id.navigation_message:
                sendUserToFriendActivity();
                break;

            case R.id.navigation_setting:
                sendUserToSettingActivity();
                break;

            case R.id.navigation_logout:
                // đăng xuất người dùng khỏi firebase authentication
                //mFirebaseAuth.signOut();

                /** thiết lập người dùng ofline khi logout */
                updateUserState(USER_OFFLINE);
                mFirebaseAuth.getInstance().signOut();
                sendUserToLoginActivity();
                break;
        }
    }

    private void displayAllUserPosts() {
            /** Biến counter dữ tổng số lượng bài post
             * vì vậy orderByChild(counter) sẽ sắp xếp counter tăng dần ,
             *  sau khi thiết lập layout hiển thị đảo ngược là setReverseLayout, setStackFromEnd
             *  vậy nên counter sẽ được đảo ngược lại là giảm dần , new post hiển thị lên top*/
            Query querySortPostOrder = mPostDatabaseRef.orderByChild("counter");
            FirebaseRecyclerOptions<Post> options =
                    new FirebaseRecyclerOptions.Builder<Post>()
                            .setQuery(querySortPostOrder, Post.class)
                            .build();

            mPostFirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(options) {


                @Override
                protected void onBindViewHolder(@NonNull PostViewHolder holder, int position,
                        @NonNull Post model) {
                    // key (tên node) hiện tại
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
                            Intent intent = new Intent(MainActivity.this, ClickPostActivity.class);
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
                            mLikeDatabaseRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    // Ref đến tên bài post
                                    if(likeChecker == true){
                                        /** postKey: vị trí bài post cụ thể, nếu có child của người dùng hiện tại rồi
                                         * có nghĩa rằng người dùng đã like , -> removalue
                                         * nếu chưa có thì set value(mCurrentUserID) = true (dã like)*/
                                        if(dataSnapshot.child(postKey).hasChild(mCurrentUserID)){
                                            mLikeDatabaseRef.child(postKey).child(mCurrentUserID).removeValue();
                                            likeChecker = false;
                                        } else {
                                            mLikeDatabaseRef.child(postKey).child(mCurrentUserID).setValue(true);
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
                            Intent intentComment = new Intent(MainActivity.this, CommentsActivity.class);
                            // Gửi tất cả dữ liệu (image , description) cùng key để ClickPostActivity  có thể nhận
                            intentComment.putExtra("PostKey", postKey);
                            startActivity(intentComment);
                        }
                    });

                }

                @NonNull
                @Override
                public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.all_post_layout_item, parent, false);
                    return new PostViewHolder(view);
                }

            };
            mPostList.setAdapter(mPostFirebaseRecyclerAdapter);

            /** Khi người dùng vào Mainactivity và các post được hiển thị , thiết lập cho người dùng online */
            updateUserState(USER_ONLINE);
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder{
        View mView;

        ImageButton likePostButton, commentPostButton;
        TextView numberOfLike;
        int countLikes;
        String currentUserID;
        DatabaseReference likeRef;


        public PostViewHolder(View itemView) {
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

    /**
     * (!dataSnapshot.hasChild(current_User_ID))
     * Sau khi đã xác thực qua firebase auth , kiểm tra xem user đã tồn tại trong database chưa
     * nếu chưa có cho người dùng đến setup activity để lưu User
     */
    private void checkUserExistence() {
        final String current_User_ID = mFirebaseAuth.getCurrentUser().getUid();

        // Đọc dữ liệu và lắng nghe dữ liệu thay đổi dùng addValueEventListener()
        mUserDatabaseRef.child(mCurrentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!(dataSnapshot.hasChild("username") && dataSnapshot.hasChild("fullname")
                        && dataSnapshot.hasChild("profileimage") && dataSnapshot.hasChild("country"))){
                    sendUserToSetupActivity();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void actionBar() {
        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Home");

        mActionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this,mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close);

        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void sendUserToSetupActivity() {
        Intent intent = new Intent(MainActivity.this, SetupActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void sendUserToPostActivity() {
        Intent addNewPostIntent = new Intent(MainActivity.this, PostActivity.class);
        startActivity(addNewPostIntent);
    }

    private void sendUserToLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        // Xử lý khi nhấn back -> thoát app chứ không quay lại mainactivity
        startActivity(intent);
    }
    private void sendUserToSettingActivity() {
        Intent intent = new Intent(MainActivity.this, SettingActivity.class);
        startActivity(intent);
    }
    private void sendUserToProfileActivity() {
        Intent Intent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(Intent);
    }
    private void sendUserToFindFriendActivity() {
        Intent Intent = new Intent(MainActivity.this, FindFriendActivity.class);
        startActivity(Intent);
    }
    private void sendUserToFriendActivity() {
//        ListFriendFragment listFriendFragment = new ListFriendFragment();
//        FragmentManager manager = getSupportFragmentManager();
//        FragmentTransaction transaction = manager.beginTransaction();
//        transaction.replace(R.id.drawable_layout,listFriendFragment, listFriendFragment.getTag());
//        transaction.addToBackStack(null);
//        transaction.commit();

        Intent Intent = new Intent(MainActivity.this, FriendActivity.class);
        startActivity(Intent);
    }
    private void sendUserToFriendsActivity() {
        Intent Intent = new Intent(MainActivity.this, FriendsActivity.class);
        startActivity(Intent);
    }


}
