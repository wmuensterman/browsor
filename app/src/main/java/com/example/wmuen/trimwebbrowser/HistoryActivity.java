package com.example.wmuen.trimwebbrowser;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private HistoryManager historyManager;
    private HistoryAdapter adapter;
    private HistoryHolder historyHolder;
    private ArrayList<JSONObject> tempData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SettingsHolder settingsHolder = SettingsHolder.getInstance();
        setTheme(settingsHolder.getTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToChooseWebView();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.historyRecyclerView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setItemViewCacheSize(1024);
        layoutManager = new LinearLayoutManager(this);
        //show history in reverse order
        ((LinearLayoutManager) layoutManager).setReverseLayout(true);
        ((LinearLayoutManager) layoutManager).setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        historyManager = new HistoryManager(this);
        historyHolder = HistoryHolder.getInstance();
        //If you don't make tempData a new ArrayList, tempData is simply a pointer to historyHolder.getArrayList()
        tempData = new ArrayList<JSONObject>(historyHolder.getArrayList());
        //Adapter seems to deal with its own set of data by itself.
        //Therefore, I should keep track of changes in tempData, and then make the actual changes only when I leave the activity.
        adapter = new HistoryAdapter(historyHolder.getArrayList(), this, historyManager);
        adapter.setTempData(tempData);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);

//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.START | ItemTouchHelper.END) {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START | ItemTouchHelper.END) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                //https://stackoverflow.com/questions/31367599/how-to-update-recyclerview-adapter-data
//                int fromPosition = viewHolder.getAdapterPosition();
//                int toPosition = viewHolder1.getAdapterPosition();
//                String item = tempData.get(fromPosition);
//                tempData.remove(fromPosition);
//                tempData.add(toPosition, item);
//                adapter.setTempData(tempData);//adapter needs tempData for its openWebview and openTextView methods
//                adapter.notifyItemMoved(fromPosition, toPosition);
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                //https://stackoverflow.com/questions/36858086/remove-an-item-in-recyclerview-android
                //Turns out that when I'm deleting, I have to remove data from the acktual list that the adapter uses
                int position = viewHolder.getAdapterPosition();
                tempData.remove(position);
                historyHolder.getArrayList().remove(position);
                historyManager.rewriteFile("History", historyManager.getJsonArrayFromHistoryArray(tempData));
                adapter.notifyItemRemoved(position);
                adapter.setTempData(tempData);
                adapter.notifyItemRangeChanged(position, historyHolder.getArrayList().size());
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
        Intent intent = new Intent(HistoryActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    public void switchToChooseWebView() {
        Intent intent = new Intent(HistoryActivity.this, ChooseWebviewActivity.class);
        startActivity(intent);
    }

    public void switchToBookmarksView() {
        Intent intent = new Intent(HistoryActivity.this, BookmarksActivity.class);
        startActivity(intent);
        finish();
    }

    public void switchToStartPageView() {
        Intent intent = new Intent(HistoryActivity.this, StartPageActivity.class);
        startActivity(intent);
    }

    public void shareAllHistory() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "History from Browsor");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, allHistoryInPlainText());
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
        getMenuInflater().inflate(R.menu.toolbar_menu_history, menu);
        return true;
    }

    @Override
    public void onDestroy() {
        //I feel iffy about this.
        historyHolder.setArrayList(tempData);
        //</iffy>
        JSONArray jsonArray = historyHolder.getJsonArrayFromArrayList();
//        HistoryHolder historyJsonHolder = HistoryHolder.getInstance();
//        historyJsonHolder.setJsonArray(jsonArray);
        historyHolder.setJsonArray(jsonArray);
        historyManager.rewriteFile("History", jsonArray);
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                switchToSettingsView();
                break;
            case R.id.share:
                shareAllHistory();
                break;
            case R.id.copy:
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("History from Browsor", allHistoryInPlainText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "Copied everything from history to clipboard.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.deleteHistory:
                showDeleteHistoryAlert();
                break;
            case R.id.startPage:
                switchToStartPageView();
                break;
            case R.id.deleteCookies:
                showDeleteCookiesAlert();
                break;
            case R.id.bookmarks:
                switchToBookmarksView();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public String allHistoryInPlainText() {
        HistoryHolder historyHolder = HistoryHolder.getInstance();
        ArrayList<JSONObject> history = historyHolder.getArrayList();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < history.size(); i++) {
            try {
                if (!history.get(i).getString("title").equals(history.get(i).getString("url"))) {
                    stringBuilder.append(history.get(i).getString("title"));
                    stringBuilder.append("\n");
                }
                stringBuilder.append(history.get(i).getString("url"));
                stringBuilder.append("\n\n");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }

    public void deleteHistory() {
        while (historyHolder.getArrayList().size() > 0) {
            tempData.remove(0);
            historyHolder.getArrayList().remove(0);
            adapter.notifyItemRemoved(0);
            adapter.setTempData(tempData);
            adapter.notifyItemRangeChanged(0, historyHolder.getArrayList().size());
            historyManager.rewriteFile("History", historyManager.getJsonArrayFromHistoryArray(tempData));
        }
        Toast.makeText(this, "Deleted history.", Toast.LENGTH_SHORT).show();
    }

    public void deleteCookies() {
        android.webkit.CookieManager cookieManager = android.webkit.CookieManager.getInstance();
        cookieManager.removeAllCookies(null);
        Toast.makeText(this, "Deleted cookies.", Toast.LENGTH_SHORT).show();
    }

    public void showDeleteHistoryAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete history");
        builder.setMessage("Are you sure?");
        builder.setCancelable(true);
        builder.setPositiveButton("Delete history", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteHistory();
                dialog.cancel();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        alertDialog.show();
    }

    public void showDeleteCookiesAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete cookies");
        builder.setMessage("Are you sure?");
        builder.setCancelable(true);
        builder.setPositiveButton("Delete cookies", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteCookies();
                dialog.cancel();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        alertDialog.show();
    }

}