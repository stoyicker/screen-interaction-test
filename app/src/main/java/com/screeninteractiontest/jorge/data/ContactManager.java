package com.screeninteractiontest.jorge.data;

import com.screeninteractiontest.jorge.data.datamodel.Contact;
import com.screeninteractiontest.jorge.io.db.SQLiteDAO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jorge Antonio Diaz-Benito Soriano (github.com/Stoyicker).
 */
public final class ContactManager {

    private ContactManager() {
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

    private static void sanitizeContact(final Contact contact) {
        if (contact.getName() == null)
            contact.setName("");

        if (contact.isFavorite() == null) {
            contact.setFavorite(Boolean.FALSE);
        }
    }
}
