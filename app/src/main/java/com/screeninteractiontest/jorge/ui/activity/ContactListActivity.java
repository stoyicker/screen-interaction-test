package com.screeninteractiontest.jorge.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.screeninteractiontest.jorge.R;
import com.screeninteractiontest.jorge.ui.activity.base.IcedAppCompatActivity;
import com.screeninteractiontest.jorge.ui.adapter.base.ContactRecyclerAdapter;
import com.screeninteractiontest.jorge.ui.component.ChainableSwipeRefreshLayout;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author Jorge Antonio Diaz-Benito Soriano (github.com/Stoyicker).
 */
public final class ContactListActivity extends IcedAppCompatActivity implements ContactRecyclerAdapter.IListObserver {

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @InjectView(R.id.contact_list)
    RecyclerView mContactList;

    @InjectView(R.id.contact_list_swipeable_layout)
    ChainableSwipeRefreshLayout mContactListSwipeToRefreshLayout;

    @InjectView(android.R.id.empty)
    View mEmptyView;

    private Context mContext;
    private ContactRecyclerAdapter mContactAdapter;

    private static final String IMAGE_LOAD_TAG = ContactListActivity.class.getCanonicalName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        ButterKnife.inject(this);

        mContext = getApplicationContext();

        initializeActionBar(mToolbar);
        initializeContactList(mContactList, mContactListSwipeToRefreshLayout);
    }

    private void initializeContactList(final RecyclerView contactList, final ChainableSwipeRefreshLayout contactListSwipeToRefreshLayout) {
        contactListSwipeToRefreshLayout.setColorSchemeColors(R.color.theme_primary, R.color.theme_text_primary);
        contactList.setLayoutManager(new LinearLayoutManager(mContext));
        contactList.setItemAnimator(new DefaultItemAnimator());
        contactList.setAdapter(mContactAdapter = new ContactRecyclerAdapter(mContext, this, IMAGE_LOAD_TAG));
        updateEmptyViewVisibility();
        final TypedValue tv = new TypedValue();
        Integer actionBarHeight = -1;
        if (mContext.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, Boolean.TRUE)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,
                    getResources().getDisplayMetrics());
        }
        if (actionBarHeight == -1) {
            //Should never happen, but just in case
            throw new IllegalStateException("Couldn't get the ActionBar height attribute");
        }
        final Integer progressBarStartMargin = mContext.getResources().getDimensionPixelSize(
                R.dimen.swipe_refresh_progress_bar_start_margin),
                progressBarEndMargin = mContext.getResources().getDimensionPixelSize(
                        R.dimen.swipe_refresh_progress_bar_end_margin);
        contactListSwipeToRefreshLayout.setProgressViewOffset(Boolean.FALSE, actionBarHeight + progressBarStartMargin, actionBarHeight + progressBarEndMargin);
        contactListSwipeToRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mContactAdapter.requestDataLoad();
            }
        });
        contactListSwipeToRefreshLayout.setRecyclerView(contactList);
    }

    private void updateEmptyViewVisibility() {
        if (mContactAdapter.getItemCount() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    private void initializeActionBar(final Toolbar toolbar) {
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.title_contact_list);
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            //TODO onOptionsItemSelected
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDataReloadCompleted() {
        mContactListSwipeToRefreshLayout.setRefreshing(Boolean.FALSE);
        updateEmptyViewVisibility();
    }

    @Override
    public void onDataReloadErrored() {
        mContactListSwipeToRefreshLayout.setRefreshing(Boolean.FALSE);
        Toast.makeText(mContext, R.string.error_contact_info_download, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onContactSelected(Integer contactId) {
        //TODO Launch the contact card activity
    }

    @Override
    public void onStop() {
        Picasso.with(mContext).cancelTag(IMAGE_LOAD_TAG);
        super.onStop();
    }
}
