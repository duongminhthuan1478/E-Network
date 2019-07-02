package com.thuanduong.education.network.Friends_RequestFriend;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.TextView;
import com.thuanduong.education.network.Model.FriendRequest;
import com.thuanduong.education.network.PersonProfileActivity;
import com.thuanduong.education.network.R;
import com.thuanduong.education.network.Ultil.ShowToast;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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

public class RequestFriendFragment extends Fragment {

    private FirebaseAuth mAuth;
    private String mSaveCurrentDate;

    /** Biến nhận các node con users(được lưu = uid) của người dùng khác để kết bạn được gửi từ FindFriendActivity*/
    private String mCurrentUserID, mReceiverUserID;

    private DatabaseReference mFriendRequestDatabaseRef, mUserDatabaseRef, mFriendDatabaseRef;

    private RecyclerView mRequestFriendRecyclerView;
    private FirebaseRecyclerAdapter<FriendRequest, RequestFriendViewHolder> mFirebaseRecyclerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview =  inflater.inflate(R.layout.fragment_request_friend, container, false);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUserID = mAuth.getCurrentUser().getUid();
        mFriendRequestDatabaseRef = FirebaseDatabase.getInstance().getReference().child("FriendRequests");
        mUserDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mFriendDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Friends");


        displayRecyclerView(rootview);

        displayAllRequestFriends();

        return rootview;
    }

    @Override
    public void onStart() {
        super.onStart();
        mFirebaseRecyclerAdapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        mFirebaseRecyclerAdapter.stopListening();
    }

    private void displayAllRequestFriends(){
        /** Vào Node RequestFriends với child mCurrentUserID, sau đó lấy từng node con ra lưu vào key
         *  Vào child key để kiểm tra dữ liệu request_type nếu là received
         *  vào node user người gửi để nhận thông tin
         *  Các node con khi requestfriend được lưu dạng uid của người dùng
         */
        Query query = mFriendRequestDatabaseRef.child(mCurrentUserID);
        FirebaseRecyclerOptions<FriendRequest> options =
                new FirebaseRecyclerOptions.Builder<FriendRequest>().setQuery(query, FriendRequest.class).build();

        mFirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<FriendRequest, RequestFriendViewHolder>(options) {


            @Override
            protected void onBindViewHolder(@NonNull final RequestFriendViewHolder holder, int position,
                    @NonNull FriendRequest model) {

                /** Khi khởi động Adapter sẽ đi từng vị trí của FriendRequest như mảng
                 * do đó chúng ta get key của từng vị trí đó để lấy dữ liệu  other user
                 * Các node con khi requestfriend được lưu dạng uid của người dùng*/
                final String key = getRef(position).getKey();
                mFriendRequestDatabaseRef.child(mCurrentUserID).child(key).addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("request_type")){
                            String request_type = dataSnapshot.child("request_type").getValue().toString();
                            if(request_type.equals("received")){
                                mUserDatabaseRef.child(key).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String fullname = dataSnapshot.child("fullname").getValue().toString();
                                        String profileimage = dataSnapshot.child("profileimage").getValue().toString();
                                        holder.setFullname(fullname);
                                        holder.setProfileimage(profileimage);

                                        holder.fullnameText.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                String visit_user_id = key;

                                                Intent profileIntent = new Intent(getActivity(), PersonProfileActivity.class);
                                                profileIntent.putExtra("visit_user_id", visit_user_id);
                                                startActivity(profileIntent);
                                            }
                                        });

                                        holder.circleImageView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                String visit_user_id = key;

                                                Intent profileIntent = new Intent(getActivity(), PersonProfileActivity.class);
                                                profileIntent.putExtra("visit_user_id", visit_user_id);
                                                startActivity(profileIntent);
                                            }
                                        });

                                        holder.acceptFriendButton.setOnClickListener(new View
                                                .OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                // key được lưu với dạng uid của user
                                                accepFriendRequest(key);
                                            }
                                        });
                                        holder.dennyFriendButton.setOnClickListener(new View
                                                .OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                // key được lưu với dạng uid của user
                                                cancelFriendRequest(key);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public RequestFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                    int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.request_friend_item, parent, false);
                return new RequestFriendViewHolder(view);
            }
        };
        mRequestFriendRecyclerView.setAdapter(mFirebaseRecyclerAdapter);
    }
    private void accepFriendRequest(final String key) {
        mReceiverUserID = key;
        // Date
        Calendar calForDate =  Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        mSaveCurrentDate = currentDate.format(calForDate.getTime());

        mFriendDatabaseRef.child(mCurrentUserID).child(mReceiverUserID).child("friend_date").setValue(mSaveCurrentDate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            mFriendDatabaseRef.child(mReceiverUserID).child(mCurrentUserID)
                                    .child("friend_date").setValue(mSaveCurrentDate);

                            /** Sau khi gửi thành công , xóa các các child request friend và điều chỉnh trạng thái
                             * mCurrentState = FRIEND, mSendFriendrRequestBtn.setText("Hủy Kết Bạn")
                             * Người gửi -> người nhận : request_type = sent */
                            mFriendRequestDatabaseRef.child(mCurrentUserID).child(mReceiverUserID).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                /** Người nhận -> người gửi : request_type = received */
                                                mFriendRequestDatabaseRef.child(mReceiverUserID).child(mCurrentUserID).removeValue();
                                                ShowToast.showToast(getActivity(),"Thêm bạn thành công!");
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
    private void cancelFriendRequest(String key)  {
        mReceiverUserID = key;

        /** Người gửi -> người nhận : request_type = sent */
        mFriendRequestDatabaseRef.child(mCurrentUserID).child(mReceiverUserID).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            /** Người nhận -> người gửi : request_type = received */
                            mFriendRequestDatabaseRef.child(mReceiverUserID).child(mCurrentUserID).removeValue();
                        }
                    }
                });
    }

    private void displayRecyclerView(View rootview) {
        mRequestFriendRecyclerView = (RecyclerView) rootview.findViewById(R.id.request_friend_recyclerview);
        mRequestFriendRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRequestFriendRecyclerView.setLayoutManager(linearLayoutManager);
    }


    public static class RequestFriendViewHolder extends RecyclerView.ViewHolder{

        View mView;
        TextView fullnameText;
        CircleImageView circleImageView;
        Button acceptFriendButton, dennyFriendButton;

        public RequestFriendViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            acceptFriendButton = itemView.findViewById(R.id.request_friend_button_comfirm);
            dennyFriendButton = itemView.findViewById(R.id.request_friend_button_deny);
        }
        public void setFullname(String fullname) {
            fullnameText = itemView.findViewById(R.id.request_friend_fullname);
            fullnameText.setText(fullname);
        }
        public void setProfileimage(String profileimage) {
            circleImageView = itemView.findViewById(R.id.request_friend_image);
            Picasso.get().load(profileimage).placeholder(R.drawable.profile).into(circleImageView);
        }

    }
}
