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

public class EnrollmentCtgContentAsyncTask extends AsyncTask<Void, Void, ArrayList<News>> {

    EnrollmentCtgContentAsyncTask.DataChangeInterface dataChangeInterface;
    private String LOG_TAG = "EnrollmentCtgContentAsyncTask";

    public EnrollmentCtgContentAsyncTask(EnrollmentCtgContentAsyncTask.DataChangeInterface dataChangeInterface){
        this.dataChangeInterface = dataChangeInterface;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dataChangeInterface.onPrepare();
    }
    @Override
    protected ArrayList<News> doInBackground(Void... voids) {
        ArrayList<News> lstNews = new ArrayList<>();
        ArrayList<String> lstPagination = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(Common_Constants.GET_ENROLL_INFO_CATEGORY).timeout(10 * 1000).get();
            // Get default page (no pagination)
            Elements news = doc.getElementsByClass("eachnews ");
            for(Element e: news) {
                String linkSuffix = e.select("p.title a").attr("href");
                String link = Common_Constants.BASE_URL + linkSuffix;
                String title = e.select("p.title a").attr("title");
                String imgSuffix = e.tagName("span").select("img").attr("src");
                String img = Common_Constants.BASE_URL + imgSuffix;
                String smrContents = e.select("span.text_wr").text();
                lstNews.add(new News(title, link ,smrContents, img));
            }

            int paginationSize = doc.select("ul[class=pagination]").select("li").size();
            for(int page = 0; page < paginationSize; page++){
                Elements pageLinkTage = doc.select("ul.pagination li a").eq(page);
                String pageLink = pageLinkTage.attr("href");
                lstPagination.add(pageLink);
            }
            //remove empty tag
            for(int p = 0; p <lstPagination.size(); p++){
                if(lstPagination.get(p).isEmpty() || lstPagination.get(p)== "" || lstPagination.get(p) == null){
                    lstPagination.remove(p);
                }
            }
            // Chi lay 5 trang
            if(lstPagination.size() > 5){
                for(int position = lstPagination.size()-1; position > 5; position--){
                    lstPagination.remove(position);
                }
            }
            // Get pages have pagination
            for(int i = 1; i < lstPagination.size(); i++) {
                String pageNumber = Common_Constants.BASE_URL + lstPagination.get(i);
                Document mEnrollInforPagination = Jsoup.connect(pageNumber).timeout(10*1000).get();
                Elements divNews = mEnrollInforPagination.getElementsByClass("eachnews ");
                for(Element e: divNews) {
                    String linkSuffix = e.select("p.title a").attr("href");
                    String link = Common_Constants.BASE_URL + linkSuffix;
                    String title = e.select("p.title a").attr("title");
                    String imgSuffix = e.tagName("span").select("img").attr("src");
                    String img = Common_Constants.BASE_URL + imgSuffix;
                    String smrContents = e.select("span.text_wr").text();
                    lstNews.add(new News(title, link,smrContents, img));
                }

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
