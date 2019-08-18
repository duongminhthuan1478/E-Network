package com.thuanduong.education.network.Event;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.thuanduong.education.network.Event.PagerAdapter.PagerAdapter;
import com.thuanduong.education.network.R;

public class EventsListActivity extends AppCompatActivity implements View.OnClickListener {
    private ViewPager pager;
    private TabLayout tabLayout;
    FloatingActionButton fab;
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_list);
        addControl();
        viewSetup();
    }
    void viewSetup(){
        fab = findViewById(R.id.event_list_fab);
        fab.setOnClickListener(this);
        actionBar();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.event_list_fab:
                sendUserToCreateEventActivity();
                break;
        }
    }
    private void sendUserToCreateEventActivity(){
        Intent Intent = new Intent(this, CreateEventActivity.class);
        startActivity(Intent);
    }
    private void addControl() {
        pager = (ViewPager) findViewById(R.id.view_pager);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        FragmentManager manager = getSupportFragmentManager();
        PagerAdapter adapter = new PagerAdapter(manager);
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setTabsFromPagerAdapter(adapter);//deprecated
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(pager));
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void actionBar() {
        mToolbar = (Toolbar) findViewById(R.id.events_list_activity_toolbar);
        setSupportActionBar(mToolbar);
        // Hiển thị dấu mũi tên quay lại
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Sự Kiện");
    }
}
