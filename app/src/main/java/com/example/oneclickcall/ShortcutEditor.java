package com.example.oneclickcall;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

public class ShortcutEditor {

    public enum ACTION {ADD, REMOVE}

    public static void editShortcut(Context context,
                                    String contactName, String phoneNumber,
                                    String packageName, String className,
                                    Bitmap bitmap,
                                    ACTION action) {

        Uri numberUri = Uri.parse("tel:" + phoneNumber);
        Intent callIntent = new Intent(Intent.ACTION_CALL, numberUri);
        callIntent.setClassName(packageName, className);
        Log.i(MainActivity.TAG, "Added shortcut to call app: " + className
                + ", contactName: " + contactName + ", phoneNumber: "
                + phoneNumber);
        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, callIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, contactName);

        int size = (int) context.getResources().getDimension(android.R.dimen.app_icon_size);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, Bitmap.createScaledBitmap(bitmap, size, size, false));

        switch (action) {
            case ADD:
                addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
                break;
            case REMOVE:
                addIntent.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
                break;
        }
        context.sendBroadcast(addIntent);
    }
}
