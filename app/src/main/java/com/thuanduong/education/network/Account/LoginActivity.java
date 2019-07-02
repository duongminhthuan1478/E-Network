package com.thuanduong.education.network.Account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.thuanduong.education.network.MainActivity;
import com.thuanduong.education.network.R;
import com.thuanduong.education.network.Ultil.ShowToast;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    //private static final String CLIENT_ID_GOOGLE_SERVICE_JSON = "847058202618-nt09ausr8f9bcen7c6m79p8pa1efdv98.apps.googleusercontent.com";
    private final static String TAG = "LoginActivity";
    private final static int RC_SIGN_IN = 1;

    private Button mLoginButton;
    private EditText mUserEmail, mUserPassword;
    private TextView mCreateAccountLink, mForgetPasswordLink;
    private FirebaseAuth mAuth;
    private ImageView mGoogleSignInBtn;

    private ProgressDialog mProgressDialogLoadingBar;


    private GoogleApiClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findID();

        mCreateAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        mForgetPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });
        mAuth = FirebaseAuth.getInstance();

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allowUserToLogin();
            }
        });
        mProgressDialogLoadingBar = new ProgressDialog(this);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        ShowToast.showToast(getApplicationContext(),"Đăng nhập với Google không thành công!");
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mGoogleSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RC_SIGN_IN:         // request signin google
                mProgressDialogLoadingBar.setTitle("Google Sign In");
                mProgressDialogLoadingBar.setMessage("Vui lòng chờ trong giây lát...");
                mProgressDialogLoadingBar.setCanceledOnTouchOutside(true);
                mProgressDialogLoadingBar.show();

                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if(result.isSuccess()){
                    GoogleSignInAccount account = result.getSignInAccount();
                    firebaseAuthWithGoogle(account);
                    ShowToast.showToast(getApplicationContext(),"Tiến trình đang xử lý, vui lòng chờ!!");
                }else {
                    sendUserToLoginActivity();
                    ShowToast.showToast(getApplicationContext(), "Tiến trình thất bại");
                    mProgressDialogLoadingBar.dismiss();
                }
                break;
        }

    }


    private void findID() {
        mCreateAccountLink = (TextView) findViewById(R.id.register_account_link);
        mUserEmail = (EditText) findViewById(R.id.login_email);
        mUserPassword = (EditText) findViewById(R.id.login_password);
        mLoginButton = (Button) findViewById(R.id.login_button);
        mGoogleSignInBtn = (ImageView) findViewById(R.id.google_signin_btn);
        mForgetPasswordLink = (TextView) findViewById(R.id.forget_password_link_text);

    }

    private void allowUserToLogin() {
        String email = mUserEmail.getText().toString().trim();
        String pw = mUserPassword.getText().toString().trim();

        // Validation
        if(TextUtils.isEmpty(email)){
            ShowToast.showToast(this, "Email không được để trống!");
        } else if(TextUtils.isEmpty(pw)){
            ShowToast.showToast(this, "Bạn chưa nhập mật khẩu!");
        } else {
            // Tạo hộp thoại tiến trình đang xử lý để người dùng biết
            mProgressDialogLoadingBar.setTitle("Đăng Nhập");
            mProgressDialogLoadingBar.setMessage("Vui lòng chờ trong giây lát...");
            mProgressDialogLoadingBar.setCanceledOnTouchOutside(true);
            mProgressDialogLoadingBar.show();

            // Đăng nhập với email and password
            mAuth.signInWithEmailAndPassword(email, pw)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                sendUserToMainActivity();
                                ShowToast.showToast(getApplicationContext(),"Đăng nhập thành công!");
                                // bỏ qua loading bar nếu task xử lý xong
                                mProgressDialogLoadingBar.dismiss();
                            }else {
                                String message = task.getException().toString();
                                ShowToast.showToast(getApplicationContext(),"Error occured: " + message);
                                // bỏ qua loading bar nếu task xử lý xong
                                mProgressDialogLoadingBar.dismiss();

                            }
                        }
                    });
        }
    }

    private void sendUserToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void sendUserToLoginActivity(){
        Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void signIn() {
        Intent signInIntent =  Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            sendUserToMainActivity();
                            mProgressDialogLoadingBar.dismiss();

                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            ShowToast.showToast(LoginActivity.this,
                                    "Có lỗi xảy ra, xảy ra" + task.getException().toString());
                            mProgressDialogLoadingBar.dismiss();
                        }

                    }
                });
    }
}
