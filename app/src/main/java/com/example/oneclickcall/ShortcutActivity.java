package com.example.oneclickcall;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ShortcutActivity extends ActionBarActivity implements
        LoaderManager.LoaderCallbacks<Cursor>  {

    TextView contactNameTextView;
    TextView phoneNumberTextView;
    //TextView callApplicationTextView;
    ImageView callApplicationIcon;
    Button editPhoneNumberButton;
    Button callApplicationButton;

    private enum ACTION { CREATE, EDIT, ILLEGAL }
    private ACTION action = ACTION.ILLEGAL;

    ShortcutDBHelper db = new ShortcutDBHelper(this);
    private ArrayList<CallAppItem> appsList;

    static final String[] PROJECTION = new String[]{
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            //ContactsContract.Contacts.PHOTO_ID,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
    };

    private String contactId;
    private ShortcutItem selectedShortcutItem;
    private ShortcutItem oldShortcutItem;
    private View titleView;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shortcut_activity);

        contactNameTextView = (TextView) findViewById(R.id.contact_name);
        phoneNumberTextView = (TextView) findViewById(R.id.phone_number);
        //callApplicationTextView = (TextView) findViewById(R.id.call_application);
        editPhoneNumberButton = (Button) findViewById(R.id.edit_phone_number);
        editPhoneNumberButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showContactPhoneNumbers();
            }
        });
        callApplicationButton = (Button) findViewById(R.id.call_application);
        callApplicationButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showCallApplications();
            }
        });
        callApplicationIcon = (ImageView)findViewById(R.id.call_application_icon);

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
                phoneNumberTextView.setText(selectedShortcutItem.getPhone());
                //callApplicationTextView.setText(selectedShortcutItem.getApplication());
                callApplicationButton.setText(selectedShortcutItem.getApplication());
                callApplicationIcon.setImageDrawable(selectedShortcutItem.getApplicationIcon());
                contactId = selectedShortcutItem.getContactId();
                oldShortcutItem = new ShortcutItem(selectedShortcutItem);
            }
        }
    }

    private void showContactPhoneNumbers() {
        String[] fromColumns = {
                ContactsContract.Contacts.DISPLAY_NAME,
                //ContactsContract.Contacts.PHOTO_ID,
                ContactsContract.CommonDataKinds.Phone.NUMBER,};

        int[] toViews = {R.id.contactName,
                         //R.id.photo,
                         R.id.phoneNumber,};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_launcher);
        titleView = getLayoutInflater().inflate(R.layout.contacts_header, null);
        builder.setCustomTitle(titleView);
        adapter = new SimpleCursorAdapter(this, R.layout.contacts_list_item,
                null, fromColumns, toViews, 0);
        getLoaderManager().initLoader(0, null, this);
        builder.setNegativeButton("cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.setAdapter(adapter,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setSelectedContactData(which);
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    private void setSelectedContactData(int position) {
        Cursor cursor = (Cursor)adapter.getItem(position);
        phoneNumberTextView.setText(cursor.getString(2));
        contactNameTextView.setText(cursor.getString(1));
        selectedShortcutItem.setName(cursor.getString(1));
        selectedShortcutItem.setPhone(cursor.getString(2));
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
        TextView titleTextView = (TextView)titleView.findViewById(R.id.contact_name_header);
        titleTextView.setText(data.getString(1));

        adapter.swapCursor(data);
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }


    private void showCallApplications() {
        final PackageManager packageManager = getPackageManager();
        Intent callIntent = new Intent(Intent.ACTION_CALL, null);
        //Intent callIntent = new Intent("android.intent.action.CALL_PRIVILEGED", null);
        callIntent.setData(Uri.parse("tel:" + "1234567890"));
        final List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(callIntent, 0);

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
            Drawable icon = packageManager.getApplicationIcon(info.activityInfo.applicationInfo);
            String label = packageManager.getApplicationLabel(info.activityInfo.applicationInfo).toString();
            //callApplicationTextView.setText(label);
            callApplicationButton.setText(label);
            callApplicationIcon.setImageDrawable(icon);
            selectedShortcutItem.setApplication(label);
            selectedShortcutItem.setApplicationIcon(icon);
            selectedShortcutItem.setPackageName(info.activityInfo.packageName);
            selectedShortcutItem.setClassName(info.activityInfo.name);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.ic_launcher);
            builder.setTitle("Call Apps");
            final CallAppAdapter callAppAdapter = new CallAppAdapter(this, R.layout.call_app_list_item, appsList);
            builder.setNegativeButton("cancel",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.setAdapter(callAppAdapter,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            CallAppItem callAppItem = callAppAdapter.getItem(which);
                            //callApplicationTextView.setText(callAppItem.getName());
                            callApplicationButton.setText(callAppItem.getName());
                            selectedShortcutItem.setApplication(callAppItem.getName());
                            selectedShortcutItem.setApplicationIcon(callAppItem.getImage());
                            selectedShortcutItem.setPackageName(callAppItem.getPackageName());
                            selectedShortcutItem.setClassName(callAppItem.getClassName());
                            dialog.dismiss();
                        }
                    });
            builder.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.shortcut, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_done) {
            switch(action) {
                case CREATE:
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

}
