package com.example.mainpage;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class RandomResult extends AppCompatActivity {
    static final String CUSTOM_FREQUENCING = "frequencing";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_result);
        Bundle extrasBundle = getIntent().getExtras();
        long wishlistId = extrasBundle.getLong("WISHLIST_ID");
        DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
        ArrayList<HashMap<String,String>> arrayList = helper.getListItems(wishlistId);
        Random rnd = new Random();
        int arrayCount = arrayList.size();
        SharedPreferences preferences = getSharedPreferences(CUSTOM_FREQUENCING, MODE_PRIVATE);
        int r;
        if (preferences == null) {
            r = rnd.nextInt(arrayCount);
        } else {
            if (preferences.getBoolean(CUSTOM_FREQUENCING, false)) {
                int count = 0;
                ArrayList<FrequencyData> freq = new ArrayList<>();
                for (HashMap<String, String> x : arrayList) {
                    long id = Long.parseLong(x.get(helper.KEY_ID));
                    FrequencyData fd = helper.getFrequencyByFK(id);
                    freq.add(fd);
                }
//                ArrayList<FrequencyData> freq = helper.getAllFrequencies();
                for (FrequencyData f:
                        freq) {
                    count += f.frequency;
                }
                r = rnd.nextInt(count);
                int i = 0;
                count = 0;
                for (FrequencyData f :
                        freq) {
                    count += f.frequency;
                    if (r <= count) {
                        r = i;
                        break;
                    }
                    i++;
                }
                if (r >= arrayList.size()) {
                    r = arrayList.size() - 1;
                }
            } else {
                r = rnd.nextInt(arrayCount);
            }
        }
        HashMap<String, String> result = arrayList.get(r);

        setTitle("Your random result!");
        ImageView image = findViewById(R.id.randomResultImage);
        TextView name = findViewById(R.id.randomResultName);
        TextView description = findViewById(R.id.randomResultDescription);

        name.setText(result.get("Name"));
        description.setText(result.get("Description"));
        if (result.get("Image") != null) {
            File imgFile = new File(result.get("Image"));
            if (imgFile.exists()) {

                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(result.get("Image"), bmOptions);

                int scaleFactor = Math.min(10, 10);

                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = scaleFactor;
                bmOptions.inPurgeable = true;

                Bitmap bitmap = BitmapFactory.decodeFile(result.get("Image"), bmOptions);
                Matrix matrix = new Matrix();

                try {
                    ExifInterface exif = new ExifInterface(result.get("Image"));
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
                image.setImageBitmap(bitmap);
                image.setImageBitmap(bitmap);
            }
        } else {
            image.setImageResource(R.drawable.default_image);
        }
    }
}