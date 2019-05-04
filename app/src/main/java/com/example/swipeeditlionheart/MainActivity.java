package com.example.swipeeditlionheart;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.AccessToken;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements SliderFragment.SliderLister  {

    // List of the images and member variables
    ArrayList<Bitmap> arrayOfBitmaps = new ArrayList<Bitmap>();
    ArrayList<Bitmap> arrayEditedOfBitmaps = new ArrayList<Bitmap>();
    private static Button saveButton;
    private static Button resetButton;
    private int pageCount = 0;

    public SliderFragment fragmentSlider = new SliderFragment();
    public PictureFragment pictureSectionFragment = new PictureFragment();

    SaveLoadImages saveLoadImages = new SaveLoadImages();
    LoginActivity loginActivity;

    public interface AsyncResponse {
        void processFinish(Bitmap bitmap);
    }

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

        saveButton = findViewById(R.id.save_button);
        resetButton = findViewById(R.id.reset_button);

        AccessToken accessToken = AccessToken.getCurrentAccessToken();

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
                        TextView topCaption = findViewById(R.id.top_caption);
                        TextView bottomCaption = findViewById(R.id.bottom_caption);
                        Spinner spinner = findViewById(R.id.spinner_colors);
                        topCaption.setVisibility(view.VISIBLE);
                        bottomCaption.setVisibility(view.VISIBLE);
                        spinner.setVisibility(view.VISIBLE);
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

                Uri uri = saveLoadImages.GetImageUri(arrayEditedOfBitmaps.get(GetPosition()), view.getContext());

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                shareIntent.setType("image/png");
                startActivity(Intent.createChooser(shareIntent, "Image"));
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

    // This method will save the image to edited image array which we will use to share the images
    private void SaveImage() {

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.image_fragement);
        ImageView imageView = fragment.getView().findViewById(R.id.picture);
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        arrayEditedOfBitmaps.remove(GetPosition());
        arrayEditedOfBitmaps.add(GetPosition(), bitmap);
        Toast.makeText(this, "This picture has been saved, and is ready to share", Toast.LENGTH_SHORT).show();
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

    public void setImageArrays() {
        Bitmap bitmap;
        String strFileDirectory = getCacheDir() + "/sample";
        File dir = new File(strFileDirectory);
        if (dir.exists()) {
            for (File f : dir.listFiles()) {
                bitmap = BitmapFactory.decodeFile(strFileDirectory + "/" + f.getName());
                arrayOfBitmaps.add(bitmap);

            }
            arrayEditedOfBitmaps = new ArrayList<>(arrayOfBitmaps);
        }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        trimCache(this);
        Intent intent = new Intent(this, LoginActivity.class);
        this.startActivity(intent);
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    //Fires after the OnStop() state
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            trimCache(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {

        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

}

