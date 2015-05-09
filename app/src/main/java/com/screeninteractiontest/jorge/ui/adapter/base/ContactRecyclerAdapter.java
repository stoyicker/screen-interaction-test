package com.screeninteractiontest.jorge.ui.adapter.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.screeninteractiontest.jorge.R;
import com.screeninteractiontest.jorge.datamodel.Contact;
import com.screeninteractiontest.jorge.io.api.ContactApiClient;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * @author Jorge Antonio Diaz-Benito Soriano (github.com/Stoyicker).
 */
public final class ContactRecyclerAdapter extends RecyclerView.Adapter<ContactRecyclerAdapter.ViewHolder> {

    private final List<Contact> items = new ArrayList<>();
    private final Context mContext;

    @SuppressWarnings("FieldCanBeLocal") //For visibility
    private final Integer DEFAULT_CONTACT_IMAGE_RES_ID = R.drawable.contact_default;

    private static String IMAGE_LOAD_TAG;
    private final IListObserver mListObserver;

    public ContactRecyclerAdapter(final Context context, final IListObserver listObserver, final String imageLoadTag) {
        this.mContext = context;
        this.mListObserver = listObserver;
        IMAGE_LOAD_TAG = imageLoadTag;
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
            holder.favoriteView.setVisibility(item.isFavorite() ? View.VISIBLE : View.GONE);
            holder.nameView.setText(item.getFullName());
            holder.positionView.setText(item.getPosition());
            Picasso.with(mContext).load(item.getPhotoUrl()).error(DEFAULT_CONTACT_IMAGE_RES_ID).tag(IMAGE_LOAD_TAG).into(holder.photoView);
        }
    }

    private Contact getItem(final int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return getItem(position).getId();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void requestDataLoad() {
        ContactApiClient.getContactApiClient(mContext).getContacts(new Callback<List<Contact>>() {
            @Override
            public void success(final List<Contact> contacts, final Response response) {
                items.clear();
                //What it has to add is not these contacts. Instead, it will send them to the manager, who will dump them to the database and return the updated ones
                items.addAll(contacts);
                notifyDataSetChanged();
                if (mListObserver != null)
                    mListObserver.onDataReloadCompleted();
            }

            @Override
            public void failure(final RetrofitError error) {
                Log.e("endpoint", error.getMessage());
                if (mListObserver != null)
                    mListObserver.onDataReloadErrored();
            }
        });
    }

    public interface IListObserver {

        void onDataReloadCompleted();

        void onDataReloadErrored();

        void onContactSelected(final Integer contactId);
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
