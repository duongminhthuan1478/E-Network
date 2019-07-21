package com.thuanduong.education.network.News.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
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
import com.thuanduong.education.network.Ultil.AsynTask.EnrollmentCtgContentAsyncTask;
import com.thuanduong.education.network.Ultil.Interface.IListItemClickListener;
import com.thuanduong.education.network.Ultil.ShowToast;

import java.util.ArrayList;

/**
 * @Author: Duong Minh Thuan
 * Get tu dong All các bài viết của mục tin tức tuyển sinh từ nguồn website:
 * https://duytan.edu.vn/tuyen-sinh/Page/ArticleView.aspx
 */
public class EnrollmentInfoCtgFragment extends Fragment implements EnrollmentCtgContentAsyncTask.DataChangeInterface{
    private ProgressDialog mProgressDialogLoadingBar;
    private RecyclerView mEnrollRecyclerview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_enrollment_info_ctg, container, false);
        findIDAndInitRecyclerview(rootView);

        new EnrollmentCtgContentAsyncTask(this).execute();
        return rootView;
    }

    private void findIDAndInitRecyclerview(View rootView) {
        mProgressDialogLoadingBar = new ProgressDialog(getContext());
        mEnrollRecyclerview = rootView.findViewById(R.id.enrollment_info_recyclerview);
        mEnrollRecyclerview.setHasFixedSize(true);
        mEnrollRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        mEnrollRecyclerview.addItemDecoration(dividerItemDecoration);
    }
    @Override
    public void onDataChange(final ArrayList<News> lstNews) {
        if(lstNews != null) {
            NewsFragmentAdapter adapter = new NewsFragmentAdapter(getContext(), lstNews);
            mEnrollRecyclerview.setAdapter(adapter);
            adapter.setOnItemClickListener(new IListItemClickListener() {
                @Override
                public void onListItemClick(int clickedItemIndex) {
                    String detailLink = lstNews.get(clickedItemIndex).getLink();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(detailLink));
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
