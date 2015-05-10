package com.screeninteractiontest.jorge.ui.activity;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.screeninteractiontest.jorge.R;
import com.screeninteractiontest.jorge.data.datamodel.Contact;
import com.screeninteractiontest.jorge.ui.activity.base.IcedAppCompatActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import icepick.Icicle;

/**
 * @author Jorge Antonio Diaz-Benito Soriano (github.com/Stoyicker).
 */
public final class ContactDetailActivity extends IcedAppCompatActivity {

    static final String EXTRA_KEY_CONTACT = "EXTRA_KEY_CONTACT";

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @Icicle
    private Contact mContact;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);
        ButterKnife.inject(this);

        mContact = getIntent().getExtras().getParcelable(EXTRA_KEY_CONTACT);

        initializeActionBar(mToolbar, mContact.getName());
    }

    private void initializeActionBar(final Toolbar toolbar, final String contactName) {
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(contactName);
            actionBar.setDisplayHomeAsUpEnabled(Boolean.FALSE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            //TODO Options
            case android.R.id.home:
                ActivityCompat.finishAfterTransition(this);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
