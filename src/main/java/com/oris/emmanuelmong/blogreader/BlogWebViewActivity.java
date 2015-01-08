package com.oris.emmanuelmong.blogreader;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import java.net.URI;


public class BlogWebViewActivity extends ActionBarActivity {

    private String postUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_web_view);
        Intent intent = getIntent();
        Uri uri = intent.getData();
        postUrl = uri.toString();

        WebView webview = (WebView)findViewById(R.id.webView);
        webview.loadUrl(postUrl);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_blog_web_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        if(id == R.id.action_share)
        {
            sharePost();
        }

        return super.onOptionsItemSelected(item);
    }

    private void sharePost()
    {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, postUrl);
        startActivity(Intent.createChooser(shareIntent, "How do you want to share this post? "));
    }

}
