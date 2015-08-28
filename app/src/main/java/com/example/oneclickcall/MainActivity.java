package com.example.oneclickcall;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

public class MainActivity extends Activity implements
        AdapterView.OnItemClickListener {

    public static final String CONTACT_ID = "CONTACT_ID";
    public static final String TAG = "MainActivity";
    public static final String APPS_LIST = "APPS_LIST";
    public static final String POSITION = "POSITION";
    private static final int CONTACT_PICKER_RESULT = 1001;
    private static final int CALL_APP_PICKER_RESULT = 1002;
    ShortcutDBHelper db = new ShortcutDBHelper(this);
    private ListView listView;
    private List<ShortcutItem> shortcutItems;
    private ShortcutAdapter shortcutAdapter;
    private ArrayList<CallAppItem> appsList;
    private ShortcutItem selectedShortcutItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //db.onUpgrade(db.getWritableDatabase(), 1, 2);//TODO remove it

        ImageButton createShortcut = (ImageButton) findViewById(R.id.buttonCreateShortcut);
        createShortcut.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                pickContact();
            }
        });

        shortcutItems = db.getAllShortcuts();
        Log.v(MainActivity.TAG, "ON_CREATE Count of shortcut items: " + shortcutItems.size());
        if (shortcutItems.size() > 0) {
            listView = (ListView) findViewById(R.id.listView);
            shortcutAdapter = new ShortcutAdapter(this, R.layout.shortcut_list_item, shortcutItems);
            listView.setAdapter(shortcutAdapter);
            listView.setOnItemClickListener(this);
            registerForContextMenu(listView);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.listView) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_list, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
        selectedShortcutItem = (ShortcutItem) shortcutAdapter
                .getItem(menuInfo.position);
        switch (item.getItemId()) {
            case R.id.recreate:
                ShortcutEditor.addShortcut(this,
                        selectedShortcutItem.getName(),
                        selectedShortcutItem.getPhone(),
                        selectedShortcutItem.getPackageName(),
                        selectedShortcutItem.getClassName());
                return true;
            case R.id.edit_call_app:
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
                } else {
                    Intent intent = new Intent(this, CallAppActivity.class);
                    intent.putParcelableArrayListExtra(APPS_LIST, appsList);
                    startActivityForResult(intent, CALL_APP_PICKER_RESULT);
                }
                return true;
            case R.id.edit_number:
                //TODO
                return true;
            case R.id.delete:
                ShortcutEditor.removeShortcut(this,
                        selectedShortcutItem.getName(),
                        selectedShortcutItem.getPhone(),
                        selectedShortcutItem.getPackageName(),
                        selectedShortcutItem.getClassName());
                db.deleteShortcut(selectedShortcutItem.getId());
                updateList();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void updateList() {
        shortcutAdapter.clear();
        shortcutItems = db.getAllShortcuts();
        Log.v(MainActivity.TAG, "UPDATE:Count of shortcut items: " + shortcutItems.size());
        shortcutAdapter.addAll(shortcutItems);
        shortcutAdapter.notifyDataSetChanged();

    }

    private void pickContact() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, CONTACT_PICKER_RESULT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CONTACT_PICKER_RESULT:
                    try {
                        Uri uri = data.getData();
                        Log.v(TAG, "Contact uri: " + uri.toString());
                        String contactId = uri.getLastPathSegment();

                        Intent i = new Intent(this, ContactActivity.class);
                        i.putExtra(CONTACT_ID, contactId);
                        startActivity(i);
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to get phone data", e);
                    }
                    break;
                case CALL_APP_PICKER_RESULT:
                    int position = data.getIntExtra(POSITION, -1);
                    Log.i(MainActivity.TAG, "returned position in application list:" + position);
                    if (position >= 0) {
                        ShortcutEditor.removeShortcut(this,
                                selectedShortcutItem.getName(),
                                selectedShortcutItem.getPhone(),
                                selectedShortcutItem.getPackageName(),
                                selectedShortcutItem.getClassName());

                        CallAppItem item = appsList.get(position);
                        ShortcutEditor.addShortcut(this,
                                selectedShortcutItem.getName(),
                                selectedShortcutItem.getPhone(),
                                item.getPackageName(),
                                item.getClassName());
                        db.updateShortcut(new ShortcutItem(selectedShortcutItem.getId(),
                                selectedShortcutItem.getName(),
                                item.getName(),
                                selectedShortcutItem.getPhone(),
                                item.getPackageName(),
                                item.getClassName()));
                        updateList();
                    }
                    break;
            }
        } else {
            Log.w(TAG, "Warning: activity result not ok");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // TODO Auto-generated method stub

    }

}
