package cz.ccnull.cc2016project;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cz.ccnull.cc2016project.model.User;
import cz.ccnull.cc2016project.rest.ApiDescription;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class App extends Application {

    public static final String SP_NAME = "prefs";
    public static final String SP_GCM_TOKEN_KEY = "gcm_token";
    public static final String SP_LOGIN = "login";

    private ApiDescription apiDescription;
    private User currentUser;

    private static App instance;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        Stetho.initializeWithDefaults(this);

        initRetrofit();
    }

    private void initRetrofit() {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(new OkHttpClient.Builder()
                        .addNetworkInterceptor(new StethoInterceptor())
                        .build())
                .build();

        apiDescription = retrofit.create(ApiDescription.class);
    }

    public ApiDescription getApiDescription() {
        return apiDescription;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public SharedPreferences getPreferences() {
        return getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

}
