package com.thuanduong.education.network.Event;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.thuanduong.education.network.ChatBotActivity;
import com.thuanduong.education.network.R;

public class CreateEventActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView classRegisterBtn,charityBtn,seminarBtn,otherBtn;
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        viewSetup();
    }
    void viewSetup(){
        classRegisterBtn = findViewById(R.id.class_register_event_btn);
        charityBtn = findViewById(R.id.charitable_event_btn);
        seminarBtn = findViewById(R.id.seminar_event_btn);
        otherBtn = findViewById(R.id.other_event_btn);
        classRegisterBtn.setOnClickListener(this);
        charityBtn.setOnClickListener(this);
        seminarBtn.setOnClickListener(this);
        otherBtn.setOnClickListener(this);
        actionBar();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.class_register_event_btn:
                sendUsertoCreateClassRegisterActivity();
                break;
            case R.id.charitable_event_btn:
                sendUsertoCreateCharitableActivity();
                break;
            case R.id.seminar_event_btn:
                sendUsertoCreateSeminarActivity();
                break;
            case R.id.other_event_btn:
                sendUsertoCreateOtherActivity();
                break;
        }
    }
    private void actionBar() {
        mToolbar = (Toolbar) findViewById(R.id.create_event_toolbar);
        setSupportActionBar(mToolbar);
        // Hiển thị dấu mũi tên quay lại
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Tạo Sự Kiện");
    }
    void sendUsertoCreateOtherActivity(){
        Intent intent = new Intent(this, CreateOtherActivity.class);
        startActivity(intent);
        finish();
    }
    void sendUsertoCreateCharitableActivity(){
        Intent intent = new Intent(this, CreateCharitableActivity.class);
        startActivity(intent);
        finish();
    }
    void sendUsertoCreateClassRegisterActivity(){
        Intent intent = new Intent(this, CreateClassRegisterActivity.class);
        startActivity(intent);
        finish();
    }
    void sendUsertoCreateSeminarActivity(){
        Intent intent = new Intent(this, CreateSeminarActivity.class);
        startActivity(intent);
        finish();
    }

}
