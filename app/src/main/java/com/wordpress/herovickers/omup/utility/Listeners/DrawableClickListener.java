package com.wordpress.herovickers.omup.utility.Listeners;

public interface DrawableClickListener {

    enum DrawablePosition { TOP, BOTTOM, LEFT, RIGHT };
    void onClick(DrawablePosition target);
}