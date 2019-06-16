package com.example.finalproject.Helper;

import android.graphics.Bitmap;

public class bBitmapHelper {
    private Bitmap bitmap = null;
    private static final fBitmapHelper instance = new fBitmapHelper();

    public bBitmapHelper(){

    }

    public static fBitmapHelper getInstance() {
        return instance;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
