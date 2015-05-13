package com.screeninteractiontest.android.ui.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.screeninteractiontest.android.R;
import com.screeninteractiontest.android.data.datamodel.Contact;
import com.screeninteractiontest.android.data.middlelayer.ContactManager;
import com.screeninteractiontest.android.io.api.ContactApiClient;
import com.screeninteractiontest.android.io.prefs.PreferenceAssistant;
import com.screeninteractiontest.android.ui.widget.ListenableRippleView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Adapter for the contact list.
 *
 * @author Jorge Antonio Diaz-Benito Soriano (github.com/Stoyicker).
 */
public final class ContactRecyclerAdapter extends RecyclerView.Adapter<ContactRecyclerAdapter.ViewHolder> {

    private IContactClickListener mContactClickListener;

    public void setOnContactClickListener(final IContactClickListener listener) {
        mContactClickListener = listener;
    }

    public enum SORT_MODE {
        SORT_MODE_FIRST_NAME_DESCENDING,
        SORT_MODE_FIRST_NAME_ASCENDING,
        SORT_MODE_LAST_NAME_DESCENDING,
        SORT_MODE_LAST_NAME_ASCENDING
    }

    private SORT_MODE mSortMode;

    private final List<Contact> items = new ArrayList<>();
    private final Context mContext;

    @SuppressWarnings("FieldCanBeLocal") //For visibility
    private final Integer
            CONTACT_THUMBNAIL_RES_ID_DEFAULT = R.drawable.contact_thumbail_default, CONTACT_THUMBNAIL_RES_ID_ERROR = R.drawable.contact_thumbail_error;

    private static String IMAGE_LOAD_TAG;
    private final IListObserver mListObserver;

    /**
     * Standard constructor. It triggers a sort.
     *
     * @param context      {@link Context} Context
     * @param listObserver {@link com.screeninteractiontest.android.ui.adapter
     *                     .ContactRecyclerAdapter.IListObserver} Observer for data load completion
     * @param imageLoadTag {@link String} Tag for Picasso image loading
     * @param sortMode     {@link Integer} Sort mode
     */
    public ContactRecyclerAdapter(final Context context, final IListObserver listObserver, final String imageLoadTag, final Integer sortMode) {
        final SORT_MODE[] sortModeValues = SORT_MODE.values();
        if (sortMode < 0 || sortMode >= sortModeValues.length) {
            throw new IllegalArgumentException("Value " + sortMode + " does not map to a correct " + SORT_MODE.class.getName() + ": " + Arrays.toString(sortModeValues));
        }
        this.mContext = context;
        this.mListObserver = listObserver;
        IMAGE_LOAD_TAG = imageLoadTag;
        setSortMode(sortModeValues[sortMode]);
    }

