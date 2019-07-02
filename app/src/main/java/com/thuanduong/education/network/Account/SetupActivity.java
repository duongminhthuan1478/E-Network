package com.thuanduong.education.network.Account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class SetupActivity extends AppCompatActivity {

    // Request code cho sự kiện pick ảnh  trong thư viện hiển thị lên mProFileImage
    private final static int GALLERY_PICK_REQUEST = 1;

    private EditText mUserNameEdt, mFullNameEdt, mCountryEdt;
    private Button mSaveBtn;
    private CircleImageView mProFileImage;

    private ProgressDialog mProgressDialogLoadingBar;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabaseReference; //User node

    private StorageReference mUserProfileImageReference;

    private String mCurrentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        findId();
        mProgressDialogLoadingBar = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        // Lấy Unique ID từ Authentication và lưu vào firebase
        // với Node cha(Users) và đối tượng node con với tên UID(username, fullname, country......)
        mCurrentUserID = mAuth.getCurrentUser().getUid();
        mUserDatabaseReference =
                FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUserID);
        mUserProfileImageReference =
                FirebaseStorage.getInstance().getReference().child("Profile Images");

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAccountInformation();
            }
        });

        mProFileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/jpeg/*");
//                galleryIntent.setAction(Intent.ACTION_VIEW);
//                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_PICK_REQUEST);
            }
        });
        //TODO: Đọc data và lắng nghe dữ liệu thay đổi dùng addValueEventListener()
        mUserDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    if(dataSnapshot.hasChild("profileimage")){
                        // Đến đường dẫn để lấy string url và hiển thị
                        String image = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.get().load(image)
                                .placeholder(R.drawable.profile)
                                .into(mProFileImage);
                    }
                    else {
                        ShowToast.showToast(getApplicationContext(), "Vui lòng chọn một hình ảnh");
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Request thành công và trả về kết quả ok
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
                            mUserDatabaseReference.child("profileimage")
                                    .setValue(dowloadResult.toString());
                            mProgressDialogLoadingBar.dismiss();

                            Intent intent = new Intent(SetupActivity.this, SetupActivity.class);
                            startActivity(intent);
                            ShowToast.showToast(SetupActivity.this, "Lưu thành công");
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

    private void saveAccountInformation() {
        String userName = mUserNameEdt.getText().toString().trim();
        String fullName = mFullNameEdt.getText().toString().trim();
        String country = mCountryEdt.getText().toString().trim();

        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(fullName) || TextUtils.isEmpty(
                country)) {
            ShowToast.showToast(this, "Vui lòng nhập thông tin đầy đủ!");
        } else {
            // Tạo tiến trình để nhận biết
            mProgressDialogLoadingBar.setTitle("Lưu Thông Tin Người Dùng");
            mProgressDialogLoadingBar.setMessage("Vui lòng chờ trong giây lát....");
            mProgressDialogLoadingBar.show();
            mProgressDialogLoadingBar.setCanceledOnTouchOutside(true);

            // Đưa dữ liệu vào HashMap và đổ lên Firebase(Database)
            HashMap map = new HashMap();
            map.put("username", userName);
            map.put("fullname", fullName);
            map.put("country", country);
            map.put("status", "hey there, i am using poster social network, developed by E-Team");
            map.put("gender", "none");
            map.put("dob", "none");
            map.put("relationshipstatus", "none");
            mUserDatabaseReference.updateChildren(map)
                    .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                sendUserToMainActivity();
                                ShowToast.showToast(getApplicationContext(),
                                        "Thông tin đã được lưu thành công!");
                                mProgressDialogLoadingBar.dismiss();
                            } else {
                                //VD không có mạng
                                String message = task.getException().getMessage();
                                Toast.makeText(SetupActivity.this, "Có lỗi xảy ra: " + message,
                                        Toast.LENGTH_LONG).show();
                                mProgressDialogLoadingBar.dismiss();
                            }
                        }
                    });
        }
    }

    private void findId() {
        mUserNameEdt = (EditText) findViewById(R.id.setup_username);
        mFullNameEdt = (EditText) findViewById(R.id.setup_full_name);
        mCountryEdt = (EditText) findViewById(R.id.setup_country);
        mSaveBtn = (Button) findViewById(R.id.setup_save_infor_btn);
        mProFileImage = (CircleImageView) findViewById(R.id.setup_profile_img);
    }

    private void sendUserToMainActivity() {
        Intent intent = new Intent(SetupActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        //finish();
    }
}
