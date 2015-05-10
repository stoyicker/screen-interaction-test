package com.screeninteractiontest.jorge.io.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.annotation.NonNull;

import com.screeninteractiontest.jorge.BuildConfig;
import com.screeninteractiontest.jorge.R;
import com.screeninteractiontest.jorge.data.datamodel.Contact;
import com.screeninteractiontest.jorge.io.db.base.RobustSQLiteOpenHelper;
import com.screeninteractiontest.jorge.ui.UIUtils;

import java.util.ArrayList;
import java.util.List;

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

    private SQLiteDAO(@NonNull Context _context) {
        super(_context, _context.getString(R.string.database_name), null, BuildConfig.VERSION_CODE);
        mContext = _context;
    }

    public synchronized static void setup(@NonNull final Context _context) {
        if (singleton == null) {
            singleton = new SQLiteDAO(_context);
            mContext = _context;
        }
    }

    public synchronized static SQLiteDAO getInstance() {
        if (UIUtils.isMainThread()) {
            throw new DatabaseOnMainThreadException(mContext);
        }
        if (singleton == null)
            throw new IllegalStateException("SQLiteDAO.setup(Context) must be called before trying to retrieve the instance.");
        return singleton;
    }

    @Override
    public void onRobustUpgrade(final SQLiteDatabase db, final int oldVersion,
                                final int newVersion) throws SQLiteException {
        //For now unused as there is no older version from the database yet
    }

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

    private ContentValues mapContactToStorable(final Contact contact) {
        ContentValues ret = new ContentValues();
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

    private Contact mapStorableToContact(final Cursor contactCursor) {
        return new Contact(unescapeString(contactCursor.getString(contactCursor.getColumnIndex(TABLE_KEY_NAME))),
                unescapeString(contactCursor.getString(contactCursor.getColumnIndex(TABLE_KEY_JOB_TITLE))),
                unescapeString(contactCursor.getString(contactCursor.getColumnIndex(TABLE_KEY_EMAIL))),
                unescapeString(contactCursor.getString(contactCursor.getColumnIndex(TABLE_KEY_PHONE))),
                unescapeString(contactCursor.getString(contactCursor.getColumnIndex(TABLE_KEY_WEBPAGE))), unescapeString(contactCursor.getString(contactCursor.getColumnIndex(TABLE_KEY_PICTURE_URL))), unescapeString(contactCursor.getString(contactCursor.getColumnIndex(TABLE_KEY_THUMBNAIL_URL))),
                contactCursor.getInt(contactCursor.getColumnIndex(TABLE_KEY_IS_FAVORITE)) == 0 ? Boolean
                        .FALSE : Boolean.TRUE);
    }

    public List<Contact> getAllContacts() {
        final List<Contact> ret = new ArrayList<>();
        final SQLiteDatabase db = getReadableDatabase();
        synchronized (DB_LOCK) {
            db.beginTransaction();
            Cursor allStorableContacts = db.query(CONTACT_TABLE_NAME, null, null, null, null, null, null);
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

    public void updateContactIsFavorite(final String contactName, final Boolean newIsFavorite) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues newReadContainer = new ContentValues();
        newReadContainer.put(TABLE_KEY_IS_FAVORITE, newIsFavorite ? 1 : 0);
        synchronized (DB_LOCK) {
            db.beginTransaction();
            db.update(CONTACT_TABLE_NAME, newReadContainer, TABLE_KEY_NAME + " = '" + contactName, null);
            db.setTransactionSuccessful();
            db.endTransaction();
        }
    }

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
     * @return {@link String} The escaped string.
     */
    private String unescapeString(final String output) {
        if (output == null)
            return null;

        return output.substring(1, output.length() - 1);
    }

    private static class DatabaseOnMainThreadException extends RuntimeException {

        private DatabaseOnMainThreadException(final Context context) {
            super(context.getString(R.string.error_database_operation_on_main_thread));
        }
    }
}
