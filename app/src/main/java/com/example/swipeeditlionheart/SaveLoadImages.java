package com.example.swipeeditlionheart;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static android.graphics.BitmapFactory.decodeResource;

class SaveLoadImages {

    private Context context2;

    public void CacheImage(Bitmap bitmap, Context context, String strFolder) {

        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        File mFolder = new File(context.getCacheDir() + "/sample");
        File imgFile = new File(mFolder.getAbsolutePath() + "/Image" + n + ".png");
        if (!mFolder.exists()) {
            mFolder.mkdir();
        }
        if (!imgFile.exists()) {
            try {
                imgFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imgFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 70, fos);
            fos.flush();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public int ReadFileCount(String strFileDirectory) {
        int count = 0;
        File dir = new File(strFileDirectory);
        if (dir.exists()) {
            for (File f : dir.listFiles()) {

                count++;
            }
        }

        return count;
    }

    public ArrayList<Bitmap> SetImages(String strFileDirectory) {

        FileInputStream fis = null;
        Bitmap bitmap;
        ArrayList<Bitmap> test = null;
        File dir = new File(strFileDirectory);
        if (dir.exists()) {
            for (File f : dir.listFiles()) {

                bitmap = BitmapFactory.decodeFile(strFileDirectory + "/" + f.getName());
                if (test != null) {
                    test.add(bitmap);
                }
            }
        }
        return test;
    }

    public ArrayList<Uri> SharedEdited(String strFileDirectory) {
        ArrayList<Uri> test = null;
        File dir = new File(strFileDirectory);
        if (dir.exists()) {
            for (File f : dir.listFiles())
            {
                Uri uri = Uri.parse(f.getPath());
                if (test != null) {
                    test.add(uri);
                }
            }

        }
        return test;
    }

    public Uri GetImageUri(Bitmap bitmap, Context context) {
        File imagesFolder = new File(context.getCacheDir(), "images");
        Uri uri = null;
        try {
            imagesFolder.mkdirs();
            File file = new File(imagesFolder, "shared_image.png");

            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(context, "com.mydomain.fileprovider", file);

        } catch (IOException e) {

        }
        return uri;
    }

    public void LoadLocalStore(Context context)
    {
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        bitmaps.add(decodeResource(context.getResources(), R.drawable.me));
        bitmaps.add(decodeResource(context.getResources(), R.drawable.instagram_icon));
        bitmaps.add(decodeResource(context.getResources(), R.drawable.facebook_logo));
        bitmaps.add(decodeResource(context.getResources(), R.drawable.linkin_logo));
        bitmaps.add(decodeResource(context.getResources(), R.drawable.tumblr_logo));
        bitmaps.add(decodeResource(context.getResources(), R.drawable.yt_logo_rgb_light));
        bitmaps.add(decodeResource(context.getResources(), R.drawable.twitter_logo_whiteonblue));

        for (int i = 0; i < bitmaps.size(); i++)
        {
            Bitmap bitmapCompressed;

            bitmapCompressed = bitmaps.get(i);

            CacheImage(bitmapCompressed, context, "Images");
        }
    }
}

