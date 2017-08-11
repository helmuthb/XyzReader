package com.example.xyzreader.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Imageview which is never higher than the screen height
 */

public class MaxHeightImageView extends AppCompatImageView {
    public MaxHeightImageView(Context ctx) {
        super(ctx);
    }

    public MaxHeightImageView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
    }

    public MaxHeightImageView(Context ctx, AttributeSet attrs, int defStyleAttr) {
        super(ctx, attrs, defStyleAttr);
    }

    private int getMaxViewHeight() {
        try {
            Activity activity = (Activity) getContext();
            Window window = activity.getWindow();
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            return layoutParams.height - 128;
        }
        catch (Exception e) {
            // return system height
            return Resources.getSystem().getDisplayMetrics().heightPixels - 128;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredHeight = getMeasuredHeight();
        int maxHeight = getMaxViewHeight();
        if (maxHeight < measuredHeight) {
            setMeasuredDimension(getMeasuredWidth(), maxHeight);
        }
    }
}
