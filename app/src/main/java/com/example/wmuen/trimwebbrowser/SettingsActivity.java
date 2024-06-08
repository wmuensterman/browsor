package com.example.wmuen.trimwebbrowser;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final SettingsHolder settings = SettingsHolder.getInstance();
        setTheme(settings.getTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

//        final SettingsHolder settings = SettingsHolder.getInstance();

        final SwitchCompat saveHistory = (SwitchCompat) findViewById(R.id.saveHistory);
        saveHistory.setChecked(settings.getSaveHistory());
        saveHistory.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setSaveHistory(isChecked);
                saveSaveHistoryToSharedPreferences(settings);
            }
        });

        final SwitchCompat saveCookies = (SwitchCompat) findViewById(R.id.saveCookies);
        saveCookies.setChecked(settings.getSaveCookies());
        saveCookies.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setSaveCookies(isChecked);
                saveSaveCookiesToSharedPreferences(settings);
            }
        });

        final SwitchCompat saveThirdPartyCookies = (SwitchCompat) findViewById(R.id.saveThirdPartyCookies);
        saveThirdPartyCookies.setChecked(settings.getSaveThirdPartyCookies());
        saveThirdPartyCookies.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setSaveThirdPartyCookies(isChecked);
                saveSaveThirdPartyCookiesToSharedPreferences(settings);
            }
        });

        final SwitchCompat imageSwitch = findViewById(R.id.imageSwitch);
        imageSwitch.setChecked(settings.getEnableImages());

        final SwitchCompat scriptSwitch = findViewById(R.id.scriptSwitch);
        scriptSwitch.setChecked(settings.getEnableScripts());

        final SwitchCompat zoomSwitch = findViewById(R.id.zoomSwitch);
        zoomSwitch.setChecked(settings.getEnableZoom());

        final RadioGroup textOrFormat = findViewById(R.id.textOrFormat);
        if (settings.getTextOnly() == true) {
            textOrFormat.check(R.id.textOnly);
            imageSwitch.setEnabled(false);
            scriptSwitch.setEnabled(false);
            zoomSwitch.setEnabled(false);
        } else {
            textOrFormat.check(R.id.allowFormatting);
            imageSwitch.setEnabled(true);
            scriptSwitch.setEnabled(true);
            zoomSwitch.setEnabled(true);
        }

        textOrFormat.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton selectedRadioButton = findViewById(group.getCheckedRadioButtonId());
                switch (selectedRadioButton.getId()) {
                    case R.id.textOnly:
                        settings.setTextOnly(true);
                        imageSwitch.setEnabled(false);
                        scriptSwitch.setEnabled(false);
                        zoomSwitch.setEnabled(false);
                        break;
                    case R.id.allowFormatting:
                        settings.setTextOnly(false);
                        imageSwitch.setEnabled(true);
                        scriptSwitch.setEnabled(true);
                        zoomSwitch.setEnabled(true);
                        break;
                }
                saveTextOnlyToSharedPreferences(settings);
            }
        });

        imageSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setEnableImages(isChecked);
                saveEnableImagesToSharedPreferences(settings);
            }
        });
        scriptSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setEnableScripts(isChecked);
                saveEnableScriptsToSharedPreferences(settings);
            }
        });
        zoomSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setEnableZoom(isChecked);
                saveEnableZoomToSharedPreferences(settings);
            }
        });

        final Spinner searchSpinner = (Spinner) findViewById(R.id.searchEngineSpin);
        String suffix = settings.getSearchSuffix();
        if (suffix.equals(settings.GOOGLESUFFIX)) {
            searchSpinner.setSelection(5, false);
        } else if (suffix.equals(settings.BINGSUFFIX)) {
            searchSpinner.setSelection(1, false);
        } else if (suffix.equals(settings.YAHOOSUFFIX)) {
            searchSpinner.setSelection(8, false);
        } else if (suffix.equals(settings.DUCKDUCKGOSUFFIX)) {
            searchSpinner.setSelection(3, false);
        } else if (suffix.equals(settings.DUCKDUCKGOLITESUFFIX)) {
            searchSpinner.setSelection(4, false);
        } else if (suffix.equals(settings.QWANTSUFFIX)) {
            searchSpinner.setSelection(6, false);
        } else if (suffix.equals(settings.QWANTLITESUFFIX)) {
            searchSpinner.setSelection(7, false);
        } else if (suffix.equals(settings.ASKJEEVESSUFFIX)) {
            searchSpinner.setSelection(0, false);
        } else if (suffix.equals(settings.YANDEXSUFFIX)) {
            searchSpinner.setSelection(9, false);
        } else if (suffix.equals(settings.BRAVESUFFIX)) {
            searchSpinner.setSelection(2, false);
        }

        searchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedSearchEngine = position;
                switch (selectedSearchEngine) {
                    case 0:
                        settings.setSearchSuffix(settings.ASKJEEVESSUFFIX);
                        break;
                    case 1:
                        settings.setSearchSuffix(settings.BINGSUFFIX);
                        break;
                    case 2:
                        settings.setSearchSuffix(settings.BRAVESUFFIX);
                        break;
                    case 3:
                        settings.setSearchSuffix(settings.DUCKDUCKGOSUFFIX);
                        break;
                    case 4:
                        settings.setSearchSuffix(settings.DUCKDUCKGOLITESUFFIX);
                        break;
                    case 5:
                        settings.setSearchSuffix(settings.GOOGLESUFFIX);
                        break;
                    case 6:
                        settings.setSearchSuffix(settings.QWANTSUFFIX);
                        break;
                    case 7:
                        settings.setSearchSuffix(settings.QWANTLITESUFFIX);
                        break;
                    case 8:
                        settings.setSearchSuffix(settings.YAHOOSUFFIX);
                        break;
                    case 9:
                        settings.setSearchSuffix(settings.YANDEXSUFFIX);
                        break;
                }
                saveSearchSuffixToSharedPreferences(settings);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final Spinner themeSpinner = (Spinner) findViewById(R.id.themeSpin);

        themeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedTheme = position;
                Intent intent = new Intent(SettingsActivity.this, StartPageActivity.class);
                switch (selectedTheme) {
                    case 0://if no theme is selected, do nothing
                        break;
                    case 1:
                        settings.setTheme(R.style.AppThemeDay);
                        saveThemeToSharedPreferences(settings);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        break;
                    case 2:
                        settings.setTheme(R.style.AppThemeNight);
                        saveThemeToSharedPreferences(settings);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        break;
                    case 3:
                        settings.setTheme(R.style.AppThemeExtraDay);
                        saveThemeToSharedPreferences(settings);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        break;
                    case 4:
                        settings.setTheme(R.style.AppThemeExtraNight);
                        saveThemeToSharedPreferences(settings);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        break;
                    case 5:
                        settings.setTheme(R.style.AppThemeBaby);
                        saveThemeToSharedPreferences(settings);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        break;
                    case 6:
                        settings.setTheme(R.style.AppThemeHotdog);
                        saveThemeToSharedPreferences(settings);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final RadioGroup fontFamily = (RadioGroup) findViewById(R.id.fontFamily);
        Typeface typeface = settings.getTypeface();
        if (typeface.equals(Typeface.SERIF)) {
            fontFamily.check(R.id.serifRadioButton);
        } else if (typeface.equals(Typeface.SANS_SERIF)) {
            fontFamily.check((R.id.sansSerifRadioButton));
        } else if (typeface.equals(Typeface.MONOSPACE)) {
            fontFamily.check((R.id.monospaceRadioButton));
        }
        fontFamily.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton selectedRadioButton = findViewById(group.getCheckedRadioButtonId());
                int selectedFont = selectedRadioButton.getId();
                SettingsHolder settings = SettingsHolder.getInstance();
                switch (selectedFont) {
                    case R.id.serifRadioButton:
                        settings.setTypeface(Typeface.SERIF);
                        break;
                    case R.id.sansSerifRadioButton:
                        settings.setTypeface(Typeface.SANS_SERIF);
                        break;
                    case R.id.monospaceRadioButton:
                        settings.setTypeface(Typeface.MONOSPACE);
                        break;
                }
                saveFontFamilyToSharedPreferences(settings);
            }
        });

        SeekBar fontSize = (SeekBar) findViewById(R.id.fontSize);
        switch (settings.getFontSize()) {
            case 10:
                fontSize.setProgress(0);
                break;
            case 14:
                fontSize.setProgress(1);
                break;
            case 18:
                fontSize.setProgress(2);
                break;
            case 22:
                fontSize.setProgress(3);
                break;
            case 26:
                fontSize.setProgress(4);
                break;
        }

        TextView demo = findViewById(R.id.fontSizeDemo);
        demo.setTextSize(settings.getFontSize());
        fontSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (progress) {
                    case 0:
                        settings.setFontSize(10);
                        break;
                    case 1:
                        settings.setFontSize(14);
                        break;
                    case 2:
                        settings.setFontSize(18);
                        break;
                    case 3:
                        settings.setFontSize(22);
                        break;
                    case 4:
                        settings.setFontSize(26);
                        break;
                }
                TextView demo = findViewById(R.id.fontSizeDemo);
                demo.setTextSize(settings.getFontSize());
                saveFontSizeToSharedPreferences(settings);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        setUpToolbar();

    }

    public void saveSaveHistoryToSharedPreferences(SettingsHolder settings) {
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.wmuen.textonlybrowser", MODE_PRIVATE);
        String saveHistoryKey = "com.example.wmuen.textonlybrowser.saveHistory";
        sharedPreferences.edit().putString(saveHistoryKey, Boolean.toString(settings.getSaveHistory())).apply();
    }

    public void saveSaveCookiesToSharedPreferences(SettingsHolder settings) {
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.wmuen.textonlybrowser", MODE_PRIVATE);
        String saveCookiesKey = "com.example.wmuen.textonlybrowser.saveCookies";
        sharedPreferences.edit().putString(saveCookiesKey, Boolean.toString(settings.getSaveCookies())).apply();
    }

    public void saveSaveThirdPartyCookiesToSharedPreferences(SettingsHolder settings) {
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.wmuen.textonlybrowser", MODE_PRIVATE);
        String saveThirdPartyCookiesKey = "com.example.wmuen.textonlybrowser.saveThirdPartyCookies";
        sharedPreferences.edit().putString(saveThirdPartyCookiesKey, Boolean.toString(settings.getSaveThirdPartyCookies())).apply();
    }

    public void saveTextOnlyToSharedPreferences(SettingsHolder settings) {
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.wmuen.textonlybrowser", MODE_PRIVATE);
        String textOnlyKey = "com.example.wmuen.textonlybrowser.textOnly";
        sharedPreferences.edit().putString(textOnlyKey, Boolean.toString(settings.getTextOnly())).apply();
    }

    public void saveEnableScriptsToSharedPreferences(SettingsHolder settings) {
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.wmuen.textonlybrowser", MODE_PRIVATE);
        String enableScriptsKey = "com.example.wmuen.textonlybrowser.enableScripts";
        sharedPreferences.edit().putString(enableScriptsKey, Boolean.toString(settings.getEnableScripts())).apply();
    }

    public void saveEnableImagesToSharedPreferences(SettingsHolder settings) {
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.wmuen.textonlybrowser", MODE_PRIVATE);
        String enableImagesKey = "com.example.wmuen.textonlybrowser.enableImages";
        sharedPreferences.edit().putString(enableImagesKey, Boolean.toString(settings.getEnableImages())).apply();
    }

    public void saveEnableZoomToSharedPreferences(SettingsHolder settings) {
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.wmuen.textonlybrowser", MODE_PRIVATE);
        String enableZoomKey = "com.example.wmuen.textonlybrowser.enableZoom";
        sharedPreferences.edit().putString(enableZoomKey, Boolean.toString(settings.getEnableZoom())).apply();
    }

    public void saveFontFamilyToSharedPreferences(SettingsHolder settings) {
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.wmuen.textonlybrowser", MODE_PRIVATE);
        String fontFamilyKey = "com.example.wmuen.textonlybrowser.fontFamily";
        sharedPreferences.edit().putString(fontFamilyKey, settings.getTypeface().toString()).apply();
    }

    public void saveFontSizeToSharedPreferences(SettingsHolder settings) {
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.wmuen.textonlybrowser", MODE_PRIVATE);
        String fontSizeKey = "com.example.wmuen.textonlybrowser.fontSize";
        sharedPreferences.edit().putString(fontSizeKey, Integer.toString(settings.getFontSize())).apply();
    }

    public void saveSearchSuffixToSharedPreferences(SettingsHolder settings) {
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.wmuen.textonlybrowser", MODE_PRIVATE);
        String searchSuffixKey = "com.example.wmuen.textonlybrowser.searchSuffix";
        sharedPreferences.edit().putString(searchSuffixKey, settings.getSearchSuffix()).apply();
    }

    public void saveThemeToSharedPreferences(SettingsHolder settings) {
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.wmuen.textonlybrowser", MODE_PRIVATE);
        String themeKey = "com.example.wmuen.textonlybrowser.theme";
        sharedPreferences.edit().putString(themeKey, String.valueOf(settings.getTheme())).apply();
    }

    public void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
