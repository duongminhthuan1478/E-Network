package com.thuanduong.education.network.Event;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    String id = "",createUser, name, classId,content;
    int min,limit;
    long startTime, endTime;
    ArrayList<String> imgs = new ArrayList<>();
    //view
    EditText startEdt, endEdt;
    EditText nameET,subjectET,contentET,minET,limitET;
    Button startBtn,endBtn,submitBtn,cancelBtn;
    //auth
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_class_register);
        eventRef = FirebaseDatabase.getInstance().getReference(Event.EVENT_REF);
        setViews();
        dataSetup();
        clickListener();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.create_class_register_event_submit:
                if(checkInputData()){
                    getData();
                    event.submit();
                    ShowToast.showToast(getApplicationContext(), "Tạo sự kiện thành công");
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
    void dataSetup(){
        if(getIntent().hasExtra("eventId")){
            final String eventId = getIntent().getStringExtra("eventId");
            eventRef.child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    RegisterClassEvent event = new RegisterClassEvent(dataSnapshot);
                    id = eventId;
                    nameET.setText(event.getName());
                    subjectET.setText(event.getClassId());
                    contentET.setText(event.getContent());
                    minET.setText(event.getMin()+"");
                    limitET.setText(event.getLimit()+"");
                    startEdt.setText(Time.LongtoTime(event.getStartTime()));
                    endEdt.setText(Time.LongtoTime(event.getEndTime()));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
    void setViews(){
        nameET = findViewById(R.id.create_class_register_event_name);
        subjectET = findViewById(R.id.create_class_register_event_subject_id);
        contentET = findViewById(R.id.create_class_register_event_content);
        minET = findViewById(R.id.create_class_register_event_min);
        limitET = findViewById(R.id.create_class_register_event_limit);
        startBtn = findViewById(R.id.create_class_register_event_start);
        startEdt = findViewById(R.id.create_class_register_event_start_edt);
        endBtn = findViewById(R.id.create_class_register_event_end);
        endEdt = findViewById(R.id.create_class_register_event_end_edt);
        submitBtn = findViewById(R.id.create_class_register_event_submit);
        cancelBtn = findViewById(R.id.create_class_register_event_cancel);
        actionBar();
    }
    void clickListener(){
        startBtn.setOnClickListener(this);
        endBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
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
                if(date+ ( hour * 60000 * 60 + min * 60000 ) > Time.getCur())//+(6*86400000))
                {
                    startTime =date+ ( hour * 60000 * 60 + min * 60000 );
                    startEdt.setText(Time.timeToString(startTime));
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
        calendar.setTimeInMillis(calendar.getTimeInMillis());
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
                    endEdt.setText(Time.timeToString(endTime));
                }
                else ShowToast.showToast(CreateClassRegisterActivity.this,"Not selected in the past");
            }
        },calendar.get(calendar.HOUR_OF_DAY),calendar.get(calendar.MINUTE),true);
        timePickerDialog.show();
        timePickerDialog.setCanceledOnTouchOutside(true);
    }
    boolean checkInputData(){
        if(nameET.getText().toString().length() < 10)
        {
            ShowToast.showToast(CreateClassRegisterActivity.this,"tên môn học không được nhỏ hơn 10");
            return false;
        }
        if(subjectET.getText().toString().length() <= 0)
        {
            ShowToast.showToast(CreateClassRegisterActivity.this,"mã môn không được để trống");
            return false;
        }
        if(contentET.getText().toString().length() < 10)
        {
            ShowToast.showToast(CreateClassRegisterActivity.this,"nội dung không được nhỏ hơn 10 ký tự");
            return false;
        }
        if(minET.getText().toString().length() <= 0)
        {
            ShowToast.showToast(CreateClassRegisterActivity.this,"só người tối thiểu không dể trống");
            return false;
        }
        if(limitET.getText().toString().length() == 0 || Integer.parseInt(limitET.getText().toString()) < Integer.parseInt(minET.getText().toString()))
        {
            ShowToast.showToast(CreateClassRegisterActivity.this,"số lượng tối đa không được bé hơn tối thiểu");
            return false;
        }
        if(startTime > endTime) {
            ShowToast.showToast(this,"thời gian bắt đầu phải bé hơn thời gian kết thúc");
            return false;
        }
        return true;
    }
    void getData(){
        mAuth = FirebaseAuth.getInstance();
        createUser = mAuth.getCurrentUser().getUid();
        classId = subjectET.getText().toString();
        name = nameET.getText().toString();
        content = contentET.getText().toString();
        min = Integer.parseInt(minET.getText().toString());
        limit = Integer.parseInt(limitET.getText().toString());
        event = new RegisterClassEvent(id,createUser, imgs, startTime, endTime, limit, name, classId, content, min) ;
    }

    private void actionBar() {
        mToolbar = (Toolbar) findViewById(R.id.create_class_register_activity_toolbar);
        setSupportActionBar(mToolbar);
        // Hiển thị dấu mũi tên quay lại
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Tạo Sự Kiện");
    }

}
