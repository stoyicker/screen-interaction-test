package com.screeninteractiontest.jorge.datamodel;

/**
 * @author Jorge Antonio Diaz-Benito Soriano (github.com/Stoyicker).
 */
public class Contact {

    private final String mFirstName;
    private final String mSecondName;

    private final String mPosition;
    private final String mPhotoUrl;
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

    public String getPosition() {
        return mPosition;
    }

    public String getSecondName() {
        return mSecondName;
    }

    public Contact(final Integer id, final String firstName, final String secondName, final String position, final String photoUrl, final Boolean isFavorite) {
        this.mId = id;
        this.mFirstName = firstName;
        this.mSecondName = secondName;
        this.mPosition = position;
        this.mPhotoUrl = photoUrl;
        this.isFavorite = isFavorite;
    }
}
