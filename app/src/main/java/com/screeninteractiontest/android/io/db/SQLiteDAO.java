package com.screeninteractiontest.android.io.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.annotation.NonNull;

import com.screeninteractiontest.android.BuildConfig;
import com.screeninteractiontest.android.R;
import com.screeninteractiontest.android.data.datamodel.Contact;
import com.screeninteractiontest.android.io.db.base.RobustSQLiteOpenHelper;
import com.screeninteractiontest.android.ui.UIUtils;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Collection;

/**
 * DAO. It enforces all database operations to be performed on a background thread.
 *
 * @author Jorge Antonio Diaz-Benito Soriano (github.com/Stoyicker).
 */
public final class SQLiteDAO extends RobustSQLiteOpenHelper {

    public static final Object DB_LOCK = new Object();
    private static final String TABLE_KEY_NAME = "TABLE_KEY_NAME";
    private static final String TABLE_KEY_JOB_TITLE = "TABLE_KEY_JOB_TITLE";
    private static final String TABLE_KEY_EMAIL = "TABLE_KEY_EMAIL";
    private static final String TABLE_KEY_PHONE = "TABLE_KEY_PHONE";
    private static final String TABLE_KEY_WEBPAGE = "TABLE_KEY_WEBPAGE";
    private static final String TABLE_KEY_PICTURE_URL = "TABLE_KEY_PICTURE_URL";
    private static final String TABLE_KEY_THUMBNAIL_URL = "TABLE_KEY_THUMBNAIL_URL";
    private static final String TABLE_KEY_IS_FAVORITE = "TABLE_KEY_IS_FAVORITE";
    private static final String CONTACT_TABLE_NAME = "TABLE_CONTACTS";
    private static Context mContext;
    private static SQLiteDAO singleton;

    /**
     * Standard constructor.
     *
     * @param _context {@link Context} Context.
     */
    private SQLiteDAO(@NonNull Context _context) {
        super(_context, _context.getString(R.string.database_name), null, BuildConfig.VERSION_CODE);
        mContext = _context;
    }

    /**
     * Initialization method. It takes care of constructing the singleton and calling it more
     * than once will have no effect. It must be invoked prior to any calls to {@link SQLiteDAO#getInstance()}
     * as the later does not have the ability to instantiate the singleton. The reason for this
     * is that constructing the database object requires a context which, if set as a parameter
     * in {@link SQLiteDAO#getInstance()}, would get passed around every time a database
     * operation needs to be performed, but would only be used the first time.
     *
     * @param _context {@link Context} Context
     */
    public synchronized static void setup(@NonNull final Context _context) {
        if (singleton == null) {
            singleton = new SQLiteDAO(_context);
            mContext = _context;
        }
    }

