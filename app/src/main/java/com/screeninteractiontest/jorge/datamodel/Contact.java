package com.screeninteractiontest.jorge.datamodel;

/**
 * @author Jorge Antonio Diaz-Benito Soriano (github.com/Stoyicker).
 */
public class Contact {

    private final String mFirstName;
    private final String mSecondName;

    private final String mPosition;
    private final String mPhotoUrl;
    private final String mLargePhotoUrl;
    private final Integer mId;
    private Boolean isFavorite;

    public Integer getId() {
        return mId;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public Boolean isFavorite() {
        return isFavorite;
    }

    public String getPhotoUrl() {
        return mPhotoUrl;
    }

    public String getLargePhotoUrl() {
        return mLargePhotoUrl;
    }

    public String getPosition() {
        return mPosition;
    }

    public String getSecondName() {
        return mSecondName;
    }

    public String getFullName() {
        return mFirstName + " " + mSecondName;
    }

    public Contact(final Integer id, final String firstName, final String secondName, final String position, final String photoUrl, final String largePhotoUrl) {
        this(id, firstName, secondName, position, photoUrl, largePhotoUrl, Boolean.FALSE);
    }

    public Contact(final Integer id, final String firstName, final String secondName, final String position, final String photoUrl, final String largePhotoUrl, final Boolean isFavorite) {
        this.mId = id;
        this.mFirstName = firstName;
        this.mSecondName = secondName;
        this.mPosition = position;
        this.mPhotoUrl = photoUrl;
        this.mLargePhotoUrl = largePhotoUrl;
        this.isFavorite = isFavorite;
    }
}
