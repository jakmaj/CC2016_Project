package cz.ccnull.cc2016project.rest;

import cz.ccnull.cc2016project.Config;
import cz.ccnull.cc2016project.model.Payment;
import cz.ccnull.cc2016project.model.User;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiDescription {

    @FormUrlEncoded
    @POST(Config.URL_LOGIN)
    Call<User> login(@Field("email") String login, @Field("password") String password);

    @FormUrlEncoded
    @POST(Config.URL_CREATE_PAYMENT)
    Call<Payment> createPayment(@Field("token") String token, @Field("amount") int amount, @Field("androidToken") String tokenGCM);

    @FormUrlEncoded
    @POST(Config.URL_PAYMENT_HEARD)
    Call<Payment> paymentHeard(@Field("token") String token, @Field("paymentCode") String code, @Field("androidToken") String tokenGCM);

    @FormUrlEncoded
    @POST(Config.URL_PAYMENT_CONFIRM)
    Call<Payment> paymentConfirm(@Field("token") String token, @Field("paymentCode") String code, @Field("_idReceiver") String receiver);
}
