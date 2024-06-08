package com.example.wmuen.trimwebbrowser;

import android.graphics.Typeface;
import android.support.design.widget.CoordinatorLayout;

public class SettingsHolder {
    private static SettingsHolder instance = null;

    private boolean saveHistory = true;
    private boolean saveCookies = true;
    private boolean saveThirdPartyCookies = false;

    private int theme = R.style.AppThemeDay;

    private Typeface typeface = Typeface.SANS_SERIF;
    private int fontSize = 14;
    private CoordinatorLayout.LayoutParams fabLocation;
    //webview settings
    private boolean enableImages = false;
    private boolean enableScripts = false;
    private boolean enableZoom = false;
    private boolean textOnly = true;

    public static String ASKJEEVESSUFFIX = "https://www.ask.com/web?q=";
    public static String BINGSUFFIX = "https://www.bing.com/search?q=";
    public static String BRAVESUFFIX = "https://search.brave.com/search?q=";
    public static String DUCKDUCKGOSUFFIX = "https://duckduckgo.com/?q=";
    public static String DUCKDUCKGOLITESUFFIX = "https://duckduckgo.com/lite/?q=";
    public static String GOOGLESUFFIX = "https://www.google.com/search?q=";
    public static String QWANTSUFFIX = "https://www.qwant.com/?q=";
    public static String QWANTLITESUFFIX = "https://lite.qwant.com/?q=";
    public static String YAHOOSUFFIX = "https://search.yahoo.com/search?q=";
    public static String YANDEXSUFFIX = "https://yandex.com/search/?text=";

    private String searchSuffix = GOOGLESUFFIX;

    protected SettingsHolder() {
        //this is here to block instantiation
    }

    public static SettingsHolder getInstance() {
        if (instance == null) {
            instance = new SettingsHolder();
        }
        return instance;
    }

    public Boolean getSaveHistory() {
        return this.saveHistory;
    }

    public void setSaveHistory(Boolean history) {
        this.saveHistory = history;
    }

    public Boolean getSaveCookies() {
        return this.saveCookies;
    }

    public void setSaveCookies(Boolean cookies) {
        this.saveCookies = cookies;
    }

    public Boolean getSaveThirdPartyCookies() {
        return this.saveThirdPartyCookies;
    }

    public int getTheme() {
        return this.theme;
    }

    public void setTheme(int theme) {
        this.theme = theme;
    }

    public void setSaveThirdPartyCookies(Boolean thirdPartyCookies) {
        this.saveThirdPartyCookies = thirdPartyCookies;
    }

    public Typeface getTypeface() {
        return typeface;
    }

    public void setTypeface(Typeface newTypeface) {
        this.typeface = newTypeface;
    }

    public int getFontSize() {
        return this.fontSize;
    }

    public void setFontSize(int f) {
        this.fontSize = f;
    }

    public CoordinatorLayout.LayoutParams getFabLocation() {
        return this.fabLocation;
    }

    public boolean getEnableImages() {
        return this.enableImages;
    }

    public void setEnableImages(boolean enable) {
        this.enableImages = enable;
    }

    public boolean getEnableScripts() {
        return this.enableScripts;
    }

    public void setEnableScripts(boolean enable) {
        this.enableScripts = enable;
    }

    public boolean getEnableZoom() {
        return this.enableZoom;
    }

    public void setEnableZoom(boolean enable) {
        this.enableZoom = enable;
    }

    public boolean getTextOnly() {
        return this.textOnly;
    }

    public void setTextOnly(boolean enable) {
        this.textOnly = enable;
    }

    public String getSearchSuffix() {
        return this.searchSuffix;
    }

    public void setSearchSuffix(String s) {
        this.searchSuffix = s;
    }

}
