package com.example.mainpage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class FrequencyAdapter extends ArrayAdapter<WishlistData> {
    public Integer[] frequencies;
    private ArrayList<WishlistData> WishlistDataSet;
    Context mContext;

    private static class ViewHolder {
        TextView customizable;
        SeekBar seekBar;
    }

    public FrequencyAdapter(ArrayList<WishlistData> wishlistData, Context context) {
        super(context, R.layout.activity_custom_frequency, wishlistData);
        this.WishlistDataSet = wishlistData;
        frequencies = new Integer[wishlistData.size()];
        this.mContext = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final WishlistData dataModel = getItem(position);

        final FrequencyAdapter.ViewHolder viewHolder;

        final View result;

        if (convertView == null) {
            viewHolder = new FrequencyAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.activity_custom_frequency, parent, false);
            viewHolder.customizable = (TextView) convertView.findViewById(R.id.customizable);
            viewHolder.seekBar = (SeekBar) convertView.findViewById(R.id.seekBar);

            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (FrequencyAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }

        viewHolder.customizable.setText(dataModel.getName() + ", freq lvl: " + dataModel.getFrequency().getFrequency());
        viewHolder.seekBar.setProgress(dataModel.getFrequency().getFrequency());
        viewHolder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onStopTrackingTouch(SeekBar arg0)
            {

            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {

            }

            @Override
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2)
            {
                frequencies[position] = arg1;
                viewHolder.customizable.setText(dataModel.getName() + ", freq lvl: " + arg1);
            }
        });

        return convertView;
    }
}
