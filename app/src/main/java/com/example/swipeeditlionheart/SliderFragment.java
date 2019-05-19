package com.example.swipeeditlionheart;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import java.util.ArrayList;

public class SliderFragment extends Fragment {

    // Member variables
    private static SeekBar seekBar;
    private static TextView textView;
    private static ArrayList<SeekBar> seekbarlist =new ArrayList<SeekBar>();
    private  static ArrayList<EditText> textViewlist =new ArrayList<EditText>();

    // Creating an interface
    private SliderLister activityCommander;
    public interface SliderLister
    {
        void colourImage(int iRed, int iGreen, int iBlue);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            activityCommander = (SliderLister) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_slider_layout, container, false);

        //Setting up the ArrayList of Seekbars
        seekbarlist.add((SeekBar) view.findViewById(R.id.seekbar_R));
        seekbarlist.add((SeekBar) view.findViewById(R.id.seekbar_G));
        seekbarlist.add((SeekBar) view.findViewById(R.id.seekbar_B));

        //Setting up the ArrayList of Textviews
        textViewlist.add((EditText) view.findViewById(R.id.seekbar_R_value));
        textViewlist.add((EditText) view.findViewById(R.id.seekbar_G_value));
        textViewlist.add((EditText) view.findViewById(R.id.seekbar_B_value));

        // Setting up the Seek bars as will as listening for clicks
        setSeekBar(seekbarlist, textViewlist, view);

        return view;
    }

    private void setSeekBar(final ArrayList<SeekBar> seekBarlist, ArrayList<EditText> textList, final View view)
    {
        for (int i = 0; i < seekBarlist.size(); i++) {
            for (int y = 0; y < textList.size(); y++) {
                seekBar = view.findViewById((seekBarlist.get(i).getId()));
                textView = view.findViewById(textList.get(y).getId());
                textView.setText(seekBar.getProgress() + "/" + seekBar.getMax());

                seekBar.setOnSeekBarChangeListener(

                        new SeekBar.OnSeekBarChangeListener() {
                            int progressValue;

                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                                progressValue = progress;
                                textView.setText(textView.getText().toString() + progress);

                                switch (seekBar.getId()) {
                                    case R.id.seekbar_R:
                                        textView =  view.findViewById(R.id.seekbar_R_value);
                                        textView.setText("R " + progress);
                                        break;
                                    case R.id.seekbar_G:
                                        textView = view.findViewById(R.id.seekbar_G_value);
                                        textView.setText("G " + progress);
                                        break;
                                    case R.id.seekbar_B:
                                        textView = view.findViewById(R.id.seekbar_B_value);
                                        textView.setText("B " + progress);
                                        break;

                                }
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {

                                switch (seekBar.getId()) {
                                    case R.id.seekbar_R:
                                        textView = view.findViewById(R.id.seekbar_R_value);
                                        textView.setText("R " + progressValue);
                                        break;
                                    case R.id.seekbar_G:
                                        textView = view.findViewById(R.id.seekbar_G_value);
                                        textView.setText("G " + progressValue);
                                        break;
                                    case R.id.seekbar_B:
                                        textView = view.findViewById(R.id.seekbar_B_value);
                                        textView.setText("B " + progressValue);
                                        break;

                                }
                                // passing all of the seekbars values
                                seekBarHasChanged(seekBarlist.get(0).getProgress(), seekBarlist.get(1).getProgress(), seekBarlist.get(2).getProgress());
                            }
                        }
                );
            }
        }
    }

    private void seekBarHasChanged(int redProgress, int greenProgress, int blueProgress)
    {
        activityCommander.colourImage(redProgress, greenProgress, blueProgress);
    }

    // This method just simply resets this frgament with the base values
    public void restSliders()
    {
        seekbarlist.get(0).setProgress(1);
        seekbarlist.get(1).setProgress(1);
        seekbarlist.get(2).setProgress(1);

        textViewlist.get(0).setText(seekbarlist.get(0).getProgress() + "/" + seekBar.getMax());
        textViewlist.get(1).setText(seekbarlist.get(1).getProgress() + "/" + seekBar.getMax());
        textViewlist.get(2).setText(seekbarlist.get(2).getProgress() + "/" + seekBar.getMax());

    }

}
