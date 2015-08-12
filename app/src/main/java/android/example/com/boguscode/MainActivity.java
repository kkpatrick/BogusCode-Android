package android.example.com.boguscode;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;
    private VideoAdapter mAdapter;
    private ArrayList<JSONObject> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.activity_main_listview);
        mAdapter = new VideoAdapter(this, R.id.list_item_video_name_textview, items);
        mListView.setAdapter(mAdapter);
        new StaffPicksAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class StaffPicksAsyncTask extends AsyncTask<Void, Void, ArrayList<JSONObject>> {

        @Override
        protected ArrayList<JSONObject> doInBackground(Void... params) {
            String url = "https://api.vimeo.com/channels/staffpicks/videos";
            String token = "bearer b8e31bd89ba1ee093dc6ab0f863db1bd";
            ArrayList<JSONObject> videos = new ArrayList<>();

            StringBuilder builder = new StringBuilder();
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            httpGet.addHeader("Authorization", token);
            try {
                HttpResponse response = client.execute(httpGet);
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                JSONObject object = new JSONObject(builder.toString());
                JSONArray data = object.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    videos.add(data.getJSONObject(i));
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return videos;
        }

        @Override
        protected void onPostExecute(ArrayList<JSONObject> result) {
            items = result;
            mAdapter.addAll(items);
            mAdapter.notifyDataSetChanged();
        }
    }
}
