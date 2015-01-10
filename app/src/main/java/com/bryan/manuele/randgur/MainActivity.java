package com.bryan.manuele.randgur;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ShareActionProvider;
import android.widget.Toast;
import java.io.InputStream;
import java.net.URL;
public class MainActivity extends Activity {
    private static Context context;
    private ShareActionProvider mShareActionProvider;
    RelativeLayout mainRelativeLayout;
    RelativeLayout startRelativeLayout;
    RelativeLayout imageHoldingLayout;
    ImageView imageHolder;
    Bitmap bitmap;
    String link = "";
    ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        mainRelativeLayout = (RelativeLayout) findViewById(R.id.mainRelativeLayout);
        startRelativeLayout = (RelativeLayout) findViewById(R.id.startRelativeLayout);
        imageHoldingLayout = (RelativeLayout) findViewById(R.id.imageHoldingLayout);
        imageHolder = (ImageView) findViewById(R.id.imageHolder);

        mainRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRelativeLayout.setVisibility(View.GONE);
                link = "https://i.imgur.com/" + generatePossibleLink() + ".png";
                new LoadImage().execute();
                System.out.println(link);
                imageHoldingLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Loading Image ....");
            pDialog.show();
        }

        protected Bitmap doInBackground(String... args) {
            try {

                do { link = generatePossibleLink();
                    bitmap = BitmapFactory.decodeStream((InputStream)new URL(
                            link).getContent()); }
                while (bitmap.getHeight() <= 81);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {
            if(image != null){
                imageHolder.setImageBitmap(image);
                pDialog.dismiss();
            }else{
                pDialog.dismiss();
                Toast.makeText(MainActivity.this, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public final String generatePossibleLink() {
        String result = "";
        String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int i = 0; i < 5; i++) {
            int index = (int) (alphabet.length() * Math.random());
            result += alphabet.charAt(index);
        }
        return  "https://i.imgur.com/" + result + ".png";
    }

    public void copyLinkToClipBoard() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(link, link);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "Image link copied to clipboard.", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) item.getActionProvider();
        Intent myIntent = new Intent();
        myIntent.setAction(Intent.ACTION_SEND);
        myIntent.putExtra(Intent.EXTRA_TEXT, "Whatever message you want to share");
        myIntent.setType("text/plain");
        mShareActionProvider.setShareIntent(myIntent);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_copy:
                copyLinkToClipBoard();
                return true;
            case R.id.action_share:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}