package com.wordpress.herovickers.omup.api;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AllApiResponse {
    public class OTPRespModel {
         @SerializedName("message")
        @Expose
        public String message;
        @SerializedName("status")
        @Expose
        public Integer status;
        @SerializedName("code")
        @Expose
        public Integer code;
        @SerializedName("verification_code")
        @Expose
        public Integer verificationCode;

    }
}






