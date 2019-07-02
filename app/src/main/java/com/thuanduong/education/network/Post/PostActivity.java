package com.thuanduong.education.network.Post;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.thuanduong.education.network.MainActivity;
import com.thuanduong.education.network.R;
import com.thuanduong.education.network.Ultil.ShowToast;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class PostActivity extends AppCompatActivity {

    private final static int GALLERY_PICK_REQUEST = 1;

    /** Đếm số lượng post */
    private long countPosts;

    /** Khai báo biến để lấy link hình ảnh khi người dùng click chọn hình ảnh */
    private Uri mImageUri;

    /** Khai báo biến để lấy text của description về hình ảnh , status */
    private String mDescription;

    private Toolbar mToolbar;
    private ProgressDialog mProgressDialogLoadingBar;

    private ImageButton mSelectPostImageBtn;
    private Button mUpdatePostBtn;
    private EditText mPostDescription;

    private StorageReference mPostImageStorageReference;
    private DatabaseReference mUserDatabaseRef, mPostDatabaseRef;
    private FirebaseAuth mAuth;

    /** Biến để xử lý lấy thời gian hiện tại lưu vào FirebaseStorage với thời gian random để tránh trùng lặp dữ liệu ảnh */
    private String mSaveCurrentDate, mSaveCurrentTime, mPostRandomName;
    private String mCurrentUserID;
    private Uri mDowloadUrl;

    String userFullName, userProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        actionBar();
        findID();

        mProgressDialogLoadingBar = new ProgressDialog(this);

        mPostImageStorageReference = FirebaseStorage.getInstance().getReference();
        mUserDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mPostDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserID = mAuth.getCurrentUser().getUid();

        mSelectPostImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý người dùng click hình, đưa đến Gallery
                openGallery();
            }
        });


        mUpdatePostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             validatePostInfo();
            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_PICK_REQUEST && resultCode == RESULT_OK && data != null){
            // Lấy và setImageURI khi người dùng chọn hình ảnh  để hiển thị hình ảnh
            mImageUri = data.getData();
            mSelectPostImageBtn.setImageURI(mImageUri);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            // Xử lý nút quay lại trên actionBar
            case android.R.id.home:
                sendUserToMainActivity();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void findID() {
        mSelectPostImageBtn = (ImageButton) findViewById(R.id.select_post_image);
        mUpdatePostBtn = (Button) findViewById(R.id.update_post_button);
        mPostDescription = (EditText) findViewById(R.id.post_description_edt);
    }

    private void actionBar() {
        mToolbar = (Toolbar) findViewById(R.id.update_post_page_toolbar);
        setSupportActionBar(mToolbar);
        // Hiển thị dấu mũi tên quay lại
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Update Post");

    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/jpeg/*");
        startActivityForResult(galleryIntent, GALLERY_PICK_REQUEST);
    }

    private void validatePostInfo() {
        mDescription = mPostDescription.getText().toString();
        if(mImageUri == null && TextUtils.isEmpty(mDescription)){
            ShowToast.showToast(this,"Chọn một hình ảnh hoặc cập nhập trạng thái!");
        }
        else if(TextUtils.isEmpty(mDescription)){
            ShowToast.showToast(this, "Bạn chưa cập nhập trạng thái!");
        }
        else {
            mProgressDialogLoadingBar.setTitle("Add New Post");
            mProgressDialogLoadingBar.setMessage("Vui lòng chờ trong giây lát....");
            mProgressDialogLoadingBar.show();
            mProgressDialogLoadingBar.setCanceledOnTouchOutside(true);

            checkNumberOfPost();
            storingImageToFirebaseStorageAndSavingPostToDatabase();


        }
    }

    /** Hàm kiểm tra số lượng của post và lấy tổng số post(node) lưu trữ trong biến
     * toàn phần countPosts, mỗi lần update 1 post countPosts đếm sẽ tăng lên 1 và lưu vào
     * để MainActivity có thể sắp xếp Query trong adapter theo "counter" giảm dần để hiển thị*/
    private void checkNumberOfPost() {
        mPostDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    // Đếm số lượng node trong child (post)
                    countPosts = dataSnapshot.getChildrenCount();
                } else {
                    countPosts = 0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /** lưu hình ảnh đến thư mục (Post Images) và sau đó tải string url của ảnh đó về
     * lưu dữ liệu liên quan đến bài post vào hashmap và đẩy lên Firebase với node(Post) */
    private void storingImageToFirebaseStorageAndSavingPostToDatabase() {

        // Date
        Calendar calForDate =  Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        mSaveCurrentDate = currentDate.format(calForDate.getTime());

        // Time, seconds
        Calendar calForTime =  Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        mSaveCurrentTime = currentTime.format(calForTime.getTime());



        /** Tạo một biến để lấy thời gian, ngày hiện tại khi người dùng đăng ảnh để tránh tên hình ảnh bị
         * trùng lặp tên, biến này cũng được dùng để lưu bài post */
        mPostRandomName = mSaveCurrentDate + mSaveCurrentTime;

        // Get a reference to store file at chat_photos/<FILE_NAME>
        // VD: content://local_images/foo/4, the file name that we've saving will be 4
        final StorageReference filePath = mPostImageStorageReference.child("Post Images") // Thư mục
                .child(mImageUri.getLastPathSegment() + mPostRandomName + "jpg");

        UploadTask uploadTask = filePath.putFile(mImageUri);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return filePath.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    mDowloadUrl = task.getResult();
                    mUserDatabaseRef.child(mCurrentUserID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                userFullName = dataSnapshot.child("fullname").getValue().toString();
                                userProfileImage = dataSnapshot.child("profileimage").getValue().toString();

                                /** Biến counter: chứa tổng số post để sắp xếp post mới lên đầu
                                 * trước khi update 1 new post checkNumberOfPost() sẽ get tổng số node (post)
                                 * và đưa vào countPosts để dễ dàng sắp xếp */
                                final HashMap postMap = new HashMap();
                                postMap.put("uid", mCurrentUserID);
                                postMap.put("date", mSaveCurrentDate);
                                postMap.put("time", mSaveCurrentTime);
                                postMap.put("description", mDescription);
                                postMap.put("postimage", mDowloadUrl.toString());
                                postMap.put("fullname", userFullName);
                                postMap.put("profileimage", userProfileImage);
                                postMap.put("counter", countPosts);

                                Thread thread = new Thread() {
                                    @Override
                                    public void run() {
                                        mPostDatabaseRef.child(mCurrentUserID + mPostRandomName).updateChildren(postMap);
                                        mProgressDialogLoadingBar.dismiss();
//                                        sendUserToMainActivity();
                                        finish(); // finish de comback ve main tranh loi chồng activity va chay nen onDatachange lien tuc
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //textView.setText("OK");
                                                //ShowToast.showToast(getApplicationContext(),"Đăng bài thành công!");

                                            }
                                        });
                                    }
                                };
                                thread.start();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }else {
                    Toast.makeText(PostActivity.this,"Upload failed"
                            + task.getException().getMessage(),Toast.LENGTH_LONG);
                }
            }
        });
    }

    private void sendUserToMainActivity() {
        Intent mainActivity = new Intent(PostActivity.this, MainActivity.class);
        startActivity(mainActivity);
        finish();
    }

}
