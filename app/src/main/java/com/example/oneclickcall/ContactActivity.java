package com.example.oneclickcall;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.LayoutInflater;
import android.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

public class ContactActivity extends ListActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CALL_APP_PICKER_RESULT = 1002;
    public static final String APPS_LIST = "APPS_LIST";
    public static final String POSITION = "POSITION";

    ShortcutDBHelper db = new ShortcutDBHelper(this);
    private SimpleCursorAdapter mAdapter;
    private String contactId;
    private ArrayList<CallAppItem> appsList;
    private TextView phoneNumberTextView;
    private TextView contactNameTextView;

    static final String[] PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone._ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w(MainActivity.TAG, "ContactActivity created");

        ListView listView = getListView();
        LayoutInflater inflater = getLayoutInflater();
        View header = inflater.inflate(R.layout.contacts_header, listView, false);
        listView.addHeaderView(header, null, false);

        if (header != null) {
            TextView headerTextView = (TextView)header.findViewById(R.id.contact_name);
            headerTextView.setText("Contact Info");
        }

        Bundle extras = getIntent().getExtras();
        contactId = extras.getString(MainActivity.CONTACT_ID);

        String[] fromColumns = {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,};

        int[] toViews = {R.id.contactName, R.id.phoneNumber,};

        mAdapter = new SimpleCursorAdapter(this, R.layout.contacts_list_item,
                null, fromColumns, toViews, 0);

        setListAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this);
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                PROJECTION,
                Phone.CONTACT_ID + "=?", new String[]{contactId},
                "DISPLAY_NAME ASC");
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);
        phoneNumberTextView = (TextView) v.findViewById(R.id.phoneNumber);
        contactNameTextView = (TextView) v.findViewById(R.id.contactName);

        PackageManager packageManager = getPackageManager();
        //Intent callIntent = new Intent(Intent.ACTION_CALL, null);
        Intent callIntent = new Intent("android.intent.action.CALL_PRIVILEGED", null);
        callIntent.setData(Uri.parse("tel:" + "1234567890"));
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(callIntent, 0);

        if (resolveInfos.size() == 0) {
            Log.i(MainActivity.TAG, "No call application");
        } else {
            Log.i(MainActivity.TAG, "Call apps list size: " + resolveInfos.size());
            appsList = new ArrayList<CallAppItem>();
            for (ResolveInfo info : resolveInfos) {
                //ApplicationInfo appInfo = info.activityInfo.applicationInfo;
                Drawable icon = packageManager.getApplicationIcon(info.activityInfo.applicationInfo);
                String label = packageManager.getApplicationLabel(info.activityInfo.applicationInfo).toString();
                Log.v(MainActivity.TAG, "Name: " + label);
                appsList.add(new CallAppItem(label, icon, info.activityInfo.packageName, info.activityInfo.name));
            }
        }
        if (resolveInfos.size() == 1) {
            Log.i(MainActivity.TAG, "Only one call application");
            ResolveInfo info = resolveInfos.get(0);
            ShortcutEditor.addShortcut(this,
                    contactNameTextView.getText().toString(),
                    phoneNumberTextView.getText().toString(),
                    info.activityInfo.packageName,
                    info.activityInfo.name);
            db.createShortcut(new ShortcutItem(contactNameTextView.getText().toString(),
                    packageManager.getApplicationLabel(info.activityInfo.applicationInfo).toString(),
                    phoneNumberTextView.getText().toString(),
                    info.activityInfo.packageName,
                    info.activityInfo.name));
            finish();
        } else {
            Intent intent = new Intent(this, CallAppActivity.class);
            intent.putParcelableArrayListExtra(APPS_LIST, appsList);
            startActivityForResult(intent, CALL_APP_PICKER_RESULT);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode++ == RESULT_OK) {
            switch (requestCode) {
                case CALL_APP_PICKER_RESULT:
                    int position = data.getIntExtra(POSITION, -1);
                    Log.i(MainActivity.TAG, "returned position in application list:" + position);
                    if (position >= 0) {
                        CallAppItem item = appsList.get(position);
                        ShortcutEditor.addShortcut(this,
                                contactNameTextView.getText().toString(),
                                phoneNumberTextView.getText().toString(),
                                item.getPackageName(),
                                item.getClassName());
                        db.createShortcut(new ShortcutItem(contactNameTextView.getText().toString(),
                                item.getName(),
                                phoneNumberTextView.getText().toString(),
                                item.getPackageName(),
                                item.getClassName()));

                    }
                    break;
            }
        } else {
            Log.w(MainActivity.TAG, "Warning: activity result not ok");
        }
        finish();
    }
}
