package com.screeninteractiontest.jorge.io.api;

import android.content.Context;

import com.google.gson.Gson;
import com.screeninteractiontest.jorge.R;
import com.screeninteractiontest.jorge.data.datamodel.Contact;

import java.lang.reflect.Type;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.converter.ConversionException;
import retrofit.converter.GsonConverter;
import retrofit.http.GET;
import retrofit.mime.TypedInput;

/**
 * @author Jorge Antonio Diaz-Benito Soriano (github.com/Stoyicker).
 */
public abstract class ContactApiClient {

    private static final Object LOCK = new Object();
    private static IContactApi apiService;

    public static IContactApi getContactApiClient(final Context context) {
        IContactApi ret = apiService;
        if (ret == null)
            synchronized (LOCK) {
                ret = apiService;
                if (ret == null) {
                    final RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(context.getString(R.string.contact_api_endpoint)).
                            setConverter(new GsonConverter(new Gson()) {
                                @Override
                                public Object fromBody(TypedInput body, Type type) throws ConversionException {
                                    return super.fromBody(body, type);
                                }
                            }).build();
                    ret = restAdapter.create(IContactApi.class);
                    apiService = ret;
                }
            }

        return ret;
    }

    public interface IContactApi {

        @GET("/worksample-android/contacts.json")
        void getContacts(final Callback<List<Contact>> callback);
    }
}
