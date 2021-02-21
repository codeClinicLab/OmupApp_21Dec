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

    @FormUrlEncoded
    @POST("pay")
    Call<AllApiResponse.PayFundTransferModel> callPayApiForFundTransfer(@Field("type") String type,@Field("app_user_id") String app_user_id,@Field("email") String email,
                                                                @Field("amount") String amount,@Field("nonce") String nonce,@Field("bank_name") String bank_name,
                                                                @Field("bank_ac_holder_name") String bank_ac_holder_name,@Field("bank_ac_number") String bank_ac_number );
}
