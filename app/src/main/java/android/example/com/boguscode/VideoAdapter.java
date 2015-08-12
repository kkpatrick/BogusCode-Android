package android.example.com.boguscode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.List;

public class VideoAdapter extends ArrayAdapter<JSONObject> {

    private List<JSONObject> mItems;

    public VideoAdapter(Context context, int resource, List<JSONObject> objects) {
        super(context, resource, objects);
        mItems = objects;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        JSONObject video = mItems.get(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_video, null);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.video_thumbnail);
        TextView nameTextView = (TextView) convertView.findViewById(R.id.list_item_video_name_textview);
        String uri = null;
        try {
            uri = video.optJSONObject("pictures").optJSONArray("sizes")
                       .getJSONObject(video.optJSONObject("pictures").optJSONArray("sizes").length() - 1)
                       .optString("link", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (uri != null) {
            new DownloadImageTask(imageView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, uri);
        }
        nameTextView.setText(video.optString("name", ""));

        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext().getApplicationContext(),
                               "Item number " + position + " has been selected", Toast.LENGTH_SHORT);
            }
        });
        return convertView;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                bmImage.setImageBitmap(result);
            }
        }
    }
}
