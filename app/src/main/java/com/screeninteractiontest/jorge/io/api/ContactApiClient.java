package com.screeninteractiontest.jorge.io.api;

import android.content.Context;

import com.screeninteractiontest.jorge.R;
import com.screeninteractiontest.jorge.datamodel.Contact;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.GET;

/**
 * @author Jorge Antonio Diaz-Benito Soriano (github.com/Stoyicker).
 */
public abstract class ContactApiClient {

    private static final Object LOCK = new Object();
    private static ContactApiInterface apiService;

    public static ContactApiInterface getContactApiClient(final Context context) {
        ContactApiInterface ret = apiService;
        if (ret == null)
            synchronized (LOCK) {
                ret = apiService;
                if (ret == null) {
                    final RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(context.getString(R.string.contact_api_endpoint)).build();
                    ret = restAdapter.create(ContactApiInterface.class);
                    apiService = ret;
                }
            }

        return ret;
    }

    public interface ContactApiInterface {

        @GET("/worksample-android/contacts.json")
        void getContacts(final Callback<List<Contact>> callback);
    }
}
