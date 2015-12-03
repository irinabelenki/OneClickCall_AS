package com.example.oneclickcall;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

public class CallAppItem implements Parcelable, Comparable<CallAppItem> {
    private String name;
    private Drawable image;
    private String packageName;
    private String className;

    public CallAppItem(String name, Drawable image, String packageName, String className) {
        this.name = name;
        this.image = image;
        this.packageName = packageName;
        this.className = className;
    }

    public CallAppItem() {
        this.name = "";
        this.image = null;
    }

    public CallAppItem(Parcel in) {
        name = in.readString();
        Bitmap bitmap = (Bitmap) in.readParcelable(getClass().getClassLoader());
        image = new BitmapDrawable(bitmap);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getClassName() {
        return className;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int describeContents() {
        return this.hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        if (image != null) {
            Bitmap bitmap = (Bitmap) ((BitmapDrawable) image).getBitmap();
            dest.writeParcelable(bitmap, flags);
        } else {
            dest.writeParcelable(null, flags);
        }
    }

    public static final Parcelable.Creator<CallAppItem> CREATOR = new Parcelable.Creator<CallAppItem>() {
        public CallAppItem createFromParcel(Parcel in) {
            return new CallAppItem(in);
        }

        public CallAppItem[] newArray(int size) {
            return new CallAppItem[size];

        }
    };

    public int compareTo(CallAppItem other) {
        if (this.name.equals(other.getName()) &&
                this.packageName.equals(other.getPackageName()) &&
                this.className.equals(other.getClassName())
                ) {
            return 0;
        }
        return -1;
    }
}
