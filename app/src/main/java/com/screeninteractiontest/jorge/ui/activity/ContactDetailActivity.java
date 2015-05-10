package com.screeninteractiontest.jorge.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
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
    public boolean onCreateOptionsMenu(final Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_contact_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_favorite:
                return Boolean.TRUE;
            case R.id.action_save:
                launchSaveContactToPhone();
                return Boolean.TRUE;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void launchSaveContactToPhone() {
        final Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
        intent.putExtra(ContactsContract.Intents.Insert.NAME, mContact.getName());
        intent.putExtra(ContactsContract.Intents.Insert.JOB_TITLE, mContact.getJobTitle());
        intent.putExtra(ContactsContract.Intents.Insert.EMAIL, mContact.getEmail());
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, mContact.getPhone());
        intent.putExtra("website", mContact.getWebpage());
        startActivity(intent);
    }
}
