package com.thuanduong.education.network.News;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.thuanduong.education.network.News.FragmentPagerAdapter.NewsFragmentPagerAdapter;
import com.thuanduong.education.network.R;

public class NewsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        displayTabLayout();
    }

    private void displayTabLayout() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.news_viewpager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.news_tablayout);

        viewPager.setAdapter(new NewsFragmentPagerAdapter(this,getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
    }
}
