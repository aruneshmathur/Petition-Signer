package com.android.petition.db;

import java.io.Serializable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

import redstone.xmlrpc.XmlRpcClient;
import redstone.xmlrpc.XmlRpcException;
import redstone.xmlrpc.XmlRpcFault;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.widget.Toast;

public class Petition_Details_db implements Serializable {

	public static int DB_VERSION = 1;

	public static String DB_NAME = "petition.db";
	public static String TABLE_NAME1 = "newpetition";
	public static String TABLE_NAME2 = "newsignee";
	// public static String KEY_USER_ID = "userID"; Implement this as a
	// SharedPreference
	public static String KEY_PETITION_ID = "petitionId";
	public static String KEY_PETITION_TITLE = "petitionTitle";
	public static String KEY_PETITION_SUMMARY = "petitionSummary";
	public static String KEY_PETITION_WEB = "petitionWeb";
	public static String KEY_PETITION_COUNTRY = "petitionCountry";
	public static String KEY_PETITION_SIGNED = "petitionSigned";
	public static String KEY_PETITION_CREATED = "petitionCreated";
	public static String KEY_PETITION_SYNCED = "petitionSynced";

	public static String KEY_PETITION_SIGNEE_ID = "signeeID";
	public static String KEY_PETITION_SIGNEE_NAME = "signeeName";
	public static String KEY_PETITION_SIGNEE_IMPORTANCE = "signeeImportance";
	public static String KEY_PETITION_SIGNEE_EMAIL = "signeemail";
	public static String KEY_PETITION_SIGNEE_CONTACT = "signeeContact";
	public static String KEY_PETITION_SIGNEE_SIGNATURE = "signeeSignature";
	public static String KEY_PETITION_SIGNEE_SYNCED = "signeeSynced";

	private static String DB_CREATE1 = "CREATE TABLE " + TABLE_NAME1 + " ( "
			+ KEY_PETITION_ID + " INTEGER PRIMARY KEY, " + KEY_PETITION_TITLE
			+ " TEXT NOT NULL, " + KEY_PETITION_SUMMARY + " TEXT NOT NULL, "
			+ KEY_PETITION_WEB + " TEXT, " + KEY_PETITION_COUNTRY + " TEXT, "
			+ KEY_PETITION_SIGNED + " TEXT, " + KEY_PETITION_CREATED
			+ " STRING, " + KEY_PETITION_SYNCED + " TEXT" + ");";

	private static String DB_CREATE2 = "CREATE TABLE " + TABLE_NAME2 + " ( "
			+ KEY_PETITION_ID + " TEXT, " + KEY_PETITION_SIGNEE_ID + " TEXT, "
			+ KEY_PETITION_SIGNEE_NAME + " TEXT NOT NULL, "
			+ KEY_PETITION_SIGNEE_IMPORTANCE + " TEXT, "
			+ KEY_PETITION_SIGNEE_EMAIL + " TEXT, "
			+ KEY_PETITION_SIGNEE_SIGNATURE + " BLOB, "
			+ KEY_PETITION_SIGNEE_CONTACT + " TEXT, "
			+ KEY_PETITION_SIGNEE_SYNCED + ");";

	private SQLiteDatabase db;
	private final Context ctx;
	private myDbHelper dbHelper;

	public Petition_Details_db(Context ctx) {
		this.ctx = ctx;
		dbHelper = new myDbHelper(ctx, DB_NAME, null, DB_VERSION);
	}

	public Petition_Details_db open() throws SQLException {
		db = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		db.close();
	}

	public void insertPetition(HashMap<String, String> map) {

		String query_frame = "INSERT INTO " + TABLE_NAME1 + " ( ";
		String query_values = "";

		for (String string : map.keySet()) {
			query_frame += (string + ',');
			query_values += ("'" + (map.get(string)) + "'" + ",");
		}

		query_frame = query_frame.substring(0, query_frame.length() - 1);
		query_frame += " ) " + " VALUES ( "
				+ query_values.substring(0, query_values.length() - 1) + ");";

		db.execSQL(query_frame);

	}

	public ArrayList<HashMap<String, String>> getPetitions() {

		HashMap<String, String> returnMap = null;
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

		String query_get = "SELECT " + KEY_PETITION_ID + " , "
				+ KEY_PETITION_TITLE + " , " + KEY_PETITION_SIGNED + " , "
				+ KEY_PETITION_CREATED + " , " + KEY_PETITION_SYNCED + " , "
				+ KEY_PETITION_SUMMARY + " , " + KEY_PETITION_WEB + " , "
				+ KEY_PETITION_COUNTRY + " FROM " + TABLE_NAME1 + ";";
		Cursor cursor = db.rawQuery(query_get, null);

		while (cursor.moveToNext()) {
			returnMap = new HashMap<String, String>();
			returnMap.put(KEY_PETITION_ID, cursor.getString(0));
			returnMap.put(KEY_PETITION_TITLE, cursor.getString(1));
			returnMap.put(KEY_PETITION_SIGNED, cursor.getString(2));
			returnMap.put(KEY_PETITION_CREATED, cursor.getString(3));
			returnMap.put(KEY_PETITION_SYNCED, cursor.getString(4));
			returnMap.put(KEY_PETITION_SUMMARY, cursor.getString(5));
			returnMap.put(KEY_PETITION_WEB, cursor.getString(6));
			returnMap.put(KEY_PETITION_COUNTRY, cursor.getString(7));

			list.add(returnMap);
		}

		cursor.close();
		return list;
	}

