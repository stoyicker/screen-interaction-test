package com.screeninteractiontest.android.data.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

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

    public Contact(final String name, final String jobTitle, final String email, final String phone, final String webpage, final String pictureUrl, final String thumbnailUrl, final Boolean isFavorite) {
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
     * @return The first name
     */
    public String getFirstName() {
        return name.split(" ")[0];
    }

    /**
     * @return The last name
     */
    public String getLastName() {
        return name.split(" ")[1];
    }

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The jobTitle
     */
    public String getJobTitle() {
        return jobTitle;
    }

    /**
     * @return The email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @return The phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @return The webpage
     */
    public String getWebpage() {
        return webpage;
    }

    /**
     * @return The pictureUrl
     */
    public String getPictureUrl() {
        return pictureUrl;
    }

    /**
     * @return The thumbnailUrl
     */
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setFavorite(final Boolean favorite) {
        this.isFavorite = favorite;
    }

    public Boolean isFavorite() {
        return isFavorite;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(name).toHashCode();
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

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

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Contact createFromParcel(final Parcel in) {
            return new Contact(in);
        }

        public Contact[] newArray(final int size) {
            return new Contact[size];
        }
    };
}
