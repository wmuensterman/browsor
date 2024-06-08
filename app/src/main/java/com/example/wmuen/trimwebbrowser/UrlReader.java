package com.example.wmuen.trimwebbrowser;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.webkit.URLUtil;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

public class UrlReader extends AsyncTask<Void, Void, Void> {

    private ProgressDialog pdLoading;
    private String text = "";
    private TextView textView;
    private String link;
    private String baseUrl;
    private BaseWebpageActivity baseWebpageActivity;
    private HttpsURLConnection httpsURLConnection;
    private HttpURLConnection httpURLConnection;
    private URLConnection urlConnection;

    private static UrlReader instance = null;

    public UrlReader(BaseWebpageActivity baseWebpageActivity) {
        this.pdLoading = new ProgressDialog(baseWebpageActivity);
        this.baseWebpageActivity = baseWebpageActivity;
    }

    public void setURL(String clickedLink) {
        this.link = clickedLink;
    }

    public String getUrl() {
        return this.link;
    }

    public String getBaseUrl() {
        return this.baseUrl;
    }

    public String getText() {
        return this.text;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        try {
            pdLoading.setMessage("Loading");
            pdLoading.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        baseWebpageActivity.setUrlString(link);
        baseWebpageActivity.showContent(this);
        baseWebpageActivity.setAddressBarText(link);
        try {
            pdLoading.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean urlExists(String urlToCheck) {
        if (urlToCheck.startsWith("https://")) {
            //http://urlregex.com/  Thanks, Dan.
            //my modified version of the regex
            if (urlToCheck.matches("^(https)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*\\.[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*")) {
                try {
                    if (URLUtil.isHttpsUrl(urlToCheck)) {
                        URL url = new URL(urlToCheck);
                        httpsURLConnection = (HttpsURLConnection) url.openConnection();
//                        httpsURLConnection.setInstanceFollowRedirects(false);
                        httpsURLConnection.setInstanceFollowRedirects(true);
                        httpsURLConnection.connect();
                        return true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (urlToCheck.startsWith("http://")) {
            if (urlToCheck.matches("^(http)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*\\.[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*")) {
                try {
                    if (URLUtil.isHttpUrl(urlToCheck)) {
                        URL url = new URL(urlToCheck);
                        httpURLConnection = (HttpURLConnection) url.openConnection();
                        httpURLConnection.setInstanceFollowRedirects(true);
                        httpURLConnection.connect();
                        return true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            URL url = new URL(urlToCheck);
            url.openStream().close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void readUrl() {
        link = getUrl();
        if (link != null) {
            try {
                //you already created a connection when you validated the url
                //reuse that connection instead of connecting twice
                if (httpsURLConnection != null) {

                    //get baseUrl
                    baseUrl = "https://" + httpsURLConnection.getURL().getHost();

                    InputStreamReader inputStreamReader = new InputStreamReader(httpsURLConnection.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    StringBuffer stringBuffer = new StringBuffer();
                    String input;
                    while ((input = bufferedReader.readLine()) != null) {
                        stringBuffer.append(input);
                    }
                    bufferedReader.close();
                    String textOnly = stringBuffer.toString();
                    if (textOnly != "") {
                        if (textOnly.contains("<title>") && textOnly.contains("</title>")) {
                            try {
                                String pageTitle = textOnly.substring(textOnly.indexOf("<title>"), textOnly.indexOf("</title>"));
                                pageTitle = pageTitle.replaceAll("<title>", "");
                                pageTitle = pageTitle.replaceAll("</title>", "");
                                baseWebpageActivity.setTitle(pageTitle);
                            } catch (Exception e) {
                                baseWebpageActivity.setTitle(baseUrl);
                            }
                        } else {
                            baseWebpageActivity.setTitle(baseUrl);
                        }
                        baseWebpageActivity.addPageToHistory(link);
                        textOnly = textOnly.replaceAll(("<script(.*?)</script>"), "");
                        //get the title before you remove the head
                        textOnly = textOnly.replaceAll(("<head(.*?)</head>"), "");
                        textOnly = textOnly.replaceAll(("<style(.*?)</style>"), "");
                        text = textOnly;
                    }
                } else if (httpURLConnection != null) {

                    //get baseUrl
                    baseUrl = "http://" + httpURLConnection.getURL().getHost();

                    InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    StringBuffer stringBuffer = new StringBuffer();
                    String input;
                    while ((input = bufferedReader.readLine()) != null) {
                        stringBuffer.append(input);
                    }
                    bufferedReader.close();
                    String textOnly = stringBuffer.toString();
                    if (textOnly != "") {
                        String pageTitle = "";
                        if (textOnly.contains("<title>") && textOnly.contains("</title>")) {
                            try {
                                pageTitle = textOnly.substring(textOnly.indexOf("<title>"), textOnly.indexOf("</title>"));
                                pageTitle = pageTitle.replaceAll("<title>", "");
                                pageTitle = pageTitle.replaceAll("</title>", "");
                                baseWebpageActivity.setTitle(pageTitle);
                            } catch (Exception e) {
                                baseWebpageActivity.setTitle(baseUrl);
                            }
                        } else {
                            baseWebpageActivity.setTitle(baseUrl);
                        }
                        baseWebpageActivity.addPageToHistory(link);
                        textOnly = textOnly.replaceAll(("<script(.*?)</script>"), "");
                        textOnly = textOnly.replaceAll(("<head(.*?)</head>"), "");
                        textOnly = textOnly.replaceAll(("<style(.*?)</style>"), "");
                        text = textOnly;
                    }
                }
            } catch (IOException e) {
            }
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        SettingsHolder settingsHolder = SettingsHolder.getInstance();
        TemporarySettingsHolder temporarySettingsHolder = TemporarySettingsHolder.getInstance();
        if (temporarySettingsHolder.getUseTemporarySettings() == true) {
            if (temporarySettingsHolder.getTextOnly() == true) {
                if (urlExists(link)) {
                    //you have already connected once, just pass the connection on
                } else if (urlExists("https://" + link)) {
                    setURL("https://" + link);
                } else if (urlExists("http://" + link)) {
                    setURL("http://" + link);
                } else {
                    try {
                        if (urlExists(settingsHolder.getSearchSuffix() + URLEncoder.encode(link, "UTF-8"))) {
                            String searchQuery = settingsHolder.getSearchSuffix() + link;
                            setURL(searchQuery);
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                readUrl();
            } else {
                if (urlExists(link)) {
                    //do nothing
                } else if (urlExists("https://" + link)) {
                    setURL("https://" + link);
                } else if (urlExists("http://" + link)) {
                    setURL("http://" + link);
                } else {
                    try {
                        if (urlExists(settingsHolder.getSearchSuffix() + URLEncoder.encode(link, "UTF-8"))) {
                            String searchQuery = settingsHolder.getSearchSuffix() + link;
                            setURL(searchQuery);
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            if (settingsHolder.getTextOnly() == true) {
                if (urlExists(link)) {
                    //you have already connected once, just pass the connection on
                } else if (urlExists("https://" + link)) {
                    setURL("https://" + link);
                } else if (urlExists("http://" + link)) {
                    setURL("http://" + link);
                } else {
                    try {
                        if (urlExists(settingsHolder.getSearchSuffix() + URLEncoder.encode(link, "UTF-8"))) {
                            String searchQuery = settingsHolder.getSearchSuffix() + link;
                            setURL(searchQuery);
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                readUrl();
            } else {
                if (urlExists(link)) {
                    //do nothing
                } else if (urlExists("https://" + link)) {
                    setURL("https://" + link);
                } else if (urlExists("http://" + link)) {
                    setURL("http://" + link);
                } else {
                    try {
                        if (urlExists(settingsHolder.getSearchSuffix() + URLEncoder.encode(link, "UTF-8"))) {
                            String searchQuery = settingsHolder.getSearchSuffix() + link;
                            setURL(searchQuery);
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

}