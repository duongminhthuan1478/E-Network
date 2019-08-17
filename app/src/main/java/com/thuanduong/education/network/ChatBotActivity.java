package com.thuanduong.education.network;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.thuanduong.education.network.Adapter.ChatBotRecyclerAdapter;
import com.thuanduong.education.network.Adapter.ViewHolder.ChatBotRecyclerViewHolder;
import com.thuanduong.education.network.Model.ChatBotMess;
import com.thuanduong.education.network.Ultil.ShowToast;
import com.thuanduong.education.network.Ultil.dataTransfer.ApiCall;
import com.thuanduong.education.network.Ultil.dataTransfer.ChatBotConfig;
import com.thuanduong.education.network.Ultil.dataTransfer.GetJsonAPI;
import com.thuanduong.education.network.Ultil.dataTransfer.JsonSnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicMarkableReference;


public class ChatBotActivity extends AppCompatActivity implements ChatBotRecyclerAdapter.ColorRecyclerViewAdapterInterface, View.OnClickListener {
    String botName = "nil",botAvt = "nil",userImage = "nil";
    ArrayList<String> defaultQuetions = new ArrayList<>();
    ArrayList<ChatBotMess> chatBotMesses = new ArrayList<>();
    // view
    EditText inputText;
    ImageButton send;
    RecyclerView mRecyclerView;
    ChatBotRecyclerAdapter adapter;
    //auth
    FirebaseAuth firebaseAuth;
    DatabaseReference mUserDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bot);
        firebaseAuth = FirebaseAuth.getInstance();
        getInitData();
    }
    void setupView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView = findViewById(R.id.chat_bot_activity_recyclerview);
        mRecyclerView.setLayoutManager(layoutManager);
        adapter = new ChatBotRecyclerAdapter(chatBotMesses,this);
        mRecyclerView.setAdapter(adapter);
        inputText= findViewById(R.id.chat_bot_activity_input);
        send = findViewById(R.id.chat_bot_activity_send);
        send.setOnClickListener(this);
    }
    void getInitData(){
        GetJsonAPI.setUrl(ChatBotConfig.INFO).get(new ApiCall.AsyncApiCall() {
            @Override
            public void onSuccess(long resTime, JsonSnapshot resultJson) {
                getUserInfo(resultJson);
            }

            @Override
            public void onFail(int responeCode, String mess) {

            }
        });
    }
    void sendQuest(){
        String quest = inputText.getText().toString();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("quest",quest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        chatBotMesses.add(new ChatBotMess(quest));
        adapter.notifyDataSetChanged();
        mRecyclerView.scrollToPosition(chatBotMesses.size() - 1);
        // send
        GetJsonAPI.setUrl(ChatBotConfig.CHAT).post(new ApiCall.AsyncApiCall() {
            @Override
            public void onSuccess(long resTime, JsonSnapshot resultJson) {
                if(resultJson.child("status").toBoolean()){
                    chatBotMesses.add(new ChatBotMess(resultJson));
                    adapter.notifyDataSetChanged();
                    mRecyclerView.scrollToPosition(chatBotMesses.size() - 1);
                    inputText.setText("");
                }
            }

            @Override
            public void onFail(int responeCode, String mess) {

            }
        },new JsonSnapshot(jsonObject));
    }
    @Override
    public void onBindViewHolder(ChatBotRecyclerViewHolder holder, ArrayList<ChatBotMess> chatBotMesses, int position) {
        ChatBotMess chatBotMess = chatBotMesses.get(position);
        Picasso.get()
                .load(chatBotMess.isBot()? botAvt :userImage)
                .into(holder.avt);
        holder.name.setText(chatBotMess.isBot()? botName :firebaseAuth.getCurrentUser().getDisplayName());
        holder.msg.setText(chatBotMess.getMess());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.chat_bot_activity_send:
                sendQuest();
                break;
        }
    }
    void getUserInfo(final JsonSnapshot jsonSnapshot){
        mUserDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserDatabaseRef.child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userImage = dataSnapshot.child("profileimage").getValue().toString();
                if(dataSnapshot.exists()){
                    setupView();
                    if(jsonSnapshot.child("status").toBoolean()){
                        JsonSnapshot resultJson = jsonSnapshot.child("data");
                        botName = resultJson.child("name").toString();
                        botAvt = resultJson.child("avt").toString();
                        chatBotMesses.add(new ChatBotMess(resultJson));
                        adapter.notifyDataSetChanged();
                        for (JsonSnapshot j : resultJson.child("defautQuest").getArrayOfChild()) {
                            defaultQuetions.add(j.toString());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
