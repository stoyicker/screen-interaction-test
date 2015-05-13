package com.screeninteractiontest.android.data.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * POJO class that models the information of a contact.
 * Note that, because deserialization is used to convert the data from the downloaded JSON, if we
 * were to use ProGuard we would need to define appropriate rules for this file.
 * This class implements {@link Parcelable} to allow objects of this type to be passed to
 * {@link com.screeninteractiontest.android.ui.activity.ContactDetailActivity}.
 *
 * @author Jorge Antonio Diaz-Benito Soriano (github.com/Stoyicker) upon code generated through
 *         <a href="http://www.jsonschema2pojo.org/">jsonschema2pojo</a>.
 */
public final class Contact implements Parcelable {

    @Expose
    private String name;
    @SerializedName("job_title")
    @Expose
    private final String jobTitle;
    @Expose
    private final String email;
    @Expose
    private final String phone;
    @Expose
    private final String webpage;
    @SerializedName("picture-url")
    @Expose
    private final String pictureUrl;
    @SerializedName("thumbnail-url")
    @Expose
    private final String thumbnailUrl;
    private Boolean isFavorite = Boolean.FALSE;

    /**
     * Constructor required for the implementation of {@link Parcelable}.
     *
     * @param in {@link Parcel} The parcel that describes this object
     */
    private Contact(final Parcel in) {
        this.name = in.readString();
        this.jobTitle = in.readString();
        this.email = in.readString();
        this.phone = in.readString();
        this.webpage = in.readString();
        this.pictureUrl = in.readString();
        this.thumbnailUrl = in.readString();
        this.isFavorite = in.readInt() == 1;
    }

    /**
     * Standard constructor, takes all fields as parameters.
     *
     * @param name         {@link String} The name
     * @param jobTitle     {@link String} The job title
     * @param email        {@link String} The e-mail address
     * @param phone        {@link String} The phone number
     * @param webpage      {@link String} The website url
     * @param pictureUrl   {@link String} The large picture url
     * @param thumbnailUrl {@link String} The thumbnail url
     * @param isFavorite   {@link Boolean} <value>Boolean.TRUE</value> if this
     *                     is a favorite contact; <value>Boolean.FALSE</value>
     *                     otherwise
     */
    public Contact(final String name, final String jobTitle, final String email, final String phone,
                   final String webpage, final String pictureUrl, final String thumbnailUrl,
                   final Boolean isFavorite) {
        this.name = name;
        this.jobTitle = jobTitle;
        this.email = email;
        this.phone = phone;
        this.webpage = webpage;
        this.pictureUrl = pictureUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.isFavorite = isFavorite;
    }

    /**
     * Calculates the first name by dropping everything in the name after the first space.
     *
     * @return {@link String} The first name (everything until the first space)
     */
    public String getFirstName() {
        return name.trim().split(" ")[0];
    }

    /**
     * Calculates the last name by dropping everything in the name before the first space.
     *
     * @return {@link String} The last name (everything from the first space)
     */
    public String getLastName() {
        final String[] parts = name.trim().split(" ");
        //The check below is not going to happen in the mockup but, in a real project, it would
        // be good to validate the operation
        if (parts.length == 1)
            return parts[0];

        final StringBuilder retBuilder = new StringBuilder(parts[1]);

        for (Integer i = 2; i < parts.length; i++)
            retBuilder.append(parts[i]);

        return retBuilder.toString();
    }

    /**
     * GETTER.
     *
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * SETTER.
     *
     * @param name {@link String} The new name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * GETTER.
     *
     * @return The job title
     */
    public String getJobTitle() {
        return jobTitle;
    }

    /**
     * GETTER.
     *
     * @return The email
     */
    public String getEmail() {
        return email;
    }

    /**
     * GETTER.
     *
     * @return The phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * GETTER.
     *
     * @return The webpage
     */
    public String getWebpage() {
        return webpage;
    }

    /**
     * GETTER.
     *
     * @return The picture url
     */
    public String getPictureUrl() {
        return pictureUrl;
    }

    /**
     * GETTER.
     *
     * @return The thumbnail url
     */
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    /**
     * SETTER.
     *
     * @param favorite {@link Boolean} <value>Boolean.TRUE</value> if this object has to become
     *                 favorite; <value>Boolean.FALSE</value> otherwise
     */
    public void setFavorite(final Boolean favorite) {
        this.isFavorite = favorite;
    }

    /**
     * GETTER.
     *
     * @return {@link Boolean} <value>Boolean.TRUE</value> if this object is favorite;
     * <value>Boolean.FALSE</value> otherwise
     */
    public Boolean isFavorite() {
        return isFavorite;
    }

    /**
     * Generates a human-readable representation of the object.
     *
     * @return {@link String} A list of the attributes and their values using reflection
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * Generates a hash value for the object. Note that the value is generated upon the name only
     * because that is deemed the key for referencing a Contact in the application model. This
     * decision is, at least, questionable, but is enforced by the lack of an id or similar field
     * being assigned to each contact by the remote endpoint.
     *
     * @return {@link Integer} Hash value for this object
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(name).toHashCode();
    }

    /**
     * Indicates whether some other object is "equal to" this one. The criteria used is the
     * contact name.
     *
     * @param other {@link Object} The object to compare to
     * @return {@link Boolean} <value>Boolean.TRUE</value> if the objects are equal based on
     * their class and name; <value>Boolean.FALSE</value> otherwise
     * @see Contact#hashCode()
     */
    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return Boolean.TRUE;
        }
        if ((other instanceof Contact) == Boolean.FALSE) {
            return Boolean.FALSE;
        }
        final Contact rhs = ((Contact) other);
        return new EqualsBuilder().append(name, rhs.name).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeToParcel(final Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(jobTitle);
        dest.writeString(email);
        dest.writeString(phone);
        dest.writeString(webpage);
        dest.writeString(pictureUrl);
        dest.writeString(thumbnailUrl);
        dest.writeInt(isFavorite ? 1 : 0);
    }

    /**
     * Required for correct behavior of the implementation of {@link Parcelable}.
     */
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Contact createFromParcel(final Parcel in) {
            return new Contact(in);
        }

        public Contact[] newArray(final int size) {
            return new Contact[size];
        }
    };
}
