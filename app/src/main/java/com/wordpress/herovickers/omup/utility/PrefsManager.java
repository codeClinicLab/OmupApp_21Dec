package com.wordpress.herovickers.omup.utility;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.wordpress.herovickers.omup.R;
import com.wordpress.herovickers.omup.models.User;

public class PrefsManager {
    private SharedPreferences prefs;
    private Context context;
    public static int STATUS_SIGNED_IN = 7646732;

    public PrefsManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences("MY_PREF", Context.MODE_PRIVATE);
    }

    public boolean checkPref() {
        return prefs.getString(context.getString(R.string.pref_key),"null") != "null";
    }
    public void writePref(){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getString(R.string.pref_key), "NEXT");
        editor.apply();
    }
    public void saveRegistrationProgress(int status){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(context.getString(R.string.status_key) , status);
        editor.commit();
    }
    public int getUserStatus(){
        return prefs.getInt(context.getString(R.string.status_key), -1);
    }

    public void saveUserData(User user) {
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        editor.putString(context.getString(R.string.user_data_key), json);
        editor.apply();
    }
    public User getUserData(){
        Gson gson = new Gson();
        String json = prefs.getString(context.getString(R.string.user_data_key), null);
        return gson.fromJson(json, User.class);
    }
}
