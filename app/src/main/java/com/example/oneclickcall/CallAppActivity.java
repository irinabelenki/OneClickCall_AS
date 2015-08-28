package com.example.oneclickcall;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

public class CallAppActivity extends Activity implements AdapterView.OnItemClickListener {

    private ListView listView;
    private List<CallAppItem> callAppItems;
    private CallAppAdapter callAppAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_app_list);
        Log.w(MainActivity.TAG, "CallAppActivity created");

        listView = (ListView) findViewById(R.id.listView);
        callAppItems = getIntent().getParcelableArrayListExtra(ContactActivity.APPS_LIST);
        callAppAdapter = new CallAppAdapter(this, R.layout.call_app_list_item, callAppItems);
        listView.setAdapter(callAppAdapter);
        listView.setOnItemClickListener(this);
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

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(MainActivity.TAG, "position:" + position);
        Intent returnIntent = new Intent();
        returnIntent.putExtra(ContactActivity.POSITION, position);
        setResult(RESULT_OK, returnIntent);
        Log.w(MainActivity.TAG, "CallAppActivity finished");
        finish();
    }
}
