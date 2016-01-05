package com.example.oneclickcall;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class ShortcutDBHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "ShortcutDB";
    private static final String TABLE_NAME = "shortcuts";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_APPLICATION = "application";
    private static final String COLUMN_APPLICATION_ICON = "application_icon";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_PACKAGE_NAME = "package_name";
    private static final String COLUMN_CLASS_NAME = "class_name";
    private static final String COLUMN_CONTACT_ID = "contact_id";
    private static final String COLUMN_CONTACT_ICON = "contact_icon";

    private static final String[] COLUMNS = {COLUMN_ID, COLUMN_NAME,
            COLUMN_APPLICATION, COLUMN_APPLICATION_ICON,
            COLUMN_PHONE, COLUMN_PACKAGE_NAME, COLUMN_CLASS_NAME, COLUMN_CONTACT_ID, COLUMN_CONTACT_ICON};

    public ShortcutDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_BOOK_TABLE = "CREATE TABLE " + TABLE_NAME + " ( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT, "
                + COLUMN_APPLICATION + " TEXT, "
                + COLUMN_APPLICATION_ICON + " BLOB, "
                + COLUMN_PHONE + " TEXT, "
                + COLUMN_PACKAGE_NAME + " TEXT, "
                + COLUMN_CLASS_NAME + " TEXT, "
                + COLUMN_CONTACT_ID + " TEXT, "
                + COLUMN_CONTACT_ICON + " BLOB )";
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
        values.put(COLUMN_APPLICATION_ICON, bitmapToByteArray(item.getApplicationIcon()));
        values.put(COLUMN_PHONE, item.getPhone());
        values.put(COLUMN_PACKAGE_NAME, item.getPackageName());
        values.put(COLUMN_CLASS_NAME, item.getClassName());
        values.put(COLUMN_CONTACT_ID, item.getContactId());
        values.put(COLUMN_CONTACT_ICON, bitmapToByteArray(item.getContactIcon()));

        long id = db.insert(TABLE_NAME, null, values);
        db.close();

        return id;
    }

    public ShortcutItem getShortcut(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        ShortcutItem item = null;
        Cursor cursor = db.query(TABLE_NAME, COLUMNS, " id = ?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            item = new ShortcutItem(
                    Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1),
                    cursor.getString(2),
                    byteArrayToBitmap(cursor.getBlob(3)),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    byteArrayToBitmap(cursor.getBlob(8)));
            Log.v(MainActivity.TAG, "GET:" + item.toString());
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
                item.setApplicationIcon(byteArrayToBitmap(cursor.getBlob(3)));
                item.setPhone(cursor.getString(4));
                item.setPackageName(cursor.getString(5));
                item.setClassName(cursor.getString(6));
                item.setContactId(cursor.getString(7));
                item.setContactIcon(byteArrayToBitmap(cursor.getBlob(8)));
                items.add(item);
                Log.v(MainActivity.TAG, "GET ALL:" + item.toString());
            } while (cursor.moveToNext());
        }
        return items;
    }

    public int updateShortcut(ShortcutItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, item.getName());
        values.put(COLUMN_APPLICATION, item.getApplication());
        values.put(COLUMN_APPLICATION_ICON, bitmapToByteArray(item.getApplicationIcon()));
        values.put(COLUMN_PHONE, item.getPhone());
        values.put(COLUMN_PACKAGE_NAME, item.getPackageName());
        values.put(COLUMN_CLASS_NAME, item.getClassName());
        values.put(COLUMN_CONTACT_ID, item.getContactId());
        values.put(COLUMN_CONTACT_ICON, bitmapToByteArray(item.getContactIcon()));
        Log.v(MainActivity.TAG, "UPDATE:" + item.toString());

        int i = db.update(TABLE_NAME, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(item.getId())});
        db.close();
        return i;
    }

    public void deleteShortcut(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }
/*
    public static byte[] drawableToByteArray(Drawable drawable) {
        BitmapDrawable bitDw = ((BitmapDrawable) drawable);
        Bitmap bitmap = bitDw.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    public static Drawable byteArrayToDrawable(byte[] byteArray) {
        Bitmap bitMapImage = BitmapFactory.decodeByteArray(
                byteArray, 0,
                byteArray.length);
        return new BitmapDrawable(bitMapImage);
    }
*/
    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    public static Bitmap byteArrayToBitmap(byte[] byteArray) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(
                byteArray, 0,
                byteArray.length);
        return bitmap;
    }
}
