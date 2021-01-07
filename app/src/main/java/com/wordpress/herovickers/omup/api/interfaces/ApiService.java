package com.wordpress.herovickers.omup.api.interfaces;

import com.wordpress.herovickers.omup.api.AllApiResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {


    @GET
    Call<Object> sendSms(@Url String url);

    @FormUrlEncoded
    @POST("email_verification")
    Call<AllApiResponse.OTPRespModel> getOTPRes(@Field("email") String email);

}
