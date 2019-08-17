package com.thuanduong.education.network.Event.PagerAdapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.thuanduong.education.network.Event.fragment.EventListFragment;
import com.thuanduong.education.network.Event.fragment.JoinedEventsFragment;
import com.thuanduong.education.network.Event.fragment.MyEventListFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {

    public PagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }
    @Override
    public Fragment getItem(int position) {
        Fragment frag=null;
        switch (position){
            case 0:
                frag = new EventListFragment();
                break;
            case 1:
                frag = new MyEventListFragment();
                break;
            case 2:
                frag = new JoinedEventsFragment();
                break;
        }
        return frag;
    }

    @Override
    public int getCount() {
        return 3;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position){
            case 0:
                title = "Tất cả";
                break;
            case 1:
                title = "Của tôi";
                break;
            case 2:
                title = "Tham gia";
                break;
        }
        return title;
    }
}