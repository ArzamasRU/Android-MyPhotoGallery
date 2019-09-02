package ru.arzamas.myphotogallery;

import android.util.Log;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

public class CircularViewPagerHandler implements ViewPager.OnPageChangeListener {
    private ViewPager   viewPager;
    private int         currentPosition;
    private volatile int         scrollState;

    public CircularViewPagerHandler(final ViewPager viewPager) {
        this.viewPager = viewPager;
    }

    @Override
    public void onPageSelected(final int position) {
        currentPosition = position;
    }

    @Override
    public void onPageScrollStateChanged(final int state) {
        handleScrollState(state);
        scrollState = state;
    }

    private void handleScrollState(final int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            setNextItemIfNeeded();
        }
    }

    private void setNextItemIfNeeded() {
        if (!isScrollStateSettling()) {
            handleSetNextItem();
        }
    }

    private boolean isScrollStateSettling() {
        return scrollState == ViewPager.SCROLL_STATE_SETTLING;
    }

    private void handleSetNextItem() {
        if (scrollState != ViewPager.SCROLL_STATE_IDLE) {
            final int lastPosition = viewPager.getAdapter().getCount() - 1;
            if (currentPosition == 0) {
                viewPager.setCurrentItem(lastPosition, true);
            } else if (currentPosition == lastPosition) {
                viewPager.setCurrentItem(0, true);
            }
        }
    }

    @Override
    public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {
    }
}
