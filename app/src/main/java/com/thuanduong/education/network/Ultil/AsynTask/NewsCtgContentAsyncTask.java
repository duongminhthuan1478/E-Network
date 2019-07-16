package com.thuanduong.education.network.Ultil.AsynTask;

import android.os.AsyncTask;
import android.util.Log;

import com.thuanduong.education.network.Constant.Common_Constants;
import com.thuanduong.education.network.Model.News;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class NewsCtgContentAsyncTask extends AsyncTask<Void, Void, ArrayList<News>> {
    DataChangeInterface dataChangeInterface;
    private String LOG_TAG = "NewsCtgContentAsyncTask";

    public NewsCtgContentAsyncTask(DataChangeInterface dataChangeInterface){
        this.dataChangeInterface = dataChangeInterface;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dataChangeInterface.onPrepare();
    }

    @Override
    protected  ArrayList<News> doInBackground(Void... voids) {
        ArrayList<News> lstNews = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(Common_Constants.GET_NEWS_CATEGORY).timeout(10 * 1000).get();
            // 1st priority
            Elements divNews1st = doc.getElementsByClass(Common_Constants.GET_NEWS_CLASS_PRIMARY_1ST);
            for(Element e : divNews1st){
                String link = e.tagName(Common_Constants.TAG_FIGURE).getElementsByTag(Common_Constants.TAG_A).attr(Common_Constants.ATTR_HREF);
                String title = e.tagName(Common_Constants.TAG_FIGURE).getElementsByTag(Common_Constants.TAG_A).attr(Common_Constants.ATTR_TITLE);
                String img =  e.tagName(Common_Constants.TAG_A).getElementsByTag(Common_Constants.TAG_IMG).attr(Common_Constants.ATTR_SRC);
                String smrContents = e.tagName(Common_Constants.TAG_P).text();
                if(smrContents.contains("...")){
                    String regex = "\\...";
                    // limit = 2: cắt tối đa 2 chuỗi
                    String[] parts = smrContents.split(regex, 2);
                    smrContents = parts[1].trim();
                }
                lstNews.add(new News(title, link,smrContents, img));
            }
            // 2nd priority
            Elements divNews2nd = doc.getElementsByClass(Common_Constants.GET_NEWS_CLASS_PRIMARY_2ND);
            for(Element e : divNews2nd){
                String link = e.tagName(Common_Constants.TAG_FIGURE).getElementsByTag(Common_Constants.TAG_A).attr(Common_Constants.ATTR_HREF);
                String title = e.tagName(Common_Constants.TAG_FIGURE).getElementsByTag(Common_Constants.TAG_A).attr(Common_Constants.ATTR_TITLE);
                String img =  e.tagName(Common_Constants.TAG_A).getElementsByTag(Common_Constants.TAG_IMG).attr(Common_Constants.ATTR_SRC);
                String smrContents = e.tagName(Common_Constants.TAG_P).text();
                // Lấy text tile để cắt chuỗi content
                String tmp = e.tagName(Common_Constants.TAG_H4).getElementsByTag(Common_Constants.TAG_A).text();
                smrContents.replace(tmp,"");
                lstNews.add(new News(title, link,smrContents, img));
            }

            // 2nd priority
            Elements divNews3th = doc.getElementsByClass(Common_Constants.GET_NEWS_CLASS_PRIMARY_3TH);
            for(Element e : divNews3th){
                String link = e.tagName(Common_Constants.TAG_FIGURE).getElementsByTag(Common_Constants.TAG_A).attr(Common_Constants.ATTR_HREF);
                String title = e.tagName(Common_Constants.TAG_FIGURE).getElementsByTag(Common_Constants.TAG_A).attr(Common_Constants.ATTR_TITLE);
                String img =  e.tagName(Common_Constants.TAG_A).getElementsByTag(Common_Constants.TAG_IMG).attr(Common_Constants.ATTR_SRC);
                String smrContents = e.tagName(Common_Constants.TAG_P).text();
                // Lấy text tile để cắt chuỗi content, vì smrContent dính chuỗi của title
                String tmp = e.tagName(Common_Constants.TAG_H3).getElementsByTag(Common_Constants.TAG_A).text();
                smrContents.replace(tmp,"");
                lstNews.add(new News(title, link,smrContents, img));
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.v(LOG_TAG, e.getMessage());
        }
        return lstNews;
    }

    @Override
    protected void onPostExecute(ArrayList<News> lstNews) {
        super.onPostExecute(lstNews);
        dataChangeInterface.onDataChange(lstNews);
    }
    public interface DataChangeInterface{
        void onDataChange(ArrayList<News> lstNews);
        void onPrepare();
    }
}





