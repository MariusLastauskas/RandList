package com.example.mainpage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    ArrayList<WishlistData> dataModels;
    private static CustomAdapter adapter;

    ListView wishListsList;
    String[] wishListName = {"Books", "Board games", "Movies"};
    String[] wishListDescription = {"Wishlist for books of 2019", "What to pick for the evening?", "Havent seen yet!"};
    int[] wishListImages = {R.drawable.book, R.drawable.board_games, R.drawable.movies};
    DatabaseHelper databaseHelper;

    @Override
    protected void onResume() {
        super.onResume();
        wishListsList=(ListView)findViewById(R.id.wishListList);
        dataModels = new ArrayList<WishlistData>();
//        ArrayList<HashMap<String,String>> arrayList = new ArrayList<>();

        databaseHelper = new DatabaseHelper(getApplicationContext());
        ArrayList<WishlistData> wishlistData = databaseHelper.getAll(databaseHelper.TABLE_WISHLISTS);
        for (WishlistData d :
                wishlistData) {
            dataModels.add(new WishlistData(d.id, d.name, d.description, d.image));
        }

        adapter = new CustomAdapter(dataModels, getApplicationContext());

        wishListsList.setAdapter(adapter);

        wishListsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), WISHLIST.class);
                long x = dataModels.get(position).id;
                intent.putExtra("LIST_ID", dataModels.get(position).id);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        wishListsList=(ListView)findViewById(R.id.wishListList);

        dataModels = new ArrayList<WishlistData>();
//        ArrayList<HashMap<String,String>> arrayList = new ArrayList<>();

        databaseHelper = new DatabaseHelper(getApplicationContext());
        ArrayList<WishlistData> wishlistData = databaseHelper.getAll(databaseHelper.TABLE_WISHLISTS);
        for (WishlistData d :
                wishlistData) {
            dataModels.add(new WishlistData(d.id, d.name, d.description, d.image));
        }

        adapter = new CustomAdapter(dataModels, getApplicationContext());

        wishListsList.setAdapter(adapter);

        wishListsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), WISHLIST.class);
                long x = dataModels.get(position).id;
                intent.putExtra("LIST_ID", dataModels.get(position).id);
                startActivity(intent);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WishListCreate.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Intent intent = new Intent(getApplicationContext(), settings.class);
        startActivity(intent);
        return true;
    }
}
