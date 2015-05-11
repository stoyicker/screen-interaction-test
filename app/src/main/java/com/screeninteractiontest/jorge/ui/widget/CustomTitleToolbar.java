package com.screeninteractiontest.jorge.ui.widget;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.TextView;

import com.screeninteractiontest.jorge.R;

/**
 * @author Jorge Antonio Diaz-Benito Soriano (github.com/Stoyicker).
 */
public final class CustomTitleToolbar extends Toolbar {

    private TextView mTitleView;

    public CustomTitleToolbar(Context context) {
        super(context);
    }

    public CustomTitleToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTitleToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setTitle(final int resId) {
        setTitle(getContext().getString(resId));
    }

    @Override
    public void setTitle(final CharSequence title) {
        if (mTitleView == null)
            mTitleView = ((TextView) findViewById(R.id.toolbar_title));
        mTitleView.setText(title);
    }
}

