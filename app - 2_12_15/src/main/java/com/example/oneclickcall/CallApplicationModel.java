package com.example.oneclickcall;

import android.graphics.drawable.Drawable;

/**
 * Created by Irina on 11/4/2015.
 */
public class CallApplicationModel {

    private String callAppName;
    private Drawable callAppIcon;

    public CallApplicationModel(String callAppName, Drawable callAppIcon) {
        this.callAppName = callAppName;
        this.callAppIcon = callAppIcon;
    }

    public void setcallAppName(String callAppName) {
        this.callAppName = callAppName;
    }

    public String getcallAppName() {
        return callAppName;
    }

    public void setCallAppIcon(Drawable callAppIcon) {
        this.callAppIcon = callAppIcon;
    }

    public Drawable getCallAppIcon() {
        return callAppIcon;
    }
}