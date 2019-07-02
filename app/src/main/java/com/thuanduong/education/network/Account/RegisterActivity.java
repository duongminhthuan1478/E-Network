package com.thuanduong.education.network.Account;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private EditText mUserEmailEdt, mPassWordEdt, mConfirmPwEdt;
    private Button mCreateAccountBtn;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgressDialogLoadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        findID();
        mCreateAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAccount();
            }
        });
        // Khởi tạo FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        mProgressDialogLoadingBar = new ProgressDialog(this);
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            // Sau khi xác thực người dùng bằng FirebaseAuth, chuyển đến main
            // onStart() trong main sẽ xem xét đã lưu User chưa để Setup hoặc vào Main
            sendUserToMainActivity();
        }
    }

    private void createNewAccount() {
        String email = mUserEmailEdt.getText().toString().trim();
        String pw = mPassWordEdt.getText().toString().trim();
        String confirmPW = mConfirmPwEdt.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            ShowToast.showToast(this, "Email không được để trống!");
        } else if(TextUtils.isEmpty(pw)){
            ShowToast.showToast(this, "Chưa nhập mật khẩu!");
        } else if(TextUtils.isEmpty(confirmPW)){
            ShowToast.showToast(this, "Vui lòng xác nhận mật khẩu!");
        } else if(!pw.equals(confirmPW)){
            // Check string phải dùng equals
            ShowToast.showToast(this,"Mật khẩu xác nhận không đúng!");
        } else {
            // Tạo hộp thoại tiến trình đang xử lý để người dùng biết
            mProgressDialogLoadingBar.setTitle("Creating New Account");
            mProgressDialogLoadingBar.setMessage("Vui lòng chờ trong giây lát....");
            mProgressDialogLoadingBar.show();
            // Khi diaglog xuất hiện người dùng click trên screen sẽ không biến mất cho đến khi authenticate người dùng
            //  hoặc có lỗi xảy ra
            mProgressDialogLoadingBar.setCanceledOnTouchOutside(true);

            // Tạo người dùng với mail và mật khẩu
            mAuth.createUserWithEmailAndPassword(email, pw)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            // Xác thực email
                            sendEmailVerificationMessage();
                            // bỏ qua loading bar nếu task xử lý xong
                            mProgressDialogLoadingBar.dismiss();

                        }else {
                            // task not successful , ví dụ email đã tồn tại....
                            String message = task.getException().getMessage();
                            ShowToast.showToast(getApplicationContext(), "Error Occured: " + message);
                            // bỏ qua loading bar nếu task xử lý xong
                            mProgressDialogLoadingBar.dismiss();
                        }
                    }
                });
        }

    }

    /**  Xác thực email*/
    private void sendEmailVerificationMessage(){
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        // trả người dùng lại  login , khi người dùng login thành công đến MainActivity
                        // Main sẽ kiểm tra nếu người dùng chưa có thông tin , sẽ dẫn đến Setup
                        sendUserToLoginActivity();
                        Toast.makeText(getApplicationContext(), "Đăng ký thành công , vui lòng đăng nhập email để xác nhận tài khoản ",Toast.LENGTH_LONG).show();
                        mAuth.signOut();
                    }
                    else {
                        ShowToast.showToast(getApplicationContext(),"Lỗi xảy ra: " + task.getException().getMessage());
                        mAuth.signOut();
                    }
                }
            });
        }
    }


    private void findID() {
        mUserEmailEdt = (EditText) findViewById(R.id.register_email);
        mPassWordEdt = (EditText) findViewById(R.id.register_password);
        mConfirmPwEdt = (EditText) findViewById(R.id.register_confirm_password);
        mCreateAccountBtn = (Button) findViewById(R.id.register_create_button);

    }

    private void sendUserToMainActivity() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    private void sendUserToLoginActivity(){
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
