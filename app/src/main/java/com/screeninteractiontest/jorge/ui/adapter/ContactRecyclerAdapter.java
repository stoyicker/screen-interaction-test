package com.screeninteractiontest.jorge.ui.adapter;

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

import com.screeninteractiontest.jorge.R;
import com.screeninteractiontest.jorge.data.datamodel.Contact;
import com.screeninteractiontest.jorge.data.middlelayer.ContactManager;
import com.screeninteractiontest.jorge.io.api.ContactApiClient;
import com.screeninteractiontest.jorge.io.prefs.PreferenceAssistant;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
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
 * @author Jorge Antonio Diaz-Benito Soriano (github.com/Stoyicker).
 */
public final class ContactRecyclerAdapter extends RecyclerView.Adapter<ContactRecyclerAdapter.ViewHolder> {

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
            CONTACT_IMAGE_RES_ID_DEFAULT = R.drawable.contact_thumbail_default, CONTACT_IMAGE_RES_ID_ERROR = R.drawable.contact_thumbail_error;

    private static String IMAGE_LOAD_TAG;
    private final IListObserver mListObserver;

    public ContactRecyclerAdapter(final Context context, final IListObserver listObserver, final String imageLoadTag, final Integer sortMode) {
        final SORT_MODE[] sortModeValues = SORT_MODE.values();
        if (sortMode < 0 || sortMode >= sortModeValues.length) {
            throw new IllegalArgumentException("Value " + sortMode + " does not map to a correct " + SORT_MODE.class.getName() + ": " + Arrays.toString(sortModeValues));
        }

        this.mContext = context;
        this.mListObserver = listObserver;
        IMAGE_LOAD_TAG = imageLoadTag;
        parseLocalContacts();
        setSortMode(sortModeValues[sortMode]);
    }

    private void parseLocalContacts() {
        new AsyncTask<Void, Void, List<Contact>>() {
            @Override
            protected List<Contact> doInBackground(final Void... params) {
                return ContactManager.getAllContacts();
            }

            @Override
            protected void onPostExecute(final List<Contact> contacts) {
                if (!contacts.isEmpty()) {
                    items.addAll(contacts);
                }
                if (mListObserver != null)
                    mListObserver.onDataReloadCompleted();
            }
        }.executeOnExecutor(Executors.newSingleThreadExecutor());
    }

    public Contact getContact(final int position) {
        return items.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout
                .list_item_contact, parent, Boolean.FALSE));
    }

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
                instance.load(CONTACT_IMAGE_RES_ID_DEFAULT).into(holder.photoView);
            } else
                instance.load(item.getThumbnailUrl()).error(CONTACT_IMAGE_RES_ID_ERROR).placeholder(CONTACT_IMAGE_RES_ID_ERROR).tag(IMAGE_LOAD_TAG).into(holder.photoView);
        }
    }

    private Contact getItem(final int position) {
        return items.get(position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void requestDataLoad() {
        ContactApiClient.getContactApiClient(mContext).getContacts(new Callback<List<Contact>>() {
            @Override
            public void success(final List<Contact> contacts, final Response response) {
                //I am assuming that the endpoint doesn't correspond to all the contacts, but rather to new ones, and therefore I should implement the storage locally, since I can't post data. Because of this, how I consume the "API" is I download the data and, if there are any new contacts, I add them and refresh, but if there are not then I'm done.
                //noinspection unchecked I don't want to pass them one by one as it is slower DB-wise
                new AsyncTask<List<Contact>, Void, List<Contact>>() {
                    @Override
                    protected List<Contact> doInBackground(final List<Contact>... params) {
                        return ContactManager.insertIfProceeds(params[0]);
                    }

                    @Override
                    protected void onPostExecute(final List<Contact> newContacts) {
                        if (!newContacts.isEmpty()) {
                            items.addAll(newContacts);
                            requestSort();
                            notifyDataSetChanged();
                        }
                        if (mListObserver != null)
                            mListObserver.onDataReloadCompleted();
                    }
                }.executeOnExecutor(Executors.newSingleThreadExecutor(), contacts);
            }

            @Override
            public void failure(final RetrofitError error) {
                Log.e(error.getUrl(), error.getMessage());
                if (mListObserver != null)
                    mListObserver.onDataReloadErrored();
            }
        });
    }

    public void setSortMode(final SORT_MODE newSortMode) {
        this.mSortMode = newSortMode;
        PreferenceAssistant.writeSharedInteger(mContext, PreferenceAssistant.PREF_SORT_MODE, mSortMode.ordinal());
        requestSort();
    }

    private void requestSort() {
        Comparator<Contact> comparator;
        switch (mSortMode) {
            case SORT_MODE_FIRST_NAME_ASCENDING:
                comparator = new Comparator<Contact>() {
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
                    @Override
                    public int compare(@NonNull final Contact lhs, @NonNull final Contact rhs) {
                        Integer ret;

                        final Boolean lF = lhs.isFavorite();
                        if (lF != rhs.isFavorite()) {
                            ret = lhs.isFavorite() ? 1 : -1;
                        } else ret = lhs.getFirstName().compareToIgnoreCase(rhs.getFirstName());

                        return ret;
                    }
                };
                break;
            case SORT_MODE_LAST_NAME_ASCENDING:
                comparator = new Comparator<Contact>() {
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
                    @Override
                    public int compare(@NonNull final Contact lhs, @NonNull final Contact rhs) {
                        Integer ret;

                        final Boolean lF = lhs.isFavorite();
                        if (lF != rhs.isFavorite()) {
                            ret = lhs.isFavorite() ? 1 : -1;
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

    public interface IListObserver {

        void onDataReloadCompleted();

        void onDataReloadErrored();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.thumbnail)
        ImageView photoView;

        @InjectView(R.id.name)
        TextView nameView;

        @InjectView(R.id.position)
        TextView positionView;

        @InjectView(R.id.favorite)
        View favoriteView;

        public ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}