    /**
     * Retrieves the singleton instance
     *
     * @return {@link SQLiteDAO} The singleton instance to use when performing database operations
     * @see SQLiteDAO#setup(Context)
     */
    public synchronized static SQLiteDAO getInstance() {
        if (UIUtils.isMainThread()) {
            throw new DatabaseOnMainThreadException(mContext);
        }
        if (singleton == null)
            throw new IllegalStateException("SQLiteDAO.setup(Context) must be called before trying to retrieve the instance.");
        return singleton;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onRobustUpgrade(final SQLiteDatabase db, final int oldVersion,
                                final int newVersion) throws SQLiteException {
        //For now unused as there is no older version from the database yet
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(final SQLiteDatabase db) {
        super.onCreate(db);

        final String createContactTableCommand = "CREATE TABLE IF NOT EXISTS " + CONTACT_TABLE_NAME + " ( " +
                /** I'm not happy about using the name as primary key. In a real
                 * setup I would expect to have some id-type field provided by
                 * the endpoint.
                 */
                TABLE_KEY_NAME + " TEXT PRIMARY KEY ON CONFLICT IGNORE, " +
                TABLE_KEY_JOB_TITLE + " TEXT, " +
                TABLE_KEY_EMAIL + " TEXT, " +
                TABLE_KEY_PHONE + " TEXT, " +
                TABLE_KEY_WEBPAGE + " TEXT, " +
                TABLE_KEY_PICTURE_URL + " TEXT, " +
                TABLE_KEY_THUMBNAIL_URL + " TEXT, " +
                TABLE_KEY_IS_FAVORITE + " INTEGER DEFAULT 0 " +
                " )";

        synchronized (DB_LOCK) {
            db.execSQL(createContactTableCommand);
            RobustSQLiteOpenHelper.addTableName(CONTACT_TABLE_NAME);
        }
    }

    /**
     * Stores a contact into the database
     *
     * @param contact {@link Contact} The contact to store
     * @return {@link Boolean} The success of the operation
     */
    public Boolean insertContact(@NonNull final Contact contact) {
        final SQLiteDatabase db = getWritableDatabase();
        final ContentValues storableContact = mapContactToStorable(contact);

        Boolean inserted;

        synchronized (DB_LOCK) {
            db.beginTransaction();
            inserted = db.insert(CONTACT_TABLE_NAME, null, storableContact) != -1;
            db.setTransactionSuccessful();
            db.endTransaction();
        }

        return inserted;
    }

    /**
     * Parses the information of a contact into a database-supported type
     *
     * @param contact {@link Contact} The contact whose information must be parsed
     * @return {@link ContentValues} The database-supported container for the parsed information
     */
    private ContentValues mapContactToStorable(final Contact contact) {
        final ContentValues ret = new ContentValues();
        ret.put(TABLE_KEY_NAME, escapeString(contact.getName()));
        ret.put(TABLE_KEY_JOB_TITLE, escapeString(contact.getJobTitle()));
        ret.put(TABLE_KEY_EMAIL, escapeString(contact.getEmail()));
        ret.put(TABLE_KEY_PHONE, escapeString(contact.getPhone()));
        ret.put(TABLE_KEY_WEBPAGE, escapeString(contact.getWebpage()));
        ret.put(TABLE_KEY_PICTURE_URL, escapeString(contact.getPictureUrl()));
        ret.put(TABLE_KEY_THUMBNAIL_URL, escapeString(contact.getThumbnailUrl()));
        ret.put(TABLE_KEY_IS_FAVORITE, contact.isFavorite() ? 1 : 0);
        return ret;
    }

    /**
     * Converts the result of a database query into a contact
     *
     * @param contactCursor {@link Cursor} The cursor pointing to the data queried
     * @return {@link Contact} The contact created
     */
    private Contact mapStorableToContact(final Cursor contactCursor) {
        return new Contact(unescapeString(contactCursor.getString(contactCursor.getColumnIndex(TABLE_KEY_NAME))),
                unescapeString(contactCursor.getString(contactCursor.getColumnIndex(TABLE_KEY_JOB_TITLE))),
                unescapeString(contactCursor.getString(contactCursor.getColumnIndex(TABLE_KEY_EMAIL))),
                unescapeString(contactCursor.getString(contactCursor.getColumnIndex(TABLE_KEY_PHONE))),
                unescapeString(contactCursor.getString(contactCursor.getColumnIndex(TABLE_KEY_WEBPAGE))), unescapeString(contactCursor.getString(contactCursor.getColumnIndex(TABLE_KEY_PICTURE_URL))), unescapeString(contactCursor.getString(contactCursor.getColumnIndex(TABLE_KEY_THUMBNAIL_URL))),
                contactCursor.getInt(contactCursor.getColumnIndex(TABLE_KEY_IS_FAVORITE)) == 0 ? Boolean
                        .FALSE : Boolean.TRUE);
    }

    /**
     * Retrieves all contacts in the database
     *
     * @return {@link Collection<Contact>} The contacts in the database
     */
    public Collection<Contact> getAllContacts() {
        final Collection<Contact> ret = new ArrayList<>();
        final SQLiteDatabase db = getReadableDatabase();
        synchronized (DB_LOCK) {
            db.beginTransaction();
            final Cursor allStorableContacts = db.query(CONTACT_TABLE_NAME, null, null, null, null, null, null);
            if (allStorableContacts != null && allStorableContacts.moveToFirst()) {
                do {
                    ret.add(mapStorableToContact(allStorableContacts));
                } while (allStorableContacts.moveToNext());

            }
            if (allStorableContacts != null)
                allStorableContacts.close();
            db.setTransactionSuccessful();
            db.endTransaction();
        }

        return ret;
    }

    /**
     * Sets the "favoriteness" of a contact in the database
     *
     * @param contact       {@link Contact} The contact whose "favoriteness" must be set
     * @param newIsFavorite {@link Boolean} <value>Boolean.TRUE</value> if the contact should be
     *                      set to favorite; <value>Boolean.FALSE otherwise</value>
     * @return {@link Boolean} The success of the operation
     */
    public Boolean updateContactIsFavorite(final Contact contact, final Boolean newIsFavorite) {
        final SQLiteDatabase db = getWritableDatabase();
        final ContentValues newFavoriteContainer = new ContentValues();
        newFavoriteContainer.put(TABLE_KEY_IS_FAVORITE, newIsFavorite ? 1 : 0);
        Boolean ret;
        synchronized (DB_LOCK) {
            db.beginTransaction();
            ret = db.update(CONTACT_TABLE_NAME, newFavoriteContainer, TABLE_KEY_NAME + " = ?", new String[]{escapeString(contact.getName())}) > 0;
            db.setTransactionSuccessful();
            db.endTransaction();
        }

        return ret;
    }

    /**
     * Escapes a string.
     *
     * @param input {@link String} The string to escape.
     * @return {@link String} The escaped string.
     */
    @Contract("null -> null")
    private String escapeString(final String input) {
        if (input == null)
            return null;

        return "'" + input.replace("'", "''") + "'";
    }

    /**
     * Unescapes a string.
     *
     * @param output {@link String} The string to unescape. It is assumed to be
     *               escaped. Otherwise unexpected behavior may occur.
     * @return {@link String} The unescaped string.
     */
    @Contract("null -> null")
    private String unescapeString(final String output) {
        if (output == null)
            return null;

        return output.substring(1, output.length() - 1);
    }

    /**
     * Simple exception used on {@link SQLiteDAO#getInstance()} to avoid execution of database
     * operations on the UI thread.
     */
    private static class DatabaseOnMainThreadException extends RuntimeException {

        private DatabaseOnMainThreadException(final Context context) {
            super(context.getString(R.string.error_database_operation_on_main_thread));
        }
    }
}
