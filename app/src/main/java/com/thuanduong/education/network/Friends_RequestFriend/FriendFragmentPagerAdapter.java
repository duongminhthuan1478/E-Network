package com.thuanduong.education.network.Friends_RequestFriend;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.thuanduong.education.network.R;

public class FriendFragmentPagerAdapter extends FragmentPagerAdapter {

    /** Context of the app */
    private Context mContext;

    public FriendFragmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new RequestFriendFragment();
            case 1: return new ListFriendFragment();
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0: return mContext.getString(R.string.title_fragment_request_friend);
            case 1: return mContext.getString(R.string.title_fragment_listfriend);
            default: return null;
        }
    }
}
