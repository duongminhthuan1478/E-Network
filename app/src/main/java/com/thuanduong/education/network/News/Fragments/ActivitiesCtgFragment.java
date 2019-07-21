package com.thuanduong.education.network.News.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thuanduong.education.network.Model.News;
import com.thuanduong.education.network.News.Adapter.NewsFragmentAdapter;
import com.thuanduong.education.network.News.NewsDetailActivity;
import com.thuanduong.education.network.R;
import com.thuanduong.education.network.Ultil.AsynTask.ActivitiesCtgContentAsyncTask;
import com.thuanduong.education.network.Ultil.Interface.IListItemClickListener;

import java.util.ArrayList;

/**
 * @Author: Duong Minh Thuan
 * Get tu dong All các bài viết của mục hoạt động từ nguồn website:
 * https://duytan.edu.vn/hoat-dong
 */
public class ActivitiesCtgFragment extends Fragment implements ActivitiesCtgContentAsyncTask.DataChangeInterface {

    private ProgressDialog mProgressDialogLoadingBar;
    private RecyclerView ActivitiesLstRecyclerview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_activities_ctg, container, false);
        findIDAndInitRecyclerview(rootView);
        new ActivitiesCtgContentAsyncTask(this).execute();
        return rootView;
    }

    private void findIDAndInitRecyclerview(View rootView) {
        mProgressDialogLoadingBar = new ProgressDialog(getContext());
        ActivitiesLstRecyclerview = rootView.findViewById(R.id.activities_recyclerview);
        ActivitiesLstRecyclerview.setHasFixedSize(true);
        ActivitiesLstRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        ActivitiesLstRecyclerview.addItemDecoration(dividerItemDecoration);
    }

    @Override
    public void onDataChange(final ArrayList<News> lstNews) {
        if(lstNews != null) {
            NewsFragmentAdapter adapter = new NewsFragmentAdapter(getContext(), lstNews);
            ActivitiesLstRecyclerview.setAdapter(adapter);
            adapter.setOnItemClickListener(new IListItemClickListener() {
                @Override
                public void onListItemClick(int clickedItemIndex) {
                    Intent intent = new Intent(getContext(), NewsDetailActivity.class);
                    intent.putExtra("newsLink", lstNews.get(clickedItemIndex).getLink());
                    startActivity(intent);
                }
            });

        }
        mProgressDialogLoadingBar.dismiss();
    }

    @Override
    public void onPrepare() {
        mProgressDialogLoadingBar.setTitle("Loading..");
        mProgressDialogLoadingBar.setMessage("Vui lòng chờ trong giây lát !!");
        mProgressDialogLoadingBar.show();
        mProgressDialogLoadingBar.setCanceledOnTouchOutside(true);
    }


}
