package com.screeninteractiontest.android.ui.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * A {@link SwipeRefreshLayout} that supports chained scrolling (with a regular one you would not
 * be able to properly scroll the list it contains upwards). This way up scrolling is deferred to
 * the list unless no up scrolling can be performed, case in which a refresh call is triggered.
 *
 * @author Jorge Antonio Diaz-Benito Soriano (github.com/Stoyicker).
 */
public final class ChainableSwipeRefreshLayout extends SwipeRefreshLayout {

    private RecyclerView mRecyclerView;

    /**
     * Standard constructor.
     *
     * @param context {@link Context} Context
     * @param attrs   {@link AttributeSet} Attributes specified through XML
     */
    public ChainableSwipeRefreshLayout(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Binds this layout to a {@link RecyclerView} object
     *
     * @param _recyclerView {@link RecyclerView} The recyclerview to bind to
     */
    public void setRecyclerView(final RecyclerView _recyclerView) {
        mRecyclerView = _recyclerView;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canChildScrollUp() {
        if (mRecyclerView == null)
            return super.canChildScrollUp();
        else
            return mRecyclerView.canScrollVertically(-1);
    }
}
