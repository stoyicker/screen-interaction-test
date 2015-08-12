package com.screeninteractiontest.android.data.middlelayer;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.screeninteractiontest.android.data.datamodel.Contact;
import com.screeninteractiontest.android.io.db.SQLiteDAO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * This class acts as a middle layer between the DAO and the adapter logic to ensure that the
 * state of the runtime objects matches the information in the database at all times.
 *
 * @author Jorge Antonio Diaz-Benito Soriano (github.com/Stoyicker).
 */
public abstract class ContactManager {

    /**
     * Retrieves all contacts in the database.
     *
     * @return {@link Collection<Contact>} Collection of contacts currently stored in the database
     */
    public static Collection<Contact> getAllContacts() {
        return SQLiteDAO.getInstance().getAllContacts();
    }

    /**
     * Mocks a behavior that, although is not really necessary since the
     * contact list is known to be static, is adequate to use. It works under the
     * assumption that the endpoint can only have data added and not removed or
     * edited.
     *
     * @param contacts {@link Collection<Contact>} The list of contacts to attempt to insert
     * @return {@link Collection<Contact>} The ones actually inserted
     */
    public static Collection<Contact> insertIfProceeds(final List<Contact> contacts) {
        final Collection<Contact> actualInsertions = new ArrayList<>();

        for (Contact x : contacts) {
            solidifyContact(x);

            if (SQLiteDAO.getInstance().insertContact(x)) {
                actualInsertions.add(x);
            }
        }

        return actualInsertions;
    }

    /**
     * Performs data integrity checks on a contact, modifying it if needed. Rules applied:
     * <ul>
     * <li><field>name</field> is not null, as it is the key of the object. A null name becomes an
     * empty one (<value>""</value>). Note, however, that the <value>"null"</value> string is
     * perfectly valid.</li>
     * <br>
     * <li><field>isFavorite</field> is not null. A null <field>isFavorite</field> becomes
     * <value>Boolean.FALSE</value>.</li>
     * </ul>
     *
     * @param contact {@link Contact} The contact to solidify
     */
    private static void solidifyContact(@NonNull final Contact contact) {
        if (contact.getName() == null)
            contact.setName("");

        if (contact.isFavorite() == null) {
            contact.setFavorite(Boolean.FALSE);
        }
    }

    /**
     * Sets a contact as favorite if it wasn't, and as non-favorite otherwise.
     *
     * @param contact  {@link Contact} The contact affected by the operation
     * @param callback {@link Runnable} The sequence of execution to run when the update has
     *                 finished on the database. Although the database operation takes place on a
     *                 background thread, the callback is executed in the UI thread.
     */
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
