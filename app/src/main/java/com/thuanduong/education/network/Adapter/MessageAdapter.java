package com.thuanduong.education.network.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.thuanduong.education.network.Model.Message;
import com.thuanduong.education.network.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> mMessageList;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabaseRef;

    public MessageAdapter(List<Message> list) {
        mMessageList = list;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater
                .from(parent.getContext()).inflate(R.layout.message_item, parent, false);

        mAuth = FirebaseAuth.getInstance();

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        private TextView senderMessageText, receiverMessageText;
        private CircleImageView receiverProfileImage;

        public MessageViewHolder(View itemView) {
            super(itemView);
            senderMessageText = (TextView) itemView.findViewById(R.id.sender_message_text);
            receiverMessageText = (TextView) itemView.findViewById(R.id.receiver_message_text);
            receiverProfileImage = (CircleImageView) itemView.findViewById(R.id.message_profile_image);
        }
        public void setData(int position){
            Message message = mMessageList.get(position);
            String  senderMessageId = mAuth.getCurrentUser().getUid();

            //Sender
            String fromUserID = message.getFrom();
            String fromMessageType = message.getType();

            mUserDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserID);
            mUserDatabaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        String image = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.get().load(image).into(receiverProfileImage);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            if(fromMessageType.equals("text")){
                /** Ẩn image từ đầu , nếu như tin nhắn từ người gửi(fromUserID) khác với currentUserID(sender)
                 * thì mới cho hiển thị ảnh */
                receiverProfileImage.setVisibility(View.INVISIBLE);
                receiverMessageText.setVisibility(View.INVISIBLE);

                // from = current
                if(fromUserID.equals(senderMessageId)){
                    senderMessageText.setText(message.getMessage());
                    //receiverProfileImage.setVisibility(View.VISIBLE);
                }
                else {
                    senderMessageText.setVisibility(View.INVISIBLE);
                    receiverMessageText.setVisibility(View.VISIBLE);
                    receiverProfileImage.setVisibility(View.VISIBLE);
                    receiverMessageText.setText(message.getMessage());
                }
            }

        }
    }
}
