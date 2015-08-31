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

    public static final String TAG = "MainActivity";
    public static final String CONTACT_ID = "CONTACT_ID";
    public static final String SHORTCUT_ITEM = "SHORTCUT_ITEM";
    private static final int CONTACT_PICKER_RESULT = 1001;
    private static final int SHORTCUT_ACTIVITY_RESULT = 1003;
    ShortcutDBHelper db = new ShortcutDBHelper(this);
    private ListView listView;
    private List<ShortcutItem> shortcutItems;
    private ShortcutAdapter shortcutAdapter;
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
        listView = (ListView) findViewById(R.id.listView);
        shortcutAdapter = new ShortcutAdapter(this, R.layout.shortcut_list_item, shortcutItems);
        listView.setAdapter(shortcutAdapter);
        listView.setOnItemClickListener(this);
        registerForContextMenu(listView);
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
    public boolean onContextItemSelected(MenuItem menuItem) {
        AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) menuItem.getMenuInfo();
        selectedShortcutItem = shortcutAdapter.getItem(menuInfo.position);
        switch (menuItem.getItemId()) {
            case R.id.recreate:
                ShortcutEditor.addShortcut(this,
                        selectedShortcutItem.getName(),
                        selectedShortcutItem.getPhone(),
                        selectedShortcutItem.getPackageName(),
                        selectedShortcutItem.getClassName());
                return true;
            case R.id.edit:
                Intent intent = new Intent(this, ShortcutActivity.class);
                intent.putExtra(SHORTCUT_ITEM, selectedShortcutItem);
                startActivityForResult(intent, SHORTCUT_ACTIVITY_RESULT);
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
                return super.onContextItemSelected(menuItem);
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
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
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

                        Intent intent = new Intent(this, ShortcutActivity.class);
                        intent.putExtra(CONTACT_ID, contactId);
                        startActivityForResult(intent, SHORTCUT_ACTIVITY_RESULT);
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to get phone data", e);
                    }
                    break;
                case SHORTCUT_ACTIVITY_RESULT:
                    updateList();
                    break;
            }
        } else {
            Log.w(TAG, "Warning: activity result not ok");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
    }

}
