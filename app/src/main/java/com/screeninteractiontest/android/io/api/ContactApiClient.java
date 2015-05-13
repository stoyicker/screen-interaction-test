package com.screeninteractiontest.android.io.api;

import android.content.Context;

import com.screeninteractiontest.android.R;
import com.screeninteractiontest.android.data.datamodel.Contact;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.GET;

/**
 * Adapted from <a href="https://www.youtube.com/watch?v=G6f9JQvGvr4">YouTube</a>.
 * This class implements a well-known pattern for REST API data consumption using <a
 * href="http://square.github.io/retrofit/">Retrofit</a>.
 *
 * @author Jorge Antonio Diaz-Benito Soriano (github.com/Stoyicker).
 */
public abstract class ContactApiClient {

    private static final Object LOCK = new Object();
    private static IContactApi apiService;

    /**
     * Retrieves the singleton instance to handle requests to this API.
     *
     * @param context {@link Context} Required by the constructor
     * @return {@link com.screeninteractiontest.android.io.api.ContactApiClient.IContactApi} The
     * singleton to use to perform requests to this API
     * @see com.screeninteractiontest.android.io.api.ContactApiClient.IContactApi
     */
    public static IContactApi getContactApiClient(final Context context) {
        IContactApi ret = apiService;
        if (ret == null)
            synchronized (LOCK) {
                ret = apiService;
                if (ret == null) {
                    final RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(context.getString(R.string.contact_api_endpoint)).build();
                    ret = restAdapter.create(IContactApi.class);
                    apiService = ret;
                }
            }

        return ret;
    }

    /**
     * This interface defines the endpoint methods (in this case only one) and how they should be
     * called.
     */
    public interface IContactApi {

        /**
         * Defines the method corresponding to the contacts.json file and states that it takes no
         * parameters and should deserialize the data parsed into a {@link List<Contact>} object.
         * The execution of the request is asynchronous.
         *
         * @param callback {@link Callback<List<Contact>>} The callback to execute upon operation
         *                 completion.
         */
        @GET("/worksample-android/contacts.json")
        void getContacts(final Callback<List<Contact>> callback);
    }
}
