package com.screeninteractiontest.jorge.data.middlelayer;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.screeninteractiontest.jorge.data.datamodel.Contact;
import com.screeninteractiontest.jorge.io.db.SQLiteDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * @author Jorge Antonio Diaz-Benito Soriano (github.com/Stoyicker).
 */
public final class ContactManager {

    private ContactManager() {
        throw new UnsupportedOperationException("Do not instantiate " + getClass().getName());
    }

    public static List<Contact> getAllContacts() {
        return SQLiteDAO.getInstance().getAllContacts();
    }

    /**
     * This method mocks a behavior that, although is not really necessary since the
     * contact list is known to be static, is adequate to use. It works under the
     * assumption that the endpoint can only have data added and not removed or
     * edited.
     *
     * @param contacts {@link List<Contact>} The list contacts to attempt to insert.
     * @return {@link List<Contact>} The ones actually inserted.
     */
    public static List<Contact> insertIfProceeds(final List<Contact> contacts) {
        final List<Contact> actualInsertions = new ArrayList<>();

        for (Contact x : contacts) {
            sanitizeContact(x);

            if (SQLiteDAO.getInstance().insertContact(x)) {
                actualInsertions.add(x);
            }
        }

        return actualInsertions;
    }

    private static void sanitizeContact(@NonNull final Contact contact) {
        if (contact.getName() == null)
            contact.setName("");

        if (contact.isFavorite() == null) {
            contact.setFavorite(Boolean.FALSE);
        }
    }

    public static void toggleFavorite(final Contact contact, final Runnable callback) {
        new AsyncTask<Object, Void, Runnable>() {

            @Override
            protected Runnable doInBackground(final Object... params) {
                final Contact contact = (Contact) params[0];
                if (SQLiteDAO.getInstance().updateContactIsFavorite(contact, !contact.isFavorite()))
                    contact.setFavorite(!contact.isFavorite());
                return (Runnable) params[1];
            }

            @Override
            protected void onPostExecute(final Runnable task) {
                task.run();
            }
        }.executeOnExecutor(Executors.newSingleThreadExecutor(), contact, callback);
    }
}