	public void insertSignee(HashMap<String, Object> map) {
		String query_frame = "INSERT INTO " + TABLE_NAME2 + " ( ";
		String query_values = "";
		byte[] signature = null;

		for (String string : map.keySet()) {

			if (string
					.equals(Petition_Details_db.KEY_PETITION_SIGNEE_SIGNATURE)) {
				signature = (byte[]) map.get(string);
				continue;
			}
			query_frame += (string + ',');
			query_values += ("'" + ((String) map.get(string)) + "'" + ",");
		}

		query_frame = query_frame.substring(0, query_frame.length() - 1);
		query_frame += " ) " + " VALUES ( "
				+ query_values.substring(0, query_values.length() - 1) + ");";

		db.execSQL(query_frame);

		// Now insert the signature
		db.execSQL("UPDATE " + TABLE_NAME2 + " SET "
				+ KEY_PETITION_SIGNEE_SIGNATURE + " = ? WHERE "
				+ KEY_PETITION_SIGNEE_ID + " = ?", new Object[] { signature,
				map.get(KEY_PETITION_SIGNEE_ID) });

		String query_get = "SELECT COUNT(" + KEY_PETITION_ID + ") FROM "
				+ TABLE_NAME2 + " WHERE " + KEY_PETITION_ID + " = "
				+ map.get(KEY_PETITION_ID) + ";";

		Cursor cursor1 = db.rawQuery(query_get, null);
		cursor1.moveToNext();
		String s = cursor1.getString(0);
		cursor1.close();

		String count_query = "UPDATE " + TABLE_NAME1 + " SET "
				+ KEY_PETITION_SIGNED + " = " + s + " , " + KEY_PETITION_SYNCED
				+ " = 0" + " WHERE " + KEY_PETITION_ID + " = "
				+ map.get(KEY_PETITION_ID);
		db.execSQL(count_query);

	}

	public ArrayList<HashMap<String, String>> getSigneeList(String pid) {
		HashMap<String, String> returnMap = null;
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

		String query_get = "SELECT " + KEY_PETITION_SIGNEE_ID + " , "
				+ KEY_PETITION_SIGNEE_NAME + " , "
				+ KEY_PETITION_SIGNEE_IMPORTANCE + " , "
				+ KEY_PETITION_SIGNEE_EMAIL + " , "
				+ KEY_PETITION_SIGNEE_CONTACT + " FROM " + TABLE_NAME2
				+ " WHERE " + KEY_PETITION_ID + " = " + pid;
		Cursor cursor = db.rawQuery(query_get, null);

		while (cursor.moveToNext()) {
			returnMap = new HashMap<String, String>();
			returnMap.put(KEY_PETITION_SIGNEE_ID, cursor.getString(0));
			returnMap.put(KEY_PETITION_SIGNEE_NAME, cursor.getString(1));
			returnMap.put(KEY_PETITION_SIGNEE_IMPORTANCE, cursor.getString(2));
			returnMap.put(KEY_PETITION_SIGNEE_EMAIL, cursor.getString(3));
			returnMap.put(KEY_PETITION_SIGNEE_CONTACT, cursor.getString(4));
			list.add(returnMap);
		}

		cursor.close();
		return list;
	}

	public ArrayList<HashMap<String, Object>> getSigneesForUpload(String pid) {
		HashMap<String, Object> returnMap = null;
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

		// Need to improve this query
		String query_get = "SELECT " + KEY_PETITION_ID + " , "
				+ KEY_PETITION_SIGNEE_ID + " , " + KEY_PETITION_SIGNEE_NAME
				+ " , " + KEY_PETITION_SIGNEE_IMPORTANCE + " , "
				+ KEY_PETITION_SIGNEE_EMAIL + " , "
				+ KEY_PETITION_SIGNEE_CONTACT + " , "
				+ KEY_PETITION_SIGNEE_SIGNATURE + " , "
				+ KEY_PETITION_SIGNEE_SYNCED + " FROM " + TABLE_NAME2
				+ " WHERE " + KEY_PETITION_ID + " = " + pid + " ;";
		Cursor cursor = db.rawQuery(query_get, null);

		while (cursor.moveToNext()) {
			returnMap = new HashMap<String, Object>();

			returnMap.put(KEY_PETITION_ID, "aruneshmathur1990@gmail.com" + pid);
			returnMap.put(KEY_PETITION_SIGNEE_NAME, cursor.getString(2));
			returnMap.put(KEY_PETITION_SIGNEE_IMPORTANCE, cursor.getString(3));
			returnMap.put(KEY_PETITION_SIGNEE_EMAIL, cursor.getString(4));
			returnMap.put(KEY_PETITION_SIGNEE_CONTACT, cursor.getString(5));

			// byte[] signature = cursor.getBlob(6);
			returnMap.put(KEY_PETITION_SIGNEE_SYNCED, cursor.getString(7));
			returnMap.put(KEY_PETITION_SIGNEE_ID, cursor.getString(1));

			if (returnMap.get(KEY_PETITION_SIGNEE_SYNCED).equals("0"))
				list.add(returnMap);
		}

		cursor.close();
		return list;
	}

