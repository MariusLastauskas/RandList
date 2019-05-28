package com.example.mainpage;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class WISHLIST extends AppCompatActivity {
    ListView wishList;
    DatabaseHelper databaseHelper;
    private static CustomAdapter adapter;

    @Override
    protected void onResume() {
        super.onResume();
        Bundle extrasBundle = getIntent().getExtras();
        final Long wishListID = extrasBundle.getLong("LIST_ID");
        wishList = (ListView)findViewById(R.id.wishListView);

        databaseHelper = new DatabaseHelper(getApplicationContext());
        ArrayList<HashMap<String,String>> arrayList = databaseHelper.getListItems(wishListID);
        ArrayList<WishlistData> dataModels = new ArrayList<WishlistData>();
        for (HashMap<String, String> item: arrayList) {
            WishlistData d = new WishlistData(-1, item.get("Name"), item.get("Description"), item.get("Image"));
            dataModels.add(d);
        }

        CustomAdapter adapter = new CustomAdapter(dataModels, getApplicationContext());
        wishList.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        Bundle extrasBundle = getIntent().getExtras();
        final Long wishListID = extrasBundle.getLong("LIST_ID");

        wishList = (ListView)findViewById(R.id.wishListView);

        databaseHelper = new DatabaseHelper(getApplicationContext());
        ArrayList<HashMap<String,String>> arrayList = databaseHelper.getListItems(wishListID);
        ArrayList<WishlistData> dataModels = new ArrayList<WishlistData>();
        for (HashMap<String, String> item: arrayList) {
            WishlistData d = new WishlistData(-1, item.get("Name"), item.get("Description"), item.get("Image"));
            dataModels.add(d);
        }

        CustomAdapter adapter = new CustomAdapter(dataModels, getApplicationContext());
        wishList.setAdapter(adapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button addBtn = (Button) findViewById(R.id.addItemBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WishListCreate.class);
                intent.putExtra("WISHLIST_ID", wishListID);
                startActivity(intent);
            }
        });

        Button rndBtn = (Button) findViewById(R.id.getRndBtn);
        rndBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RandomResult.class);
                intent.putExtra("WISHLIST_ID", wishListID);
                startActivity(intent);
            }
        });

        setTitle("Custom wish list");
    }
}
