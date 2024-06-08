package com.example.wmuen.trimwebbrowser;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.design.widget.CoordinatorLayout;

import static android.content.Context.MODE_PRIVATE;

public class TemporarySettingsHolder {
    private static TemporarySettingsHolder instance = null;

    private boolean saveHistory = true;
    private boolean saveCookies = true;
    private boolean saveThirdPartyCookies = false;

    private Typeface typeface = Typeface.SANS_SERIF;
    private int fontSize = 14;
    private CoordinatorLayout.LayoutParams fabLocation;
    //webview settings
    private boolean enableImages;
    private boolean enableScripts;
    private boolean enableZoom;
    private boolean textOnly;

    private boolean useTemporarySettings = false;

    public static String GOOGLESUFFIX = "https://www.google.com/search?q=";
    public static String BINGSUFFIX = "https://www.bing.com/search?q=";
    public static String YAHOOSUFFIX = "https://search.yahoo.com/search?q=";
    public static String DUCKDUCKGOSUFFIX = "https://duckduckgo.com/?q=";
    public static String DUCKDUCKGOLITESUFFIX = "https://duckduckgo.com/lite/?q=";
    public static String QWANTSUFFIX = "https://www.qwant.com/?q=";
    public static String QWANTLITESUFFIX = "https://lite.qwant.com/?q=";
    public static String ASKJEEVESSUFFIX = "https://www.ask.com/web?q=";
    public static String YANDEXSUFFIX = "https://yandex.com/search/?text=";
    public static String BRAVESUFFIX = "https://search.brave.com/search?q=";

    private String searchSuffix = GOOGLESUFFIX;

    protected TemporarySettingsHolder() {
        //this is here to block instantiation
    }

    public static TemporarySettingsHolder getInstance() {
        if (instance == null) {
            instance = new TemporarySettingsHolder();
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

    public void setFabLocation(CoordinatorLayout.LayoutParams fabLocation) {
        this.fabLocation = fabLocation;
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

    public boolean getUseTemporarySettings() {
        return this.useTemporarySettings;
    }

    public void setUseTemporarySettings(boolean useTemporarySettings) {
        this.useTemporarySettings = useTemporarySettings;
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

    public void saveEverythingToSharedPreferences(Activity callingActivity) {
        SharedPreferences sharedPreferences = callingActivity.getSharedPreferences("com.example.wmuen.textonlybrowser", MODE_PRIVATE);

        String textOnlyKey = "com.example.wmuen.textonlybrowser.textOnly";
        String fontFamilyKey = "com.example.wmuen.textonlybrowser.fontFamily";
        String fontSizeKey = "com.example.wmuen.textonlybrowser.fontSize";
        String textColorKey = "com.example.wmuen.textonlybrowser.textColor";
        String backgroundColorKey = "com.example.wmuen.textonlybrowser.backgroundColor";
        String enableScriptsKey = "com.example.wmuen.textonlybrowser.enableScripts";
        String enableImagesKey = "com.example.wmuen.textonlybrowser.enableImages";
        String enableZoomKey = "com.example.wmuen.textonlybrowser.enableZoom";
        String searchSuffixKey = "com.example.wmuen.textonlybrowser.searchSuffix";

        sharedPreferences.edit().putString(textOnlyKey, Boolean.toString(this.getTextOnly())).apply();
        sharedPreferences.edit().putString(fontFamilyKey, this.getTypeface().toString()).apply();
        sharedPreferences.edit().putString(fontSizeKey, Integer.toString(this.getFontSize())).apply();
        sharedPreferences.edit().putString(enableScriptsKey, Boolean.toString(this.getEnableScripts())).apply();
        sharedPreferences.edit().putString(enableImagesKey, Boolean.toString(this.getEnableImages())).apply();
        sharedPreferences.edit().putString(enableZoomKey, Boolean.toString(this.getEnableZoom())).apply();
        sharedPreferences.edit().putString(searchSuffixKey, this.getSearchSuffix()).apply();
    }

}
