package com.screeninteractiontest.jorge.ui.component;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Adapted from <a href="https://gist.github.com/lnikkila/d9493a0626e89059c6aa">https://gist.github.com/lnikkila/d9493a0626e89059c6aa</a>. Used for listening to RecyclerView item clicks. You can either implement an OnItemClickListener
 * or extend SimpleOnItemClickListener and override its methods.
 * <p/>
 * Licence: MIT
 *
 * @author Leo Nikkilä <hello@lnikki.la>
 */
public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {

    private final OnItemClickListener listener;

    private final GestureDetector gestureDetector;

    @Nullable
    private View childView;

    private int childViewPosition;

    public RecyclerItemClickListener(final Context context, final OnItemClickListener listener) {
        this.gestureDetector = new GestureDetector(context, new GestureListener());
        this.listener = listener;
    }

    @Override
    public boolean onInterceptTouchEvent(final RecyclerView view, final MotionEvent event) {
        childView = view.findChildViewUnder(event.getX(), event.getY());
        childViewPosition = view.getChildAdapterPosition(childView);

        return childView != null && gestureDetector.onTouchEvent(event);
    }

    @Override
    public void onTouchEvent(final RecyclerView view, final MotionEvent event) {
        // Not needed.
    }

    /**
     * A click listener for items.
     */
    public interface OnItemClickListener {

        /**
         * Called when an item is clicked.
         *
         * @param childView View of the item that was clicked.
         * @param position  Position of the item that was clicked.
         */
        void onItemClick(final View childView, final int position);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(final MotionEvent event) {
            if (childView != null) {
                listener.onItemClick(childView, childViewPosition);
            }

            return Boolean.FALSE;
        }

    }

}
