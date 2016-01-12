package org.davidd.connect.model;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.davidd.connect.util.BitmapUtil;
import org.davidd.connect.util.DataUtils;
import org.jivesoftware.smack.roster.RosterEntry;

public class User {

    @SerializedName("userJIDProperties")
    @Expose(serialize = true, deserialize = true)
    private UserJIDProperties userJIDProperties;

    @SerializedName("password")
    @Expose(serialize = true, deserialize = true)
    private String password;

    @SerializedName("userInitials")
    @Expose(serialize = true, deserialize = true)
    private String userInitials;

    @Expose(serialize = false, deserialize = false)
    private Bitmap userPhoto;

    @Expose(serialize = false, deserialize = false)
    private RosterEntry rosterEntry;

    @Expose(serialize = false, deserialize = false)
    private UserPresence userPresence;

    public User(@NonNull UserJIDProperties userJIDProperties) {
        this.userJIDProperties = userJIDProperties;
    }

    public User(@NonNull UserJIDProperties userJIDProperties, String password) {
        this.userJIDProperties = userJIDProperties;
        this.password = password;
    }

    public User(@NonNull UserJIDProperties userJIDProperties, RosterEntry rosterEntry, UserPresence userPresence) {
        this.userJIDProperties = userJIDProperties;
        this.rosterEntry = rosterEntry;
        this.userPresence = userPresence;
    }

    @NonNull
    public UserJIDProperties getUserJIDProperties() {
        return userJIDProperties;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserInitials() {
        if (DataUtils.isEmpty(userInitials)) {
            userInitials = userJIDProperties.getName().substring(0, 1);
        }
        return userInitials;
    }

    public Bitmap getUserPhoto() {
        if (userPhoto == null) {
            userPhoto = BitmapUtil.drawTextToBitmap(getUserInitials());
        }
        return userPhoto;
    }

    public void setUserPhoto(Bitmap userPhoto) {
        this.userPhoto = userPhoto;
    }

    public RosterEntry getRosterEntry() {
        return rosterEntry;
    }

    public void setRosterEntry(RosterEntry rosterEntry) {
        this.rosterEntry = rosterEntry;
    }

    public UserPresence getUserPresence() {
        return userPresence;
    }

    public void setUserPresence(UserPresence userPresence) {
        this.userPresence = userPresence;
    }
}
