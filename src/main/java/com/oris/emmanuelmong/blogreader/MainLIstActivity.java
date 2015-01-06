package com.oris.emmanuelmong.blogreader;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainLIstActivity extends ListActivity {

    protected String blogPostTitles[];
    protected JSONObject blogData;

    public static final int numberOfPosts = 20;
    public static final String TAG = MainLIstActivity.class.getSimpleName();
    protected ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);

        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        //First check if network is available, before trying to get blog posts
        if(isNetworkAvailable())
        {
            progressBar.setVisibility(View.VISIBLE);
            //Calling the GetBlogPostsTask doInBackGround method through execute
            GetBlogPostsTask getBlogPostsTask = new GetBlogPostsTask();
            getBlogPostsTask.execute();
        }
        else
        {
            Toast.makeText(this, "Network is unavailable right now", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isNetworkAvailable()
    {
        ConnectivityManager manager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected())
        {
            return true;
        }
        return false;
    }

    public void handleBlogResponse()
    {
        progressBar.setVisibility(View.INVISIBLE);
        if(blogData == null)
        {
            updateDisplayForError();
        }
        else
        {
            try
            {
                JSONArray jsonPosts = blogData.getJSONArray("posts");
                blogPostTitles = new String[jsonPosts.length()];
                for(int i = 0; i < jsonPosts.length(); i++)
                {
                    String title = jsonPosts.getJSONObject(i).getString("title");
                    title = Html.fromHtml(title).toString() ;
                    blogPostTitles[i] = title;
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, blogPostTitles);
                setListAdapter(adapter);
            }
            catch(JSONException e)
            {
                Log.e(TAG, "Exception Caught in UpdateList:MainListActivity!", e);
            }
        }
    }

    private void updateDisplayForError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.error_title));
        builder.setMessage(getString(R.string.error_message));
        builder.setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
        TextView emptyTextView = (TextView)getListView().getEmptyView();
        emptyTextView.setText(getString(R.string.no_items));
    }

    //Class created to allow multi-threading/DoInbackground operations away from the main thread
    private class GetBlogPostsTask extends AsyncTask<Object, Void, JSONObject>
    {

        @Override
        protected JSONObject doInBackground(Object ... args){
            int responseCode = -1;
            JSONObject jsonResponse = null;
            try
            {
                URL blogFeed = new URL("http://blog.teamtreehouse.com/api/get_recent_summary/?count=" + numberOfPosts);
                HttpURLConnection connection = (HttpURLConnection)blogFeed.openConnection();
                connection.connect();
                responseCode = connection.getResponseCode();


                if(responseCode == HttpURLConnection.HTTP_OK)
                {
                    InputStream inputStream = connection.getInputStream();
                    Reader reader = new InputStreamReader(inputStream);
                    int nextCharacter;
                    String responseData = "";
                    while(true)// Infinite loop, can only be stopped by a "break" statement
                    {
                        nextCharacter = reader.read(); // read() without parameters returns one character
                        if(nextCharacter == -1) // A return value of -1 means that we reached the end
                            break;
                        responseData += (char) nextCharacter; // The += operator appends the character to the end of the string
                    }
                    //Now Parse JSON data
                    jsonResponse = new JSONObject(responseData);


                }
                else
                {
                    Log.i(TAG, "Status code" + responseCode);
                }
            }
            catch (MalformedURLException e)
            {
                Log.e(TAG, "Exception caught: ", e);
            }
            catch (IOException e)
            {
                Log.e(TAG, "Exception caught: ", e);
            }
            catch (Exception e)
            {
                Log.e(TAG, "Exception caught: ", e);
            }
            return jsonResponse;
        }

        @Override
        protected void onPostExecute(JSONObject result)
        {
            blogData = result;
            handleBlogResponse();
        }
    }


}
