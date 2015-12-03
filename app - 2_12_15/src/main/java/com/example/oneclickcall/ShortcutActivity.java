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
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
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

public class ShortcutActivity extends ActionBarActivity implements
        LoaderManager.LoaderCallbacks<Cursor>  {

    private TextView contactNameTextView;
    private ImageView contactPhotoImageView;
    //private Button callApplicationButton;
    //private ImageView callApplicationIcon;
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
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.PHOTO_ID,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
    };

    private String contactId;
    private ShortcutItem selectedShortcutItem;
    private ShortcutItem oldShortcutItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shortcut_activity);

        contactNameTextView = (TextView) findViewById(R.id.contact_name);
        contactPhotoImageView = (ImageView)findViewById(R.id.contact_photo);

        listView = (ListView)findViewById(R.id.phone_number_listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Log.i(MainActivity.TAG, "Clicked");
            }
        });

        String[] fromColumns = {
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_ID,
                ContactsContract.CommonDataKinds.Phone.NUMBER,};

        int[] toViews = {R.id.contact_name,
                         R.id.photo,
                         R.id.phone_number,};

        adapter = new SimpleCursorAdapter(this, R.layout.contacts_list_item,
                null, fromColumns, toViews, 0);
        listView.setAdapter(adapter);

        callApplicationSpinner = (Spinner)findViewById(R.id.call_application_spinner);
        setCallApplicationData();
        callApplicationAdapter = new CallApplicationAdapter(this, R.layout.call_app_spinner_rows, appsList);
        callApplicationSpinner.setAdapter(callApplicationAdapter);

        callApplicationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View v, int position, long id) {
                String appName = ((TextView)v.findViewById(R.id.call_app_name)).getText().toString();
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
                int position = callApplicationAdapter.getPosition(new CallAppItem(selectedShortcutItem.getApplication(),
                        selectedShortcutItem.getApplicationIcon(),
                        selectedShortcutItem.getPackageName(),
                        selectedShortcutItem.getClassName()  ));
                Log.i(MainActivity.TAG, "position:" + position);
                callApplicationSpinner.setSelection(position);
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
        /*else if (resolveInfos.size() == 1) {
            Log.i(MainActivity.TAG, "Only one call application");
            ResolveInfo info = resolveInfos.get(0);
            Drawable icon = packageManager.getApplicationIcon(info.activityInfo.applicationInfo);
            String label = packageManager.getApplicationLabel(info.activityInfo.applicationInfo).toString();
            appsList.add(new CallAppItem(label, icon, info.activityInfo.packageName, info.activityInfo.name));
            //callApplicationTextView.setText(label);
            //callApplicationButton.setText(label);
            //callApplicationIcon.setImageDrawable(icon);
            selectedShortcutItem.setApplication(label);
            selectedShortcutItem.setApplicationIcon(icon);
            selectedShortcutItem.setPackageName(info.activityInfo.packageName);
            selectedShortcutItem.setClassName(info.activityInfo.name);
        }*/ else {
            Log.i(MainActivity.TAG, "Call apps list size: " + resolveInfos.size());

            for (ResolveInfo info : resolveInfos) {
                //ApplicationInfo appInfo = info.activityInfo.applicationInfo;
                Drawable icon = packageManager.getApplicationIcon(info.activityInfo.applicationInfo);
                String label = packageManager.getApplicationLabel(info.activityInfo.applicationInfo).toString();
                Log.v(MainActivity.TAG, "Name: " + label);
                appsList.add(new CallAppItem(label, icon, info.activityInfo.packageName, info.activityInfo.name));
            }
        }
    }

    private void setSelectedContactData(int position) {
        Cursor cursor = (Cursor)adapter.getItem(position);
        contactNameTextView.setText(cursor.getString(1));
        int photoId = cursor.getInt(2);
        Log.i(MainActivity.TAG, "photoId: " + photoId);
        if(photoId > 0) {
            Bitmap bitmap = queryContactImage(photoId);
            contactPhotoImageView.setImageBitmap(bitmap);
        }
        //phoneNumberTextView.setText(cursor.getString(3));

        selectedShortcutItem.setName(cursor.getString(1));
        selectedShortcutItem.setPhone(cursor.getString(3));
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                PROJECTION,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{contactId},
                "DISPLAY_NAME ASC");
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();
        contactNameTextView.setText(data.getString(1));
        int photoId = data.getInt(2);
        Log.i(MainActivity.TAG, "photoId: " + photoId);
        if(photoId > 0) {
            Bitmap bitmap = queryContactImage(photoId);
            contactPhotoImageView.setImageBitmap(bitmap);
        }

        adapter.swapCursor(data);
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
        if (id == R.id.action_done) {
            switch(action) {
                case CREATE:
                    callAppItem = (CallAppItem)callApplicationSpinner.getSelectedItem();
                    selectedShortcutItem.setApplication(callAppItem.getName());
                    selectedShortcutItem.setApplicationIcon(callAppItem.getImage());
                    selectedShortcutItem.setPackageName(callAppItem.getPackageName());
                    selectedShortcutItem.setClassName(callAppItem.getClassName());

                    if(selectedShortcutItem.isFilled()) {
                        ShortcutEditor.addShortcut(ShortcutActivity.this,
                                selectedShortcutItem.getName(),
                                selectedShortcutItem.getPhone(),
                                selectedShortcutItem.getPackageName(),
                                selectedShortcutItem.getClassName());
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

                    ShortcutEditor.removeShortcut(ShortcutActivity.this,
                            oldShortcutItem.getName(),
                            oldShortcutItem.getPhone(),
                            oldShortcutItem.getPackageName(),
                            oldShortcutItem.getClassName());
                    ShortcutEditor.addShortcut(ShortcutActivity.this,
                            selectedShortcutItem.getName(),
                            selectedShortcutItem.getPhone(),
                            selectedShortcutItem.getPackageName(),
                            selectedShortcutItem.getClassName());
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

}
