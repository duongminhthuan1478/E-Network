package com.thuanduong.education.network.Event;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.thuanduong.education.network.Adapter.EventImageAdapter;
import com.thuanduong.education.network.Adapter.ViewHolder.eventImageRecyclerViewHolder;
import com.thuanduong.education.network.Model.Event;
import com.thuanduong.education.network.Model.RegisterClassEvent;
import com.thuanduong.education.network.Model.SeminarEvent;
import com.thuanduong.education.network.R;
import com.thuanduong.education.network.Ultil.ShowToast;
import com.thuanduong.education.network.Ultil.Time;

import java.util.ArrayList;
import java.util.Calendar;

public class CreateSeminarActivity extends AppCompatActivity implements View.OnClickListener, EventImageAdapter.OtherEventRecyclerViewAdapterInterface {
    // data
    DatabaseReference eventRef ;
    //obj
    SeminarEvent event;
    //attribute
    String id = "",createUser, name, org, speakers,  recmdAudien, content, address;
    int limit = 0;
    long startTime, endTime;
    ArrayList<String> imgs = new ArrayList<>();
    //view
    TextView startTv,endTv;
    EditText nameET,orgET,speakerET,recmdAudienceET,contentET,addressET;
    Button startBtn,endBtn,submitBtn,cancelBtn;
    RecyclerView eventImgRecyclerview;
    ProgressDialog progressDialog;
    //auth
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    // adapter
    EventImageAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_seminar);
        eventRef = FirebaseDatabase.getInstance().getReference(Event.EVENT_REF);
        setViews();
        dataSetup();
        clickListener();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.create_seminar_event_submit:
                if(checkInputData()){
                    getData();
                    event.submit();
                    finish();
                }
                else Snackbar.make(getWindow().getDecorView().getRootView(),"đề nghị bạn nhập đầy đủ chính xác",Snackbar.LENGTH_LONG).show();
                break;
            case R.id.create_seminar_event_cancel:
                finish();
                break;
            case R.id.create_seminar_event_start:
                setStartdate();
                break;
            case R.id.create_seminar_event_end:
                setEndDate();
                break;
        }
    }
    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, 0);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        progressDialog = new ProgressDialog(this);
        progressDialog.show();
        if(data!=null)
        {
            final StorageReference mountainImagesRef =  FirebaseStorage.getInstance().getReference().child("Events").child("icon/"+ Time.getCur()+".jpg");
            mountainImagesRef.putFile(data.getData()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(CreateSeminarActivity.this, "upload success",
                            Toast.LENGTH_LONG).show();
                    mountainImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imgs.add(uri.toString());
                            adapter.notifyDataSetChanged();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure( Exception exception) {
                            finish();
                        }
                    });
                }
            });
        }
    }
    void dataSetup(){
        if(getIntent().hasExtra("eventId")){
            final String eventId = getIntent().getStringExtra("eventId");
            eventRef.child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    SeminarEvent event = new SeminarEvent(dataSnapshot);
                    id = eventId;
                    nameET.setText(event.getName());
                    orgET.setText(event.getOrg());
                    speakerET.setText(event.getSpeakers());
                    recmdAudienceET.setText(event.getRecmdAudien());
                    contentET.setText(event.getContent());
                    addressET.setText(event.getAddress());
                    imgs.addAll(event.getImgs());
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
    void setViews(){
        actionBar();
        nameET = findViewById(R.id.create_seminar_event_name);
        orgET = findViewById(R.id.create_seminar_event_org);
        speakerET = findViewById(R.id.create_seminar_event_speakes);
        recmdAudienceET = findViewById(R.id.create_seminar_event_recmd_audience);
        contentET = findViewById(R.id.create_seminar_event_content);
        addressET = findViewById(R.id.create_seminar_event_address);
        startBtn = findViewById(R.id.create_seminar_event_start);
        startTv = findViewById(R.id.create_seminar_event_start_tv);
        endBtn = findViewById(R.id.create_seminar_event_end);
        endTv = findViewById(R.id.create_seminar_event_end_tv);
        submitBtn = findViewById(R.id.create_seminar_event_submit);
        cancelBtn = findViewById(R.id.create_seminar_event_cancel);
        eventImgRecyclerview = findViewById(R.id.create_seminar_event_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        eventImgRecyclerview.setLayoutManager(layoutManager);
        adapter = new EventImageAdapter(imgs,this);
        eventImgRecyclerview.setAdapter(adapter);
    }
    void clickListener(){
        startBtn.setOnClickListener(this);
        endBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        eventImgRecyclerview.setOnClickListener(this);
    }


    private void setStartdate()
    {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(calendar.getTimeInMillis());
        new DatePickerDialog(CreateSeminarActivity.this, new DatePickerDialog.OnDateSetListener() {
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
        TimePickerDialog timePickerDialog =new TimePickerDialog(CreateSeminarActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int min) {
                if(date+ ( hour * 60000 * 60 + min * 60000 )> Time.getCur())//+(6*86400000))
                {
                    startTime =date+ ( hour * 60000 * 60 + min * 60000 );
                    startTv.setText(Time.timeToString(startTime));
                }
                else ShowToast.showToast(CreateSeminarActivity.this,"Not selected in the past");
            }
        },calendar.get(calendar.HOUR_OF_DAY),calendar.get(calendar.MINUTE),true);
        timePickerDialog.show();
        timePickerDialog.setCanceledOnTouchOutside(true);
    }
    private void setEndDate()
    {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(calendar.getTimeInMillis());
        new DatePickerDialog(CreateSeminarActivity.this, new DatePickerDialog.OnDateSetListener() {
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
        TimePickerDialog timePickerDialog =new TimePickerDialog(CreateSeminarActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int min) {
                if(date+ ( hour * 60000 * 60 + min * 60000 )> Time.getCur())//+(6*86400000))
                {
                    endTime =date+ ( hour * 60000 * 60 + min * 60000 );
                    endTv.setText(Time.timeToString(endTime));
                }
                else ShowToast.showToast(CreateSeminarActivity.this,"Not selected in the past");
            }
        },calendar.get(calendar.HOUR_OF_DAY),calendar.get(calendar.MINUTE),true);
        timePickerDialog.show();
        timePickerDialog.setCanceledOnTouchOutside(true);
    }
    boolean checkInputData(){
        boolean check = true;
        check &= nameET.getText().toString().length() > 0
                &&orgET.getText().toString().length() > 0
                &&speakerET.getText().toString().length() > 0
                &&recmdAudienceET.getText().toString().length() > 0
                &&contentET.getText().toString().length() > 0
                &&addressET.getText().toString().length() > 0;
        if(!check) {
            ShowToast.showToast(CreateSeminarActivity.this,"you have entered incomplete information");
            return false;
        }
        check &= startTime < endTime;
        if(!check) {
            ShowToast.showToast(CreateSeminarActivity.this,"start time must be less than end time");
            return false;
        }
        check &= adapter.getItemCount() > 1;
        if(!check) {
            ShowToast.showToast(CreateSeminarActivity.this,"missing avatar for event");
            return false;
        }
        return check;
    }
    void getData(){
        mAuth = FirebaseAuth.getInstance();
        createUser = mAuth.getCurrentUser().getUid();
        name = nameET.getText().toString();
        speakers = speakerET.getText().toString();
        recmdAudien = recmdAudienceET.getText().toString();
        org = orgET.getText().toString();
        content = contentET.getText().toString();
        address = addressET.getText().toString();
        limit = 0;
        event = new SeminarEvent(id,createUser, imgs, startTime, endTime, limit, name, org, speakers, recmdAudien, content, address);
    }

    @Override
    public void onBindViewHolder(eventImageRecyclerViewHolder holder, ArrayList<String> imgs, int position) {
        if(position < imgs.size())
            Picasso.get()
                    .load(imgs.get(position))
                    .placeholder(R.drawable.profile)
                    .resize(100,100)
                    .centerCrop()
                    .into(holder.img);
        else {
            Picasso.get()
                    .load(R.drawable.add_post)
                    .placeholder(R.drawable.add_post)
                    .resize(100,100)
                    .centerCrop()
                    .into(holder.img);
            holder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openGallery();
                }
            });
        }
    }
    private void actionBar() {
        mToolbar = (Toolbar) findViewById(R.id.create_seminar_activity_toolbar);
        setSupportActionBar(mToolbar);
        // Hiển thị dấu mũi tên quay lại
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Tạo Sự Kiện");
    }
}
