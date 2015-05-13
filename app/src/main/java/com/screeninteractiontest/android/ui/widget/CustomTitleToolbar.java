package com.screeninteractiontest.android.ui.widget;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.TextView;

import com.screeninteractiontest.android.R;

/**
 * Custom toolbar that allows its title to be configured in detail.
 *
 * @author Jorge Antonio Diaz-Benito Soriano (github.com/Stoyicker).
 */
public final class CustomTitleToolbar extends Toolbar {

    private TextView mTitleView;

    /**
     * Standard constructor.
     *
     * @param context {@link Context} Context
     * @param attrs   {@link AttributeSet} Attributes specified through XML
     */
    public CustomTitleToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTitle(final int resId) {
        setTitle(getContext().getString(resId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTitle(final CharSequence title) {
        if (mTitleView == null)
            mTitleView = ((TextView) findViewById(R.id.toolbar_title));
        mTitleView.setText(title);
    }
}

