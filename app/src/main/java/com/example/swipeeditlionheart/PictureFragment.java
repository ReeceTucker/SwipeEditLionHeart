package com.example.swipeeditlionheart;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.Toast;

public class PictureFragment extends Fragment implements AdapterView.OnItemSelectedListener {


    private static EditText  topCaption;
    private static EditText bottomCaption;
    private static ImageView imageView;
    private static Button confimTextButton;
    private String color;
    private int colorchosen = 0;
    Spinner spinner;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)

    {
        View view = inflater.inflate(R.layout.fragment_image, container, false);

        imageView =  view.findViewById(R.id.picture);
        topCaption = view.findViewById(R.id.top_caption);
        bottomCaption = view.findViewById(R.id.bottom_caption);
        confimTextButton =  view.findViewById(R.id.button_confim_text);

        confimTextButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ConfimTextClicked(view);
                    }
                }
        );

        spinner = view.findViewById(R.id.spinner_colors);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(), R.array.colors, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        return  view;
    }

    // This method handles the confirm button being press and editing the Bitmap and put the text entered onto it
    private void ConfimTextClicked(View view) {

        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        imageView.setImageBitmap(DrawCaption(bitmap, topCaption.getText().toString(), bottomCaption.getText().toString()));
        Toast.makeText(getActivity(),"Press the save button to save this image", Toast.LENGTH_SHORT).show();
        if(topCaption.getText().length() != 0)
        {
            topCaption.setVisibility(view.INVISIBLE);
            topCaption.setText("");
        }

        if(bottomCaption.getText().length() != 0)
        {
            bottomCaption.setVisibility(view.INVISIBLE);
            bottomCaption.setText("");
        }

        if(topCaption.getText().length() != 0 && bottomCaption.getText().length() != 0)
        {
            spinner.setVisibility(view.INVISIBLE);
        }

    }

    // This method will loop through all of the pixels in the Bitmap and tweak it by the RBG passed in
    public Bitmap Editphoto(Bitmap original, int R, int G, int B /*int height, int width*/)
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
    public Bitmap DrawCaption(Bitmap bitmap, String topCaption, String bottomCaption)
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
            paint.setColor(colorchosen);
            // text size in pixels
            paint.setTextSize((int) (16 * scale));
            // text shadow
            paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY);

            // draw the text to the top and the bottom of the image
            Rect bounds = new Rect();
            paint.getTextBounds(topCaption, 0, topCaption.length(), bounds);
            paint.getTextBounds(bottomCaption, 0, bottomCaption.length(), bounds);
            int xTop = (bitmap.getWidth() - bounds.width())/ 8;
            int yTop = (bitmap.getHeight() + bounds.height())/ 20;
            int xBottom = (bitmap.getWidth() - bounds.width()) / 7;
            int yBottom = (bitmap.getHeight() + bounds.height())/ 5;

            canvas.drawText(topCaption, xTop * scale, yTop * scale, paint);
            canvas.drawText(bottomCaption, xBottom * scale, yBottom * scale, paint);

            return bitmap;
        }
        catch (Exception e)
        {
            return null;
        }

    }

    public void resetCaption()
    {
        topCaption.setText("");
        bottomCaption.setText("");
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        color = parent.getItemAtPosition(position).toString();
        colorchosen = Color.parseColor(color);
        spinner.setBackgroundColor(colorchosen);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
