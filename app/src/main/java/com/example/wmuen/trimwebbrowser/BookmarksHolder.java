package com.example.wmuen.trimwebbrowser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BookmarksHolder {
    private static BookmarksHolder instance = null;

    private JSONArray jsonArray;
    private String[] list;
    private ArrayList<JSONObject> arrayList;

    protected BookmarksHolder() {
        //this is here to block instantiation
    }

    public static BookmarksHolder getInstance() {
        if (instance == null) {
            instance = new BookmarksHolder();
        }
        return instance;
    }

    public JSONArray getJsonArray() {
        return this.jsonArray;
    }

    public void setJsonArray(JSONArray json) {
        this.jsonArray = json;
    }

    public String[] getList() {
        return this.list;
    }

    public void setList(String[] l) {
        this.list = l;
    }

    public ArrayList<JSONObject> getArrayList() {
        return this.arrayList;
    }

    public ArrayList<JSONObject> getShortenedArrayList() {
        ArrayList<JSONObject> shortArrayList = new ArrayList<JSONObject>();
        if (arrayList.size() >= 5) {
            for (int i = 0; i < 5; i++) {
                shortArrayList.add(arrayList.get(i));
            }
        } else {
            for (int i = 0; i < arrayList.size(); i++) {
                shortArrayList.add(arrayList.get(i));
            }
        }
        return shortArrayList;
    }

    public void setArrayList(ArrayList<JSONObject> al) {
        this.arrayList = al;
    }

    public ArrayList<String> getAllUrls() {
        ArrayList<String> arrayList = new ArrayList<String>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                arrayList.add(jsonArray.getJSONObject(i).getString("url"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return arrayList;
    }

    public JSONArray getJsonArrayFromArrayList() {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < arrayList.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("url", arrayList.get(i).getString("url"));
                jsonObject.put("title", arrayList.get(i).getString("title"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

    public ArrayList<JSONObject> getArrayListFromJsonArray() {
        ArrayList<JSONObject> arrayList = new ArrayList<JSONObject>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                arrayList.add(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return arrayList;
    }
}
