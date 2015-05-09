package com.screeninteractiontest.jorge.io.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.annotation.NonNull;

import com.screeninteractiontest.jorge.BuildConfig;
import com.screeninteractiontest.jorge.R;
import com.screeninteractiontest.jorge.datamodel.Contact;
import com.screeninteractiontest.jorge.io.db.base.RobustSQLiteOpenHelper;
import com.screeninteractiontest.jorge.ui.UIUtils;

import java.util.ArrayList;
import java.util.List;

public class SQLiteDAO extends RobustSQLiteOpenHelper {

    //TODO Sanitize the urls for reliable database storage

    public static final Object DB_LOCK = new Object();
    private static final String TABLE_KEY_ID = "TABLE_KEY_TIMESTAMP";
    private static final String TABLE_KEY_FIRST_NAME = "TABLE_KEY_TITLE";
    private static final String TABLE_KEY_SECOND_NAME = "TABLE_KEY_URL";
    private static final String TABLE_KEY_POSITION = "TABLE_KEY_DESC";
    private static final String TABLE_KEY_IS_FAVORITE = "TABLE_KEY_READ";
    private static final String TABLE_KEY_PHOTO_URL = "TABLE_KEY_PHOTO_URL";
    private static final String TABLE_KEY_LARGE_PHOTO_URL = "TABLE_KEY_LARGE_PHOTO_URL";
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
            getInstance().getWritableDatabase(); //Force database creation
        }
    }

    public synchronized static SQLiteDAO getInstance() {
        if (singleton == null)
            throw new IllegalStateException("SQLiteDAO.setup(Context) must be called before " +
                    "trying to retrieve the instance.");
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
                TABLE_KEY_ID + " INTEGER PRIMARY KEY ON CONFLICT IGNORE, " +
                TABLE_KEY_FIRST_NAME + " TEXT NOT NULL ON CONFLICT IGNORE, " +
                TABLE_KEY_SECOND_NAME + " TEXT NOT NULL ON CONFLICT IGNORE, " +
                TABLE_KEY_IS_FAVORITE + " INTEGER DEFAULT 0, " +
                TABLE_KEY_PHOTO_URL + " TEXT NOT NULL ON CONFLICT IGNORE, " +
                TABLE_KEY_LARGE_PHOTO_URL + " TEXT NOT NULL ON CONFLICT IGNORE, " +
                TABLE_KEY_POSITION + " TEXT NOT NULL ON CONFLICT IGNORE )";

        synchronized (DB_LOCK) {
            db.execSQL(createContactTableCommand);
            RobustSQLiteOpenHelper.addTableName(CONTACT_TABLE_NAME);
        }
    }

    public void insertContacts(@NonNull final List<Contact> contacts) {
        if (UIUtils.isMainThread()) {
            throw new DatabaseOnMainThreadException(mContext);
        }

        final SQLiteDatabase db = getWritableDatabase();
        List<ContentValues> storableContacts = new ArrayList<>();
        for (Contact contact : contacts)
            storableContacts.add(mapContactToStorable(contact));

        synchronized (DB_LOCK) {
            db.beginTransaction();
            for (ContentValues storableArticle : storableContacts)
                db.insert(CONTACT_TABLE_NAME, null, storableArticle);
            db.setTransactionSuccessful();
            db.endTransaction();
        }
    }

    private ContentValues mapContactToStorable(final Contact contact) {
        ContentValues ret = new ContentValues();
        ret.put(TABLE_KEY_ID, contact.getId());
        ret.put(TABLE_KEY_FIRST_NAME, contact.getFirstName());
        ret.put(TABLE_KEY_SECOND_NAME, contact.getSecondName());
        ret.put(TABLE_KEY_POSITION, contact.getPosition());
        ret.put(TABLE_KEY_PHOTO_URL, contact.getPhotoUrl());
        ret.put(TABLE_KEY_LARGE_PHOTO_URL, contact.getLargePhotoUrl());
        ret.put(TABLE_KEY_IS_FAVORITE, contact.isFavorite() ? 1 : 0);
        return ret;
    }

    private Contact mapStorableToContact(final Cursor contactCursor) {
        return new Contact(contactCursor.getInt(contactCursor.getColumnIndex(TABLE_KEY_ID)),
                contactCursor.getString(contactCursor.getColumnIndex(TABLE_KEY_FIRST_NAME)),
                contactCursor.getString(contactCursor.getColumnIndex(TABLE_KEY_SECOND_NAME)),
                contactCursor.getString(contactCursor.getColumnIndex(TABLE_KEY_POSITION)),
                contactCursor.getString(contactCursor.getColumnIndex(TABLE_KEY_PHOTO_URL)),
                contactCursor.getString(contactCursor.getColumnIndex(TABLE_KEY_LARGE_PHOTO_URL)),
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

    public void updateContactIsFavorite(final Integer contactId, final Boolean newIsFavorite) {
        if (UIUtils.isMainThread()) {
            throw new DatabaseOnMainThreadException(mContext);
        }
        SQLiteDatabase db = getWritableDatabase();
        ContentValues newReadContainer = new ContentValues();
        newReadContainer.put(TABLE_KEY_IS_FAVORITE, newIsFavorite ? 1 : 0);
        synchronized (DB_LOCK) {
            db.beginTransaction();
            db.update(CONTACT_TABLE_NAME, newReadContainer, TABLE_KEY_ID + " = '" + contactId, null);
            db.setTransactionSuccessful();
            db.endTransaction();
        }
    }

    private static class DatabaseOnMainThreadException extends RuntimeException {

        private DatabaseOnMainThreadException(final Context context) {
            super(context.getString(R.string.error_database_operation_on_main_thread));
        }
    }
}
