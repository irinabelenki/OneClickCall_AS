package com.example.oneclickcall;

/**
 * Created by Irina on 1/25/2016.
 */
import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ContactsActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    @SuppressLint("InlinedApi")
    private final static String[] FROM_COLUMNS = {
            Build.VERSION.SDK_INT
                    >= Build.VERSION_CODES.HONEYCOMB ?
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                    ContactsContract.Contacts.DISPLAY_NAME
    };

    private final static int[] TO_IDS = {
            R.id.display_name
    };

    @SuppressLint("InlinedApi")
    private static final String[] PROJECTION = {
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.LOOKUP_KEY,
            Build.VERSION.SDK_INT
                    >= Build.VERSION_CODES.HONEYCOMB ?
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                    ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.HAS_PHONE_NUMBER
    };

    @SuppressLint("InlinedApi")
    private static final String SELECTION =
            "(" + (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
            ContactsContract.Contacts.DISPLAY_NAME) + " LIKE ?"  + ") AND (" + ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1)";

    private static final int CONTACT_ID_INDEX = 0;
    private static final int LOOKUP_KEY_INDEX = 1;

    private ListView contactsListView;
    private long contactId;
    private String contactKey;
    private Uri contactUri;
    private SimpleCursorAdapter cursorAdapter;
    private EditText searchEditText;

    private String searchString;
    private String[] selectionArgs = { searchString };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_list_activity);

        contactsListView = (ListView)findViewById(R.id.contacts_listview);
        cursorAdapter = new SimpleCursorAdapter(this,
                R.layout.contacts_list_item,
                null,
                FROM_COLUMNS, TO_IDS,
                0);
        contactsListView.setAdapter(cursorAdapter);
        contactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(MainActivity.TAG, "Clicked on position " + position);
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor.moveToPosition(position)) {
                    contactId = cursor.getLong(CONTACT_ID_INDEX);
                    contactKey = cursor.getString(LOOKUP_KEY_INDEX);
                    contactUri = ContactsContract.Contacts.getLookupUri(contactId, contactKey);

                    Intent data = new Intent();
                    data.putExtra(MainActivity.CONTACT_ID, contactId);
                    setResult(RESULT_OK, data);
                    finish();
                }
            }
        });
        searchEditText = (EditText)findViewById(R.id.search_edittext);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.equals("") ) {
                    getLoaderManager().restartLoader(0, null, ContactsActivity. this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        searchString = searchEditText.getText().toString();
        selectionArgs[0] = "%" + searchString + "%";

        return new CursorLoader(
                this,
                ContactsContract.Contacts.CONTENT_URI,
                PROJECTION,
                SELECTION,
                selectionArgs,
                ContactsContract.Contacts.SORT_KEY_PRIMARY
        );
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Put the result Cursor in the adapter for the ListView
        cursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Delete the reference to the existing Cursor
        cursorAdapter.swapCursor(null);
    }
}