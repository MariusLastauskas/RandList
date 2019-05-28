package com.example.mainpage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.FontRequest;
import android.util.ArrayMap;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 19;
    private static final String DATABASE_NAME = "wishlistManager";
    public static final String TABLE_WISHLISTS = "wishlists";
    public static final String TABLE_ITEM = "wishlistItem";
    public static final String TABLE_FREQUENCY = "frequency";

    public static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_IMAGE_ID = "imageId";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_FK_PARENT = "fk_parent";
    private static final String KEY_FK_ITEM = "fk_item";
    private static final String KEY_FREQUENCY = "frequency";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_WISHLIST_TABLE = "CREATE TABLE " + TABLE_WISHLISTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_NAME + " TEXT, "
                + KEY_DESCRIPTION + " TEXT, "
                + KEY_IMAGE_ID + " TEXT"
                + ")";
        String CREATE_ITEM_TABLE = "CREATE TABLE " + TABLE_ITEM + "("
                + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_NAME + " TEXT, "
                + KEY_DESCRIPTION + " TEXT, "
                + KEY_IMAGE_ID + " TEXT, "
                + KEY_FK_PARENT + " INTEGER"
                + ")";
        String CREATE_FREQUENCY_TABLE = "CREATE TABLE " + TABLE_FREQUENCY + "("
                + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_FK_ITEM + " INTEGER, "
                + KEY_FREQUENCY + " INTEGER"
                + ")";
        Log.d("Database", CREATE_WISHLIST_TABLE);
        Log.d("Database", CREATE_ITEM_TABLE);
        Log.d("Database", CREATE_FREQUENCY_TABLE);
        db.execSQL(CREATE_WISHLIST_TABLE);
        db.execSQL(CREATE_ITEM_TABLE);
        db.execSQL(CREATE_FREQUENCY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WISHLISTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FREQUENCY);
        onCreate(db);
    }

    @Override
    public void onDowngrade (SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public long add(WishlistData wishlistData, String table) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, wishlistData.getName());
        values.put(KEY_DESCRIPTION, wishlistData.getDescription());
        values.put(KEY_IMAGE_ID, wishlistData.getImage());

        if (wishlistData.getFk_parent() != -1) {
            values.put(KEY_FK_PARENT, wishlistData.getFk_parent());
        }

        long idValue = db.insert(table, null, values);
        db.close();
        return idValue;
    }

    private void initFrequencies() {
        ArrayList<HashMap<String, String>> itemsList = new ArrayList<HashMap<String, String>>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT " + KEY_ID + " FROM " + TABLE_ITEM;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                addFrequency(cursor.getLong(0), 1);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
    }

    public long addFrequency(long itemId, int frequency) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FK_ITEM, itemId);
        values.put(KEY_FREQUENCY, frequency);

        long idValue = db.insert(TABLE_FREQUENCY, null, values);
        db.close();
        return idValue;
    }

    public void deleteFrequency(long itemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FREQUENCY, KEY_FK_ITEM + "=?",
                new String[] { itemId + "" });
        db.close();
    }

    public void updateFrequencies(HashMap<Long, Integer> frequencies) {
        for (HashMap.Entry<Long, Integer> frequency: frequencies.entrySet()) {
            if (frequency.getValue() != null) {
                deleteFrequency(frequency.getKey());
                addFrequency(frequency.getKey(), frequency.getValue());
            }
        }
    }

    public WishlistData get(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        WishlistData wishlistData = null;
        Cursor cursor;
        cursor = db.query(TABLE_WISHLISTS, new String[] {KEY_ID, KEY_NAME, KEY_DESCRIPTION, KEY_IMAGE_ID},
                KEY_NAME + "=?", new String[] {name}, null, null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            wishlistData = new WishlistData(Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1), cursor.getString(2), cursor.getString(3));
        }
        else {
            cursor.close();
            cursor = db.query(TABLE_ITEM, new String[] {KEY_ID, KEY_NAME, KEY_DESCRIPTION, KEY_IMAGE_ID, KEY_FK_PARENT},
                    KEY_NAME + "=?", new String[] {String.valueOf(name)}, null, null, null, null);
            if (cursor.getCount() != 0) {
                cursor.moveToFirst();
                wishlistData = new WishlistData(Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1), cursor.getString(2), cursor.getString(3), Integer.parseInt(cursor.getString(4)), getFrequencyByFK(Integer.parseInt(cursor.getString(0))));
            }
        }
        cursor.close();
        db.close();
        return wishlistData;
    }

    public WishlistData get(long id, String table) {
        SQLiteDatabase db = this.getReadableDatabase();
        WishlistData wishlistData;
        Cursor cursor;

        if (table == TABLE_WISHLISTS) {
            cursor = db.query(TABLE_WISHLISTS, new String[] {KEY_ID, KEY_NAME, KEY_DESCRIPTION, KEY_IMAGE_ID},
                    KEY_ID + "=?", new String[] {String.valueOf(id)}, null, null, null, null);
        } else {
            cursor = db.query(TABLE_ITEM, new String[] {KEY_ID, KEY_NAME, KEY_DESCRIPTION, KEY_IMAGE_ID, KEY_FK_PARENT},
                    KEY_ID + "=?", new String[] {String.valueOf(id)}, null, null, null, null);
        }
        if (cursor != null)
            cursor.moveToFirst();

        if (table == TABLE_WISHLISTS) {
            wishlistData = new WishlistData(Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1), cursor.getString(2), cursor.getString(3));
        }
        else {
            wishlistData = new WishlistData(Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1), cursor.getString(2), cursor.getString(3), Integer.parseInt(cursor.getString(4)), getFrequencyByFK(Integer.parseInt(cursor.getString(0))));
        }
        cursor.close();
        db.close();
        return wishlistData;
    }

    public ArrayList<HashMap<String, String>> getListItems(long id) {
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_ITEM + " WHERE " + KEY_FK_PARENT + " = ? ";

        Cursor cursor = db.query(TABLE_ITEM, new String[] {KEY_ID, KEY_NAME, KEY_DESCRIPTION, KEY_IMAGE_ID, KEY_FK_PARENT},
                KEY_FK_PARENT + "=?", new String[] {String.valueOf(id)}, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("id", cursor.getString((0)));
                hashMap.put("Name", cursor.getString(1));
                hashMap.put("Description", cursor.getString(2));
                hashMap.put("Image", cursor.getString(3));
                arrayList.add(hashMap);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return arrayList;
    }

//    public ArrayList<Long> getWishlistsIds() {
//        ArrayList<Long> wishlistList = new ArrayList<WishlistData>();
//
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        String selectQuery = "SELECT * FROM " + TABLE_WISHLISTS + " ORDER BY " + KEY_ID + " DESC";
//
//        Cursor cursor = db.rawQuery(selectQuery, null);
//        if (cursor.moveToFirst()) {
//            do {
//                if (table == TABLE_WISHLISTS) {
//                    WishlistData wishlist = new WishlistData(
//                            Integer.parseInt(cursor.getString(0)),
//                            cursor.getString(1),
//                            cursor.getString(2),
//                            cursor.getString(3));
//                    wishlistList.add(wishlist);
//                } else {
//                    WishlistData wishlist = new WishlistData(
//                            Integer.parseInt(cursor.getString(0)),
//                            cursor.getString(1),
//                            cursor.getString(2),
//                            cursor.getString(3),
//                            Integer.parseInt(cursor.getString(4)));
//                    wishlistList.add(wishlist);
//                }
//            } while (cursor.moveToNext());
//        }
//
//        cursor.close();
//        db.close();
//    }

    public ArrayList<WishlistData> getAll(String table) {
        ArrayList<WishlistData> wishlistList = new ArrayList<WishlistData>();

        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM " + table + " ORDER BY " + KEY_ID + " DESC";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                if (table == TABLE_WISHLISTS) {
                    WishlistData wishlist = new WishlistData(
                            Integer.parseInt(cursor.getString(0)),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3));
                    wishlistList.add(wishlist);
                } else {
                    WishlistData wishlist = new WishlistData(
                            Integer.parseInt(cursor.getString(0)),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            Integer.parseInt(cursor.getString(4)),
                            getFrequencyByFK(Integer.parseInt(cursor.getString(0))));
                    wishlistList.add(wishlist);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return wishlistList;
    }

    public FrequencyData getFrequencyByFK(long itemId) {
        SQLiteDatabase db = this.getReadableDatabase();
        FrequencyData frequencyData;
        Cursor cursor;

        cursor = db.query(TABLE_FREQUENCY, new String[] {KEY_ID, KEY_FK_ITEM, KEY_FREQUENCY},
                KEY_FK_ITEM + "=?", new String[] {String.valueOf(itemId)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        frequencyData = new FrequencyData(Integer.parseInt(cursor.getString(0)),
                Integer.parseInt(cursor.getString(1)),Integer.parseInt(cursor.getString(2)));


        cursor.close();
        db.close();
        return frequencyData;
    }

    public ArrayList<FrequencyData> getAllFrequencies() {
        ArrayList<FrequencyData> frequencies = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_FREQUENCY + " ORDER BY " + KEY_ID + " ASC";
        Cursor cursor = db.rawQuery(selectQuery, null);if (cursor.moveToFirst()) {
            do {
                    FrequencyData freq = new FrequencyData(
                            Integer.parseInt(cursor.getString(0)),
                            Integer.parseInt(cursor.getString(1)),
                            Integer.parseInt(cursor.getString(2)));
                    frequencies.add(freq);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return frequencies;
    }

    public void deleteWishlist(WishlistData wishlist, String table) {
      SQLiteDatabase db = this.getWritableDatabase();
      if (table == TABLE_WISHLISTS) {
          db.delete(TABLE_ITEM, KEY_FK_PARENT + "=?",
                  new String[] {String.valueOf(wishlist.getId())});
          db.delete(TABLE_WISHLISTS, KEY_ID + "=?",
                  new String[] {String.valueOf(wishlist.getId())});
      }
      else {
          db.delete(TABLE_ITEM, KEY_ID + "=?",
                  new String[] {String.valueOf(wishlist.getId())});
      }
      db.close();
    }

//    public void updateWishlist(WishlistData wishlist) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(KEY_ID, wishlist.getId());
//        values.put(KEY_NAME, wishlist.getName());
//        values.put(KEY_DESCRIPTION, wishlist.getDescription());
//        values.put(KEY_IMAGE_ID, wishlist.getImage());
//
//        Log.d("Values", values.toString());
//        db.update(TABLE_WISHLISTS, values, KEY_ID + "= ?",
//                new String[] { String.valueOf(wishlist.getId()) });
//        db.close();
//    }
}
