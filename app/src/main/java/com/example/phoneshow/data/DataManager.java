package com.example.phoneshow.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;

import com.example.phoneshow.R;
import com.example.phoneshow.bean.ThemesBean;
import com.example.phoneshow.phone.CallerOperationManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/10 0010.
 */

public class DataManager {
    private DataManager() {
    }

    private static class DataManagerHolder {
        private static DataManager instance = new DataManager();
    }

    public static DataManager getInstance() {
        return DataManagerHolder.instance;
    }

    public List<ThemesBean> getSimulatedData(Context context) {
        List<ThemesBean> themesList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            ThemesBean themesBean = new ThemesBean();
            if (i == 0) {
                themesBean.setThemeName("TestNameOne");
                themesBean.setCollection(getCollection(context,"item1"));
                themesBean.setUriStr("android.resource://com.example.phoneshow" + "/" + R.raw.background_1);
                themesBean.setUse(getUse(context,"item1"));
                themesBean.setCacheBg(CallerOperationManager.getInstance().cacheThemeBg(context,
                        "android.resource://com.example.phoneshow" + "/" + R.raw.background_1));
                themesList.add(themesBean);
            } else if (i == 1) {
                themesBean.setThemeName("TestNameTwo");
                themesBean.setCollection(getCollection(context,"item2"));
                themesBean.setUriStr("android.resource://com.example.phoneshow" + "/" + R.raw.background_2);
                themesBean.setUse(getUse(context,"item2"));
                themesBean.setCacheBg(CallerOperationManager.getInstance().cacheThemeBg(context,
                        "android.resource://com.example.phoneshow" + "/" + R.raw.background_2));
                themesList.add(themesBean);
            } else if (i == 2) {
                themesBean.setThemeName("TestNameThree");
                themesBean.setCollection(getCollection(context,"item3"));
                themesBean.setUriStr("android.resource://com.example.phoneshow" + "/" + R.raw.background_3);
                themesBean.setUse(getUse(context,"item3"));
                themesBean.setCacheBg(CallerOperationManager.getInstance().cacheThemeBg(context,
                        "android.resource://com.example.phoneshow" + "/" + R.raw.background_3));
                themesList.add(themesBean);
            }
        }
        return themesList;
    }

    public void saveCollection(Context context,String s,Boolean b){
        SharedPreferences sp = context.getSharedPreferences("collection", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (s.endsWith("item1")){
            editor.putBoolean("item1", b);
        } else if (s.endsWith("item2")){
            editor.putBoolean("item2", b);
        } else if (s.endsWith("item3")){
            editor.putBoolean("item3", b);
        }
        editor.commit();
    }

    private boolean getCollection(Context context,String s){
        SharedPreferences sp = context.getSharedPreferences("collection", Context.MODE_PRIVATE);
        boolean b = false;
        if (s.endsWith("item1")){
            b = sp.getBoolean("item1", false);
        } else if (s.endsWith("item2")){
            b = sp.getBoolean("item2", false);
        } else if (s.endsWith("item3")){
            b = sp.getBoolean("item3", false);
        }
        return b;
    }

    public void saveUse(Context context,String s,Boolean b){
        SharedPreferences sp = context.getSharedPreferences("use", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (s.endsWith("item1")){
            editor.putBoolean("item1", b);
        } else if (s.endsWith("item2")){
            editor.putBoolean("item2", b);
        } else if (s.endsWith("item3")){
            editor.putBoolean("item3", b);
        }
        editor.commit();
    }

    public boolean getUse(Context context,String s){
        SharedPreferences sp = context.getSharedPreferences("use", Context.MODE_PRIVATE);
        boolean b = false;
        if (s.endsWith("item1")){
            b = sp.getBoolean("item1", true);
        } else if (s.endsWith("item2")){
            b = sp.getBoolean("item2", false);
        } else if (s.endsWith("item3")){
            b = sp.getBoolean("item3", false);
        }
        return b;
    }

    public void savePhoneState(Context context,boolean isOpen){
        SharedPreferences sp = context.getSharedPreferences("phoneState", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("isOpen",isOpen);
        editor.commit();
    }

    public boolean getPhoneState(Context context){
        SharedPreferences sp = context.getSharedPreferences("phoneState", Context.MODE_PRIVATE);
        return sp.getBoolean("isOpen",true);
    }
}
