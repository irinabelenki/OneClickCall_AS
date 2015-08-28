package com.example.oneclickcall;

public class ShortcutItem {
    private int id;
    private String name;
    private String application;
    private String phone;
    private String packageName;
    private String className;

    public ShortcutItem() {
        this.id = -1;
    }

    public ShortcutItem(int id, String name, String application,
                        String phone,
                        String packageName, String className) {
        super();
        this.id = id;
        this.name = name;
        this.application = application;
        this.phone = phone;
        this.packageName = packageName;
        this.className = className;
    }

    public ShortcutItem(String name, String application, String phone,
                        String packageName, String className) {
        super();
        this.name = name;
        this.application = application;
        this.phone = phone;
        this.packageName = packageName;
        this.className = className;
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

    @Override
    public String toString() {
        return "ShortcutItem: id=" + id + ", name=" + name +
                ", application=" + application + ", phone=" + phone +
                ", packageName=" + packageName + ", className=" + className;
    }

}
