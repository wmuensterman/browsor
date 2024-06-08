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

public class HistoryManager {

    private Activity callingActivity;

    public HistoryManager(Activity activity) {
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
            fileInputStream = callingActivity.getApplicationContext().openFileInput("History");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                stringBuilder.append(line);
            }
            jsonArray = new JSONArray(stringBuilder.toString());
            return jsonArray;
        } catch (FileNotFoundException e) {
            createFile("History");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }

    public JSONArray getJsonArrayFromHistoryArray(ArrayList<JSONObject> arrayList) {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < arrayList.size(); i++) {
            jsonArray.put(arrayList.get(i));
        }
        return jsonArray;
    }

    public void addToHistory(String url, String title, Boolean isUsingTopMenu) {
        if (url != "" && url != null) {
            HistoryHolder historyHolder = HistoryHolder.getInstance();
            JSONArray jsonArray = historyHolder.getJsonArray();
            JSONObject jsonObject = new JSONObject();
            Boolean historyExists = false;
            try {
                jsonObject.put("url", url);
                jsonObject.put("title", title);
                for (int i = 0; i < jsonArray.length(); i++) {
                    if (jsonArray.getJSONObject(i).getString("url").equals(jsonObject.getString("url"))) {
                        historyExists = true;
                        break;
                    }
                }
                if (historyExists == false) {
                    jsonArray.put(jsonObject);
                    rewriteFile("History", jsonArray);
                    historyHolder.setJsonArray(jsonArray);
                    ArrayList<JSONObject> arrayList = historyHolder.getArrayListFromJsonArray();
                    historyHolder.setArrayList(arrayList);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeFromHistory(int index, String url) {
        HistoryHolder historyHolder = HistoryHolder.getInstance();
        JSONArray jsonArray = historyHolder.getJsonArray();
        jsonArray.remove(index);
        rewriteFile("History", jsonArray);
        historyHolder.setJsonArray(jsonArray);
        ArrayList<JSONObject> arrayList = historyHolder.getArrayListFromJsonArray();
        historyHolder.setArrayList(arrayList);
        Toast.makeText(callingActivity, "Removed " + url + " from history.", Toast.LENGTH_SHORT).show();
    }

}
