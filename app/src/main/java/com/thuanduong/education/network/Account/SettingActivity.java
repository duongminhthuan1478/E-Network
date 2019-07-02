package com.thuanduong.education.network.Account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.HashMap;

public class SettingActivity extends AppCompatActivity {

    private final static int GALLERY_PICK_REQUEST = 1;

    private FirebaseAuth mAuth;
    private String mCurrentUserID;
    private DatabaseReference mSettingUserDatabaseRef;
    private StorageReference mUserProfileImageReference;

    private DatabaseReference mPostRef;


    private Toolbar mToolbar;
    private EditText mUserName, mFullName, mUserStatus, mUserCountry, mUserGender, mUserRelation, mUserDOB;
    private Button mUpdateAccountSettingBtn;
    private CircleImageView mUserProfileImage;

    private ProgressDialog mProgressDialogLoadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        actionBar();
        findID();

        mProgressDialogLoadingBar = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUserID = mAuth.getCurrentUser().getUid();
        mUserProfileImageReference = FirebaseStorage.getInstance().getReference().child("Profile Images");
        /** Đường dẫn đến node user hiện tại để cập nhập */
        mSettingUserDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUserID);
        mPostRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        mSettingUserDatabaseRef.addValueEventListener(new ValueEventListener() {
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


                    mUserName.setText(myUserName);
                    mFullName.setText(myFullName);
                    mUserStatus.setText(myStatus);
                    mUserGender.setText(myGender);
                    mUserCountry.setText(myCountry);
                    mUserDOB.setText(myDOB);
                    mUserRelation.setText(myRelation);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mUpdateAccountSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAccountInfor();
            }
        });

        //Chọn ảnh đại diện
        mUserProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/jpeg/*");
                startActivityForResult(galleryIntent, GALLERY_PICK_REQUEST);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK_REQUEST && resultCode == RESULT_OK && data != null) {
                    // Lấy Uri từ Firebase Storage
                    Uri imageUri = data.getData();

                    // Xử lý Crop ảnh
                    // start picker to get image for cropping and then use the image in cropping activity
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .start(this);

                    CropImage.activity(imageUri)
                            .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && data!= null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                // Tạo tiến trình để nhận biết
                mProgressDialogLoadingBar.setTitle("Profile Image");
                mProgressDialogLoadingBar.setMessage("Vui lòng chờ trong giây lát....");
                mProgressDialogLoadingBar.setCanceledOnTouchOutside(true);
                mProgressDialogLoadingBar.show();

                // Khi người dùng nhấn nút cắt ảnh , ta sẽ lấy uri và lưu vào resultUri
                // Sau đó lưu vào Firebase Storage mUserProfileImageReference với thư mục profile
                // Images đã được dẫn đến
                Uri resultUri = result.getUri();
                // Lưu  hình ảnh với tên của UID(authentication)
                final StorageReference filePath =
                        mUserProfileImageReference.child(mCurrentUserID + ".jpg");
                UploadTask uploadTask = filePath.putFile(resultUri);
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task)
                            throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            // lấy kết quả url trả về và lưu vao firebase databasae
                            Uri dowloadResult = task.getResult();
                            // Thêm thuộc tính cho node  mCurrentUserID
                            mSettingUserDatabaseRef.child("profileimage").setValue(dowloadResult.toString());
                            mProgressDialogLoadingBar.dismiss();
                            finish(); //finish activity nay truoc de tranh lap lai 2 lan activity
                            Intent intent = new Intent(SettingActivity.this, SettingActivity.class);
                            startActivity(intent);
                            ShowToast.showToast(SettingActivity.this, "Lưu thành công");

                            // Sau khi cập nhập, kiểm tra dữ liệu cũ và đổi lại ảnh mới của post item
                            changePostOldImage(dowloadResult.toString());
                        } else {
                            ShowToast.showToast(getApplicationContext(),
                                    "Có lỗi xảy ra với : " + task.getException().getMessage());
                            mProgressDialogLoadingBar.dismiss();
                        }
                    }
                });
            } else {
                // Result code = cancle
                ShowToast.showToast(getApplicationContext(),
                        "Có lỗi xảy ra, cắt ảnh không thành công. Thử lại!");
                mProgressDialogLoadingBar.dismiss();
            }
        }
    }

    private void validateAccountInfor() {
        String userName = mUserName.getText().toString();
        String fullname = mFullName.getText().toString();
        String dob = mUserDOB.getText().toString();
        String gender = mUserGender.getText().toString();
        String country = mUserCountry.getText().toString();
        String relatioship = mUserRelation.getText().toString();
        String status = mUserStatus.getText().toString();

        if(TextUtils.isEmpty(userName) || TextUtils.isEmpty(fullname)
                || TextUtils.isEmpty(dob) || TextUtils.isEmpty(gender) || TextUtils.isEmpty(country)
                || TextUtils.isEmpty(relatioship) || TextUtils.isEmpty(status)){
            ShowToast.showToast(this, "Thông tin không được để trống, vui lòng nhập đầy đủ!");
        }else {
            updateAccountInfor(userName, fullname, dob, gender, country, relatioship, status);
            // kiểm tra và thay đổi dữ liệu của bài post cũ khi update tên mới
            changePostOldData(fullname);
            finish();

        }

    }

    private void updateAccountInfor(String userName, final String fullname, String dob, String gender,
            String country, String relatioship, String status) {

        HashMap userInforMap = new HashMap();
        userInforMap.put("username", userName);
        userInforMap.put("fullname", fullname);
        userInforMap.put("dob", dob);
        userInforMap.put("gender", gender);
        userInforMap.put("country", country);
        userInforMap.put("relationshipstatus", relatioship);
        userInforMap.put("status", status);
        mSettingUserDatabaseRef.updateChildren(userInforMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    ShowToast.showToast(SettingActivity.this, "Cập nhập thông tin thành công!");
                    sendUserToMainActivity();
                }else{
                    ShowToast.showToast(SettingActivity.this, "Có lỗi xảy ra với: "
                            + task.getException().toString());
                }
            }
        });
    }

    private void changePostOldData(final String fullname) {
         Thread thread = new Thread() {
            @Override
            public void run() {
                mPostRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        /** Vào node Posts chạy vòng for từng node children
                         *  dùng ds.getKey().toString(); để lấy key(node) hiện tại*/
                        for(DataSnapshot ds: dataSnapshot.getChildren()){
                            String key = ds.getKey().toString();
                            String uid = ds.child("uid").getValue().toString();
                            /** Kiểm tra uid của post và uid người dùng hiện tại nếu trùng nhau,
                             *  thay đổi fullname trong post cho trùng */
                            if(mCurrentUserID.equals(uid)){
                                mPostRef.child(key).child("fullname").setValue(fullname);
                            }

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(SettingActivity.this, "loi " + databaseError.getMessage()
                        , Toast.LENGTH_LONG).show();

                    }
                });
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                       ShowToast.showToast(getApplicationContext(),"OK");

                    }
                });
            }
        };
        thread.start();
    }
    private void changePostOldImage(final String profileimage) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                mPostRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        /** Vào node Posts chạy vòng for từng node children
                         *  dùng ds.getKey().toString(); để lấy key(node) hiện tại*/
                        for(DataSnapshot ds: dataSnapshot.getChildren()){
                            String key = ds.getKey().toString();
                            String uid = ds.child("uid").getValue().toString();
                            /** Kiểm tra uid của post và uid người dùng hiện tại nếu trùng nhau,
                             *  thay đổi fullname trong post cho trùng */
                            if(mCurrentUserID.equals(uid)){
                                mPostRef.child(key).child("profileimage").setValue(profileimage);
                            }

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(SettingActivity.this, "loi " + databaseError.getMessage()
                                , Toast.LENGTH_LONG).show();

                    }
                });
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //                       ShowToast.showToast(getApplicationContext(),"OK");

                    }
                });
            }
        };
        thread.start();
    }

    private void findID() {
        mUserName = (EditText) findViewById(R.id.setting_username);
        mFullName = (EditText) findViewById(R.id.setting_fullname);
        mUserStatus = (EditText) findViewById(R.id.setting_status);
        mUserCountry = (EditText) findViewById(R.id.setting_country);
        mUserGender = (EditText) findViewById(R.id.setting_gender);
        mUserDOB = (EditText) findViewById(R.id.setting_dob);
        mUserRelation = (EditText) findViewById(R.id.setting_reletionship_status);
        mUserProfileImage = (CircleImageView) findViewById(R.id.setting_profile_circle_image);
        mUpdateAccountSettingBtn = (Button) findViewById(R.id.update_account_setting_button);

    }

    private void actionBar() {
        mToolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(mToolbar);
        // Hiển thị dấu mũi tên quay lại
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Account Settings");
    }

    private void sendUserToMainActivity() {
        Intent mainActivity = new Intent(SettingActivity.this, MainActivity.class);
        startActivity(mainActivity);
    }
}
