package com.example.xyzreader.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * ImageView subclass which allows to define the aspect ratio
 */

public class AspectRatioImageView extends AppCompatImageView {

    private double aspectRatio = 1.5;

    public AspectRatioImageView(Context ctx) {
        super(ctx);
    }

    public AspectRatioImageView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
    }

    public AspectRatioImageView(Context ctx, AttributeSet attrs, int defStyleAttr) {
        super(ctx, attrs, defStyleAttr);
    }

    public void setAspectRatio(double aspectRatio) {
        this.aspectRatio = aspectRatio;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = getMeasuredWidth();
        setMeasuredDimension(measuredWidth, (int) (measuredWidth / aspectRatio));
    }
}
