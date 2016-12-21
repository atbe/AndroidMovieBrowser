package com.atbe.abe.topmovieslist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MovieWebPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_web_page);

        // Set the toolbar
        setSupportActionBar(((Toolbar) findViewById(R.id.my_toolbar)));

        // Recieve the link to the movie trailer
        Bundle messages = getIntent().getExtras();
        if (messages != null) {
            String url = messages.getString("url");

            System.out.println("DEBUG: MovieWebPageActivity opening url = " + url);
            WebView webView = ((WebView) findViewById(R.id.movie_webview));
            webView.setWebViewClient(new WebViewClient());
            WebSettings webSettings=webView.getSettings();
            webSettings.setJavaScriptEnabled(true);

            webView.loadUrl(url);
        }
    }
}
