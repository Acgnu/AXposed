package org.acgnu.model;

public class MyAppinfo {
    private String appname;
    private String storagepath;

    public MyAppinfo(){}

    public MyAppinfo(String appname, String storagepath) {
        this.appname = appname;
        this.storagepath = storagepath;
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
}
