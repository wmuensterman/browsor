package com.example.wmuen.trimwebbrowser;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.text.Editable;
import android.text.Html;
import android.text.Layout;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.URLSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BaseWebpageActivity {

    private String urlString;
    private TextView siteContent;
    private EditText addressAndSearchBar;
    private String clickedLinkUrl = "";
    private BookmarkManager bookmarkManager;
    private CharSequence textModeOptions = "Copy URL to clipboardShareOpenOpen in text modeBookmark";
    private int currentFindOnPageIndex;
    private int nextFindOnPageIndex;
    private int prevFindOnPageIndex;

    @Override
    public void showContent(UrlReader urlReader) {
        final EditText addressAndSearchBar = findViewById(R.id.addressAndSearchBar);
        addressAndSearchBar.clearFocus();
        TextView siteContent = findViewById(R.id.siteContent);
        showWebpageInTextMode(urlReader);
        applyTextModeSettings();

        NestedScrollView siteContentNestedScrollview = findViewById(R.id.siteContentNestedScrollView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            siteContentNestedScrollview.setOnScrollChangeListener(new View.OnScrollChangeListener() {
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

    public void showWebpageInTextMode(UrlReader urlReader) {

        String baseUrl = urlReader.getBaseUrl();

        CharSequence sequence = Html.fromHtml(urlReader.getText());
        System.out.println("Here it is, boys!");
        System.out.println(urlReader.getText());
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(sequence);
        URLSpan[] urls = stringBuilder.getSpans(0, sequence.length(), URLSpan.class);
        for (URLSpan span : urls) {
            makeLinkClickable(stringBuilder, span, baseUrl);
        }
        siteContent = findViewById(R.id.siteContent);
        siteContent.setText(stringBuilder);
        siteContent.setMovementMethod(LongClickLinkMovementMethod.getInstance());
    }

    public void applyTextModeSettings() {
        if (siteContent != null) {
            SettingsHolder settingsHolder = SettingsHolder.getInstance();
            siteContent.setTypeface(settingsHolder.getTypeface());
            siteContent.setTextSize(settingsHolder.getFontSize());
        }
    }

    protected void makeLinkClickable(SpannableStringBuilder stringBuilder, final URLSpan span, final String baseUrl) {
        int start = stringBuilder.getSpanStart(span);
        int end = stringBuilder.getSpanEnd(span);
        int flags = stringBuilder.getSpanFlags(span);

        final LongClickableSpan clickable = new LongClickableSpan() {

            @Override
            public void onClick(View view) {
                String url = span.getURL();
                if (!url.startsWith("https://") && !url.startsWith("http://")) {
                    if (!url.startsWith(baseUrl)) {
//                        url = baseUrl.substring(0, baseUrl.lastIndexOf('/')) + '/' + url;
                        url = baseUrl + url;
                    }
                }
                setClickedLinkUrl(url);
                openUrlInNewPageBasedOnSettings(url);
            }

            @Override
            public void onLongClick(View view) {

                String url = span.getURL();
                if (!url.startsWith("https://") && !url.startsWith("http://")) {
                    if (!url.startsWith(baseUrl)) {
//                        url = baseUrl.substring(0, baseUrl.lastIndexOf('/')) + '/' + url;
                        url = baseUrl + url;
                    }
                }
                setClickedLinkUrl(url);

                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
                builder.setTitle(url);
                builder.setCancelable(true);
                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        clickedLinkUrl = "";
                        findViewById(R.id.siteContent).clearFocus();
                    }
                });
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        clickedLinkUrl = "";
                        findViewById(R.id.siteContent).clearFocus();
                    }
                });
                builder.setItems(new CharSequence[]{"Copy URL to clipboard", "Share", "Open site settings", "Open in text mode", "Bookmark"},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                        ClipData clip = ClipData.newPlainText("URL", clickedLinkUrl);
                                        clipboard.setPrimaryClip(clip);
                                        Toast.makeText(MainActivity.this, "Copied " + clickedLinkUrl + " to clipboard.", Toast.LENGTH_SHORT).show();
                                        break;
                                    case 1:
                                        shareLongPressLink(clickedLinkUrl);
                                        break;
                                    case 2:
                                        switchToChooseWebView(clickedLinkUrl);
                                        break;
                                    case 3:
                                        TemporarySettingsHolder temporarySettingsHolder = TemporarySettingsHolder.getInstance();
                                        temporarySettingsHolder.setUseTemporarySettings(true);
                                        temporarySettingsHolder.setTextOnly(true);
                                        openUrlInNewPage(clickedLinkUrl, MainActivity.class);
                                        break;
                                    case 4:
                                        bookmarkManager.addToBookmarks(clickedLinkUrl, clickedLinkUrl, false);
                                        break;
                                }
                            }
                        });
                android.support.v7.app.AlertDialog alertDialog = builder.create();
                alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                alertDialog.show();
            }
        };
        stringBuilder.setSpan(clickable, start, end, flags);
        stringBuilder.removeSpan(span);
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
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
    }

    public void openURL() {
        super.openURL();
    }

    @Override
    public void configureWebView() {
        siteContent = findViewById(R.id.siteContent);

        siteContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickedLinkUrl != "") {
                    siteContent.clearFocus();
                    SettingsHolder settingsHolder = SettingsHolder.getInstance();
                    if (settingsHolder.getTextOnly() == true) {
                        openUrlInNewPage(clickedLinkUrl, MainActivity.class);
                    } else {
                        openUrlInNewPage(clickedLinkUrl, WebviewActivity.class);
                    }
                }
            }
        });
    }

    public void configureAddressAndSearchBar() {
        super.configureAddressAndSearchBar();
    }

    public void configureFindOnPageBar() {
        final EditText findOnPageEditText = (EditText) findViewById(R.id.findOnPageEditText);
        final NestedScrollView nestedScrollView = (NestedScrollView) findViewById(R.id.siteContentNestedScrollView);
        final Layout layout = siteContent.getLayout();
        findOnPageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String criteria = findOnPageEditText.getText().toString().toLowerCase();
                String fullText = siteContent.getText().toString().toLowerCase();
                if (fullText.contains(criteria)) {
                    currentFindOnPageIndex = fullText.indexOf(criteria);
                    int lineNumber = siteContent.getLayout().getLineForOffset(currentFindOnPageIndex);
                    nestedScrollView.scrollTo(0, siteContent.getLayout().getLineTop(lineNumber));
                    SpannableString wordToSpan = new SpannableString(siteContent.getText());
                    wordToSpan.setSpan(new BackgroundColorSpan(Color.TRANSPARENT), 0, fullText.length(), 0);
                    siteContent.setText(wordToSpan, TextView.BufferType.SPANNABLE);
                    wordToSpan.setSpan(new BackgroundColorSpan(Color.rgb(255, 165, 0)), currentFindOnPageIndex, currentFindOnPageIndex + criteria.length(), 0);
                    siteContent.setText(wordToSpan, TextView.BufferType.SPANNABLE);
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
                String fullText = siteContent.getText().toString().toLowerCase();
                if (fullText.contains(criteria)) {
                    nextFindOnPageIndex = fullText.indexOf(criteria, currentFindOnPageIndex + 1);
                    if (nextFindOnPageIndex == currentFindOnPageIndex || nextFindOnPageIndex == -1) {
                        nextFindOnPageIndex = fullText.indexOf(criteria);
                    }
                    int lineNumber = siteContent.getLayout().getLineForOffset(nextFindOnPageIndex);
                    nestedScrollView.scrollTo(0, siteContent.getLayout().getLineTop(lineNumber));
                    currentFindOnPageIndex = nextFindOnPageIndex;
                    SpannableString wordToSpan = new SpannableString(siteContent.getText());
                    wordToSpan.setSpan(new BackgroundColorSpan(Color.TRANSPARENT), 0, fullText.length(), 0);
                    siteContent.setText(wordToSpan, TextView.BufferType.SPANNABLE);
                    wordToSpan.setSpan(new BackgroundColorSpan(Color.rgb(255, 165, 0)), currentFindOnPageIndex, currentFindOnPageIndex + criteria.length(), 0);
                    siteContent.setText(wordToSpan, TextView.BufferType.SPANNABLE);
                }
            }
        });
        ImageButton findPreviousOnPage = (ImageButton) findViewById(R.id.findPreviousOnPage);
        findPreviousOnPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String criteria = findOnPageEditText.getText().toString().toLowerCase();
                String fullText = siteContent.getText().toString().toLowerCase();
                if (fullText.contains(criteria)) {
                    prevFindOnPageIndex = fullText.lastIndexOf(criteria, currentFindOnPageIndex - 1);
                    if (prevFindOnPageIndex == currentFindOnPageIndex || prevFindOnPageIndex == -1) {
                        prevFindOnPageIndex = fullText.lastIndexOf(criteria);
                    }
                    int lineNumber = siteContent.getLayout().getLineForOffset(prevFindOnPageIndex);
                    nestedScrollView.scrollTo(0, siteContent.getLayout().getLineTop(lineNumber));
                    currentFindOnPageIndex = prevFindOnPageIndex;
                    SpannableString wordToSpan = new SpannableString(siteContent.getText());
                    wordToSpan.setSpan(new BackgroundColorSpan(Color.TRANSPARENT), 0, fullText.length(), 0);
                    siteContent.setText(wordToSpan, TextView.BufferType.SPANNABLE);
                    wordToSpan.setSpan(new BackgroundColorSpan(Color.rgb(255, 165, 0)), currentFindOnPageIndex, currentFindOnPageIndex + criteria.length(), 0);
                    siteContent.setText(wordToSpan, TextView.BufferType.SPANNABLE);
                }
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

    public void setAddressBarText(String addressBarText) {
        super.setAddressBarText(addressBarText);
    }

    public void setUpToolbar() {
        super.setUpToolbar();
    }

    @Override
    public void switchToChooseWebView(String urlToOpen) {
        super.switchToChooseWebView(urlToOpen);
    }

    @Override
    public void switchToSettingsView() {
        super.switchToSettingsView();
    }

    @Override
    public void switchToBookmarksView() {
        super.switchToBookmarksView();
    }

    @Override
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

    @Override
    public void setClickedLinkUrl(String click) {
        this.clickedLinkUrl = click;
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
                findViewById(R.id.findOnPageToolbar).setVisibility(View.VISIBLE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyTextModeSettings();
    }

    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}