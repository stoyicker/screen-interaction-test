package com.screeninteractiontest.jorge.ui.adapter.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.screeninteractiontest.jorge.R;
import com.screeninteractiontest.jorge.datamodel.Contact;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jorge Antonio Diaz-Benito Soriano (github.com/Stoyicker).
 */
public final class ContactRecyclerAdapter extends RecyclerView.Adapter<ContactRecyclerAdapter.ViewHolder> {

    private final List<Contact> items = new ArrayList<>();
    private final Context mContext;
    private final Integer DEFAULT_CONTACT_IMAGE_RES_ID = R.drawable.contact_default;
    private final IOnContactSelectionListener mOnItemSelectedCallback;

    public ContactRecyclerAdapter(final Context context, final IOnContactSelectionListener onItemSelectedListener) {
        this.mContext = context;
        this.mOnItemSelectedCallback = onItemSelectedListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout
                .list_item_contact, parent, Boolean.FALSE));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface IOnContactSelectionListener {

        void onContactSelected(final Integer contactId);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(final View itemView) {
            super(itemView);
        }
    }
}
