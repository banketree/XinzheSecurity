package com.linxinzhe.android.xinzhesecurity.domain;

import android.graphics.drawable.Drawable;

/**
 * Created by linxinzhe on 2015/3/10.
 */
public class AppInfo {
    private Drawable icon;
    private String name;
    private String packname;
    private long memory;
    private boolean inRom;
    private boolean userApp;
    public Drawable getIcon() {
        return icon;
    }
    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPackname() {
        return packname;
    }
    public void setPackname(String packname) {
        this.packname = packname;
    }
    public boolean isInRom() {
        return inRom;
    }
    public void setInRom(boolean inRom) {
        this.inRom = inRom;
    }
    public boolean isUserApp() {
        return userApp;
    }
    public void setUserApp(boolean userApp) {
        this.userApp = userApp;
    }
    @Override
    public String toString() {
        return "AppInfo [name=" + name + ", packname=" + packname + ", inRom="
                + inRom + ", userApp=" + userApp + "]";
    }

    public long getMemory() {
        return memory;
    }

    public void setMemory(long memory) {
        this.memory = memory;
    }
}
