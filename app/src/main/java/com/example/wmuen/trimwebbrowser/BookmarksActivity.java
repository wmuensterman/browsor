package com.example.wmuen.trimwebbrowser;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BookmarksActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private BookmarkManager bookmarkManager;
    private BookmarkAdapter adapter;
    private BookmarksHolder bookmarksHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SettingsHolder settingsHolder = SettingsHolder.getInstance();
        setTheme(settingsHolder.getTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);

        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToChooseWebView();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.bookmarkRecyclerView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setItemViewCacheSize(1024);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        bookmarkManager = new BookmarkManager(this);
        bookmarksHolder = BookmarksHolder.getInstance();
        //If you don't make tempData a new ArrayList, tempData is simply a pointer to bookmarksHolder.getArrayList()
        //Adapter seems to deal with its own set of data by itself.
        //Therefore, I should keep track of changes in tempData, and then make the actual changes only when I leave the activity.
        adapter = new BookmarkAdapter(bookmarksHolder.getArrayList(), this, bookmarkManager);
        adapter.setTempData(bookmarksHolder.getArrayList());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.START | ItemTouchHelper.END) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                //https://stackoverflow.com/questions/31367599/how-to-update-recyclerview-adapter-data
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = viewHolder1.getAdapterPosition();
                JSONObject item = bookmarksHolder.getArrayList().get(fromPosition);
                bookmarksHolder.getArrayList().remove(fromPosition);
                bookmarksHolder.getArrayList().add(toPosition, item);
                adapter.setTempData(bookmarksHolder.getArrayList());
                bookmarkManager.rewriteFile("Bookmarks", bookmarkManager.getJsonArrayFromBookmarksArray(bookmarksHolder.getArrayList()));
                adapter.notifyItemMoved(fromPosition, toPosition);
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                //https://stackoverflow.com/questions/36858086/remove-an-item-in-recyclerview-android
                //Turns out that when I'm deleting, I have to remove data from the acktual list that the adapter uses
                int position = viewHolder.getAdapterPosition();
                bookmarksHolder.getArrayList().remove(position);
                adapter.notifyItemRemoved(position);
                adapter.setTempData(bookmarksHolder.getArrayList());
                bookmarkManager.rewriteFile("Bookmarks", bookmarkManager.getJsonArrayFromBookmarksArray(bookmarksHolder.getArrayList()));
                adapter.notifyItemRangeChanged(position, bookmarksHolder.getArrayList().size());
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);

        setUpToolbar();
    }

    public void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    public void switchToSettingsView() {
        Intent intent = new Intent(BookmarksActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    public void switchToChooseWebView() {
        Intent intent = new Intent(BookmarksActivity.this, ChooseWebviewActivity.class);
        startActivity(intent);
    }

    public void switchToHistoryView() {
        Intent intent = new Intent(BookmarksActivity.this, HistoryActivity.class);
        startActivity(intent);
        finish();
    }

    public void switchToStartPageView() {
        Intent startPageActivityIntent = new Intent(BookmarksActivity.this, StartPageActivity.class);
        startActivity(startPageActivityIntent);
    }

    public void shareAllBookmarks() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Bookmarks from Browsor");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, allBookmarksInPlainText());
        startActivity(Intent.createChooser(sharingIntent, "Shared from Browsor"));
    }

    @Override
    public void onContextMenuClosed(Menu menu) {
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu_bookmarks, menu);
        return true;
    }

    @Override
    public void onDestroy() {
        JSONArray jsonArray = bookmarksHolder.getJsonArrayFromArrayList();
        bookmarksHolder.setJsonArray(jsonArray);
        bookmarkManager.rewriteFile("Bookmarks", jsonArray);
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                switchToSettingsView();
                break;
            case R.id.share:
                shareAllBookmarks();
                break;
            case R.id.copy:
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Bookmarks from Browsor", allBookmarksInPlainText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "Copied all bookmarks to clipboard.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.history:
                switchToHistoryView();
                break;
            case R.id.startPage:
                switchToStartPageView();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public String allBookmarksInPlainText() {
        BookmarksHolder bookmarksHolder = BookmarksHolder.getInstance();
        ArrayList<JSONObject> bookmarks = bookmarksHolder.getArrayList();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < bookmarks.size(); i++) {
            try {
                if (!bookmarks.get(i).getString("title").equals(bookmarks.get(i).getString("url"))) {
                    stringBuilder.append(bookmarks.get(i).getString("title"));
                    stringBuilder.append("\n");
                }
                stringBuilder.append(bookmarks.get(i).getString("url"));
                stringBuilder.append("\n\n");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }

}