package com.example.wmuen.trimwebbrowser;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class BookmarkManager {

    private Activity callingActivity;

    public BookmarkManager(Activity activity) {
        this.callingActivity = activity;
    }

    public void createFile(String fileName) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = callingActivity.getApplicationContext().openFileOutput(fileName, Context.MODE_PRIVATE);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            JSONArray jsonArray = new JSONArray();
            outputStreamWriter.write(jsonArray.toString());
            outputStreamWriter.flush();
            outputStreamWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(callingActivity, e.toString(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(callingActivity, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void rewriteFile(String fileName, JSONArray jsonArray) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = callingActivity.getApplicationContext().openFileOutput(fileName, Context.MODE_PRIVATE);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.write(jsonArray.toString());
            outputStreamWriter.flush();
            outputStreamWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(callingActivity, e.toString(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(callingActivity, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public JSONArray getJsonArrayFromFile(String filename) {
        JSONArray jsonArray;
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = callingActivity.getApplicationContext().openFileInput("Bookmarks");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder stringBuilder = new StringBuilder();
            for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                stringBuilder.append(line);
            }

            jsonArray = new JSONArray(stringBuilder.toString());
            return jsonArray;
        } catch (FileNotFoundException e) {
            createFile("Bookmarks");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }

    public JSONArray getJsonArrayFromBookmarksArray(ArrayList<JSONObject> arrayList) {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < arrayList.size(); i++) {
            jsonArray.put(arrayList.get(i));
        }
        return jsonArray;
    }

    public void addToBookmarks(String url, String title, Boolean isUsingTopMenu) {
        if (url != "" && url != null) {
            BookmarksHolder bookmarksHolder = BookmarksHolder.getInstance();
            JSONArray jsonArray = bookmarksHolder.getJsonArray();
            JSONObject jsonObject = new JSONObject();
            Boolean bookmarkExists = false;
            try {
                jsonObject.put("url", url);
                jsonObject.put("title", title);
                for (int i = 0; i < jsonArray.length(); i++) {
                    if (jsonArray.getJSONObject(i).getString("url").equals(jsonObject.getString("url"))) {
                        bookmarkExists = true;
                        if (isUsingTopMenu == true) {
                            removeFromBookmarks(i, url);
                        } else {
                            Toast.makeText(callingActivity, "Already bookmarked " + url + ".", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }
                }
                if (bookmarkExists == false) {
                    jsonArray.put(jsonObject);
                    rewriteFile("Bookmarks", jsonArray);
                    bookmarksHolder.setJsonArray(jsonArray);
                    ArrayList<JSONObject> arrayList = bookmarksHolder.getArrayListFromJsonArray();
                    bookmarksHolder.setArrayList(arrayList);
                    Toast.makeText(callingActivity, "Bookmarked " + url + ".", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateBookmarkTitle(String url, String title) {
        if (url != "" && url != null) {
            BookmarksHolder bookmarksHolder = BookmarksHolder.getInstance();
            JSONArray jsonArray = bookmarksHolder.getJsonArray();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("url", url);
                jsonObject.put("title", title);
                for (int i = 0; i < jsonArray.length(); i++) {
                    if (jsonArray.getJSONObject(i).getString("url").equals(jsonObject.getString("url"))) {
                        if (title.equals(jsonArray.getJSONObject(i).getString("title"))) {
                            //do nothing
                        } else {
                            //update the bookmark
                            jsonArray.getJSONObject(i).put("title", title);
                            rewriteFile("Bookmarks", jsonArray);
                            bookmarksHolder.setJsonArray(jsonArray);
                            ArrayList<JSONObject> arrayList = bookmarksHolder.getArrayListFromJsonArray();
                            bookmarksHolder.setArrayList(arrayList);
                        }
                        break;
                    } else {
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeFromBookmarks(int index, String url) {
        BookmarksHolder bookmarksHolder = BookmarksHolder.getInstance();
        JSONArray jsonArray = bookmarksHolder.getJsonArray();
        jsonArray.remove(index);
        rewriteFile("Bookmarks", jsonArray);
        bookmarksHolder.setJsonArray(jsonArray);
        ArrayList<JSONObject> arrayList = bookmarksHolder.getArrayListFromJsonArray();
        bookmarksHolder.setArrayList(arrayList);
        Toast.makeText(callingActivity, "Removed " + url + " from bookmarks.", Toast.LENGTH_SHORT).show();
    }

}
