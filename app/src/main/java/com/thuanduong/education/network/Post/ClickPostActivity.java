package com.thuanduong.education.network.Post;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.thuanduong.education.network.R;
import com.thuanduong.education.network.Ultil.ShowToast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ClickPostActivity extends AppCompatActivity {

    private DatabaseReference mClickPostDatabaseRef;
    private FirebaseAuth mAuth;

    private String  mCurrentUserID;

    private ImageView mPostImage;
    private TextView mPostDescriptionTxt;
    private Button mDeleteButton, mEditButton;

    String postKey, databaseUserID, postDescription, postImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_post);
        findID();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserID = mAuth.getCurrentUser().getUid();

        // Ẩn hai nút
        mDeleteButton.setVisibility(View.INVISIBLE);
        mEditButton.setVisibility(View.INVISIBLE);

        //  key của vị trí Post cụ thể tại firebase(tên 1 node)
        postKey = getIntent().getExtras().get("PostKey").toString();
        mClickPostDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(postKey);
        mClickPostDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    // Kiểm tra dữ liệu, hiển thị View
                    postDescription = dataSnapshot.child("description").getValue().toString();
                    postImage = dataSnapshot.child("postimage").getValue().toString();
                    mPostDescriptionTxt.setText(postDescription);
                    Picasso.get().load(postImage).into(mPostImage);

                    /** Kiểm tra xem người dùng hiện tại (đang online)
                     * khi cick vào một bài post có trùng uid(người đăng bài post không)
                     * Nếu trung mới hiển thị nút và cho cập nhập dữ liệu*/
                    databaseUserID = dataSnapshot.child("uid").getValue().toString();
                    if(mCurrentUserID.equals(databaseUserID)){
                        mDeleteButton.setVisibility(View.VISIBLE);
                        mEditButton.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });
        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editCurrentPost(postDescription);
            }
        });

    }

    private void deleteCurrentPost() {
        // Xóa bài trên Database,
        mClickPostDatabaseRef.removeValue();
        //TODO: Xóa hình ảnh trên Storage
    }

    private void editCurrentPost(String description) {
        AlertDialog.Builder  builder = new AlertDialog.Builder(ClickPostActivity.this);
        builder.setTitle("Edit Post");
        final EditText inputFiel = new EditText(ClickPostActivity.this);
        inputFiel.setText(description);
        builder.setView(inputFiel);
        builder.setPositiveButton("Cập Nhập", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mClickPostDatabaseRef.child("description").setValue(inputFiel.getText().toString());
                ShowToast.showToast(ClickPostActivity.this, "Cập nhập thành công");
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        Dialog dialog = builder.create();
        dialog.show();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Bạn muốn xóa bài đăng này? ");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteCurrentPost();
                ShowToast.showToast(ClickPostActivity.this,"Xóa bài thành công");
                // Đóng activity sau khi xóa bài
                finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void findID() {
        mPostImage = (ImageView) findViewById(R.id.post_click_image);
        mPostDescriptionTxt = (TextView) findViewById(R.id.post_click_description);
        mDeleteButton = (Button) findViewById(R.id.delete_post_btn);
        mEditButton = (Button) findViewById(R.id.edit_post_btn);

    }

}
