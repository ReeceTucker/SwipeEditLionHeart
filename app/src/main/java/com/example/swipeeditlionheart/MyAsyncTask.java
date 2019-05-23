package com.example.swipeeditlionheart;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static com.facebook.login.widget.ProfilePictureView.TAG;

class MyAsyncTask extends AsyncTask<String, Void, Bitmap>
{
    private LoginActivity loginActivity;
    private Context context;
    private SaveLoadImages saveImages = new SaveLoadImages();


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
            saveImages.CacheImage(bitmap, context, "");
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
        if (saveImages.ReadFileCount(context.getCacheDir() + "/sample") == 7)
        {
            loginActivity.Turnoff();
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
            ((Activity) context).finish();

        }

    }

}

