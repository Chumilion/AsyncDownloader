package chumilion.asyncdownloader;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class MainActivity extends AppCompatActivity
{
    ProgressBar progressBar;
    TextView scrollable;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        scrollable = (TextView) findViewById(R.id.scrollable);
        scrollable.setMovementMethod(new ScrollingMovementMethod());
        scrollable.setVisibility(View.INVISIBLE);


        DownloadTask downloadTask = new DownloadTask(this);
        downloadTask.execute("http://www.opensourceshakespeare.org/views/plays/play_view.php?WorkID=hamlet&Scope=entire&pleasewait=1&msg=pl");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setUpProgress(int m)
    {
        progressBar.setMax(m);
    }

    public void updateProgress(int run)
    {
        progressBar.setProgress(run);
    }

    public void finishProgress(String file)
    {
        progressBar.setVisibility(View.GONE);
        scrollable.setText(file);
        scrollable.setVisibility(View.VISIBLE);

    }

    class DownloadTask extends AsyncTask<String, Integer, String>
    {
        Context myContext;

        public DownloadTask(Context c)
        {
            myContext = c;
        }
        protected String doInBackground(String... urls)
        {
            MainActivity myActivity = (MainActivity) myContext;
            InputStream input = null;
            OutputStream output = null;
            StringBuilder build = new StringBuilder();
            HttpURLConnection connection = null;
            try
            {
                URL url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if(connection.getResponseCode() != HttpURLConnection.HTTP_OK)
                {
                    return "Server returned HTTP " + connection.getResponseCode() +
                            " " + connection.getResponseMessage();
                }

                int fileLength = connection.getContentLength();
                myActivity.setUpProgress(100);

                long total = 0;
                String addLine = "";
                input = connection.getInputStream();
                BufferedReader breader = new BufferedReader(new InputStreamReader(input, "UTF-8"));

                while((addLine = breader.readLine()) != null)
                {
                    build.append(addLine);
                    build.append("\n");

                    total += addLine.length();
                    Log.i("total", total + " " + fileLength);
                    updateProgress((int) (total * 100.0 / fileLength));
                }

                return build.toString();

            }
            catch(Exception e)
            {
                return e.toString();
            }

        }

        protected void onProgressUpdate(Integer... runn)
        {
            ((MainActivity) myContext).updateProgress(runn[0]);
        }

        protected void onPostExecute(String result)
        {
            ((MainActivity) myContext).finishProgress(result);
        }
    }
}
