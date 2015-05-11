package com.screeninteractiontest.jorge.ui.activity;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.screeninteractiontest.jorge.R;
import com.screeninteractiontest.jorge.data.datamodel.Contact;
import com.screeninteractiontest.jorge.io.prefs.PreferenceAssistant;
import com.screeninteractiontest.jorge.ui.activity.base.IcedAppCompatActivity;
import com.screeninteractiontest.jorge.ui.adapter.ContactRecyclerAdapter;
import com.screeninteractiontest.jorge.ui.widget.ChainableSwipeRefreshLayout;
import com.screeninteractiontest.jorge.ui.component.RecyclerItemClickListener;
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

        initializeActionBar();
        initializeContactList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Picasso.with(mContext).cancelTag(IMAGE_LOAD_TAG);
    }

    private void initializeContactList() {
        mContactListSwipeToRefreshLayout.setColorSchemeColors(R.color.theme_primary, R.color.theme_text_primary);
        mContactList.setLayoutManager(new LinearLayoutManager(mContext));
        mContactList.setItemAnimator(new DefaultItemAnimator());
        mContactList.setAdapter(mContactAdapter = new ContactRecyclerAdapter(mContext, this, IMAGE_LOAD_TAG, PreferenceAssistant.readSharedInteger(mContext, PreferenceAssistant.PREF_SORT_MODE, 0)));
        updateEmptyViewVisibility();
        final TypedValue tv = new TypedValue();
        Integer actionBarHeight = -1;
        if (mContext.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, Boolean.TRUE)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,
                    getResources().getDisplayMetrics());
        }
        final Integer progressBarStartMargin = mContext.getResources().getDimensionPixelSize(
                R.dimen.swipe_refresh_progress_bar_start_margin),
                progressBarEndMargin = mContext.getResources().getDimensionPixelSize(
                        R.dimen.swipe_refresh_progress_bar_end_margin);
        mContactListSwipeToRefreshLayout.setProgressViewOffset(Boolean.FALSE, actionBarHeight + progressBarStartMargin, actionBarHeight + progressBarEndMargin);
        mContactListSwipeToRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mContactAdapter.requestDataLoad();
            }
        });
        mContactList.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(final View childView, final int position) {
                ContactListActivity.this.launchContactDetail(mContactAdapter.getContact(position));
            }
        }));
        mContactListSwipeToRefreshLayout.setRecyclerView(mContactList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mContactAdapter.parseLocalContacts();
    }

    private void launchContactDetail(final Contact contact) {
        final Intent intent = new Intent(mContext, ContactDetailActivity.class);
        final Bundle extras = new Bundle();
        extras.putParcelable(ContactDetailActivity.EXTRA_KEY_CONTACT, contact);
        intent.putExtras(extras);
        //noinspection unchecked
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    private void updateEmptyViewVisibility() {
        if (mContactAdapter.getItemCount() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    private void initializeActionBar() {
        setSupportActionBar(mToolbar);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.title_contact_list);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_contact_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort:
                final View anchorView = findViewById(R.id.action_sort);
                showSortModes(anchorView != null ? anchorView : findViewById(android.R.id.home));
                return Boolean.TRUE;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSortModes(@NonNull final View sourceView) {
        final PopupMenu popupMenu = new PopupMenu(this, sourceView);
        popupMenu.inflate(R.menu.popup_sort_modes);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final MenuItem item) {
                ContactRecyclerAdapter.SORT_MODE newSortMode;

                switch (item.getItemId()) {
                    case R.id.sort_mode_first_name_descending:
                        newSortMode = ContactRecyclerAdapter.SORT_MODE.SORT_MODE_FIRST_NAME_DESCENDING;
                        break;
                    case R.id.sort_mode_first_name_ascending:
                        newSortMode = ContactRecyclerAdapter.SORT_MODE.SORT_MODE_FIRST_NAME_ASCENDING;
                        break;
                    case R.id.sort_mode_last_name_descending:
                        newSortMode = ContactRecyclerAdapter.SORT_MODE.SORT_MODE_LAST_NAME_DESCENDING;
                        break;
                    case R.id.sort_mode_last_name_ascending:
                        newSortMode = ContactRecyclerAdapter.SORT_MODE.SORT_MODE_LAST_NAME_ASCENDING;
                        break;
                    default:
                        //Will never happen
                        throw new IllegalArgumentException("Received invalid id " + item.getItemId());
                }
                mContactAdapter.setSortMode(newSortMode);
                return Boolean.TRUE;
            }
        });
        popupMenu.show();
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
    public void onStop() {
        Picasso.with(mContext).cancelTag(IMAGE_LOAD_TAG);
        super.onStop();
    }
}
