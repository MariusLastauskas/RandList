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
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<WishlistData> {

    private ArrayList<WishlistData> dataSet;
    Context mContext;

    private static class ViewHolder {
        TextView txtName;
        TextView txtDescription;
        ImageView image;
    }

    public CustomAdapter(ArrayList<WishlistData> data, Context context) {
        super(context, R.layout.activity_wish_list_item, data);
        this.dataSet = data;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        WishlistData dataModel = getItem(position);

        ViewHolder viewHolder;

        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.activity_wish_list_item, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.Name);
            viewHolder.txtDescription = (TextView) convertView.findViewById(R.id.description);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.image);

            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        viewHolder.txtName.setText(dataModel.getName());
        viewHolder.txtDescription.setText(dataModel.getDescription());
        if (dataModel.getImage() != null) {
            File imgFile = new File(dataModel.getImage());
            if (imgFile.exists()) {

                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(dataModel.getImage(), bmOptions);

                int scaleFactor = Math.min(10, 10);

                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = scaleFactor;
                bmOptions.inPurgeable = true;

                Bitmap bitmap = BitmapFactory.decodeFile(dataModel.image, bmOptions);
                Matrix matrix = new Matrix();

                try {
                    ExifInterface exif = new ExifInterface(dataModel.image);
                    Log.d("EXIF value ", exif.getAttribute(ExifInterface.TAG_ORIENTATION));
                    if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("6")) {
                        matrix.postRotate(90);
                    } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("8")) {
                        matrix.postRotate(270);
                    } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("3")) {
                        matrix.postRotate(180);
                    }
                } catch (IOException ex) {

                }

                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                viewHolder.image.setImageBitmap(bitmap);
                viewHolder.image.setImageBitmap(bitmap);
            }
        } else {
            viewHolder.image.setImageResource(R.drawable.default_image);
        }

        return convertView;
    }
}
