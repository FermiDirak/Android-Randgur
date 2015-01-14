package com.bryan.manuele.randgur;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class LoadingActivity extends Activity {

    public static List<ImgurImage> images = new ArrayList<>();

    Bitmap bitmap;
    String link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        generateImages(10);

    }

    public void generateImages(int n) {

        for (int i = 0; i < n; i++) {
            try {
                do { link = ImgurImage.generatePossibleLink();
                    bitmap = BitmapFactory.decodeStream((InputStream) new URL(
                            link).getContent()); }
                while (bitmap.getHeight() <= 81);

            } catch (Exception e) {
                e.printStackTrace();
            }

            images.add( new ImgurImage(link, bitmap));
        }

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_loading, menu);
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

}
