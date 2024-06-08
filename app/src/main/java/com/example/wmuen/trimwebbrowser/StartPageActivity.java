package com.example.wmuen.trimwebbrowser;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;

public class StartPageActivity extends BaseWebpageActivity {

    private String clickedLinkUrl = "";
    private BookmarkManager bookmarkManager;
    private RecyclerView bookmarkRecyclerView;
    private RecyclerView.LayoutManager bookmarkLayoutManager;
    private BookmarkAdapter bookmarkAdapter;
    private BookmarksHolder bookmarksHolder;
    private ArrayList<JSONObject> bookmarkTempData;
    private HistoryManager historyManager;
    private RecyclerView historyRecyclerView;
    private RecyclerView.LayoutManager historyLayoutManager;
    private HistoryAdapter historyAdapter;
    private HistoryHolder historyHolder;
    private ArrayList<JSONObject> historyTempData;

    @Override
    public void showContent(UrlReader urlReader) {
    }

    @Override
    public void doOnCreateThings(Bundle savedInstanceState) {
        super.doOnCreateThings(savedInstanceState);
        bookmarkManager = new BookmarkManager(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadSharedPreferences();
        SettingsHolder settingsHolder = SettingsHolder.getInstance();
        setTheme(settingsHolder.getTheme());
        setContentView(R.layout.activity_startpage);
        super.onCreate(savedInstanceState);

        bookmarkRecyclerView = (RecyclerView) findViewById(R.id.bookmarkRecyclerView);
        bookmarkRecyclerView.setHasFixedSize(false);
        bookmarkRecyclerView.setItemViewCacheSize(1024);
        bookmarkLayoutManager = new LinearLayoutManager(this);
        bookmarkRecyclerView.setLayoutManager(bookmarkLayoutManager);
        bookmarkManager = new BookmarkManager(this);
        bookmarksHolder = BookmarksHolder.getInstance();
        bookmarkTempData = new ArrayList<JSONObject>(bookmarksHolder.getShortenedArrayList());
        bookmarkAdapter = new BookmarkAdapter(bookmarksHolder.getShortenedArrayList(), this, bookmarkManager);
        bookmarkAdapter.setTempData(bookmarkTempData);
        bookmarkRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        bookmarkRecyclerView.setAdapter(bookmarkAdapter);

        historyRecyclerView = (RecyclerView) findViewById(R.id.historyRecyclerView);
        historyRecyclerView.setHasFixedSize(false);
        historyRecyclerView.setItemViewCacheSize(1024);
        historyLayoutManager = new LinearLayoutManager(this);
        //don't reverse because the short list is already in reverse order
//        ((LinearLayoutManager) historyLayoutManager).setReverseLayout(true);
//        ((LinearLayoutManager) historyLayoutManager).setStackFromEnd(true);
        historyRecyclerView.setLayoutManager(historyLayoutManager);
        historyManager = new HistoryManager(this);
        historyHolder = HistoryHolder.getInstance();
        //If you don't make tempData a new ArrayList, tempData is simply a pointer to historyHolder.getArrayList()
        historyTempData = new ArrayList<JSONObject>(historyHolder.getShortenedArrayList());
        //Adapter seems to deal with its own set of data by itself.
        //Therefore, I should keep track of changes in tempData, and then make the actual changes only when I leave the activity.
        historyAdapter = new HistoryAdapter(historyHolder.getShortenedArrayList(), this, historyManager);
        historyAdapter.setTempData(historyTempData);
        historyRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        historyRecyclerView.setAdapter(historyAdapter);

        Button moreBookmarks = findViewById(R.id.moreBookmarks);
        moreBookmarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToBookmarksView();
            }
        });

        Button moreHistory = findViewById(R.id.moreHistory);
        moreHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToHistoryView();
            }
        });
    }

    @Override
    public void openUrlInNewPageBasedOnSettings(String urlToOpen) {
        super.openUrlInNewPageBasedOnSettings(urlToOpen);
//        finish();
    }

    public void openURL() {
        super.openURL();
    }

    @Override
    public void configureWebView() {
    }

    public void configureAddressAndSearchBar() {
        super.configureAddressAndSearchBar();
    }

    public void setUrlString(String url) {
        super.setUrlString(url);
    }

    public void setAddressBarText(String addressBarText) {
        super.setAddressBarText(addressBarText);
    }

    public void setUpToolbar() {
//        super.setUpToolbar();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
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
//        super.setBookmarkIcon(menuItem);
    }

    public void loadSharedPreferences() {
        super.loadSharedPreferences();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
//        return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.toolbar_menu_startpage, menu);
        return true;
    }

    public void onContextMenuClosed(Menu menu) {
        super.onContextMenuClosed(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                switchToSettingsView();
                break;
            case R.id.bookmarks:
                switchToBookmarksView();
                break;
            case R.id.history:
                switchToHistoryView();
                break;
        }
        //I'll just keep this line
//        return super.onOptionsItemSelected(item);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        bookmarkTempData = new ArrayList<JSONObject>(bookmarksHolder.getShortenedArrayList());
        bookmarkAdapter = new BookmarkAdapter(bookmarksHolder.getShortenedArrayList(), this, bookmarkManager);
        bookmarkAdapter.setTempData(bookmarkTempData);
        bookmarkRecyclerView.setAdapter(bookmarkAdapter);

        historyTempData = new ArrayList<JSONObject>(historyHolder.getShortenedArrayList());
        historyAdapter = new HistoryAdapter(historyHolder.getShortenedArrayList(), this, historyManager);
        historyAdapter.setTempData(historyTempData);
        historyRecyclerView.setAdapter(historyAdapter);
    }

    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void deleteHistory() {
        while (historyHolder.getArrayList().size() > 0) {
            ArrayList<JSONObject> tempData = new ArrayList<JSONObject>();
            historyHolder.getArrayList().remove(0);
            historyManager.rewriteFile("History", historyManager.getJsonArrayFromHistoryArray(tempData));
        }
        Toast.makeText(this, "Deleted history.", Toast.LENGTH_SHORT).show();
    }

    public void deleteCookies() {
        android.webkit.CookieManager cookieManager = android.webkit.CookieManager.getInstance();
        cookieManager.removeAllCookies(null);
        Toast.makeText(this, "Deleted cookies.", Toast.LENGTH_SHORT).show();
    }
}