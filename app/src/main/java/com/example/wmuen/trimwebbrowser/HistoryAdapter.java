package com.example.wmuen.trimwebbrowser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private ArrayList<JSONObject> historyArray;
    private Context context;
    private HistoryManager historyManager;
    private ArrayList<JSONObject> tempData;

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        public TextView title, url;
        public RelativeLayout container;

        public HistoryViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.title);
            url = (TextView) v.findViewById(R.id.url);
            container = (RelativeLayout) v.findViewById(R.id.container);
        }
    }

    public HistoryAdapter(ArrayList<JSONObject> ha, Context c, HistoryManager h) {
        this.historyArray = ha;
        this.historyManager = h;
        context = c;
    }

    @Override
    public HistoryAdapter.HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_text_view, parent, false);
        HistoryViewHolder vh = new HistoryViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, final int position) {
        try {
            String title = historyArray.get(position).getString("title");
            String url = historyArray.get(position).getString("url");
            holder.title.setText(title);
            holder.url.setText(url);
            if (title.equals(url)) {
                holder.url.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String urlToOpen = historyArray.get(position).getString("url");
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(urlToOpen));
                    if (shouldOpenInAnotherApp(i)) {
                        Intent chooser = Intent.createChooser(i, urlToOpen);
                        if (i.resolveActivity(context.getPackageManager()) != null) {
                            context.startActivity(chooser);
                        }
                    } else {
                        SettingsHolder settingsHolder = SettingsHolder.getInstance();
                        if (settingsHolder.getTextOnly() == true) {
                            switchToTextView(urlToOpen);
                        } else {
                            switchToWebView(urlToOpen);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyArray.size();
    }

    public void switchToWebView(String url) {
        Intent intent = new Intent(context, WebviewActivity.class);
        intent.putExtra("UrlString", url);
        context.startActivity(intent);
        //remove bookmark activity from the back stack
        Activity contextActivity = (Activity) context;
        contextActivity.finish();
    }

    public void switchToTextView(String url) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("UrlString", url);

        context.startActivity(intent);
        //remove bookmark activity from the back stack
        Activity contextActivity = (Activity) context;
        contextActivity.finish();
    }

    //from Lightning Browser
    //https://github.com/anthonycr/Lightning-Browser/blob/dev/app/src/main/java/acr/browser/lightning/utils/IntentUtils.java
    public boolean shouldOpenInAnotherApp(Intent intent) {
        PackageManager packageManager = context.getPackageManager();

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

    public ArrayList<JSONObject> getBookmarkArray() {
        return this.historyArray;
    }

    public void setTempData(ArrayList<JSONObject> t) {
        this.tempData = t;
    }
}
