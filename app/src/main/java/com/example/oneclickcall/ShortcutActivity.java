package com.example.oneclickcall;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ShortcutActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>  {

    private TextView contactNameTextView;
    private ImageView contactPhotoImageView;
    private SimpleCursorAdapter adapter;
    private ListView listView;
    private Spinner callApplicationSpinner;
    private CallApplicationAdapter callApplicationAdapter;

    private enum ACTION { CREATE, EDIT, ILLEGAL }
    private ACTION action = ACTION.ILLEGAL;

    ShortcutDBHelper db = new ShortcutDBHelper(this);
    private ArrayList<CallAppItem> appsList = new ArrayList<CallAppItem>();

    static final String[] PROJECTION = new String[]{
            ContactsContract.Contacts._ID,
            Build.VERSION.SDK_INT
                    >= Build.VERSION_CODES.HONEYCOMB ?
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                    ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.PHOTO_ID,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
    };

    static final String[] fromColumns = {
            Build.VERSION.SDK_INT
                    >= Build.VERSION_CODES.HONEYCOMB ?
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                    ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.PHOTO_ID,
            ContactsContract.CommonDataKinds.Phone.NUMBER,};

    static final int[] toViews = {R.id.contact_name,
            R.id.photo,
            R.id.phone_number,};

    private static final int DISPLAY_NAME_INDEX = 1;
    private static final int PHOTO_ID_INDEX = 2;
    private static final int PHONE_NUMBER_INDEX = 3;

    private String contactId;
    private ShortcutItem selectedShortcutItem;
    private ShortcutItem oldShortcutItem;
    private String selectedPhone = null;
    private Bitmap contactBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shortcut_activity);

        contactNameTextView = (TextView) findViewById(R.id.contact_name);
        contactPhotoImageView = (ImageView)findViewById(R.id.contact_photo);

        listView = (ListView)findViewById(R.id.phone_number_listview);
        adapter = new SimpleCursorAdapter(this, R.layout.phone_number_item,
                null, fromColumns, toViews, 0);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor.moveToPosition(position)) {
                    selectedPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    Log.i(MainActivity.TAG, "Clicked on " + selectedPhone);
                }
            }
        });

        callApplicationSpinner = (Spinner)findViewById(R.id.call_application_spinner);
        setCallApplicationData();
        callApplicationAdapter = new CallApplicationAdapter(this, R.layout.call_app_spinner_rows, appsList);
        callApplicationSpinner.setAdapter(callApplicationAdapter);

        callApplicationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View v, int position, long id) {
                //String appName = ((TextView)v.findViewById(R.id.call_app_name)).getText().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });

        contactId = getIntent().getStringExtra(MainActivity.CONTACT_ID);
        Log.v(MainActivity.TAG, "contactId: " + contactId);
        if (contactId != null) {
            action = ACTION.CREATE;
            selectedShortcutItem = new ShortcutItem();
            selectedShortcutItem.setContactId(contactId);
        }
        else {
            selectedShortcutItem = getIntent().getExtras().getParcelable(MainActivity.SHORTCUT_ITEM);
            if (selectedShortcutItem != null) {
                action = ACTION.EDIT;
                contactNameTextView.setText(selectedShortcutItem.getName());
                int appPosition = callApplicationAdapter.getPosition(new CallAppItem(selectedShortcutItem.getApplication(),
                        selectedShortcutItem.getApplicationIcon(),
                        selectedShortcutItem.getPackageName(),
                        selectedShortcutItem.getClassName()  ));
                Log.i(MainActivity.TAG, "appPosition:" + appPosition);
                callApplicationSpinner.setSelection(appPosition);
                contactId = selectedShortcutItem.getContactId();

                oldShortcutItem = new ShortcutItem(selectedShortcutItem);
            }
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private void setCallApplicationData() {
        final PackageManager packageManager = getPackageManager();
        Intent callIntent = new Intent(Intent.ACTION_CALL, null);
        //Intent callIntent = new Intent("android.intent.action.CALL_PRIVILEGED", null);
        callIntent.setData(Uri.parse("tel:" + "1234567890"));
        final List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(callIntent, 0);
        if (resolveInfos.size() == 0) {
            Log.i(MainActivity.TAG, "No call application");
            callApplicationSpinner.setPrompt("No call applications");
        }
        else {
            Log.i(MainActivity.TAG, "Call apps list size: " + resolveInfos.size());

            for (ResolveInfo info : resolveInfos) {
                //ApplicationInfo appInfo = info.activityInfo.applicationInfo;
                Drawable icon = packageManager.getApplicationIcon(info.activityInfo.applicationInfo);
                Bitmap bitmap = ((BitmapDrawable)icon).getBitmap();
                String label = packageManager.getApplicationLabel(info.activityInfo.applicationInfo).toString();
                Log.v(MainActivity.TAG, "Name: " + label);
                appsList.add(new CallAppItem(label, bitmap, info.activityInfo.packageName, info.activityInfo.name));
            }
        }
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                PROJECTION,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{contactId},
                //"DISPLAY_NAME ASC"
                ContactsContract.Contacts.SORT_KEY_PRIMARY);
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            //int columnCount = data.getColumnCount();
            //Log.i(MainActivity.TAG, "column count: " + columnCount);
            //String[] columnNames = data.getColumnNames();
            //for (int i = 0; i < columnCount; i++) {
            //    Log.i(MainActivity.TAG, columnNames[i]);
            //}

            String contactName = data.getString(DISPLAY_NAME_INDEX);
            if (contactName != null) {
                contactNameTextView.setText(contactName);
            }
            int contactPhotoId = data.getInt(PHOTO_ID_INDEX);
            Log.i(MainActivity.TAG, "contactPhotoId: " + contactPhotoId);
            if (contactPhotoId > 0) {
                contactBitmap = queryContactImage(contactPhotoId);
                contactPhotoImageView.setImageBitmap(contactBitmap);
            } else {
                contactBitmap = BitmapFactory.decodeResource(this.getResources(),
                        R.drawable.ic_contact_picture);
            }
        }
        adapter.swapCursor(data);

        switch (action) {
            case CREATE:
                listView.setItemChecked(0, true);
                break;
            case EDIT:
                String phone = selectedShortcutItem.getPhone();
                int phonePosition = getPhonePosition(phone);
                listView.setItemChecked(phonePosition, true);
                break;
            case ILLEGAL:
                Log.e(MainActivity.TAG, "Illegal action");
        }
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.shortcut, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        CallAppItem callAppItem;
        Cursor cursor;
        int position;
        if (id == R.id.action_done) {
            switch(action) {
                case CREATE:
                    callAppItem = (CallAppItem)callApplicationSpinner.getSelectedItem();
                    selectedShortcutItem.setApplication(callAppItem.getName());
                    selectedShortcutItem.setApplicationIcon(callAppItem.getImage());
                    selectedShortcutItem.setPackageName(callAppItem.getPackageName());
                    selectedShortcutItem.setClassName(callAppItem.getClassName());

                    position = listView.getCheckedItemPosition();
                    cursor = (Cursor)adapter.getItem(position);
                    selectedShortcutItem.setName(cursor.getString(DISPLAY_NAME_INDEX));
                    selectedShortcutItem.setPhone(cursor.getString(PHONE_NUMBER_INDEX));
                    selectedShortcutItem.setContactIcon(contactBitmap);

                    if(selectedShortcutItem.isFilled()) {
                        ShortcutEditor.editShortcut(ShortcutActivity.this,
                                selectedShortcutItem.getName(),
                                selectedShortcutItem.getPhone(),
                                selectedShortcutItem.getPackageName(),
                                selectedShortcutItem.getClassName(),
                                selectedShortcutItem.getContactIcon(),
                                ShortcutEditor.ACTION.ADD);
                        db.createShortcut(selectedShortcutItem);
                    } else {
                        Log.e(MainActivity.TAG, "Fill it!");
                        Toast.makeText(ShortcutActivity.this, "Fill all shortcut fields", Toast.LENGTH_SHORT).show();
                        //TODO not finish
                    }
                    break;
                case EDIT:
                    callAppItem = (CallAppItem)callApplicationSpinner.getSelectedItem();
                    selectedShortcutItem.setApplication(callAppItem.getName());
                    selectedShortcutItem.setApplicationIcon(callAppItem.getImage());
                    selectedShortcutItem.setPackageName(callAppItem.getPackageName());
                    selectedShortcutItem.setClassName(callAppItem.getClassName());

                    position = listView.getCheckedItemPosition();
                    cursor = (Cursor)adapter.getItem(position);
                    selectedShortcutItem.setPhone(cursor.getString(PHONE_NUMBER_INDEX));

                    ShortcutEditor.editShortcut(ShortcutActivity.this,
                            oldShortcutItem.getName(),
                            oldShortcutItem.getPhone(),
                            oldShortcutItem.getPackageName(),
                            oldShortcutItem.getClassName(),
                            oldShortcutItem.getContactIcon(),
                            ShortcutEditor.ACTION.REMOVE);
                    ShortcutEditor.editShortcut(ShortcutActivity.this,
                            selectedShortcutItem.getName(),
                            selectedShortcutItem.getPhone(),
                            selectedShortcutItem.getPackageName(),
                            selectedShortcutItem.getClassName(),
                            selectedShortcutItem.getContactIcon(),
                            ShortcutEditor.ACTION.ADD);
                    db.updateShortcut(selectedShortcutItem);
                    break;
                case ILLEGAL:
                    Log.e(MainActivity.TAG, "Illegal action");
                    break;
            }
            Intent returnIntent = new Intent();
            setResult(RESULT_OK, returnIntent);
            Log.w(MainActivity.TAG, "ShortcutActivity finished with OK");
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private Bitmap queryContactImage(int imageDataRow) {
        Cursor c = getContentResolver().query(ContactsContract.Data.CONTENT_URI, new String[]{
                ContactsContract.CommonDataKinds.Photo.PHOTO
        }, ContactsContract.Data._ID + "=?", new String[]{
                Integer.toString(imageDataRow)
        }, null);
        byte[] imageBytes = null;
        if (c != null) {
            if (c.moveToFirst()) {
                imageBytes = c.getBlob(0);
            }
            c.close();
        }

        if (imageBytes != null) {
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        } else {
            return null;
        }
    }

    private int getPhonePosition(String phone) {
        Cursor cursor;
        String tmpPhone;

        for (int i = 0; i < adapter.getCount(); i++) {
            cursor = (Cursor) adapter.getItem(i);
            if (cursor.moveToPosition(i)) {
                tmpPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                if (tmpPhone.contentEquals(phone)) {
                    Log.d(MainActivity.TAG, "Found match");
                    return i;
                }
            }
        }
        return -1;
    }
}
