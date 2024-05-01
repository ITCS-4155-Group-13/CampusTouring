package com.example.campustouring;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import common.helpers.MarkerDbHelper;

public class CustomMarkerContract {
    MarkerDbHelper dbHelper;
    public CustomMarkerContract(Context context) {
        dbHelper = new MarkerDbHelper(context);
    }
    public static class MarkerEntry implements BaseColumns {
        public static final String TABLE_NAME = "CustomMarkers";
        public static final String COLUMN_NAME_LOCALINDEX = "localIndex";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_SHORTNAME = "shortName";
        public static final String COLUMN_NAME_LINK = "link";
        public static final String COLUMN_NAME_LAT = "lat";
        public static final String COLUMN_NAME_LONG = "long";
        public static final String COLUMN_NAME_ISDEFAULTMARKER = "isDefaultMarker";
    }
    public static class MarkerEntryObj {
        public MarkerEntryObj(String localIndex, String name, String shortName, String link, String latitude, String longitude){
            this.localIndex = localIndex;
            this.name = name;
            this.shortName = shortName;
            this.link = link;
            this.latitude = latitude;
            this.longitude = longitude;
        }
        public MarkerEntryObj(String localIndex, String name, String shortName, String link, String latitude, String longitude, String isDefaultMarker){
            this.localIndex = localIndex;
            this.name = name;
            this.shortName = shortName;
            this.link = link;
            this.latitude = latitude;
            this.longitude = longitude;
            this.isDefaultMarker = isDefaultMarker;
        }
        String localIndex;
        String name;
        String shortName;
        String link;
        String latitude;
        String longitude;
        String isDefaultMarker;
    }
    public void saveToDb(CustomMarkerContract.MarkerEntryObj marker){
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();

        // Check if the marker with the same local index already exists in the database
        HashMap<String, Object> existingMarker = readSingleFromDb(marker.localIndex);
        if (!existingMarker.isEmpty()) {
            // If the marker already exists, update its details instead of adding a new entry
            updateMarkerInDb(marker);
            return;
        }

        ContentValues values = new ContentValues();
        values.put(CustomMarkerContract.MarkerEntry.COLUMN_NAME_LOCALINDEX, marker.localIndex);
        values.put(CustomMarkerContract.MarkerEntry.COLUMN_NAME_NAME, marker.name);
        values.put(CustomMarkerContract.MarkerEntry.COLUMN_NAME_SHORTNAME, marker.shortName);
        values.put(CustomMarkerContract.MarkerEntry.COLUMN_NAME_LINK, marker.link);
        values.put(CustomMarkerContract.MarkerEntry.COLUMN_NAME_LAT, marker.latitude);
        values.put(CustomMarkerContract.MarkerEntry.COLUMN_NAME_LONG, marker.longitude);
        values.put(CustomMarkerContract.MarkerEntry.COLUMN_NAME_ISDEFAULTMARKER, 0);

        db.insert(CustomMarkerContract.MarkerEntry.TABLE_NAME, null, values);
    }
    public HashMap<String, Object> readSingleFromDb (String localIndex) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                CustomMarkerContract.MarkerEntry._ID,
                CustomMarkerContract.MarkerEntry.COLUMN_NAME_LOCALINDEX,
                CustomMarkerContract.MarkerEntry.COLUMN_NAME_NAME,
                CustomMarkerContract.MarkerEntry.COLUMN_NAME_SHORTNAME,
                CustomMarkerContract.MarkerEntry.COLUMN_NAME_LINK,
                CustomMarkerContract.MarkerEntry.COLUMN_NAME_LAT,
                CustomMarkerContract.MarkerEntry.COLUMN_NAME_LONG,
                CustomMarkerContract.MarkerEntry.COLUMN_NAME_ISDEFAULTMARKER
        };

        String selection = CustomMarkerContract.MarkerEntry.COLUMN_NAME_LOCALINDEX + " = ?";
        String[] selectionArgs = { localIndex };

        Cursor cursor = db.query(
                CustomMarkerContract.MarkerEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        HashMap<String, Object> marker = new HashMap<>();

        if (cursor.moveToNext()) {
            marker.put(CustomMarkerContract.MarkerEntry._ID, cursor.getLong(cursor.getColumnIndexOrThrow(CustomMarkerContract.MarkerEntry._ID)));
            marker.put(CustomMarkerContract.MarkerEntry.COLUMN_NAME_LOCALINDEX, cursor.getString(cursor.getColumnIndexOrThrow(CustomMarkerContract.MarkerEntry.COLUMN_NAME_LOCALINDEX)));
            marker.put(CustomMarkerContract.MarkerEntry.COLUMN_NAME_NAME, cursor.getString(cursor.getColumnIndexOrThrow(CustomMarkerContract.MarkerEntry.COLUMN_NAME_NAME)));
            marker.put(CustomMarkerContract.MarkerEntry.COLUMN_NAME_SHORTNAME, cursor.getString(cursor.getColumnIndexOrThrow(CustomMarkerContract.MarkerEntry.COLUMN_NAME_SHORTNAME)));
            marker.put(CustomMarkerContract.MarkerEntry.COLUMN_NAME_LINK, cursor.getString(cursor.getColumnIndexOrThrow(CustomMarkerContract.MarkerEntry.COLUMN_NAME_LINK)));
            marker.put(CustomMarkerContract.MarkerEntry.COLUMN_NAME_LAT, cursor.getString(cursor.getColumnIndexOrThrow(CustomMarkerContract.MarkerEntry.COLUMN_NAME_LAT)));
            marker.put(CustomMarkerContract.MarkerEntry.COLUMN_NAME_LONG, cursor.getString(cursor.getColumnIndexOrThrow(CustomMarkerContract.MarkerEntry.COLUMN_NAME_LONG)));
            marker.put(CustomMarkerContract.MarkerEntry.COLUMN_NAME_ISDEFAULTMARKER, cursor.getString(cursor.getColumnIndexOrThrow(CustomMarkerContract.MarkerEntry.COLUMN_NAME_ISDEFAULTMARKER)));
        }
        cursor.close();

        return marker;
    }
    public List<HashMap<String, Object>> readAllFromDb() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                CustomMarkerContract.MarkerEntry._ID,
                CustomMarkerContract.MarkerEntry.COLUMN_NAME_LOCALINDEX,
                CustomMarkerContract.MarkerEntry.COLUMN_NAME_NAME,
                CustomMarkerContract.MarkerEntry.COLUMN_NAME_SHORTNAME,
                CustomMarkerContract.MarkerEntry.COLUMN_NAME_LINK,
                CustomMarkerContract.MarkerEntry.COLUMN_NAME_LAT,
                CustomMarkerContract.MarkerEntry.COLUMN_NAME_LONG,
                CustomMarkerContract.MarkerEntry.COLUMN_NAME_ISDEFAULTMARKER
        };


        Cursor cursor = db.query(
                CustomMarkerContract.MarkerEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );
        List<HashMap<String, Object>> markerList = new ArrayList<>();

        while (cursor.moveToNext()) {
            HashMap<String, Object> marker = new HashMap<>();

            marker.put(CustomMarkerContract.MarkerEntry._ID, cursor.getLong(cursor.getColumnIndexOrThrow(CustomMarkerContract.MarkerEntry._ID)));
            marker.put(CustomMarkerContract.MarkerEntry.COLUMN_NAME_LOCALINDEX, cursor.getInt(cursor.getColumnIndexOrThrow(CustomMarkerContract.MarkerEntry.COLUMN_NAME_LOCALINDEX)));
            marker.put(CustomMarkerContract.MarkerEntry.COLUMN_NAME_NAME, cursor.getString(cursor.getColumnIndexOrThrow(CustomMarkerContract.MarkerEntry.COLUMN_NAME_NAME)));
            marker.put(CustomMarkerContract.MarkerEntry.COLUMN_NAME_SHORTNAME, cursor.getString(cursor.getColumnIndexOrThrow(CustomMarkerContract.MarkerEntry.COLUMN_NAME_SHORTNAME)));
            marker.put(CustomMarkerContract.MarkerEntry.COLUMN_NAME_LINK, cursor.getString(cursor.getColumnIndexOrThrow(CustomMarkerContract.MarkerEntry.COLUMN_NAME_LINK)));
            marker.put(CustomMarkerContract.MarkerEntry.COLUMN_NAME_LAT, cursor.getDouble(cursor.getColumnIndexOrThrow(CustomMarkerContract.MarkerEntry.COLUMN_NAME_LAT)));
            marker.put(CustomMarkerContract.MarkerEntry.COLUMN_NAME_LONG, cursor.getDouble(cursor.getColumnIndexOrThrow(CustomMarkerContract.MarkerEntry.COLUMN_NAME_LONG)));
            marker.put(CustomMarkerContract.MarkerEntry.COLUMN_NAME_ISDEFAULTMARKER, cursor.getInt(cursor.getColumnIndexOrThrow(CustomMarkerContract.MarkerEntry.COLUMN_NAME_ISDEFAULTMARKER)));

            markerList.add(marker);
        }
        cursor.close();

        return markerList;
    }
    public void deleteOneFromDb(String localIndex){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = CustomMarkerContract.MarkerEntry.COLUMN_NAME_LOCALINDEX + " = ?";
        String[] selectionArgs = { localIndex };

        db.delete(CustomMarkerContract.MarkerEntry.TABLE_NAME, selection, selectionArgs);
    }

    public void updateMarkerInDb(CustomMarkerContract.MarkerEntryObj marker) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CustomMarkerContract.MarkerEntry.COLUMN_NAME_NAME, marker.name);
        values.put(CustomMarkerContract.MarkerEntry.COLUMN_NAME_SHORTNAME, marker.shortName);
        values.put(CustomMarkerContract.MarkerEntry.COLUMN_NAME_LINK, marker.link);
        values.put(CustomMarkerContract.MarkerEntry.COLUMN_NAME_LAT, marker.latitude);
        values.put(CustomMarkerContract.MarkerEntry.COLUMN_NAME_LONG, marker.longitude);
        values.put(CustomMarkerContract.MarkerEntry.COLUMN_NAME_ISDEFAULTMARKER, marker.isDefaultMarker);

        String selection = CustomMarkerContract.MarkerEntry.COLUMN_NAME_LOCALINDEX + " = ?";
        String[] selectionArgs = { marker.localIndex };

        db.update(
                CustomMarkerContract.MarkerEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );
    }

    public void clearDb() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(MarkerEntry.TABLE_NAME, null, null);
    }
}