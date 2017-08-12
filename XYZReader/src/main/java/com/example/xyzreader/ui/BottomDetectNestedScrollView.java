package com.example.xyzreader.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.View;

import com.example.xyzreader.R;

/**
 * Based on http://marteinn.se/blog/android-determinate-when-scrollview-has-reached-the-bottom/
 */

public class BottomDetectNestedScrollView extends NestedScrollView {
    private boolean atBottom = false;
    private int bottomTolerance = 0;

    public BottomDetectNestedScrollView(Context context) {
        this(context, null, 0);
    }

    public BottomDetectNestedScrollView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public BottomDetectNestedScrollView(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attributeSet,
                R.styleable.BottomDetectNestedScrollView,
                defStyleAttr,
                0);

        try {
            bottomTolerance = a.getDimensionPixelSize(
                    R.styleable.BottomDetectNestedScrollView_bottom_tolerance, 0);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt)
    {
        // Grab the last child placed in the ScrollView, we need it to determinate the bottom position.
        View view = (View) getChildAt(getChildCount()-1);

        // Calculate the scrolldiff
        int diff = (view.getBottom()-(getHeight()+getScrollY()));

        // if diff is zero, then the bottom has been reached
        atBottom = (diff <= bottomTolerance);

        super.onScrollChanged(l, t, oldl, oldt);
    }

    public boolean isAtBottom() {
        return atBottom;
    }
}
