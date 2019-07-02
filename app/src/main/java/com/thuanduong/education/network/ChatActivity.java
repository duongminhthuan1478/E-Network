package com.thuanduong.education.network;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.thuanduong.education.network.Adapter.MessageAdapter;
import com.thuanduong.education.network.Model.Message;
import com.thuanduong.education.network.Ultil.ShowToast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    /** Hai biến thể hiện trạng thái khi user onl/off */
    private static final String USER_ONLINE = "ONLINE";
    private static final String USER_OFFLINE = "OFFLINE";

    private FirebaseAuth mAuth;
    private DatabaseReference mRootDatabaseRef, mUserDatabaseRef;

    private RecyclerView mMessageRecyclerView;
    private final List<Message> mMessageList = new ArrayList<>();
    private MessageAdapter mMessageAdapter;

    private Toolbar mChatToolbar;
    private ImageButton mSendImageFileButton, mSendMessageButton;
    private EditText mMessageInputEdt;

    // Hai biến nhận thời gian
    private String mSaveCurrentDate, mSaveCurrentTime;

    /**current user id*/
    private String mMessageSenderID;

    /** Khởi tạo 2 biến nhận key intent từ (FriendActivity), uid and fullname of user */
    private String mMessageReceiverID, mMessageReceiverFullName;

    /** Khởi tạo 2 view để hiển thị mMessageReceiverID, mMessageReceiverFullName */
    private TextView mReceiverFullName;
    private CircleImageView mReiceiverProfileImage;

    /** TextView hiển thị người dùng online/offline */
    private TextView mUserOnlineOrOffLineText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        actionBar();
        findID();

         /** 2 biến nhận key intent từ (FriendActivity) uid, fullname*/
        mMessageReceiverID = getIntent().getExtras().getString("visit_user_id").toString();
        mMessageReceiverFullName  = getIntent().getExtras().getString("fullname").toString();

        mRootDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mUserDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        mMessageSenderID = mAuth.getCurrentUser().getUid();

        displayReceiverInformation();

        // set onclick gửi tin nhắn
        mSendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        fetchMessages();
    }

    @Override
    protected void onStop() {
        super.onStop();
        updateUserState(USER_OFFLINE);

    }


    private void sendMessage() {
        // Thiết lập trạng tháingười dùng online
        updateUserState(USER_ONLINE);

        String messgaeText = mMessageInputEdt.getText().toString();
        // validation
        if(TextUtils.isEmpty(messgaeText)){
            ShowToast.showToast(this,"Nội dung gửi không được để trống !");
        } else {
            // current - > người nhận, người nhận -> current
            String message_sender_ref = "Messages/" + mMessageSenderID + "/" + mMessageReceiverID;
            String message_receiver_ref = "Messages/" + mMessageReceiverID + "/" + mMessageSenderID;

            // Đẩy lên dạng node pushid và lấy nó về
            DatabaseReference userMessageRef = mRootDatabaseRef.child("Messages").child(
                    mMessageSenderID).child(mMessageReceiverID).push();
            String message_pushID = userMessageRef.getKey();

            // Date
            Calendar calForDate =  Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            mSaveCurrentDate = currentDate.format(calForDate.getTime());

            // Time
            Calendar calForTime =  Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
            mSaveCurrentTime = currentTime.format(calForTime.getTime());

            Map valueMap = new HashMap();
            valueMap.put("message", messgaeText);
            valueMap.put("time", mSaveCurrentTime);
            valueMap.put("date", mSaveCurrentDate);
            valueMap.put("type", "text"); // type of send text/ image
            // Người gửi(current user online)
            // cả hai childe current - > người nhận, người nhận -> current phải hiển thị duy nhất người gửi tin nhắn là sender(online user)
            valueMap.put("from", mMessageSenderID);



            /** Data sẽ được đẩy lên với 2 child:
             * 1: Người gửi -> người nhận -> pushID với map giá trị
             * 2: Người nhận -> người gửi -> pushId với map giá trị*/
            Map messageBodyDetailMap = new HashMap();
            messageBodyDetailMap.put(message_sender_ref + "/" + message_pushID, valueMap);
            messageBodyDetailMap.put(message_receiver_ref + "/" + message_pushID, valueMap);

            mRootDatabaseRef.updateChildren(messageBodyDetailMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()) {
                        mMessageInputEdt.setText("");
                    }else {
                        ShowToast.showToast(ChatActivity.this,"Có lỗi xảy ra : "
                                + task.getException().getMessage());
                    }
                }
            });

        }

    }

    private void fetchMessages() {
        mRootDatabaseRef.child("Messages").child(mMessageSenderID).child(mMessageReceiverID).addChildEventListener(

                new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if (dataSnapshot.exists()) {
                            Message mess = dataSnapshot.getValue(Message.class);
                            mMessageList.add(mess);
                            mMessageAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if (dataSnapshot.exists()) {
                            Message mess = dataSnapshot.getValue(Message.class);
                            mMessageList.add(mess);
                            mMessageAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String
                            s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void displayReceiverInformation() {
        /** hoder adapter từ FriendActivity đã vào đúng vị trí của user
         * vì vậy, chỉ cần gửi biến qua intent và setText cho name*/
        mReceiverFullName.setText(mMessageReceiverFullName);
        // Vào child Users và vào đúng child uid của intent đã được gửi để lấy ảnh hiển thị lên
        mRootDatabaseRef.child("Users").child(mMessageReceiverID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String profileimage = dataSnapshot.child("profileimage").getValue().toString();
                    Picasso.get().load(profileimage).into(mReiceiverProfileImage);

                    // user Online/offline
                    String type = dataSnapshot.child("userState").child("type").getValue().toString();
                    String lastDate = dataSnapshot.child("userState").child("date").getValue().toString();
                    String lastTime = dataSnapshot.child("userState").child("time").getValue().toString();

                    if(type.equals(USER_ONLINE)){
                        mUserOnlineOrOffLineText.setText("Online");
                    }else {
                        mUserOnlineOrOffLineText.setText("Hoạt động lúc " + lastTime + " " +  lastDate);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /** Check người dùng đang online hay offline*/
    private void updateUserState(String state){
        String saveCurrentDate, saveCurrentTime;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calForTime.getTime());

        Map currentUserState = new HashMap();
        currentUserState.put("time", saveCurrentTime);
        currentUserState.put("date", saveCurrentDate);
        currentUserState.put("type", state);

        // CurrentUser = mMessageSenderID
        mUserDatabaseRef.child(mMessageSenderID).child("userState").updateChildren(currentUserState);

    }

    private void actionBar() {
        mChatToolbar = (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(mChatToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // set customeview
        actionBar.setDisplayShowCustomEnabled(true); // hiển thị layout custom
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = layoutInflater.inflate(R.layout.chat_custom_bar, null);
        actionBar.setCustomView(action_bar_view);
    }

    private void findID() {
        mSendImageFileButton = (ImageButton) findViewById(R.id.chat_send_image_imagebutton);
        mSendMessageButton = (ImageButton) findViewById(R.id.chat_send_message_imagebutton);
        mMessageInputEdt = (EditText) findViewById(R.id.chat_input_message_edt);
        mUserOnlineOrOffLineText = (TextView) findViewById(R.id.custom_user_last_online);

        mReceiverFullName = (TextView) findViewById(R.id.custom_profile_name);
        mReiceiverProfileImage = (CircleImageView) findViewById(R.id.custom_profile_image);

        //recycler message
        mMessageAdapter = new MessageAdapter(mMessageList);
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.message_list_recyclerview);
        mMessageRecyclerView.setHasFixedSize(true);
        mMessageRecyclerView.setAdapter(mMessageAdapter);
        mMessageRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

}
