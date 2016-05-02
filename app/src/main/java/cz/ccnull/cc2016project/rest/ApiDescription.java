package cz.ccnull.cc2016project.rest;

import cz.ccnull.cc2016project.Config;
import cz.ccnull.cc2016project.model.User;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiDescription {

    @GET(Config.URL_LOGIN)
    Call<User> login(@Query("login") String login, @Query("password") String password);
}
