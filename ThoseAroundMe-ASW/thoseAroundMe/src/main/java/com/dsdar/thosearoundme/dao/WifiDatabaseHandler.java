package com.dsdar.thosearoundme.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.dsdar.thosearoundme.dto.Wifi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * MamaBear class for database handler to insert location records
 * 
 * @author senthil_kumaran
 */
public class WifiDatabaseHandler extends SQLiteOpenHelper implements
		Serializable {

	private static final long serialVersionUID = 1L;

	private static final int DATABASE_VERSION = 1; // Database Version

	private static final String DATABASE_NAME = "WifiLocTracker"; // Database
																	// Name

	private static final String TABLE_WIFI = "wifi"; // table name

	private static final String KEY_BSSID = "bssid";
	private static final String KEY_NAME = "name";
	private static final String KEY_LAT = "lat";
	private static final String KEY_LNG = "lng";
	private static final String KEY_ACCURACY = "accuracy";

	public WifiDatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_WIFI_TABLE = "CREATE TABLE " + TABLE_WIFI + "("
				+ KEY_BSSID + " TEXT," + KEY_NAME + " TEXT," + KEY_LAT
				+ " TEXT," + KEY_LNG + " TEXT," + KEY_ACCURACY + " TEXT" + ")";
		db.execSQL(CREATE_WIFI_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_WIFI);
		// Create tables again
		onCreate(db);
	}

	/**
	 * Inserts new record
	 */
	public void insertLocation(Wifi theWifiLocation) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_BSSID, theWifiLocation.getBssid());
		values.put(KEY_NAME, theWifiLocation.getName());
		values.put(KEY_LAT, theWifiLocation.getLat());
		values.put(KEY_LNG, theWifiLocation.getLng());
		values.put(KEY_ACCURACY, theWifiLocation.getAccuracy());

		// Inserting Row
		db.insert(TABLE_WIFI, null, values);
	}

	/**
	 * Update a record by BSSID
	 * 
	 * @param theWifiLocation
	 */
	public void updateLocation(Wifi theWifiLocation) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_LAT, theWifiLocation.getLat());
		values.put(KEY_LNG, theWifiLocation.getLng());
		values.put(KEY_ACCURACY, theWifiLocation.getAccuracy());

		// Inserting Row
		db.update(TABLE_WIFI, values, KEY_BSSID + "=?",
				new String[] { theWifiLocation.getBssid() });
	}

	/**
	 * Clears the table
	 */
	public void clearLocation() {
		SQLiteDatabase db = this.getWritableDatabase();
		String deleteQuery = "DELETE FROM " + TABLE_WIFI;
		db.execSQL(deleteQuery);
	}

	/**
	 * Returns all the records from the table
	 * 
	 * @return
	 */
	public List<Wifi> getAllWifiLocation() {
		List<Wifi> aWifilocationtList = new ArrayList<Wifi>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_WIFI;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Wifi aWifiLocation = new Wifi();
				aWifiLocation.setBssid(cursor.getString(0));
				aWifiLocation.setName(cursor.getString(1));
				aWifiLocation.setLat(cursor.getString(2));
				aWifiLocation.setLng(cursor.getString(3));
				aWifiLocation.setAccuracy(cursor.getString(4));
				aWifilocationtList.add(aWifiLocation);
			} while (cursor.moveToNext());
		}
		cursor.close();

		// return contact list
		return aWifilocationtList;
	}

	public Wifi getAssociatedWifi(String theWifiId) {
		Wifi aWifiLocation = null;

		String selectQuery = "SELECT  * FROM " + TABLE_WIFI + " WHERE "
				+ KEY_BSSID + " = '" + theWifiId + "'";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			aWifiLocation = new Wifi();
			aWifiLocation.setBssid(cursor.getString(0));
			aWifiLocation.setName(cursor.getString(1));
			aWifiLocation.setLat(cursor.getString(2));
			aWifiLocation.setLng(cursor.getString(3));
			aWifiLocation.setAccuracy(cursor.getString(4));
		}
		cursor.close();

		return aWifiLocation;
	}
}