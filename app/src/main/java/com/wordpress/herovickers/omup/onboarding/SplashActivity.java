package com.wordpress.herovickers.omup.onboarding;


import android.content.Intent;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import java.util.ArrayList;
import java.util.List;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.wordpress.herovickers.omup.R;
import com.wordpress.herovickers.omup.destinations.HomeActivity;
import com.wordpress.herovickers.omup.utility.PrefsManager;

import static com.wordpress.herovickers.omup.utility.PrefsManager.STATUS_SIGNED_IN;


public class SplashActivity extends AppCompatActivity {
//    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private static int SPLASH_TIME_OUT = 2000;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("dddddddd", new PrefsManager(this).getUserStatus()+" u " );
        if (new PrefsManager(this).getUserStatus() == STATUS_SIGNED_IN){
            if (mUser != null){
                goToHome();
                Log.d("dddddddd", "user is signed in" );
            }
        }else {
            getTheme().applyStyle(R.style.AppThemeWithTitleBar, true);
            setContentView(R.layout.activity_splash);
            mFirebaseAuth = FirebaseAuth.getInstance();

            goToAppIntro();
        }

    }

    private void goToAppIntro() {
        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(SplashActivity.this, AppIntro.class);
                        startActivity(intent);
                        finish();
                    }
                },SPLASH_TIME_OUT
        );
    }

    private void goToHome() {
        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(SplashActivity.this, HomeActivity.class);
                        startActivity(i);
                        finish();
                    }
                },SPLASH_TIME_OUT
        );

    }

   /* @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }
    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }*/
}
