package com.thuanduong.education.network.Event;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import com.thuanduong.education.network.Adapter.EventMissionAdapter;
import com.thuanduong.education.network.Adapter.ViewHolder.MissionRecyclerViewHolder;
import com.thuanduong.education.network.Adapter.ViewHolder.eventImageRecyclerViewHolder;
import com.thuanduong.education.network.Model.Event;
import com.thuanduong.education.network.Model.OtherEvent;
import com.thuanduong.education.network.Model.SeminarEvent;
import com.thuanduong.education.network.PersonProfileActivity;
import com.thuanduong.education.network.R;
import com.thuanduong.education.network.Ultil.ShowToast;
import com.thuanduong.education.network.Ultil.Time;

import java.util.ArrayList;
import java.util.Calendar;

public class CreateOtherActivity extends AppCompatActivity implements View.OnClickListener, EventImageAdapter.OtherEventRecyclerViewAdapterInterface, EventMissionAdapter.OtherEventRecyclerViewAdapterInterface {
    // data
    DatabaseReference eventRef ;
    //obj
    OtherEvent event;
    //attribute
    String id = "",createUser, name, detail, org, address;
    int limit;
    long startTime, endTime;
    ArrayList<String> imgs = new ArrayList<>();
    ArrayList<EventMission> missions = new ArrayList<>();
    //view
    EditText nameET,detailET,orgET,addressET;
    Button startBtn,endBtn,submitBtn,cancelBtn;
    ImageButton addMissionBtn;
    RecyclerView eventImgRecyclerview;
    RecyclerView eventMissionRecyclerview;
    ProgressDialog progressDialog;
    //auth
    private FirebaseAuth mAuth;
    // adapter
    EventImageAdapter imageAdapter;
    EventMissionAdapter missionAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_other);
        eventRef = FirebaseDatabase.getInstance().getReference(Event.EVENT_REF);
        setViews();
        dataSetup();
        setDefaultTime();
        clickListener();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.create_other_event_submit:
                if(checkInputData()){
                    getData();
                    event.submit();
                    finish();
                }
                else Snackbar.make(getWindow().getDecorView().getRootView(),"đề nghị bạn nhập đầy đủ chính xác",Snackbar.LENGTH_LONG).show();
                break;
            case R.id.create_other_event_cancel:
                finish();
                break;
            case R.id.create_other_event_start:
                setStartdate();
                break;
            case R.id.create_other_event_end:
                setEndDate();
                break;
            case R.id.create_other_event_mission_add_btn:
                addMission();
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
            final StorageReference mountainImagesRef =  FirebaseStorage.getInstance().getReference().child("Events").child("icon/"+Time.getCur()+".jpg");
            mountainImagesRef.putFile(data.getData()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(CreateOtherActivity.this, "upload success",
                            Toast.LENGTH_LONG).show();
                    mountainImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imgs.add(uri.toString());
                            imageAdapter.notifyDataSetChanged();
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
                    OtherEvent event = new OtherEvent(dataSnapshot);
                    id = eventId;
                    nameET.setText(event.getName());
                    detailET.setText(event.getDetail());
                    orgET.setText(event.getOrg());
                    addressET.setText(event.getAddress());
                    imgs.addAll(event.getImgs());
                    imageAdapter.notifyDataSetChanged();
                    missions.addAll(event.getMissions());
                    missionAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
    void setViews(){
        nameET = findViewById(R.id.create_other_event_name);
        detailET = findViewById(R.id.create_other_event_detail);
        orgET = findViewById(R.id.create_other_event_org);
        addressET = findViewById(R.id.create_other_event_address);
        startBtn = findViewById(R.id.create_other_event_start);
        endBtn = findViewById(R.id.create_other_event_end);
        submitBtn = findViewById(R.id.create_other_event_submit);
        cancelBtn = findViewById(R.id.create_other_event_cancel);
        addMissionBtn = findViewById(R.id.create_other_event_mission_add_btn);
        eventImgRecyclerview = findViewById(R.id.create_other_event_img_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        eventImgRecyclerview.setLayoutManager(layoutManager);
        imageAdapter = new EventImageAdapter(imgs,this);
        eventImgRecyclerview.setAdapter(imageAdapter);

        eventMissionRecyclerview = findViewById(R.id.create_other_event_mission_recyclerview);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        layoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        eventMissionRecyclerview.setLayoutManager(layoutManager1);
        missionAdapter = new EventMissionAdapter(missions,this);
        eventMissionRecyclerview.setAdapter(missionAdapter);
    }
    void clickListener(){
        startBtn.setOnClickListener(this);
        endBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        eventImgRecyclerview.setOnClickListener(this);
        addMissionBtn.setOnClickListener(this);
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
        new DatePickerDialog(CreateOtherActivity.this, new DatePickerDialog.OnDateSetListener() {
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
        TimePickerDialog timePickerDialog =new TimePickerDialog(CreateOtherActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int min) {
                if(date+ ( hour * 60000 * 60 + min * 60000 )> Time.getCur())//+(6*86400000))
                {
                    startTime =date+ ( hour * 60000 * 60 + min * 60000 );
                    if(endTime < startTime) {
                        ShowToast.showToast(CreateOtherActivity.this,"this can't end before it's begin");
                    }
                }
                else ShowToast.showToast(CreateOtherActivity.this,"Not selected in the past");
            }
        },calendar.get(calendar.HOUR_OF_DAY),calendar.get(calendar.MINUTE),true);
        timePickerDialog.show();
        timePickerDialog.setCanceledOnTouchOutside(true);
    }
    private void setEndDate()
    {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(calendar.getTimeInMillis()+86400000);
        new DatePickerDialog(CreateOtherActivity.this, new DatePickerDialog.OnDateSetListener() {
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
        TimePickerDialog timePickerDialog =new TimePickerDialog(CreateOtherActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int min) {
                if(date+ ( hour * 60000 * 60 + min * 60000 )> Time.getCur())//+(6*86400000))
                {
                    endTime =date+ ( hour * 60000 * 60 + min * 60000 );
                    if(endTime < startTime) {
                        ShowToast.showToast(CreateOtherActivity.this,"this can't end before it's begin");
                    }
                }
                else ShowToast.showToast(CreateOtherActivity.this,"Not selected in the past");
            }
        },calendar.get(calendar.HOUR_OF_DAY),calendar.get(calendar.MINUTE),true);
        timePickerDialog.show();
        timePickerDialog.setCanceledOnTouchOutside(true);
    }
    boolean checkInputData(){
        boolean check = true;
        check &= nameET.getText().toString().length() > 0
                &&detailET.getText().toString().length() > 0
                &&orgET.getText().toString().length() > 0
                &&addressET.getText().toString().length() > 0;
        if(!check) {
            ShowToast.showToast(CreateOtherActivity.this,"you have entered incomplete information");
            return false;
        }
        check &= startTime < endTime;
        if(!check) {
            ShowToast.showToast(CreateOtherActivity.this,"start time must be less than end time");
            return false;
        }
        check &= imageAdapter.getItemCount() > 1;
        if(!check) {
            ShowToast.showToast(CreateOtherActivity.this,"missing avatar for event");
            return false;
        }
        check &= missions.size() > 0;
        if(!check) {
            ShowToast.showToast(CreateOtherActivity.this,"missing avatar for event");
            return false;
        }
        return check;
    }
    void getData(){
        mAuth = FirebaseAuth.getInstance();
        createUser = mAuth.getCurrentUser().getUid();
        detail = detailET.getText().toString();
        name = nameET.getText().toString();
        org = orgET.getText().toString();
        address = addressET.getText().toString();
        limit = 0;
        event = new OtherEvent(id,createUser, imgs, startTime, endTime, limit, missions, name, detail, org, address);
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

    @Override
    public void onBindViewHolder1(MissionRecyclerViewHolder holder, ArrayList<EventMission> missions, int position) {
        EventMission mission = missions.get(position);
        holder.nameTv.setText(mission.name);
        holder.amountTv.setText(String.valueOf(mission.amount));
    }
    EditText et_name,et_amount;
    void addMission(){
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateOtherActivity.this);
        builder.setTitle(" New mission : " );
        // tạo các comportment
        TextView tv_name= new TextView(CreateOtherActivity.this);
        tv_name.setText("Mission Name");
        et_name= new EditText(CreateOtherActivity.this);
        TextView tv_amount= new TextView(CreateOtherActivity.this);
        tv_amount.setText("Mission limit");
        et_amount = new EditText(CreateOtherActivity.this);
        et_amount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(tv_name);
        linearLayout.addView(et_name);
        linearLayout.addView(tv_amount);
        linearLayout.addView(et_amount);
        builder.setView(linearLayout);
        //
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String name = et_name.getText().toString();
                int amount = Integer.parseInt(et_amount.getText().toString());
                EventMission eventMission = new EventMission(name,amount);
                missions.add(eventMission);
                missionAdapter.notifyDataSetChanged();
            }
        });
        builder.show();
    }

}
