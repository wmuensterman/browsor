package com.example.wmuen.trimwebbrowser;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.List;

public abstract class BaseWebpageActivity extends AppCompatActivity {

    private String urlString;
    private WebView siteContentWebView;
    private EditText addressAndSearchBar;
    private BookmarkManager bookmarkManager;
    private HistoryManager historyManager;
    //not used in webview at all
    private String clickedLinkUrl = "";
    private String title;

    public void showContent(UrlReader urlReader) {
    }

    public void doOnCreateThings(Bundle savedInstanceState) {
        bookmarkManager = new BookmarkManager(this);
        historyManager = new HistoryManager(this);
        setUpToolbar();
        configureWebView();
        configureAddressAndSearchBar();
        loadSharedPreferences();

        //todo: make bookmarkManager.getJsonArrayFromFile make a file if there is not one, and then set the jsonArray
        //todo: rename the method to something that makes sense
        //todo: the current setup is too confusing
        JSONArray jsonArrayFromFile = bookmarkManager.getJsonArrayFromFile("Bookmarks");
        //if there is no file, the bookmark manager will create one. I put the code in the getArrayFromFile method.
        BookmarksHolder bookmarksHolder = BookmarksHolder.getInstance();
        //this is junky, but but getting an array from the file twice is the easiest way I can think to make sure that I don't get a null array
        bookmarksHolder.setJsonArray(bookmarkManager.getJsonArrayFromFile("Bookmarks"));
        bookmarksHolder.setArrayList(bookmarksHolder.getArrayListFromJsonArray());

        //todo: make historyManager.getJsonArrayFromFile make a file if there is not one, and then set the jsonArray
        //todo: rename the method to something that makes sense
        //todo: the current setup is too confusing
        JSONArray historyJsonArrayFromFile = historyManager.getJsonArrayFromFile("History");
        //if there is no file, the history manager will create one. I put the code in the getArrayFromFile method.
        HistoryHolder historyHolder = HistoryHolder.getInstance();
        //this is junky, but but getting an array from the file twice is the easiest way I can think to make sure that I don't get a null array
        historyHolder.setJsonArray(historyManager.getJsonArrayFromFile("History"));
        historyHolder.setArrayList(historyHolder.getArrayListFromJsonArray());

        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToChooseWebView(urlString);
            }
        });

        handleIncomingUrls(savedInstanceState);
    }

    public void handleIncomingUrls(Bundle savedInstanceState) {
        //Below are all the ways you can enter into a website
        //Set the urlString correctly, and then open it at the end

        if (savedInstanceState != null) {
            urlString = savedInstanceState.getString(urlString);
        }

        Uri uriFromClickingALink = this.getIntent().getData();
        if (uriFromClickingALink != null) {
            try {
                urlString = uriFromClickingALink.toString();
                openUrlInNewPageBasedOnSettings(urlString);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Intent intentFromAnotherAppOrActivity = getIntent();
        String actionFromIntentFromAnotherApp = intentFromAnotherAppOrActivity.getAction();
        String typeOfIntentFromAnotherApp = intentFromAnotherAppOrActivity.getType();
        //if from the share button in another app
        if (Intent.ACTION_SEND.equals(actionFromIntentFromAnotherApp) && typeOfIntentFromAnotherApp != null) {
            if ("text/plain".equals(typeOfIntentFromAnotherApp)) {
                String urlStringFromAnotherApp = intentFromAnotherAppOrActivity.getStringExtra(Intent.EXTRA_TEXT);
                if (urlStringFromAnotherApp != null) {
                    urlString = urlStringFromAnotherApp;
                    openUrlInNewPageBasedOnSettings(urlString);
                }
            }
        } else {//else if from another activity
            Bundle bundleFromAnotherActivity = intentFromAnotherAppOrActivity.getExtras();
            if (bundleFromAnotherActivity != null) {
                String urlStringFromAnotherActivity = (String) bundleFromAnotherActivity.getString("UrlString");
                if (urlStringFromAnotherActivity != null) {
                    urlString = urlStringFromAnotherActivity;
                }
            }
        }

        if (urlString != null) {
            openURL();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doOnCreateThings(savedInstanceState);
    }

    public void openURL() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final UrlReader urlReader = new UrlReader(BaseWebpageActivity.this);
                        if (urlString != null) {
                            urlReader.setURL(urlString);
                            urlReader.execute();
                        }
                    }
                });
            }
        }).start();
    }

    public void addPageToHistory(String url) {
        SettingsHolder settingsHolder = SettingsHolder.getInstance();
        if (title != null) {
            if (title.trim() != "") {
                bookmarkManager.updateBookmarkTitle(url, title);
            }
        }
        if (settingsHolder.getSaveHistory() == true) {
            if (title != null) {
                if (title.trim() != "") {
                    historyManager.addToHistory(url, String.valueOf(Html.fromHtml(title)), false);
                }
            } else {
                historyManager.addToHistory(url, url, false);
            }
        }
    }

    public void configureWebView() {
    }

    public void configureAddressAndSearchBar() {
        addressAndSearchBar = findViewById(R.id.addressAndSearchBar);
        //hide keyboard on losing focus
        addressAndSearchBar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        addressAndSearchBar.clearFocus();
        if (urlString != "") {
            addressAndSearchBar.setText(urlString);
        }
        //when you enter something in the address bar
        addressAndSearchBar.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && keyCode == KeyEvent.KEYCODE_ENTER) {
                    addressAndSearchBar.clearFocus();
                    openUrlInNewPageBasedOnSettings(addressAndSearchBar.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }

    public void setUrlString(String url) {
        this.urlString = url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAddressBarText(String addressBarText) {
        addressAndSearchBar.setText(addressBarText);
    }

    public void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    public void switchToChooseWebView(String urlToOpen) {
        Intent intent = new Intent(BaseWebpageActivity.this, ChooseWebviewActivity.class);
        intent.putExtra("UrlString", urlToOpen);
        startActivity(intent);
    }

    public void switchToSettingsView() {
        Intent intent = new Intent(BaseWebpageActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    public void switchToBookmarksView() {
        Intent bookmarkActivityIntent = new Intent(BaseWebpageActivity.this, BookmarksActivity.class);
        startActivity(bookmarkActivityIntent);
    }

    public void switchToHistoryView() {
        Intent historyActivityIntent = new Intent(BaseWebpageActivity.this, HistoryActivity.class);
        startActivity(historyActivityIntent);
    }

    public void switchToStartPageView() {
        Intent startPageActivityIntent = new Intent(BaseWebpageActivity.this, StartPageActivity.class);
        startActivity(startPageActivityIntent);
    }

    public void shareCurrentPage() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, Html.fromHtml(title));
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, urlString);
        startActivity(Intent.createChooser(sharingIntent, title));
    }

    public void shareLongPressLink(String link) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Shared from Browsor");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, link);
        startActivity(Intent.createChooser(sharingIntent, "Shared from Browsor"));
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    //not used in webview at all
    public void setClickedLinkUrl(String click) {
        this.clickedLinkUrl = click;
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View
            view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        super.onCreateContextMenu(contextMenu, view, contextMenuInfo);
    }

    public void setBookmarkIcon(MenuItem menuItem) {
        BookmarksHolder bookmarkJsonHolder = BookmarksHolder.getInstance();
        if (bookmarkJsonHolder.getAllUrls().contains(urlString)) {
            menuItem.setTitle("Remove bookmark");
        } else {
            menuItem.setTitle("Add bookmark");
        }
    }

    public void loadSharedPreferences() {
        SettingsHolder settingsHolder = SettingsHolder.getInstance();
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.wmuen.textonlybrowser", Context.MODE_PRIVATE);

        String saveHistoryKey = "com.example.wmuen.textonlybrowser.saveHistory";
        String saveCookiesKey = "com.example.wmuen.textonlybrowser.saveCookies";
        String saveThirdPartyCookiesKey = "com.example.wmuen.textonlybrowser.saveThirdPartyCookies";

        String textOnlyKey = "com.example.wmuen.textonlybrowser.textOnly";
        String enableScriptsKey = "com.example.wmuen.textonlybrowser.enableScripts";
        String enableImagesKey = "com.example.wmuen.textonlybrowser.enableImages";
        String enableZoomKey = "com.example.wmuen.textonlybrowser.enableZoom";
        String fontFamilyKey = "com.example.wmuen.textonlybrowser.fontFamily";
        String fontSizeKey = "com.example.wmuen.textonlybrowser.fontSize";
        String textColorKey = "com.example.wmuen.textonlybrowser.textColor";
        String backgroundColorKey = "com.example.wmuen.textonlybrowser.backgroundColor";
        String searchSuffixKey = "com.example.wmuen.textonlybrowser.searchSuffix";
        String themeKey = "com.example.wmuen.textonlybrowser.theme";

        String saveHistory = sharedPreferences.getString(saveHistoryKey, "true");
        String saveCookies = sharedPreferences.getString(saveCookiesKey, "true");
        String saveThirdPartyCookies = sharedPreferences.getString(saveThirdPartyCookiesKey, "true");

        String textOnly = sharedPreferences.getString(textOnlyKey, "false");
        String enableScripts = sharedPreferences.getString(enableScriptsKey, "true");
        String enableImages = sharedPreferences.getString(enableImagesKey, "true");
        String enableZoom = sharedPreferences.getString(enableZoomKey, "true");
        String fontFamily = sharedPreferences.getString(fontFamilyKey, Typeface.SANS_SERIF.toString());
        String fontSize = sharedPreferences.getString(fontSizeKey, "14");
        String textColor = sharedPreferences.getString(textColorKey, "#000000");
        String backgroundColor = sharedPreferences.getString(backgroundColorKey, "#ffffff");
        String searchSuffix = sharedPreferences.getString(searchSuffixKey, settingsHolder.GOOGLESUFFIX);
        String theme = sharedPreferences.getString(themeKey, String.valueOf(R.style.AppThemeDay));

        settingsHolder.setSaveHistory(Boolean.valueOf(saveHistory));
        settingsHolder.setSaveCookies(Boolean.valueOf(saveCookies));
        settingsHolder.setSaveThirdPartyCookies(Boolean.valueOf(saveThirdPartyCookies));

        settingsHolder.setTextOnly(Boolean.valueOf(textOnly));
        settingsHolder.setEnableScripts(Boolean.valueOf(enableScripts));
        settingsHolder.setEnableImages(Boolean.valueOf(enableImages));
        settingsHolder.setEnableZoom(Boolean.valueOf(enableZoom));

        if (fontFamily != null && fontFamily != "") {
            if (fontFamily.equals(Typeface.SANS_SERIF.toString())) {
                settingsHolder.setTypeface(Typeface.SANS_SERIF);
            } else if (fontFamily.equals(Typeface.SERIF.toString())) {
                settingsHolder.setTypeface(Typeface.SERIF);
            } else if (fontFamily.equals(Typeface.MONOSPACE.toString())) {
                settingsHolder.setTypeface(Typeface.MONOSPACE);
            }
        }

        if (fontSize != null && fontSize != "") {
            settingsHolder.setFontSize(Integer.parseInt(fontSize));
        }

        settingsHolder.setSearchSuffix(searchSuffix);
        settingsHolder.setTheme(Integer.parseInt(theme));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        setBookmarkIcon(menu.getItem(2));
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        invalidateOptionsMenu();
        setBookmarkIcon(menu.getItem(2));
        return true;
    }

    //not used in webview
    @Override
    public void onContextMenuClosed(Menu menu) {
        clickedLinkUrl = "";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                switchToSettingsView();
                break;
            case R.id.share:
                shareCurrentPage();
                break;
            case R.id.copy:
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("URL", urlString);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "Copied " + urlString + " to clipboard.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bookmarkThisPage:
                bookmarkManager.addToBookmarks(urlString, String.valueOf(Html.fromHtml(title)), true);
                setBookmarkIcon(item);
                break;
            case R.id.bookmarks:
                switchToBookmarksView();
                break;
            case R.id.history:
                switchToHistoryView();
                break;
            case R.id.startPage:
                switchToStartPageView();
                break;
        }
        //I'll just keep this line
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        addressAndSearchBar.setText(urlString);
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        //I'll keep this line
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    public void openUrlInNewPage(String urlToOpen, Class classToOpen) {
        Intent intent = new Intent(BaseWebpageActivity.this, classToOpen);
        intent.putExtra("UrlString", urlToOpen);

        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(urlToOpen));
        if (shouldOpenInAnotherApp(i)) {
            Intent chooser = Intent.createChooser(i, urlToOpen);
            if (i.resolveActivity(getPackageManager()) != null) {
                startActivity(chooser);
            }
        } else {
            startActivity(intent);
        }

    }

    //from Lightning Browser
    //https://github.com/anthonycr/Lightning-Browser/blob/dev/app/src/main/java/acr/browser/lightning/utils/IntentUtils.java
    public boolean shouldOpenInAnotherApp(Intent intent) {
        PackageManager packageManager = this.getPackageManager();

        List<ResolveInfo> handlers = packageManager.queryIntentActivities(intent, PackageManager.GET_RESOLVED_FILTER);
        if (handlers == null || handlers.isEmpty()) {
            return false;
        }
        for (ResolveInfo resolveInfo : handlers) {
            IntentFilter intentFilter = resolveInfo.filter;
            if (intentFilter == null) {
                continue;
            }
            if (intentFilter.countDataAuthorities() == 0) {
                continue;
            }
            return true;
        }
        return false;
    }

    public void openUrlInNewPageBasedOnSettings(String urlToOpen) {
        SettingsHolder settingsHolder = SettingsHolder.getInstance();
        TemporarySettingsHolder temporarySettingsHolder = TemporarySettingsHolder.getInstance();
        if (temporarySettingsHolder.getUseTemporarySettings() == true) {
            if (temporarySettingsHolder.getTextOnly() == true) {
                openUrlInNewPage(urlToOpen, MainActivity.class);

            } else {
                openUrlInNewPage(urlToOpen, WebviewActivity.class);
            }
        } else {
            if (settingsHolder.getTextOnly() == true) {
                openUrlInNewPage(urlToOpen, MainActivity.class);
            } else {
                openUrlInNewPage(urlToOpen, WebviewActivity.class);
            }
        }
    }
}