    /**
     * Fills the adapter with the locally stored contacts.
     */
    public void parseLocalContacts() {
        new AsyncTask<Void, Void, Collection<Contact>>() {
            /**
             * {@inheritDoc}
             */
            @Override
            protected Collection<Contact> doInBackground(final Void... params) {
                return ContactManager.getAllContacts();
            }

            /**
             * {@inheritDoc}
             */
            @Override
            protected void onPostExecute(final Collection<Contact> contacts) {
                items.clear();
                items.addAll(contacts);
                requestSort();

                if (mListObserver != null)
                    mListObserver.onDataReloadCompleted();
            }
        }.executeOnExecutor(Executors.newSingleThreadExecutor());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout
                .list_item_contact, parent, Boolean.FALSE), mContactClickListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Contact item = getItem(position);
        if (item != null) {
            holder.favoriteView.setVisibility(item.isFavorite() ? View.VISIBLE : View.INVISIBLE);
            holder.nameView.setText(item.getName());
            holder.positionView.setText(item.getJobTitle());
            final String thumbnailUrl = item.getThumbnailUrl();
            final Picasso instance = Picasso.with(mContext);
            if (thumbnailUrl.isEmpty()) {
                instance.load(CONTACT_THUMBNAIL_RES_ID_DEFAULT).into(holder.photoView);
            } else
                instance.load(thumbnailUrl).error(CONTACT_THUMBNAIL_RES_ID_ERROR).placeholder(CONTACT_THUMBNAIL_RES_ID_DEFAULT).tag(IMAGE_LOAD_TAG).into(holder.photoView);
        }
    }

    /**
     * Retrieves a contact based on its position on the adapter
     *
     * @param position {@link Integer} The index to fetch
     * @return {@link Contact} The contact requested
     * @see ArrayList#get(int)
     */
    private Contact getItem(final int position) {
        return items.get(position);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Triggers a data reload and reacts to its execution.
     */
    public void requestDataLoad() {
        ContactApiClient.getContactApiClient(mContext).getContacts(new Callback<List<Contact>>() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void success(final List<Contact> contacts, final Response response) {
                /**
                 * The way that I am assuming that the "endpoint" would work if it wasn't static
                 * data is that it would provide new contacts only, so the only thing that I
                 * with the data downloaded is insert that which is not already into the database
                 */
                //noinspection unchecked I don't want to pass them one by one as it is slower DB-wise
                new AsyncTask<List<Contact>, Void, Collection<Contact>>() {

                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    protected Collection<Contact> doInBackground(final List<Contact>... params) {
                        return ContactManager.insertIfProceeds(params[0]);
                    }

                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    protected void onPostExecute(final Collection<Contact> newContacts) {
                        if (!newContacts.isEmpty()) {
                            items.addAll(newContacts);
                            requestSort();
                        }
                        if (mListObserver != null)
                            mListObserver.onDataReloadCompleted();
                    }
                }.executeOnExecutor(Executors.newSingleThreadExecutor(), contacts);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void failure(final RetrofitError error) {
                Log.e(error.getUrl(), error.getMessage());
                if (mListObserver != null)
                    mListObserver.onDataReloadErrored();
            }
        });
    }

    /**
     * Updates the sorting mode and requests the contacts to be sorted.
     *
     * @param newSortMode {@link com.screeninteractiontest.android.ui.adapter
     *                    .ContactRecyclerAdapter.SORT_MODE} The new sort mode
     */
    public void setSortMode(final SORT_MODE newSortMode) {
        this.mSortMode = newSortMode;
        PreferenceAssistant.writeSharedInteger(mContext, PreferenceAssistant.PREF_SORT_MODE, mSortMode.ordinal());
        requestSort();
    }

    /**
     * Sorts the contacts using the current sorting mode.
     */
    private void requestSort() {
        Comparator<Contact> comparator;
        switch (mSortMode) {
            case SORT_MODE_FIRST_NAME_ASCENDING:
                comparator = new Comparator<Contact>() {
                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    public int compare(@NonNull final Contact lhs, @NonNull final Contact rhs) {
                        Integer ret;

                        final Boolean lF = lhs.isFavorite();
                        if (lF != rhs.isFavorite()) {
                            ret = lhs.isFavorite() ? 1 : -1;
                        } else ret = lhs.getFirstName().compareToIgnoreCase(rhs.getFirstName());

                        return -1 * ret;
                    }
                };
                break;
            case SORT_MODE_FIRST_NAME_DESCENDING:
                comparator = new Comparator<Contact>() {
                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    public int compare(@NonNull final Contact lhs, @NonNull final Contact rhs) {
                        Integer ret;

                        final Boolean lF = lhs.isFavorite();
                        if (lF != rhs.isFavorite()) {
                            ret = lhs.isFavorite() ? -1 : 1;
                        } else ret = lhs.getFirstName().compareToIgnoreCase(rhs.getFirstName());

                        return ret;
                    }
                };
                break;
            case SORT_MODE_LAST_NAME_ASCENDING:
                comparator = new Comparator<Contact>() {
                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    public int compare(@NonNull final Contact lhs, @NonNull final Contact rhs) {
                        Integer ret;

                        final Boolean lF = lhs.isFavorite();
                        if (lF != rhs.isFavorite()) {
                            ret = lhs.isFavorite() ? 1 : -1;
                        } else ret = lhs.getLastName().compareToIgnoreCase(rhs.getLastName());

                        return -1 * ret;
                    }
                };
                break;
            case SORT_MODE_LAST_NAME_DESCENDING:
                comparator = new Comparator<Contact>() {
                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    public int compare(@NonNull final Contact lhs, @NonNull final Contact rhs) {
                        Integer ret;

                        final Boolean lF = lhs.isFavorite();
                        if (lF != rhs.isFavorite()) {
                            ret = lhs.isFavorite() ? -1 : 1;
                        } else ret = lhs.getLastName().compareToIgnoreCase(rhs.getLastName());

                        return ret;
                    }
                };
                break;
            default:
                throw new IllegalStateException("Illegal " + SORT_MODE.class.getName() + " enum value " + mSortMode);
        }
        Collections.sort(items, comparator);
        notifyDataSetChanged();
    }

    /**
     * Interface to allow surveillance of data load events.
     */
    public interface IListObserver {

        /**
         * Called when a data reload is completed successfully.
         */
        void onDataReloadCompleted();

        /**
         * Called when a data reload is not completed successfully.
         */
        void onDataReloadErrored();
    }

    /**
     * <a href="http://developer.android.com/training/improving-layouts/smooth-scrolling
     * .html#ViewHolder">Hold View Objects in a View Holder</a>
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.thumbnail)
        ImageView photoView;

        @InjectView(R.id.name)
        TextView nameView;

        @InjectView(R.id.position)
        TextView positionView;

        @InjectView(R.id.favorite)
        View favoriteView;

        @InjectView(R.id.ripple_wrapper)
        ListenableRippleView mRippleWrapper;

        /**
         * Default constructor.
         *
         * @param itemView             {@link View} The item view
         * @param contactClickListener {@link com.screeninteractiontest.android.ui.adapter.ContactRecyclerAdapter.IContactClickListener} The listener that should be notified if this item is clicked.
         */
        private ViewHolder(final View itemView, final IContactClickListener contactClickListener) {
            super(itemView);
            ButterKnife.inject(this, itemView);

            mRippleWrapper.setOnRippleCompleteListener(new ListenableRippleView.IRippleComplete() {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public void onComplete(final ListenableRippleView rippleView) {
                    if (contactClickListener != null)
                        contactClickListener.onContactClick(items.get(getAdapterPosition()));
                }
            });
        }
    }

    /**
     * Interface to allow reaction to click-type events on adapter items.
     */
    public interface IContactClickListener {
        void onContactClick(final Contact contact);
    }
}
