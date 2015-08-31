package com.example.oneclickcall;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Parcel;
import android.os.Parcelable;

public class ShortcutItem implements Parcelable {
    private int id;
    private String name;
    private String application;
    private String phone;
    private String packageName;
    private String className;
    private String contactId;

    public ShortcutItem() {
        this.id = -1;
    }

    public ShortcutItem(int id, String name, String application,
                        String phone,
                        String packageName, String className, String contactId) {
        super();
        this.id = id;
        this.name = name;
        this.application = application;
        this.phone = phone;
        this.packageName = packageName;
        this.className = className;
        this.contactId = contactId;
    }

    public ShortcutItem(String name, String application, String phone,
                        String packageName, String className, String contactId) {
        super();
        this.name = name;
        this.application = application;
        this.phone = phone;
        this.packageName = packageName;
        this.className = className;
        this.contactId = contactId;
    }

    public ShortcutItem(ShortcutItem other) {
        id = other.getId();
        name = other.getName();
        application = other.getApplication();
        phone = other.getPhone();
        packageName = other.getPackageName();
        className = other.getClassName();
        contactId = other.getContactId();
    }

    public ShortcutItem(Parcel in) {
        id = in.readInt();
        name = in.readString();
        application = in.readString();
        phone = in.readString();
        packageName = in.readString();
        className = in.readString();
        contactId = in.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    @Override
    public String toString() {
        return "ShortcutItem: id=" + id + ", name=" + name +
                ", application=" + application + ", phone=" + phone +
                ", packageName=" + packageName + ", className=" + className +
                ", contactId=" + contactId;
    }

    @Override
    public int describeContents() {
        return this.hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(application);
        dest.writeString(phone);
        dest.writeString(packageName);
        dest.writeString(className);
        dest.writeString(contactId);
    }

    public static final Parcelable.Creator<ShortcutItem> CREATOR = new Parcelable.Creator<ShortcutItem>() {
        public ShortcutItem createFromParcel(Parcel in) {
            return new ShortcutItem(in);
        }

        public ShortcutItem[] newArray(int size) {
            return new ShortcutItem[size];

        }
    };

    public boolean isFilled() {
        if (name != null && application != null && phone != null &&
                packageName != null && className != null &&
                contactId != null)
            return true;
        return false;
    }

}
