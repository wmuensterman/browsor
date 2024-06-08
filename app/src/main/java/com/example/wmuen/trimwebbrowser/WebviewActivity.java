package com.example.wmuen.trimwebbrowser;

import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.lang.reflect.Method;

public class WebviewActivity extends BaseWebpageActivity {

    private String urlString;
    private WebView siteContentWebView;
    private EditText addressAndSearchBar;
    private BookmarkManager bookmarkManager;
    //not used in webview at all
    private String clickedLinkUrl = "";
    private String title;

    @Override
    public void showContent(UrlReader urlReader) {
        System.out.println("Showing content");
        final EditText addressAndSearchBar = findViewById(R.id.addressAndSearchBar);
        addressAndSearchBar.clearFocus();

        NestedWebView siteContentWebview = findViewById(R.id.siteContentWebView);
        String url = urlReader.getUrl();
        siteContentWebview.loadUrl(url);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            siteContentWebview.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    FloatingActionButton fab = findViewById(R.id.fab);
                    if (scrollY - oldScrollY > 0) {
                        fab.hide();
                    } else if (scrollY - oldScrollY < 0) {
                        fab.show();
                    }
                }
            });
        }
        TemporarySettingsHolder temporarySettingsHolder = TemporarySettingsHolder.getInstance();
        temporarySettingsHolder.setUseTemporarySettings(false);
    }

    @Override
    public void doOnCreateThings(Bundle savedInstanceState) {
        super.doOnCreateThings(savedInstanceState);
        bookmarkManager = new BookmarkManager(this);
        configureFindOnPageBar();
    }

    public void handleIncomingUrls(Bundle savedInstanceState) {
        super.handleIncomingUrls(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SettingsHolder settingsHolder = SettingsHolder.getInstance();
        setTheme(settingsHolder.getTheme());
        setContentView(R.layout.activity_webview);
        super.onCreate(savedInstanceState);
    }

    public void openURL() {
        super.openURL();
    }

    @Override
    public void configureWebView() {
        siteContentWebView = findViewById(R.id.siteContentWebView);

        final SettingsHolder settingsHolder = SettingsHolder.getInstance();
        final TemporarySettingsHolder temporarySettingsHolder = TemporarySettingsHolder.getInstance();
        if (siteContentWebView != null) {
            if (temporarySettingsHolder.getUseTemporarySettings() == true) {
                siteContentWebView.getSettings().setJavaScriptEnabled(temporarySettingsHolder.getEnableScripts());
                siteContentWebView.getSettings().setLoadsImagesAutomatically(temporarySettingsHolder.getEnableImages());
                siteContentWebView.getSettings().setBuiltInZoomControls(temporarySettingsHolder.getEnableZoom());
            } else {
                siteContentWebView.getSettings().setJavaScriptEnabled(settingsHolder.getEnableScripts());
                siteContentWebView.getSettings().setLoadsImagesAutomatically(settingsHolder.getEnableImages());
                siteContentWebView.getSettings().setBuiltInZoomControls(settingsHolder.getEnableZoom());
            }
            siteContentWebView.getSettings().setDisplayZoomControls(false);
            CookieManager.getInstance().setAcceptCookie(settingsHolder.getSaveCookies());
            CookieManager.getInstance().setAcceptThirdPartyCookies(siteContentWebView, settingsHolder.getSaveThirdPartyCookies());
            registerForContextMenu(siteContentWebView);
            siteContentWebView.setWebViewClient(new WebViewClient() {
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (url != null && (url.startsWith("http://") || url.startsWith("https://"))) {
                        openUrlInNewPageBasedOnSettings(url);
                        return false;
                    } else {
                        return true;
                    }
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    if (view.getTitle() != null && view.getTitle().length() > 0) {
                        setTitle(view.getTitle());
                    } else {
                        setTitle(url);
                    }
                    addPageToHistory(url);
                }
            });
        }
    }

    public void configureAddressAndSearchBar() {
        super.configureAddressAndSearchBar();
    }

    public void configureFindOnPageBar() {
        System.out.println("Configuring find on page bar");
        final EditText findOnPageEditText = (EditText) findViewById(R.id.findOnPageEditText);
        final WebView siteContentWebView = (WebView) findViewById(R.id.siteContentWebView);
        findOnPageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String criteria = findOnPageEditText.getText().toString().toLowerCase();
                siteContentWebView.findAllAsync(criteria);
                try {
                    Method m = WebView.class.getMethod("setFindIsUp", Boolean.TYPE);
                    m.invoke(siteContentWebView, true);
                } catch (Throwable ignored) {
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        ImageButton findNextOnPage = (ImageButton) findViewById(R.id.findNextOnPage);
        findNextOnPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String criteria = findOnPageEditText.getText().toString().toLowerCase();
                siteContentWebView.findNext(true);
            }
        });
        ImageButton findPreviousOnPage = (ImageButton) findViewById(R.id.findPreviousOnPage);
        findPreviousOnPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String criteria = findOnPageEditText.getText().toString().toLowerCase();
                siteContentWebView.findNext(false);
            }
        });
        ImageButton closeFindOnPage = (ImageButton) findViewById(R.id.closeFindOnPage);
        closeFindOnPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.findOnPageToolbar).setVisibility(View.GONE);
            }
        });
    }

    public void setUrlString(String url) {
        super.setUrlString(url);
    }

    public void setTitle(String pageTitle) {
        super.setTitle(pageTitle);
    }

    public void setAddressBarText(String addressBarText) {
        super.setAddressBarText(addressBarText);
    }

    public void setUpToolbar() {
        super.setUpToolbar();
    }

    public void switchToChooseWebView(String urlToOpen) {
        super.switchToChooseWebView(urlToOpen);
    }

    public void switchToSettingsView() {
        super.switchToSettingsView();
    }

    public void switchToBookmarksView() {
        super.switchToBookmarksView();
    }

    public void switchToHistoryView() {
        super.switchToHistoryView();
    }

    public void switchToStartPageView() {
        super.switchToStartPageView();
    }

    public void shareCurrentPage() {
        super.shareCurrentPage();
    }

    public void shareLongPressLink(String link) {
        super.shareLongPressLink(link);
    }

    public void hideKeyboard(View view) {
        super.hideKeyboard(view);
    }

    public void setClickedLinkUrl(String click) {
        super.setClickedLinkUrl(click);
    }

    public void onCreateContextMenu(ContextMenu contextMenu, View
            view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        super.onCreateContextMenu(contextMenu, view, contextMenuInfo);

        final WebView.HitTestResult webViewHitTestResult = siteContentWebView.getHitTestResult();
        if (webViewHitTestResult.getType() == WebView.HitTestResult.SRC_ANCHOR_TYPE) {
            final String longPressUrl = webViewHitTestResult.getExtra();
            contextMenu.setHeaderTitle(longPressUrl);
            contextMenu.add(0, 1, 0, "Copy URL to clipboard").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("URL", longPressUrl);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(WebviewActivity.this, "Copied " + longPressUrl + " to clipboard.", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
            contextMenu.add(0, 1, 0, "Share").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    shareLongPressLink(longPressUrl);
                    return false;
                }
            });
            contextMenu.add(0, 1, 0, "Open site settings").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switchToChooseWebView(longPressUrl);
                    return false;
                }
            });
            contextMenu.add(0, 1, 0, "Open in text mode").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    TemporarySettingsHolder temporarySettingsHolder = TemporarySettingsHolder.getInstance();
                    temporarySettingsHolder.setUseTemporarySettings(true);
                    temporarySettingsHolder.setTextOnly(true);
                    openUrlInNewPage(longPressUrl, MainActivity.class);
                    return false;
                }
            });
            contextMenu.add(0, 1, 0, "Bookmark").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    bookmarkManager.addToBookmarks(longPressUrl, longPressUrl, false);
                    return false;
                }
            });
        }
        if (webViewHitTestResult.getType() == WebView.HitTestResult.IMAGE_TYPE || webViewHitTestResult.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
            contextMenu.setHeaderTitle("Download Image From Below");
            contextMenu.add(0, 1, 0, "Save - Download Image").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    String DownloadImageURL = webViewHitTestResult.getExtra();
                    if (URLUtil.isValidUrl(DownloadImageURL)) {
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(DownloadImageURL));
                        request.allowScanningByMediaScanner();
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                        downloadManager.enqueue(request);
                        Toast.makeText(WebviewActivity.this, "Image Downloaded Successfully.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(WebviewActivity.this, "Failed to download image.", Toast.LENGTH_LONG).show();
                    }
                    return false;
                }
            });
        }
    }

    public void setBookmarkIcon(MenuItem menuItem) {
        super.setBookmarkIcon(menuItem);
    }

    public void loadSharedPreferences() {
        super.loadSharedPreferences();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    public void onContextMenuClosed(Menu menu) {
        super.onContextMenuClosed(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.findOnPage:
                NestedWebView siteContentWebview = findViewById(R.id.siteContentWebView);
                findViewById(R.id.findOnPageToolbar).setVisibility(View.VISIBLE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    public void openUrlInNewPage(String urlToOpen, Class classToOpen) {
        super.openUrlInNewPage(urlToOpen, classToOpen);
    }
}