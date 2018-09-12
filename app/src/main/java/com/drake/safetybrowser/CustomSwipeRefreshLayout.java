package com.drake.safetybrowser;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CustomSwipeRefreshLayout extends SwipeRefreshLayout {

    private boolean paused;

    public CustomSwipeRefreshLayout(Context context) {
        super(context);
        setColorScheme();
    }

    public CustomSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setColorScheme();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (paused) {
            return false;
        } else {
            return super.onInterceptTouchEvent(ev);
        }
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }
}