	public HashMap<String, String> getSignee(String pid, String signeeId) {
		HashMap<String, String> returnMap = null;

		String query_get = "SELECT " + KEY_PETITION_SIGNEE_ID + " , "
				+ KEY_PETITION_SIGNEE_NAME + " , "
				+ KEY_PETITION_SIGNEE_IMPORTANCE + " , "
				+ KEY_PETITION_SIGNEE_EMAIL + " , "
				+ KEY_PETITION_SIGNEE_CONTACT + " FROM " + TABLE_NAME2
				+ " WHERE " + KEY_PETITION_ID + " = " + pid + " AND "
				+ KEY_PETITION_SIGNEE_ID + " = " + signeeId;
		Cursor cursor = db.rawQuery(query_get, null);

		cursor.moveToNext();
		returnMap = new HashMap<String, String>();
		returnMap.put(KEY_PETITION_SIGNEE_ID, cursor.getString(0));
		returnMap.put(KEY_PETITION_SIGNEE_NAME, cursor.getString(1));
		returnMap.put(KEY_PETITION_SIGNEE_IMPORTANCE, cursor.getString(2));
		returnMap.put(KEY_PETITION_SIGNEE_EMAIL, cursor.getString(3));
		returnMap.put(KEY_PETITION_SIGNEE_CONTACT, cursor.getString(4));

		cursor.close();
		return returnMap;
	}

	public HashMap<String, String> getSigneeStatus(String pid) {
		String query = "SELECT " + KEY_PETITION_SIGNED + " , "
				+ KEY_PETITION_SYNCED + " FROM " + TABLE_NAME1 + " WHERE "
				+ KEY_PETITION_ID + " = " + pid;
		Cursor cursor = db.rawQuery(query, null);
		cursor.moveToNext();
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Petition_Details_db.KEY_PETITION_SIGNED, cursor.getString(0));
		map.put(Petition_Details_db.KEY_PETITION_SYNCED, cursor.getString(1));
		cursor.close();
		return map;
	}

	public void deletePetition(String id) {
		id = (id.contains("aruneshmathur1990@gmail.com") ? id.substring(id
				.indexOf("com") + 3) : id);
		String query1 = "DELETE FROM " + TABLE_NAME1 + " WHERE "
				+ KEY_PETITION_ID + " = " + id;
		String query2 = "DELETE FROM " + TABLE_NAME2 + " WHERE "
				+ KEY_PETITION_ID + " = " + id;
		db.execSQL(query1);
		db.execSQL(query2);
	}

	public void deleteSignee(String id) {

		String query_get = "SELECT " + KEY_PETITION_ID + " FROM " + TABLE_NAME2
				+ " WHERE " + KEY_PETITION_SIGNEE_ID + " = " + id + ";";

		Cursor cursor1 = db.rawQuery(query_get, null);
		cursor1.moveToNext();
		String s = cursor1.getString(0);
		cursor1.close();

		query_get = "SELECT " + KEY_PETITION_SIGNED + " FROM " + TABLE_NAME1
				+ " WHERE " + KEY_PETITION_ID + " = " + s + ";";

		cursor1 = db.rawQuery(query_get, null);
		cursor1.moveToNext();
		String s1 = cursor1.getString(0);
		cursor1.close();

		String count = String.valueOf(Integer.parseInt(s1) - 1);
		String count_query = "UPDATE " + TABLE_NAME1 + " SET "
				+ KEY_PETITION_SIGNED + " = " + count + " WHERE "
				+ KEY_PETITION_ID + " = " + s;
		db.execSQL(count_query);

		String query = "DELETE FROM " + TABLE_NAME2 + " WHERE "
				+ KEY_PETITION_SIGNEE_ID + " = " + id;
		db.execSQL(query);

	}

	public void setPetitionStatus(String pid) {
		String sql = "UPDATE " + TABLE_NAME1 + " SET " + KEY_PETITION_CREATED
				+ " = " + "1" + " WHERE " + KEY_PETITION_ID + " = "
				+ pid.substring(pid.indexOf("com") + 3) + " ;";
		db.execSQL(sql);
	}

	public void setSigneeStatus(String sid) {
		String sql = "UPDATE " + TABLE_NAME2 + " SET "
				+ KEY_PETITION_SIGNEE_SYNCED + " = " + "1" + " WHERE "
				+ KEY_PETITION_SIGNEE_ID + " = " + sid + ";";
		db.execSQL(sql);
	}

	private static class myDbHelper extends SQLiteOpenHelper {

		public myDbHelper(Context context, String name, CursorFactory factory,
				int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DB_CREATE1);
			db.execSQL(DB_CREATE2);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME1);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME2);
			onCreate(db);
		}

	}

}
