package com.example.wmuen.trimwebbrowser;

import android.text.style.ClickableSpan;
import android.view.View;

//https://stackoverflow.com/questions/8702573/in-android-how-can-i-register-only-long-clicks-using-a-clickablespan
//https://stackoverflow.com/questions/26813104/long-click-on-clickable-span-not-firing-until-click-is-released#31786969
public abstract class LongClickableSpan extends ClickableSpan {

    @Override
    public void onClick(View widget) {

    }

    public abstract void onLongClick(View widget);

}
