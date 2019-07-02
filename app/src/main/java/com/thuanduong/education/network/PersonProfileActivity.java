package com.thuanduong.education.network;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PersonProfileActivity extends AppCompatActivity {

    /** 4 biến trạng thái của  mCurrentState Yêu cầu friend and not friend*/
    private final static String NOT_FRIEND = "NOT_FRIEND";
    private final static String FRIEND = "FRIEND";
    private final static String REQUEST_SENT = "REQUEST_SENT";
    private final static String REQUEST_RECEIVED = "REQUEST_RECEIVED";

    /** Hai biến trạng thái của đã gửi và đã nhận khi request friend được lưu vào child database */
    private final static String STATE_SENT = "sent";
    private final static String STATE_RECEIVED = "received";


    /** Biến thể hiện trạng thái friend(gửi request friend hay chưa) */
    private String mCurrentState;

    private DatabaseReference mFriendRequestDatabaseRef, mUserRef, mFriendDatabaseRef;
    private FirebaseAuth mAuth;

    private String mSenderUserID, mSaveCurrentDate;

    /** Biến nhận các node con users(được lưu = uid) của người dùng khác để kết bạn được gửi từ FindFriendActivity*/
    private String mReceiverUserID;

    private TextView mUserName, mFullName, mUserStatus, mUserCountry, mUserGender, mUserRelation, mUserDOB;
    private CircleImageView mUserProfileImage;
    private Button mSendFriendRequestBtn, mCancelFriendRequestBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);

        findID();

        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mFriendRequestDatabaseRef = FirebaseDatabase.getInstance().getReference().child("FriendRequests");
        mFriendDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Friends");

        /** Nhận các node user ( với tên uid) của người dùng khác để kết bạn, được gửi từ FindFriendActivity*/
        mReceiverUserID = getIntent().getExtras().getString("visit_user_id").toString();

        mAuth = FirebaseAuth.getInstance();
        /** Lấy UID của người dùng hiện tại(online)*/
        mSenderUserID = mAuth.getCurrentUser().getUid();


        /** Khi FindFriendActivity search friend, click vào người dùng cụ thể,
         *  mReceiverUserID nhận UID của người dùng đó và Ref đến child đó
         *  để nhận thông tin người ấy , hiển thị ra kết bạn*/
        displayPersonProfile();

        /** Khởi tạo trạng thái ban đầu là chưa kết bạn*/
        mCurrentState = NOT_FRIEND;

        /** Kiểm tra và hiển thị các button đúng lúc và setOnCLick mSendFriendRequestBtn gửi yêu cầu kết bạn*/
        validateButton();

    }

    /** Kiểm tra và hiển thị các button đúng lúc , khi click nút thêm bạn
     * nếu mCurrentState = "NOT_FRIEND " thì có thể gửi yêu cầu : sendFriendRequestToPerson */
    private void validateButton() {
        mCancelFriendRequestBtn.setVisibility(View.INVISIBLE);
        mCancelFriendRequestBtn.setEnabled(false);

        // kiểm tra nếu uid người gửi và nhận kb cùng nhau thì ẩn 2 nút
        if(mSenderUserID.equals(mReceiverUserID)){
            mCancelFriendRequestBtn.setVisibility(View.INVISIBLE);
            mSendFriendRequestBtn.setVisibility(View.INVISIBLE);
        }else{
            mSendFriendRequestBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSendFriendRequestBtn.setEnabled(false);
                    if(mCurrentState == NOT_FRIEND){
                        // Gửi Y/C kết bạn
                        sendFriendRequestToPerson();
                    }
                    if(mCurrentState == REQUEST_SENT){
                        // Hủy Yêu cầu kết bạn
                        cancelFriendRequest();
                    }
                    if(mCurrentState == REQUEST_RECEIVED){
                        // Người nhận kb  ,chấp nhận
                        accepFriendRequest();
                    }
                    if(mCurrentState == FRIEND){
                        // Hủy Bạn bè
                        unFriendExisting();
                    }
                }
            });
            // onClick Từ Chối
            mCancelFriendRequestBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelFriendRequest();
                }
            });
        }
    }

    private void unFriendExisting() {
        mFriendDatabaseRef.child(mSenderUserID).child(mReceiverUserID).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            mFriendDatabaseRef.child(mReceiverUserID).child(mSenderUserID).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                mSendFriendRequestBtn.setEnabled(true);
                                                mCurrentState = NOT_FRIEND;
                                                mSendFriendRequestBtn.setText("Thêm Bạn");

                                                mCancelFriendRequestBtn.setVisibility(View.INVISIBLE);
                                                mCancelFriendRequestBtn.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });

    }

    private void accepFriendRequest() {
        // Date
        Calendar calForDate =  Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        mSaveCurrentDate = currentDate.format(calForDate.getTime());

        mFriendDatabaseRef.child(mSenderUserID).child(mReceiverUserID).child("friend_date").setValue(mSaveCurrentDate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            mFriendDatabaseRef.child(mReceiverUserID).child(mSenderUserID)
                                    .child("friend_date").setValue(mSaveCurrentDate);

                            /** Sau khi gửi thành công , xóa các các child request friend và điều chỉnh trạng thái
                             * mCurrentState = FRIEND, mSendFriendrRequestBtn.setText("Hủy Kết Bạn")
                             * Người gửi -> người nhận : request_type = sent */
                            mFriendRequestDatabaseRef.child(mSenderUserID).child(mReceiverUserID).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                /** Người nhận -> người gửi : request_type = received */
                                                mFriendRequestDatabaseRef.child(mReceiverUserID).child(mSenderUserID).removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    mSendFriendRequestBtn.setEnabled(true);
                                                                    mCurrentState = FRIEND;
                                                                    mSendFriendRequestBtn.setText("Xóa Bạn Bè");

                                                                    mCancelFriendRequestBtn.setVisibility(View.INVISIBLE);
                                                                    mCancelFriendRequestBtn.setEnabled(false);
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void cancelFriendRequest()  {
        /** Người gửi -> người nhận : request_type = sent */
        mFriendRequestDatabaseRef.child(mSenderUserID).child(mReceiverUserID).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            /** Người nhận -> người gửi : request_type = received */
                            mFriendRequestDatabaseRef.child(mReceiverUserID).child(mSenderUserID).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                mSendFriendRequestBtn.setEnabled(true);
                                                mCurrentState = NOT_FRIEND;
                                                mSendFriendRequestBtn.setText("Thêm Bạn");

                                                mCancelFriendRequestBtn.setVisibility(View.INVISIBLE);
                                                mCancelFriendRequestBtn.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    /** Hiển thị thông tin của một person để có thể kết bạn*/
    private void displayPersonProfile() {
        mUserRef.child(mReceiverUserID).addValueEventListener(new ValueEventListener() {
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
                    mUserRelation.setText("Relationship: " + myRelation);

                    /** Kiểm tra dữ liệu database và setText tên nút từng trường hợp , setOnClick mCancelFriendRequestBtn*/
                    maintainanceOfButton();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**  Kiểm tra dữ liệu database và setText tên nút từng trường hợp, setOnClick mCancelFriendRequestBtn
     * nếu kết quả trả về trong request_type = SENT tức là đã gửi thì thay đổi nút mSendFriendRequestBtn thành Hủy Yêu cầu*/
    private void maintainanceOfButton() {
        mFriendRequestDatabaseRef.child(mSenderUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(mReceiverUserID)){
                    String request_type = dataSnapshot.child(mReceiverUserID).child("request_type").getValue().toString();
                    if(request_type.equals(STATE_SENT)){ // đã gửi

                        mCurrentState = REQUEST_SENT;
                        mSendFriendRequestBtn.setText("Hủy Yêu Cầu");
                        mCancelFriendRequestBtn.setVisibility(View.INVISIBLE);
                        mCancelFriendRequestBtn.setEnabled(false);

                    }else if(request_type.equals(STATE_RECEIVED)) {
                        // Thay text cho mSendFriendRequestBtn sau khi kiểm tra database STATE_RECEIVED
                        mCurrentState = REQUEST_RECEIVED;
                        mSendFriendRequestBtn.setText("Đồng Ý");

                        mCancelFriendRequestBtn.setVisibility(View.VISIBLE);
                        mCancelFriendRequestBtn.setEnabled(true);

                    }
                } else {
                    mFriendDatabaseRef.child(mSenderUserID).addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(mReceiverUserID)){
                                mCurrentState = FRIEND;
                                // Thay text cho mSendFriendRequestBtn sau khi kiểm tra database đã kết bạn
                                mSendFriendRequestBtn.setText("Xóa Bạn Bè");
                                // Đã kết bạn, nên ẩn nút request cancel(Từ chối)
                                mCancelFriendRequestBtn.setVisibility(View.INVISIBLE);
                                mCancelFriendRequestBtn.setEnabled(false);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendFriendRequestToPerson() {
        /** Người gửi -> người nhận : request_type = sent */
        mFriendRequestDatabaseRef.child(mSenderUserID).child(mReceiverUserID).child("request_type").setValue(STATE_SENT)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            /** Người nhận -> người gửi : request_type = received */
                            mFriendRequestDatabaseRef.child(mReceiverUserID).child(mSenderUserID).child("request_type")
                                    .setValue(STATE_RECEIVED)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                mSendFriendRequestBtn.setEnabled(true);
                                                mCurrentState = REQUEST_SENT;
                                                mSendFriendRequestBtn.setText("Hủy Yêu Cầu");

                                                mCancelFriendRequestBtn.setVisibility(View.INVISIBLE);
                                                mCancelFriendRequestBtn.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void findID() {
        mUserName = (TextView) findViewById(R.id.person_profile_username);
        mFullName = (TextView) findViewById(R.id.person_profile_fullname);
        mUserStatus = (TextView) findViewById(R.id.person_profile_status);
        mUserCountry = (TextView) findViewById(R.id.person_profile_country);
        mUserGender = (TextView) findViewById(R.id.person_profile_gender);
        mUserDOB = (TextView) findViewById(R.id.person_profile_dob);
        mUserRelation = (TextView) findViewById(R.id.person_profile_relationship_status);
        mUserProfileImage = (CircleImageView) findViewById(R.id.person_profile_circle_image);
        mSendFriendRequestBtn = (Button) findViewById(R.id.person_profile_send_friend_button);
        mCancelFriendRequestBtn = (Button) findViewById(R.id.person_profile_denny_friend_button);

    }
}
