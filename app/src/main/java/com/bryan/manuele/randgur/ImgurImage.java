package com.bryan.manuele.randgur;

import android.graphics.Bitmap;

public class ImgurImage {
    String link;
    Bitmap bitmap;

    public ImgurImage(String link, Bitmap bitmap) {
        this.link = link;
        this.bitmap = bitmap;
    }

    public static String generatePossibleLink() {
        String result = "";
        String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int i = 0; i < 5; i++) {
            int index = (int) (alphabet.length() * Math.random());
            result += alphabet.charAt(index);
        }
        return  "https://i.imgur.com/" + result + ".png";
    }

}
