package com.example.swipeeditlionheart;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;


public class PictureFragment extends Fragment implements AdapterView.OnItemSelectedListener {


    private static EditText  topCaption;
    private static EditText bottomCaption;
    private static ImageView imageView;
    private int colorChosen = 0;
    private static Spinner colorSpinner;
    private static Spinner filterSpinner;
    private static Bitmap mOriginalBitmap;

    private Map<String, String> colors = new HashMap<>();
    private Map<String, Integer> filters = new HashMap<>();
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)

    {
        View view = inflater.inflate(R.layout.fragment_image, container, false);

        imageView =  view.findViewById(R.id.picture);
        topCaption = view.findViewById(R.id.top_caption);
        bottomCaption = view.findViewById(R.id.bottom_caption);
        Button confirmTextButton = view.findViewById(R.id.button_confim_text);

        confirmTextButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ConfirmTextClicked(view);
                    }
                }
        );

        // Map of colors for the spinner
        colors.put("White", "#FFFFFF");
        colors.put("Black", "#000000");
        colors.put("Purple", "#9400D3");
        colors.put("Violet", "#4B0082");
        colors.put("Blue", "#0000FF");
        colors.put("Green", "#00FF00");
        colors.put("Yellow", "#FFFF00");
        colors.put("Orange", "#FF7F00");
        colors.put("Red", "#FF0000");


        filters.put("Normal", 0);
        filters.put("Crystal", R.drawable.filter_crystal);
        filters.put("Cloud", R.drawable.clouds_filter);
        filters.put("Static", R.drawable.static_filter);
        filters.put("Spiral", R.drawable.spiral_filter);
        filters.put("Blobs", R.drawable.blobs_filter);
        filters.put("Whirlpool", R.drawable.whirlpool_filter);


        // colour Spinner
        colorSpinner = view.findViewById(R.id.spinner_colors);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(), R.array.colors, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorSpinner.setAdapter(adapter);
        colorSpinner.setOnItemSelectedListener(this);

        filterSpinner = view.findViewById(R.id.spinner_filters);
        ArrayAdapter<CharSequence> adapterFilter = ArrayAdapter.createFromResource(this.getContext(), R.array.filters, android.R.layout.simple_spinner_item);
        adapterFilter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(adapterFilter);
        filterSpinner.setOnItemSelectedListener(this);

        return  view;
    }

    // This method handles the confirm button being press and editing the Bitmap and put the text entered onto it
    private void ConfirmTextClicked(View view) {

        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        imageView.setImageBitmap(DrawCaption(bitmap, topCaption.getText().toString(), bottomCaption.getText().toString()));
        Toast.makeText(getActivity(),"Press the save button to save this image", Toast.LENGTH_SHORT).show();
        if (topCaption.getText().length() != 0)
        {
            topCaption.setVisibility(View.INVISIBLE);
            topCaption.setText("");
        }

        if (bottomCaption.getText().length() != 0)
        {
            bottomCaption.setVisibility(View.INVISIBLE);
            bottomCaption.setText("");
        }

        if (topCaption.getText().length() != 0 && bottomCaption.getText().length() != 0)
        {
            colorSpinner.setVisibility(View.INVISIBLE);
        }

    }

    // This method will loop through all of the pixels in the Bitmap and tweak it by the RBG passed in
    public Bitmap EditPhotoRGB(Bitmap original, int R, int G, int B)
    {
        Bitmap editedImage = Bitmap.createBitmap(original.getWidth(),original.getHeight(),original.getConfig());

        int pixelColor;
        int height = original.getHeight();
        int width = original.getWidth();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixelColor = original.getPixel(x,y);
                int A = Color.alpha(pixelColor);
                int Red = R + Color.red(pixelColor);
                int Green = G + Color.green(pixelColor);
                int Blue = B + Color.blue(pixelColor);

                editedImage.setPixel(x, y, Color.argb(A,Red,Green,Blue));
            }
        }
        return editedImage;
    }

    // This method will draw the Top and bottom caption onto the bit map onces the user has confimed their text
    private Bitmap DrawCaption(Bitmap bitmap, String topCaption, String bottomCaption)
    {
        try{

            Resources resources = getResources();
            float scale = resources.getDisplayMetrics().density;
            android.graphics.Bitmap.Config bitmapConfig =   bitmap.getConfig();

            // set default bitmap config if none
            if(bitmapConfig == null) {
                bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
            }
            // resource bitmaps are imutable,
            // so we need to convert it to mutable one
            bitmap = bitmap.copy(bitmapConfig, true);

            Canvas canvas = new Canvas(bitmap);
            // new antialised Paint
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            // text color - this is whatever the user has selected in the spinner list
            paint.setColor(colorChosen);
            // text size in pixels
            paint.setTextSize((int) (16 * scale));
            // text shadow
            paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY);

            // draw the text to the top and the bottom of the image
            Rect bounds = new Rect();

            paint.getTextBounds(topCaption, 0, topCaption.length(), bounds);
            paint.getTextBounds(bottomCaption, 0, bottomCaption.length(), bounds);
            int xTop = bitmap.getWidth()/ 2 ;
            int yTop = (bitmap.getHeight() + bounds.height())/ 4;
            int xBottom = (bitmap.getWidth() - bounds.width()) / 7;
            int yBottom = (bitmap.getHeight() + bounds.height())/ 5;

            canvas.drawText(topCaption, xTop, yTop, paint);
            canvas.drawText(bottomCaption, xBottom * scale , yBottom * scale, paint);

            return bitmap;
        }
        catch (Exception e)
        {
            return null;
        }

    }

    public void ResetCaption()
    {
        topCaption.setVisibility(View.VISIBLE);
        bottomCaption.setVisibility(View.VISIBLE);
        topCaption.setText("");
        bottomCaption.setText("");
        filterSpinner.setSelection(0);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (parent.getId())
        {
            case R.id.spinner_colors:
                String colorName = parent.getItemAtPosition(position).toString();
                String color = colors.get(colorName);
                colorChosen = Color.parseColor(color);
                ((TextView)view).setText(null);

                // This is so i can change the colour of the image to what the text colour is set to.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    colorSpinner.setBackgroundTintList(ColorStateList.valueOf(colorChosen));
                }
                break;

            case R.id.spinner_filters:

                ResetSliders();
                ((TextView)view).setText(null);
                String filterName = parent.getItemAtPosition(position).toString();
                Integer filter = filters.get(filterName);
                imageView.setImageBitmap(GetOriginalImageViewBitmap());

                if (!filterName.equalsIgnoreCase("Normal"))
                {
                    Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                    imageView.setImageBitmap(FilterImage(bitmap, filter));
                }
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    // This method simply stacks Bitmaps to create a dual layer image
    private Bitmap FilterImage(Bitmap bitmap, Integer filter)
    {
        Bitmap finishedBitmap = null;

        Drawable bottomImage = new BitmapDrawable(getResources(), bitmap);
        Drawable[] filter_layers = new Drawable[2];
        filter_layers[0] = bottomImage;
        filter_layers[1] = getResources().getDrawable(filter);
        LayerDrawable layerDrawable = new LayerDrawable(filter_layers);
        drawableToBitmap(layerDrawable, bitmap);
        finishedBitmap = drawableToBitmap(layerDrawable, bitmap);

        return finishedBitmap;
    }

    // This method is simply converting my drawable layer to a bitmap
    private static Bitmap drawableToBitmap (Drawable drawable, Bitmap originalBitmap) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        // I'm passing the original in so i can keep its size
        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public void SetOriginalImageViewBitmap(Bitmap bitmap)
    {
        mOriginalBitmap = bitmap;
    }

    private Bitmap GetOriginalImageViewBitmap()
    {
        return mOriginalBitmap;
    }

    public void ResetSliders()
    {
        SliderFragment sliderFragment = (SliderFragment) getFragmentManager().findFragmentById(R.id.slider_fragment);
        sliderFragment.restSliders();
    }

}
