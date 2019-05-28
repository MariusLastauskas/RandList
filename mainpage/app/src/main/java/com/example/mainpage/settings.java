package com.example.mainpage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;

public class settings extends AppCompatActivity {

    List<String> spinnerList = new ArrayList<String>();
    ListView customizablesList;
    DatabaseHelper helper;
    static final String CUSTOM_FREQUENCING = "frequencing";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        helper = new DatabaseHelper(getApplicationContext());
        ArrayList<WishlistData> wishlistsArray = helper.getAll(helper.TABLE_WISHLISTS);
        ArrayList<WishlistData> itemsArray = helper.getAll(helper.TABLE_ITEM);
        ArrayList<FrequencyData> frequencyArray = helper.getAllFrequencies();

        for (WishlistData wishlist :
                wishlistsArray) {
            spinnerList.add(wishlist.name);
        }

        for (WishlistData item :
                itemsArray) {
            spinnerList.add(item.name);
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerList);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner sItems = (Spinner) findViewById(R.id.deleteSpinner);
        sItems.setAdapter(adapter);

        customizablesList=(ListView)findViewById(R.id.customizablesList);
        ArrayList<WishlistData> dataModels = helper.getAll(helper.TABLE_ITEM);

        final FrequencyAdapter frequencyAdapter = new FrequencyAdapter(dataModels, getApplicationContext());
        customizablesList.setAdapter(frequencyAdapter);

        Button deleteBtn = (Button) findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String wishlistName = sItems.getSelectedItem().toString();
                WishlistData wishlist = helper.get(wishlistName);
                if (wishlist != null) {
                    if (wishlist.getFk_parent() != -1) {
                        helper.deleteWishlist(wishlist, helper.TABLE_ITEM);
                    } else {
                        helper.deleteWishlist(wishlist, helper.TABLE_WISHLISTS);
                    }
                }
            }
        });

        Switch sw = findViewById(R.id.freqSwitch);
        SharedPreferences preferences = getSharedPreferences(CUSTOM_FREQUENCING, MODE_PRIVATE);
        if (preferences == null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(CUSTOM_FREQUENCING, false);
            editor.commit();
        }

        if (preferences.getBoolean(CUSTOM_FREQUENCING, false)) {
            sw.setChecked(true);
        }

        if (sw.isChecked()) {
            sw.setText("Custom frequencing is ON");
        } else {
            sw.setText("Custom frequencing is OFF");
        }
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                SharedPreferences preferences = getSharedPreferences(CUSTOM_FREQUENCING, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                if (isChecked) {
                    Switch sw = findViewById(R.id.freqSwitch);
                    sw.setText("Custom frequencing is ON");
                    editor.putBoolean(CUSTOM_FREQUENCING, true);
                } else {
                    Switch sw = findViewById(R.id.freqSwitch);
                    sw.setText("Custom frequencing is OFF");
                    editor.putBoolean(CUSTOM_FREQUENCING, false);
                }
                editor.commit();
            }
        });

        Button saveBtn = (Button) findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<Long, Integer> frequencies = new HashMap<>();
                SeekBar seekBarSeekBar;
                TextView nameTextView;
                for (int i = 0; i < customizablesList.getCount(); i++) {
                    v = customizablesList.getAdapter().getView(i, null, null);
                    View vg = ((ViewGroup)v).getChildAt(1);
                    nameTextView = (TextView) v.findViewById(R.id.customizable);
                    seekBarSeekBar = (SeekBar) v.findViewById(R.id.seekBar);
                    String name = nameTextView.getText().toString();
                    frequencies.put(helper.get(name.substring(0, name.lastIndexOf(','))).id,
//                            Integer.parseInt(name.substring(name.lastIndexOf(":") + 2 )));
                            frequencyAdapter.frequencies[i]);

                }
                helper.updateFrequencies(frequencies);
            }
        });
    }
}
