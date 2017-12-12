package com.example.phoneshow.bean;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by Administrator on 2017/12/10 0010.
 */

public class ThemesBean {
    private String themeName;
    private boolean isCollection;
    private boolean isUse;
    private String uriStr;
    private Bitmap cacheBg;

    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }

    public boolean isCollection() {
        return isCollection;
    }

    public void setCollection(boolean collection) {
        isCollection = collection;
    }

    public boolean isUse() {
        return isUse;
    }

    public void setUse(boolean use) {
        isUse = use;
    }

    public String getUriStr() {
        return uriStr;
    }

    public void setUriStr(String uriStr) {
        this.uriStr = uriStr;
    }

    public Bitmap getCacheBg() {
        return cacheBg;
    }

    public void setCacheBg(Bitmap cacheBg) {
        this.cacheBg = cacheBg;
    }

    @Override
    public String toString() {
        return "ThemesBean{" +
                "themeName='" + themeName + '\'' +
                ", isCollection=" + isCollection +
                ", isUse=" + isUse +
                ", uriStr='" + uriStr + '\'' +
                ", cacheBg=" + cacheBg +
                '}';
    }
}
