package cz.ccnull.cc2016project.rest;

import cz.ccnull.cc2016project.Config;
import cz.ccnull.cc2016project.model.Payment;
import cz.ccnull.cc2016project.model.User;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiDescription {

    @FormUrlEncoded
    @POST(Config.URL_LOGIN)
    Call<User> login(@Field("email") String login, @Field("password") String password);

    @GET(Config.URL_CREATE_PAYMENT)
    Call<Payment> createPayment(@Query("auth_token") String token, @Query("amount") String amount);

    @FormUrlEncoded
    @POST(Config.URL_PAYMENT_HEARD)
    Call<Payment> paymentHeard(@Field("auth_token") String token, @Field("payment_code") String code);

    @FormUrlEncoded
    @POST(Config.URL_PAYMENT_CONFIRM)
    Call<Payment> paymentConfirm(@Field("auth_token") String token, @Field("payment_code") String code, @Field("payment_receiver") String receiver);
}
