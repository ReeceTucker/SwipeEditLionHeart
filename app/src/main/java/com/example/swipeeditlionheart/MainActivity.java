package com.example.swipeeditlionheart;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import static android.graphics.BitmapFactory.*;
import static com.example.swipeeditlionheart.R.drawable.tumblr_logo;

public class MainActivity extends AppCompatActivity implements SliderFragment.SliderLister {

    // List of the images and member variables
    ArrayList<Bitmap> arrayOfBitmaps = new ArrayList<Bitmap>();
    ArrayList<Bitmap> arrayEditedOfBitmaps = new ArrayList<Bitmap>();
    private static Button saveButton;
    private static Button resetButton;
    private int pageCount = 0;

    public SliderFragment fragmentSlider = new SliderFragment();
    public PictureFragment pictureSectionFragment = new PictureFragment();

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        saveButton = findViewById(R.id.save_button);
        resetButton = findViewById(R.id.reset_button);

        saveButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SaveImage();
                    }
                }
        );

        resetButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ResetImage();
                    }
                }
        );

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.image_fragement);
                ImageView imageView = fragment.getView().findViewById(R.id.picture);
                imageView.setImageBitmap(arrayEditedOfBitmaps.get(position));
                SetPosition(position);
                pictureSectionFragment.resetCaption();
                fragmentSlider.restSliders();
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // Just initializes the bitmap arrays
        setImageArrays();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    // Getters and setters for the view pager count
    public void SetPosition (int position)
    {
        pageCount = position;
    }
    public int GetPosition ()
    {
        return pageCount;
    }

    // This method simply resets the images to the original state and everything else
    private void ResetImage() {

        Fragment imageFragment = getSupportFragmentManager().findFragmentById(R.id.image_fragement);
        ImageView imageView = imageFragment.getView().findViewById(R.id.picture);
        imageView.setImageBitmap(arrayOfBitmaps.get(GetPosition()));

        pictureSectionFragment.resetCaption();
        fragmentSlider.restSliders();

    }

    // This method will save the image to the android device allowing them to share it how they like.
    private void SaveImage() {

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.image_fragement);
        ImageView imageView = fragment.getView().findViewById(R.id.picture);
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        arrayEditedOfBitmaps.remove(GetPosition());
        arrayEditedOfBitmaps.add(GetPosition(), bitmap);

        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        OutputStream fOutputStream = null;

        File mFolder = new File(getFilesDir() + "/sample");
        File imgFile = new File(mFolder.getAbsolutePath() + GetPosition() + "image.png");

        if (!mFolder.exists()) {
            mFolder.mkdir();
        }
        if (!imgFile.exists()) {
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imgFile);
            bitmap.compress(Bitmap.CompressFormat.PNG,70, fos);
            fos.flush();
            fos.close();
            Toast.makeText(this, "We have saved this image", Toast.LENGTH_SHORT).show();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    // the Interface method
    @Override
    public void colourImage(int iRed, int iGreen, int iBlue) {

        PictureFragment pictureSectionFragment = (PictureFragment) getSupportFragmentManager().findFragmentById(R.id.image_fragement);
        ImageView imageView = (ImageView)pictureSectionFragment.getView().findViewById(R.id.picture);
        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        imageView.setImageBitmap(pictureSectionFragment.Editphoto(bitmap, iRed, iGreen, iBlue));
        Toast.makeText(this, "To save this picture please press the save image button", Toast.LENGTH_SHORT).show();
    }

    public void setImageArrays()
    {
        Bitmap bitmap1 = decodeResource(getResources(), R.drawable.me);//assign your bitmap;
        Bitmap bitmap2 = decodeResource(getResources(), R.drawable.facebook_logo);//assign your bitmap;
        Bitmap bitmap3 = decodeResource(getResources(), R.drawable.instagram_icon);//assign your bitmap;
        Bitmap bitmap4 = decodeResource(getResources(), R.drawable.yt_logo_rgb_light);//assign your bitmap;
        Bitmap bitmap5 = decodeResource(getResources(), R.drawable.twitter_logo_whiteonblue);//assign your bitmap;
        Bitmap bitmap6 = decodeResource(getResources(), tumblr_logo);//assign your bitmap;
        Bitmap bitmap7 = decodeResource(getResources(), R.drawable.linkin_logo);//assign your bitmap;
        arrayOfBitmaps.add(bitmap1);
        arrayOfBitmaps.add(bitmap2);
        arrayOfBitmaps.add(bitmap3);
        arrayOfBitmaps.add(bitmap4);
        arrayOfBitmaps.add(bitmap5);
        arrayOfBitmaps.add(bitmap6);
        arrayOfBitmaps.add(bitmap7);
        arrayEditedOfBitmaps.add(bitmap1);
        arrayEditedOfBitmaps.add(bitmap2);
        arrayEditedOfBitmaps.add(bitmap3);
        arrayEditedOfBitmaps.add(bitmap4);
        arrayEditedOfBitmaps.add(bitmap5);
        arrayEditedOfBitmaps.add(bitmap6);
        arrayEditedOfBitmaps.add(bitmap7);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 7 total pages.
            return 7;
        }
    }
}
