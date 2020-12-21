package com.wordpress.herovickers.omup.models;



import java.util.HashMap;
import java.util.Map;

public class RecentCall {
    String callType;
    Long createdAt;
    String lastCallDuration;
    Map<String, String> userInfo;

    public RecentCall(String callType, Long createdAt, String lastCallDuration, Map<String, String> userInfo) {
        this.callType = callType;
        this.createdAt = createdAt;
        this.lastCallDuration = lastCallDuration;
        this.userInfo = userInfo;
    }

    public RecentCall() {
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public void setLastCallDuration(String lastCallDuration) {
        this.lastCallDuration = lastCallDuration;
    }


    public String getCallType() {
        return callType;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public String getLastCallDuration() {
        return lastCallDuration;
    }

    public void setUserInfo(Map<String, String> userInfo) {
        this.userInfo = userInfo;
    }

    public Map<String, String> getUserInfo() {
        return userInfo;
    }
}
