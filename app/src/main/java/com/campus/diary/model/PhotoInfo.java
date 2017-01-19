package com.campus.diary.model;

import com.droi.sdk.core.DroiExpose;
import com.droi.sdk.core.DroiFile;
import com.droi.sdk.core.DroiObject;

/**
 * Created by Allen.Zeng on 2016/12/15.
 */
public class PhotoInfo extends DroiObject{

    @DroiExpose
    private DroiFile icon;

    private String url;

    private int w;

    private int h;

    public DroiFile getIcon() {
        return icon;
    }

    public void setIcon(DroiFile icon) {
        this.icon = icon;
    }

    public String getIconUrl() {
        return url;
    }

    public void setIconUrl(String url) {
        this.url = url;
    }

    public int getIconW() {
        return w;
    }

    public void setIconW(int w) {
        this.w = w;
    }

    public int getIconH() {
        return h;
    }

    public void setIconH(int h) {
        this.h = h;
    }
}
