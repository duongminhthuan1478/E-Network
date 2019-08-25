package com.thuanduong.education.network.News.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.thuanduong.education.network.Constant.Common_Constants;
import com.thuanduong.education.network.Model.News;
import com.thuanduong.education.network.R;
import com.thuanduong.education.network.Ultil.Interface.IListItemClickListener;

import java.util.ArrayList;

public class NewsFragmentAdapter extends RecyclerView.Adapter<NewsFragmentAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<News> mLstNews;
    private IListItemClickListener mOnClickListener;

    public NewsFragmentAdapter(Context context, ArrayList<News> news) {
        mContext = context;
        mLstNews = news;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.all_news_item, viewGroup, false);;
        return new ViewHolder(itemView, mOnClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.getData(position);
    }

    @Override
    public int getItemCount() {
        return mLstNews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView imgView;
        TextView titleTxt, sumContentTxt;

        public ViewHolder(View itemView, final IListItemClickListener listener) {
            super(itemView);
            imgView = itemView.findViewById(R.id.news_img);
            titleTxt = itemView.findViewById(R.id.news_title);
            sumContentTxt = itemView.findViewById(R.id.news_sum_content);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                           listener.onListItemClick(position);
                        }
                    }
                }
            });

        }

        public void getData(int position){
            String image = mLstNews.get(position).getImg();
            // only BASE URL no append suffix link to img
            if(image.equals(Common_Constants.BASE_URL)){
                imgView.setVisibility(View.GONE);
            } else {
                Picasso.get().load(image).placeholder(R.drawable.app_icon).error(R.drawable.app_icon).into(imgView);
            }
            titleTxt.setText(mLstNews.get(position).getTitle());
            sumContentTxt.setText(mLstNews.get(position).getSmrContents());
        }

    }
    public void setOnItemClickListener(IListItemClickListener listener) {
        mOnClickListener = listener;
    }
}
