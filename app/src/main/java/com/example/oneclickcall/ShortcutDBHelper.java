package com.example.oneclickcall;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ShortcutDBHelper extends SQLiteOpenHelper {
	private static final int DB_VERSION = 1;
	private static final String DB_NAME = "ShortcutDB";
	private static final String TABLE_NAME = "shortcuts";
	private static final String COLUMN_ID = "id";
	private static final String COLUMN_NAME = "name";
	private static final String COLUMN_APPLICATION = "application";
	private static final String COLUMN_PHONE = "phone";
	private static final String COLUMN_PACKAGE_NAME = "package_name";
	private static final String COLUMN_CLASS_NAME = "class_name";
	
	private static final String[] COLUMNS = { COLUMN_ID, COLUMN_NAME,
			COLUMN_APPLICATION, COLUMN_PHONE, COLUMN_PACKAGE_NAME, COLUMN_CLASS_NAME };

	public ShortcutDBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_BOOK_TABLE = "CREATE TABLE " + TABLE_NAME + " ( "
				+ "id INTEGER PRIMARY KEY AUTOINCREMENT, " 
				+ COLUMN_NAME + " TEXT, " 
				+ COLUMN_APPLICATION + " TEXT, "
				+ COLUMN_PHONE + " TEXT, "
				+ COLUMN_PACKAGE_NAME + " TEXT, "
				+ COLUMN_CLASS_NAME	+ " TEXT )";
		db.execSQL(CREATE_BOOK_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		this.onCreate(db);
	}

	public long createShortcut(ShortcutItem item) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME, item.getName());
		values.put(COLUMN_APPLICATION, item.getApplication());
		values.put(COLUMN_PHONE, item.getPhone());
		values.put(COLUMN_PACKAGE_NAME, item.getPackageName());
		values.put(COLUMN_CLASS_NAME, item.getClassName());
		
		long id = db.insert(TABLE_NAME, null, values);
		db.close();
		
		return id;
	}

	public ShortcutItem getShortcut(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		ShortcutItem item = null;
		Cursor cursor = db.query(TABLE_NAME, COLUMNS, " id = ?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			item = new ShortcutItem(cursor.getString(0), 
									cursor.getString(1),
									cursor.getString(2),
									cursor.getString(3),
									cursor.getString(4));
		}
		return item;
	}

	public List<ShortcutItem> getAllShortcuts() {
		List<ShortcutItem> items = new LinkedList<ShortcutItem>();
		String query = "SELECT  * FROM " + TABLE_NAME;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		ShortcutItem item = null;
		if (cursor.moveToFirst()) {
			do {
				item = new ShortcutItem();
				item.setId(Integer.parseInt(cursor.getString(0)));
				item.setName(cursor.getString(1));
				item.setApplication(cursor.getString(2));
				item.setPhone(cursor.getString(3));
				item.setPackageName(cursor.getString(4));
				item.setClassName(cursor.getString(5));
				items.add(item);
			} while (cursor.moveToNext());
		}
		return items;
	}

	public int updateShortcut(ShortcutItem item) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME, item.getName());
		values.put(COLUMN_APPLICATION, item.getApplication());
		values.put(COLUMN_PHONE, item.getPhone());
		values.put(COLUMN_PACKAGE_NAME, item.getPackageName());
		values.put(COLUMN_CLASS_NAME, item.getClassName());
		
		int i = db.update(TABLE_NAME, values, COLUMN_ID + " = ?",
				new String[] { String.valueOf(item.getId()) });
		db.close();
		return i;
	}

	public void deleteShortcut(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_NAME, COLUMN_ID + " = ?",
				new String[] { String.valueOf(id) });
		db.close();
	}
}
