package com.thuanduong.education.network.Friends_RequestFriend;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import com.thuanduong.education.network.R;

/**
 * FriendsActiviy chua ListFriendFragment and RequestFriendFragment
 */
public class FriendsActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        actionBar();

        displayTabLayout();
    }

    private void displayTabLayout() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.friends_view_pager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.friends_tab_layout);

        viewPager.setAdapter(new FriendFragmentPagerAdapter(this,getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
    }
    private void actionBar() {
        mToolbar = (Toolbar) findViewById(R.id.friend_toolbar);
        setSupportActionBar(mToolbar);
        // Hiển thị dấu mũi tên quay lại
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.title_toolbar_friends);

    }
}
