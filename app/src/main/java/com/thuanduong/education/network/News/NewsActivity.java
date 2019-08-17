package com.thuanduong.education.network.News;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.thuanduong.education.network.ChatBotActivity;
import com.thuanduong.education.network.Model.ChatBotMess;
import com.thuanduong.education.network.News.FragmentPagerAdapter.NewsFragmentPagerAdapter;
import com.thuanduong.education.network.R;

public class NewsActivity extends AppCompatActivity implements View.OnClickListener {
    FloatingActionButton fa_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        displayTabLayout();
        viewSetup();
    }
    void viewSetup(){
        fa_btn = findViewById(R.id.fa_btn);
        fa_btn.setOnClickListener(this);
    }
    private void displayTabLayout() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.news_viewpager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.news_tablayout);

        viewPager.setAdapter(new NewsFragmentPagerAdapter(this,getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fa_btn:
                sendUserToChatBot();
                break;
        }
    }

    void sendUserToChatBot(){
        Intent intent = new Intent(NewsActivity.this, ChatBotActivity.class);
        startActivity(intent);
    }
}
