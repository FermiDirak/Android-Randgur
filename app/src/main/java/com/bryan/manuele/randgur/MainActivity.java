package com.bryan.manuele.randgur;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    RelativeLayout startRelativeLayout;
    RelativeLayout left;
    RelativeLayout right;
    ProgressDialog pDialog;

    ImageView imageView;

    List<ImgurImage> images;
    int slidePosition;

    Boolean firstLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startRelativeLayout = (RelativeLayout) findViewById(R.id.startRelativeLayout);
        left = (RelativeLayout) findViewById(R.id.left);
        right = (RelativeLayout) findViewById(R.id.right);
        imageView = (ImageView) findViewById(R.id.imageView);

        images = new ArrayList<>();
        slidePosition = -1;
        firstLoad = true;

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firstLoad) {
                    firstLoad();
                    firstLoad = false;
                } else {
                    loadNextImage();
                }
            }
        });

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firstLoad) {
                    firstLoad();
                    firstLoad = false;
                } else {
                    loadPreviousImage();
                }
            }
        });
    }

    public void firstLoad() {
        startRelativeLayout.setVisibility(View.GONE);

        loadNextImage();
    }

    public void loadNextImage() {
        if (slidePosition == images.size() - 1) {
            new LoadImage().execute();
        } else {
            slidePosition++;
            loadImageAtPosition();
        }
    }

    public void loadPreviousImage() {
        if (slidePosition != 0) {
            slidePosition--;
            loadImageAtPosition();
        }
    }

    public void loadImageAtPosition() {
        imageView.setImageBitmap(images.get(slidePosition).bitmap);
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
        ClipData clip = ClipData.newPlainText(images.get(slidePosition).link,
                images.get(slidePosition).link);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getBaseContext(), "Image link copied to clipboard.", Toast.LENGTH_LONG).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_copy:
                copyLinkToClipBoard();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        Bitmap bitmap;
        String link = "";

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
                ImgurImage imgurImage = new ImgurImage(link, bitmap);
                images.add(imgurImage);
                slidePosition++;
                loadImageAtPosition();

                pDialog.dismiss();
            }else{
                pDialog.dismiss();
                Toast.makeText(MainActivity.this, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();
            }
        }
    }
}