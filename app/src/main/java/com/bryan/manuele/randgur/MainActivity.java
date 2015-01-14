package com.bryan.manuele.randgur;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
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
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_copy:
                copyLinkToClipBoard();
                return true;
            case R.id.action_share:
                shareImage();
                return true;
            case R.id.action_download:
                downloadImage();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void shareImage() {
        Bitmap bitmap = images.get(slidePosition).bitmap;

        try {
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File imageFile = new File(path, getCurrentTime()+ ".png");
            FileOutputStream fileOutPutStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, fileOutPutStream);

            fileOutPutStream.flush();
            fileOutPutStream.close();
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("image/png");
            Uri uri = Uri.parse("file://" + imageFile.getAbsolutePath());
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.putExtra(Intent.EXTRA_TEXT, images.get(slidePosition).link);
            startActivity(Intent.createChooser(shareIntent, "Send your image"));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void downloadImage() {
        try {

            Toast.makeText(getBaseContext(), "Downloading Image...", Toast.LENGTH_LONG).show();

            Bitmap bitmap = images.get(slidePosition).bitmap;
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File imageFile = new File(path, getCurrentTime() + ".png");
            FileOutputStream fileOutPutStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, fileOutPutStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getCurrentTime() {
        Calendar calendar = new GregorianCalendar();

        //monthdayhouryear
        return (calendar.get(Calendar.MONTH) + 1) + "" +
                calendar.get(Calendar.DAY_OF_MONTH) + "" +
                calendar.get(Calendar.HOUR) + "" +
                calendar.get(Calendar.MINUTE) + "" +
                calendar.get(Calendar.SECOND);
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