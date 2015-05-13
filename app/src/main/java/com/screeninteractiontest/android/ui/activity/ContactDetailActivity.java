package com.screeninteractiontest.android.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.screeninteractiontest.android.R;
import com.screeninteractiontest.android.data.datamodel.Contact;
import com.screeninteractiontest.android.data.middlelayer.ContactManager;
import com.screeninteractiontest.android.ui.activity.base.IcedAppCompatActivity;
import com.screeninteractiontest.android.ui.widget.ContactFieldView;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import icepick.Icicle;

/**
 * @author Jorge Antonio Diaz-Benito Soriano (github.com/Stoyicker).
 */
public final class ContactDetailActivity extends IcedAppCompatActivity {

    static final String EXTRA_KEY_CONTACT = "EXTRA_KEY_CONTACT";

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @InjectView(R.id.picture)
    ImageView mPictureView;

    @InjectView(R.id.overlay)
    TextView mOverlayView;

    @InjectView(R.id.phone)
    ContactFieldView mPhoneView;

    @InjectView(R.id.email)
    ContactFieldView mEmailView;

    @InjectView(R.id.website)
    ContactFieldView mWebpageView;

    @SuppressWarnings("FieldCanBeLocal") //For visibility
    private final Integer
            CONTACT_PICTURE_RES_ID_DEFAULT = R.drawable.contact_picture_default, CONTACT_PICTURE_RES_ID_ERROR = R.drawable.contact_picture_error;

    private static final String IMAGE_LOAD_TAG = ContactDetailActivity.class.getCanonicalName();

    @Icicle
    private Contact mContact;

    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);
        ButterKnife.inject(this);

        mContext = getApplicationContext();

        mContact = getIntent().getExtras().getParcelable(EXTRA_KEY_CONTACT);
        fillFields();
        initializeActionBar();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Picasso.with(mContext).cancelTag(IMAGE_LOAD_TAG);
    }

    @OnClick(R.id.picture)
    @SuppressWarnings("unused") //Butter Knife uses it
    public void iAmAStar(final View view) {
        if (mContact.isFavorite())
            tweetEgg();
    }

    /**
     * Dirty way to post a tweet. However, the formal way includes setting
     * up a rather heavy SDK, session management and the like, which is
     * a complete overkill for an easter egg.
     */
    private void tweetEgg() {
        final Intent tweetIntent = new Intent(Intent.ACTION_SEND);
        tweetIntent.putExtra(Intent.EXTRA_TEXT, String.format(Locale.ENGLISH, mContext.getString(R.string.easter_egg_message), mContact.getFirstName()));
        tweetIntent.setType("text/plain");

        final PackageManager packManager = getPackageManager();
        final List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(tweetIntent, PackageManager.MATCH_DEFAULT_ONLY);

        Boolean resolved = Boolean.FALSE;
        for (ResolveInfo resolveInfo : resolvedInfoList) {
            if (resolveInfo.activityInfo.name.contains("twitter")) {
                tweetIntent.setClassName(
                        resolveInfo.activityInfo.packageName,
                        resolveInfo.activityInfo.name);
                resolved = Boolean.TRUE;
                break;
            }
        }
        if (resolved) {
            startActivity(tweetIntent);
        } else {
            Toast.makeText(this, R.string.easter_egg_error_message, Toast.LENGTH_LONG).show();
        }
    }

    private void fillFields() {
        final String pictureUrl = mContact.getPictureUrl();
        final Picasso instance = Picasso.with(mContext);
        if (pictureUrl.isEmpty()) {
            instance.load(CONTACT_PICTURE_RES_ID_DEFAULT).into(mPictureView);
        } else
            instance.load(pictureUrl).error(CONTACT_PICTURE_RES_ID_ERROR).placeholder(CONTACT_PICTURE_RES_ID_DEFAULT).tag(IMAGE_LOAD_TAG).into(mPictureView);
        mOverlayView.setText(mContact.getJobTitle());

        final String phone = mContact.getPhone(), email = mContact.getEmail(), webpage = mContact.getWebpage();

        if (!TextUtils.isEmpty(phone)) {
            mPhoneView.setVisibility(View.VISIBLE);
            mPhoneView.setFieldValue(phone);
            mPhoneView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ContactDetailActivity.this.launchCallPhone();
                }
            });
        }
        if (!TextUtils.isEmpty(email)) {
            mEmailView.setVisibility(View.VISIBLE);
            mEmailView.setFieldValue(email);
            mEmailView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ContactDetailActivity.this.launchSendEmail();
                }
            });
        }
        if (!TextUtils.isEmpty(webpage)) {
            mWebpageView.setVisibility(View.VISIBLE);
            mWebpageView.setFieldValue(webpage);
            mWebpageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ContactDetailActivity.this.launchBrowseWebpage();
                }
            });
        }
    }

    /**
     * Shows the dialer with the phone number ready. In most cases it is a better approach than
     * commencing the call straightaway ({@link Intent#ACTION_CALL} cans be used instead with the
     * CALL_PHONE permission if this is a must).
     */
    private void launchCallPhone() {
        final Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + mContact.getPhone()));
        startActivity(intent);
    }

    private void launchSendEmail() {
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, mContact.getEmail());
        startActivity(intent);
    }

    private void launchBrowseWebpage() {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mContact.getWebpage()));
        startActivity(intent);
    }

    private void initializeActionBar() {
        setSupportActionBar(mToolbar);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(mContact.getName());
            actionBar.setDisplayHomeAsUpEnabled(Boolean.FALSE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_contact_detail, menu);
        menu.findItem(R.id.action_favorite).setIcon(getResources().getDrawable(mContact.isFavorite() ? R.drawable.ic_action_star_enabled : R.drawable.ic_action_star_disabled));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_favorite:
                toggleContactFavorite(item);
                return Boolean.TRUE;
            case R.id.action_save:
                launchSaveContactToPhone();
                return Boolean.TRUE;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void toggleContactFavorite(final MenuItem item) {
        ContactManager.toggleFavorite(mContact, new Runnable() {
            @Override
            public void run() {
                item.setIcon(getResources().getDrawable(mContact.isFavorite() ? R.drawable.ic_action_star_enabled : R.drawable.ic_action_star_disabled));
            }
        });
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
