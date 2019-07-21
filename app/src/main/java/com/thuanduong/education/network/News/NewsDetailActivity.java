package com.thuanduong.education.network.News;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;

import com.thuanduong.education.network.Constant.Common_Constants;
import com.thuanduong.education.network.R;

public class NewsDetailActivity extends AppCompatActivity {

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        mWebView = findViewById(R.id.webView);

        String getLink = getIntent().getExtras().getString("newsLink");
        String linkDetail = Common_Constants.BASE_URL + getLink;
        mWebView = findViewById(R.id.webView);

        WebView webView = (WebView)findViewById(R.id.webView);
        webView.loadUrl(linkDetail);

    }

}
