package org.acgnu.model;

public class MyAppinfo {
    private String appname;
    private String pkg;
    private String storagepath;

    public MyAppinfo(){}

    public MyAppinfo(String appname, String pkg, String storagepath) {
        this.appname = appname;
        this.storagepath = storagepath;
        this.pkg = pkg;
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
}
