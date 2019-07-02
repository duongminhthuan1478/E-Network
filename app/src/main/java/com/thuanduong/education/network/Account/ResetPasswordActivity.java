package com.thuanduong.education.network.Account;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.thuanduong.education.network.R;
import com.thuanduong.education.network.Ultil.ShowToast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private Toolbar mToolbar;
    private Button mResetPwSendEmailButton;
    private EditText mResetEmailInputEdt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        findID();
        actionBar();


        mAuth = FirebaseAuth.getInstance();

        mResetPwSendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = mResetEmailInputEdt.getText().toString().trim();
                if(TextUtils.isEmpty(userEmail)){
                    ShowToast.showToast(ResetPasswordActivity.this, "Vui lòng nhập địa chỉ email...");
                }else{
                    mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                String mess = "Xác nhận thành công, vui lòng kiểm tra email";
                                ShowToast.showToast(ResetPasswordActivity.this, mess);
                                startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                            }
                            else {
                                ShowToast.showToast(ResetPasswordActivity.this,"Có lỗi xảy ra : "
                                    + task.getException().getMessage());
                            }
                        }
                    });
                }

            }
        });
    }

    private void actionBar() {
        mToolbar = (Toolbar) findViewById(R.id.forget_password_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_activity_reset_password);
    }

    private void findID() {
        mResetPwSendEmailButton = (Button) findViewById(R.id.reset_password_email_button);
        mResetEmailInputEdt = (EditText) findViewById(R.id.reset_password_email_edt);
    }
}
