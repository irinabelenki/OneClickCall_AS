package com.example.oneclickcall;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class ShortcutEditor {

	public static void addShortcut(	Context context, 
									String contactName, String phoneNumber, 
									String packageName, String className) {

		Uri numberUri = Uri.parse("tel:" + phoneNumber);
		Intent callIntent = new Intent(Intent.ACTION_CALL, numberUri);
		callIntent.setClassName(packageName, className);
		Log.i(MainActivity.TAG, "Added shortcut to call app: " + className
				+ ", contactName: " + contactName + ", phoneNumber: "
				+ phoneNumber);
		Intent addIntent = new Intent();
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, callIntent);
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, contactName);
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
				Intent.ShortcutIconResource.fromContext(context,
						R.drawable.ic_launcher));

		addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		context.sendBroadcast(addIntent);
	}
	
	public static void removeShortcut(	Context context, 
										String contactName,	String phoneNumber, 
										String packageName, String className) {
		Uri numberUri = Uri.parse("tel:" + phoneNumber);
		Intent callIntent = new Intent(Intent.ACTION_CALL, numberUri);
		callIntent.setClassName(packageName, className);
		Log.i(MainActivity.TAG, "Added shortcut to call app: " + className
				+ ", contactName: " + contactName + ", phoneNumber: "
				+ phoneNumber);
		Intent addIntent = new Intent();
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, callIntent);
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, contactName);
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
				Intent.ShortcutIconResource.fromContext(context,
						R.drawable.ic_launcher));

		addIntent.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
		context.sendBroadcast(addIntent);
	}
}
