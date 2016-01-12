package org.davidd.connect.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.davidd.connect.util.DataUtils;

public class UserJIDProperties {

    @SerializedName("JID")
    @Expose(serialize = true, deserialize = true)
    private String JID;

    @SerializedName("name")
    @Expose(serialize = true, deserialize = true)
    private String name;

    @SerializedName("domain")
    @Expose(serialize = true, deserialize = true)
    private String domain;

    @SerializedName("resource")
    @Expose(serialize = true, deserialize = true)
    private String resource;

    public UserJIDProperties(@NonNull String JID) {
        this.JID = JID;
        createPropertiesFromJID();
    }

    public UserJIDProperties(@NonNull String name, @NonNull String domain, String resource) {
        this.name = name;
        this.domain = domain;
        this.resource = resource;
        createJIDFromProperties();
    }

    public String getJID() {
        return JID;
    }

    public String getName() {
        return name;
    }

    public String getDomain() {
        return domain;
    }

    public String getResource() {
        return resource;
    }

    public String getNameAndDomain() {
        return name + "@" + domain;
    }

    public boolean isNameAndDomainValid() {
        return !DataUtils.isEmpty(name) && !DataUtils.isEmpty(domain);
    }

    private void createPropertiesFromJID() {
        String[] nameAndTheRest = splitByDelimiter(JID, "@");
        name = nameAndTheRest[0];

        String[] domainAndResource = splitByDelimiter(nameAndTheRest[1], "/");
        domain = domainAndResource[0];
        resource = domainAndResource[1];
    }

    private String[] splitByDelimiter(String text, String delimiter) {
        String[] array = new String[]{"", ""};

        if (!DataUtils.isEmpty(text)) {
            if (text.contains(delimiter)) {
                array = text.split(delimiter);
            } else {
                array[0] = text;
            }
        }

        return array;
    }

    private void createJIDFromProperties() {
        if (isNameAndDomainValid()) {
            JID = name + "@" + domain;
            if (!DataUtils.isEmpty(resource)) {
                JID += "/" + resource;
            }
        } else if (!DataUtils.isEmpty(name)) {
            JID = name;
        } else {
            JID = "";
        }
    }
}
