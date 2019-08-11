package com.thuanduong.education.network.Event;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.thuanduong.education.network.Adapter.EventImageAdapter;
import com.thuanduong.education.network.Adapter.ViewHolder.eventImageRecyclerViewHolder;
import com.thuanduong.education.network.Model.Event;
import com.thuanduong.education.network.Model.RegisterClassEvent;
import com.thuanduong.education.network.R;
import com.thuanduong.education.network.Ultil.ShowToast;
import com.thuanduong.education.network.Ultil.Time;

import java.util.ArrayList;
import java.util.Calendar;

public class CreateClassRegisterActivity extends AppCompatActivity  implements View.OnClickListener {
    // data
    DatabaseReference eventRef ;
    //obj
    RegisterClassEvent event;
    //attribute
    String createUser, name, classId,content;
    int min,limit;
    long startTime, endTime;
    ArrayList<String> imgs = new ArrayList<>();
    //view
    EditText nameET,subjectET,contentET,minET,limitET;
    Button startBtn,endBtn,submitBtn,cancelBtn;
    //auth
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_class_register);
        eventRef = FirebaseDatabase.getInstance().getReference(Event.EVENT_REF);
        setViews();
        setDefaultTime();
        clickListener();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.create_class_register_event_submit:
                if(checkInputData()){
                    getData();
                    event.submit();
                    finish();
                }
                else Snackbar.make(getWindow().getDecorView().getRootView(),"đề nghị bạn nhập đầy đủ chính xác",Snackbar.LENGTH_LONG).show();
                break;
            case R.id.create_class_register_event_cancel:
                finish();
                break;
            case R.id.create_class_register_event_start:
                setStartdate();
                break;
            case R.id.create_class_register_event_end:
                setEndDate();
                break;
        }
    }

    void setViews(){
        nameET = findViewById(R.id.create_class_register_event_name);
        subjectET = findViewById(R.id.create_class_register_event_subject_id);
        contentET = findViewById(R.id.create_class_register_event_content);
        minET = findViewById(R.id.create_class_register_event_min);
        limitET = findViewById(R.id.create_class_register_event_limit);
        startBtn = findViewById(R.id.create_class_register_event_start);
        endBtn = findViewById(R.id.create_class_register_event_end);
        submitBtn = findViewById(R.id.create_class_register_event_submit);
        cancelBtn = findViewById(R.id.create_class_register_event_cancel);
    }
    void clickListener(){
        startBtn.setOnClickListener(this);
        endBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }

    void setDefaultTime(){
        long tomorrow = Time.getCur()+86400000l;
        startTime = tomorrow;
        endTime = tomorrow + 30000;
    }

    private void setStartdate()
    {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(calendar.getTimeInMillis()+86400000);
        new DatePickerDialog(CreateClassRegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year,month,dayOfMonth,0,0);
                setStartTime(calendar.getTimeInMillis());
            }
        },calendar.get(calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE)).show();
    }
    private void setStartTime(final long date)
    {
        final Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog =new TimePickerDialog(CreateClassRegisterActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int min) {
                if(date+ ( hour * 60000 * 60 + min * 60000 )> Time.getCur())//+(6*86400000))
                {
                    startTime =date+ ( hour * 60000 * 60 + min * 60000 );
                    if(endTime < startTime) {
                        ShowToast.showToast(CreateClassRegisterActivity.this,"this can't end before it's begin");
                    }
                }
                else ShowToast.showToast(CreateClassRegisterActivity.this,"Not selected in the past");
            }
        },calendar.get(calendar.HOUR_OF_DAY),calendar.get(calendar.MINUTE),true);
        timePickerDialog.show();
        timePickerDialog.setCanceledOnTouchOutside(true);
    }
    private void setEndDate()
    {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(calendar.getTimeInMillis()+86400000);
        new DatePickerDialog(CreateClassRegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year,month,dayOfMonth,0,0);
                setEndTime(calendar.getTimeInMillis());
            }
        },calendar.get(calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE)).show();
    }
    private void setEndTime(final long date)
    {
        final Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog =new TimePickerDialog(CreateClassRegisterActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int min) {
                if(date+ ( hour * 60000 * 60 + min * 60000 )> Time.getCur())//+(6*86400000))
                {
                    endTime =date+ ( hour * 60000 * 60 + min * 60000 );
                    if(endTime < startTime) {
                        ShowToast.showToast(CreateClassRegisterActivity.this,"this can't end before it's begin");
                    }
                }
                else ShowToast.showToast(CreateClassRegisterActivity.this,"Not selected in the past");
            }
        },calendar.get(calendar.HOUR_OF_DAY),calendar.get(calendar.MINUTE),true);
        timePickerDialog.show();
        timePickerDialog.setCanceledOnTouchOutside(true);
    }
    boolean checkInputData(){
        boolean check = true;
        check &= nameET.getText().toString().length() > 0
                &&contentET.getText().toString().length() > 0
                &&minET.getText().toString().length() > 0
                &&limitET.getText().toString().length() > 0;
        if(!check) {
            ShowToast.showToast(CreateClassRegisterActivity.this,"you have entered incomplete information");
            return false;
        }
        check &= startTime < endTime;
        if(!check) {
            ShowToast.showToast(CreateClassRegisterActivity.this,"start time must be less than end time");
            return false;
        }
        return check;
    }
    void getData(){
        mAuth = FirebaseAuth.getInstance();
        createUser = mAuth.getCurrentUser().getUid();
        classId = subjectET.getText().toString();
        name = nameET.getText().toString();
        content = contentET.getText().toString();
        min = Integer.parseInt(minET.getText().toString());
        limit = Integer.parseInt(limitET.getText().toString());
        event = new RegisterClassEvent(createUser, imgs, startTime, endTime, limit, name, classId, content, min) ;
    }

}
