package com.example.swipeeditlionheart;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static com.facebook.login.widget.ProfilePictureView.TAG;

public class MyAsyncTask extends AsyncTask<String, Void, Bitmap>
{
    int mCount = 0;
    Bitmap mBitmap = null;
    LoginActivity loginActivity;
    private Context context;
    ArrayList<Bitmap> arrayOfBitmaps = new ArrayList<Bitmap>();
    SaveLoadImages saveImages = new SaveLoadImages();

    public MyAsyncTask(LoginActivity loginActivity, Context context)
    {
        this.loginActivity = loginActivity;
        this.context = context;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        String src = strings[0];
        Bitmap bitmap = null;
        try {
            URL url = new URL(src);
            bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());

            Log.i(TAG,"OK!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        } catch (MalformedURLException e) {
            Log.i(TAG,"!!!!!!!!!Errore1");
            e.printStackTrace();
        } catch (IOException e) {
            Log.i(TAG,"!!!!!!!!!Errore2");
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap)
    {
        loginActivity.dialog.show();
        loginActivity.dialog.setMessage("Wait one moment, just fetching your images");
        saveImages.CacheImage(bitmap, context, "Images");

        if (saveImages.ReadFileCount(context.getCacheDir() + "/sample") ==7)
        {
            loginActivity.dialog.hide();

            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
            ((Activity) context).finish();

        }

    }
}

