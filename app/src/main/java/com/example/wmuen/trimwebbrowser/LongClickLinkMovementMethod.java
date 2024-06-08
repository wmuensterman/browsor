package com.example.wmuen.trimwebbrowser;

import android.os.Handler;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.view.MotionEvent;
import android.widget.TextView;

//https://stackoverflow.com/questions/8702573/in-android-how-can-i-register-only-long-clicks-using-a-clickablespan
//https://stackoverflow.com/questions/26813104/long-click-on-clickable-span-not-firing-until-click-is-released#31786969
public class LongClickLinkMovementMethod extends LinkMovementMethod {

    private Handler mLongClickHandler;
    private static int LONG_CLICK_TIME = 500;
    private boolean mIsLongPressed = false;

    @Override
    public boolean onTouchEvent(final TextView widget, Spannable buffer,
                                MotionEvent event) {
        int action = event.getAction();

        if (action == MotionEvent.ACTION_CANCEL) {
            if (mLongClickHandler != null) {
                mLongClickHandler.removeCallbacksAndMessages(null);
            }
        }

        if (action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            final LongClickableSpan[] link = buffer.getSpans(off, off, LongClickableSpan.class);

            if (link.length != 0) {
                if (action == MotionEvent.ACTION_UP) {
                    if (mLongClickHandler != null) {
                        mLongClickHandler.removeCallbacksAndMessages(null);
                    }
                    if (!mIsLongPressed) {
                        link[0].onClick(widget);
                    }
                    mIsLongPressed = false;
                } else {
                    Selection.setSelection(buffer,
                            buffer.getSpanStart(link[0]),
                            buffer.getSpanEnd(link[0]));
                    mLongClickHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            link[0].onLongClick(widget);
                            mIsLongPressed = true;
                        }
                    }, LONG_CLICK_TIME);
                }
                return true;
            }
        }

//        return super.onTouchEvent(widget, buffer, event);
        return false;
    }


    public static MovementMethod getInstance() {
        if (sInstance == null) {
            sInstance = new LongClickLinkMovementMethod();
            sInstance.mLongClickHandler = new Handler();
        }

        return sInstance;
    }

    private static LongClickLinkMovementMethod sInstance;
}