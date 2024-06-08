package com.example.wmuen.trimwebbrowser;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class ChooseWebviewActivity extends AppCompatActivity {

    private String urlString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final SettingsHolder settings = SettingsHolder.getInstance();
        setTheme(settings.getTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_webview);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            urlString = (String) bundle.getString("UrlString");
        }

        TextView urlToOpenInWebview = findViewById(R.id.urlToOpenInWebview);
        urlToOpenInWebview.setText(urlString);

        final SettingsHolder settingsHolder = SettingsHolder.getInstance();
        final TemporarySettingsHolder temporarySettingsHolder = TemporarySettingsHolder.getInstance();
        temporarySettingsHolder.setTextOnly(settingsHolder.getTextOnly());
        temporarySettingsHolder.setEnableImages(settingsHolder.getEnableImages());
        temporarySettingsHolder.setEnableScripts(settingsHolder.getEnableScripts());
        temporarySettingsHolder.setEnableZoom(settingsHolder.getEnableZoom());

        final CheckBox imageCheckbox = findViewById(R.id.imageCheckbox);
        imageCheckbox.setChecked(settingsHolder.getEnableImages());

        final CheckBox scriptCheckbox = findViewById(R.id.scriptCheckbox);
        scriptCheckbox.setChecked(settingsHolder.getEnableScripts());

        final CheckBox zoomCheckbox = findViewById(R.id.zoomCheckbox);
        zoomCheckbox.setChecked(settingsHolder.getEnableZoom());

        final RadioGroup textOrFormat = findViewById(R.id.textOrFormat);
        if (settingsHolder.getTextOnly() == true) {
            textOrFormat.check(R.id.textOnly);
            imageCheckbox.setEnabled(false);
            scriptCheckbox.setEnabled(false);
            zoomCheckbox.setEnabled(false);
        } else {
            textOrFormat.check(R.id.allowFormatting);
            imageCheckbox.setEnabled(true);
            scriptCheckbox.setEnabled(true);
            zoomCheckbox.setEnabled(true);
        }

        textOrFormat.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton selectedRadioButton = findViewById(group.getCheckedRadioButtonId());
                switch (selectedRadioButton.getId()) {
                    case R.id.textOnly:
                        imageCheckbox.setEnabled(false);
                        scriptCheckbox.setEnabled(false);
                        zoomCheckbox.setEnabled(false);
                        temporarySettingsHolder.setTextOnly(true);
                        break;
                    case R.id.allowFormatting:
                        imageCheckbox.setEnabled(true);
                        scriptCheckbox.setEnabled(true);
                        zoomCheckbox.setEnabled(true);
                        temporarySettingsHolder.setTextOnly(false);
                        break;
                }
            }
        });

        imageCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                temporarySettingsHolder.setEnableImages(isChecked);
            }
        });
        scriptCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                temporarySettingsHolder.setEnableScripts(isChecked);
            }
        });
        zoomCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                temporarySettingsHolder.setEnableZoom(isChecked);
            }
        });

        Button openInWebview = findViewById(R.id.openInWebview);
        if (urlString == null || urlString == "") {
            openInWebview.setEnabled(false);
            openInWebview.setVisibility(View.GONE);
        }
        openInWebview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temporarySettingsHolder.setUseTemporarySettings(true);
                if (temporarySettingsHolder.getTextOnly() == true) {
                    switchToTextView();
                } else {
                    switchToWebView();
                }
            }
        });

        Button moreSettings = findViewById(R.id.moreSettings);
        moreSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToMoreSettings();
            }
        });

        setUpToolbar();
    }

    public void switchToWebView() {
        Intent intent = new Intent(ChooseWebviewActivity.this, WebviewActivity.class);
        intent.putExtra("UrlString", urlString);
        startActivity(intent);
        finish();
    }

    public void switchToTextView() {
        Intent intent = new Intent(ChooseWebviewActivity.this, MainActivity.class);
        intent.putExtra("UrlString", urlString);
        startActivity(intent);
        finish();
    }

    public void switchToMoreSettings() {
        Intent intent = new Intent(ChooseWebviewActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    public void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    public void setBookmarkIcon(MenuItem menuItem) {
        BookmarksHolder bookmarkJsonHolder = BookmarksHolder.getInstance();
        if (bookmarkJsonHolder.getAllUrls().contains(urlString)) {
            menuItem.setTitle("Remove bookmark");
        } else {
            menuItem.setTitle("Add bookmark");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void switchToHistoryView() {
        Intent intent = new Intent(ChooseWebviewActivity.this, HistoryActivity.class);
        startActivity(intent);
    }

    public void switchToStartPageView() {
        Intent startPageActivityIntent = new Intent(ChooseWebviewActivity.this, StartPageActivity.class);
        startActivity(startPageActivityIntent);
    }

    public void switchToBookmarksView() {
        Intent intent = new Intent(ChooseWebviewActivity.this, BookmarksActivity.class);
        startActivity(intent);
    }

    public void shareCurrentPage() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, urlString);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, urlString);
        startActivity(Intent.createChooser(sharingIntent, urlString));
    }

}
