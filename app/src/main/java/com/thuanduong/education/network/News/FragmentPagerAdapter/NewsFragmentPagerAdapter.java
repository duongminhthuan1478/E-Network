package com.thuanduong.education.network.News.FragmentPagerAdapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.thuanduong.education.network.News.Fragments.ActivitiesCtgFragment;
import com.thuanduong.education.network.News.Fragments.EnrollmentInfoCtgFragment;
import com.thuanduong.education.network.News.Fragments.NewsCtgFragment;
import com.thuanduong.education.network.R;

public class NewsFragmentPagerAdapter extends FragmentPagerAdapter {

    /** Context of the app */
    private Context mContext;

    public NewsFragmentPagerAdapter(Context context, FragmentManager fm){
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return  new EnrollmentInfoCtgFragment();
            case 1:
                return new ActivitiesCtgFragment();
            case 2:
                return new NewsCtgFragment();
             default:
                 return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0: return mContext.getString(R.string.title_fragment_enroll_infor);
            case 1: return mContext.getString(R.string.title_fragment_activities);
            case 2: return mContext.getString(R.string.title_fragment_news);
            default: return null;
        }
    }
}
