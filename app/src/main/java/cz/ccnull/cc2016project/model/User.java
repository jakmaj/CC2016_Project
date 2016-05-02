package cz.ccnull.cc2016project.model;

import com.google.gson.annotations.SerializedName;

public class User {

    String name;

    @SerializedName("token")
    String authToken;

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
