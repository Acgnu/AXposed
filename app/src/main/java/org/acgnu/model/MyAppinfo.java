package org.acgnu.model;

import android.graphics.drawable.Drawable;

public class MyAppinfo {
    private String appname;
    private String pkg;
    private String storagepath;
    private Drawable icon;

    public MyAppinfo(){}

    public MyAppinfo(String appname, String pkg, Drawable icon, String storagepath) {
        this.appname = appname;
        this.storagepath = storagepath;
        this.pkg = pkg;
        this.icon = icon;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getStoragepath() {
        return storagepath;
    }

    public void setStoragepath(String storagepath) {
        this.storagepath = storagepath;
    }

    public String getPkg() {
        return pkg;
    }

    public void setPkg(String pkg) {
        this.pkg = pkg;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
}
