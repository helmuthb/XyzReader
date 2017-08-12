package com.example.xyzreader.ui;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

/**
 * Hide the FAB when the user scrolls, and show it when they are at the end.
 * Based on https://stackoverflow.com/q/41153619/813725
 */

public class HideFabOnScrollBehavior extends FloatingActionButton.Behavior {

    static final int DELTA_SCROLL = 10;

    public HideFabOnScrollBehavior() {
        super();
    }

    public HideFabOnScrollBehavior(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout cl, FloatingActionButton child, View direct,
                                       View target, int nestedScrollAxes) {
        return super.onStartNestedScroll(cl, child, direct, target, nestedScrollAxes) ||
                nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedScroll(CoordinatorLayout cl, FloatingActionButton fab, View target,
                               int dxUsed, int dyUsed, int dxFree, int dyFree) {
        super.onNestedScroll(cl, fab, target, dxUsed, dyUsed, dxFree, dyFree);
        if (target instanceof BottomDetectNestedScrollView) {
            // show the FAB when we are at the bottom
            if (((BottomDetectNestedScrollView) target).isAtBottom()) {
                fab.show();
            }
        }
        if (dyUsed > DELTA_SCROLL) {
            // user scrolled down -> hide FAB
            fab.hide(
                    new FloatingActionButton.OnVisibilityChangedListener() {
                        @Override
                        public void onHidden(FloatingActionButton fab) {
                            super.onHidden(fab);
                            fab.setVisibility(View.INVISIBLE);
                        }
                    }
            );
        } else if (dyUsed < -DELTA_SCROLL) {
            // user scrolled up -> show FAB
            fab.show();
        }
    }

}
