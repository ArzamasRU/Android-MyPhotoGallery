package ru.arzamas.myphotogallery;

import android.view.View;

@FunctionalInterface
public interface IRecyclerViewListener {
    void onclick(View view, int position);
}
