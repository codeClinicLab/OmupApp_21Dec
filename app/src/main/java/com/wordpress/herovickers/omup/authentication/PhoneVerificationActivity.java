package com.wordpress.herovickers.omup.authentication;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.wordpress.herovickers.omup.R;
import com.wordpress.herovickers.omup.utility.NetworkUtils;
import java.util.Collections;
import java.util.List;


public class PhoneVerificationActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private LinearLayout noInternetLayout;
    private Button retry;
    private static final int RC_SIGN_IN = 97;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTheme().applyStyle(R.style.AppThemeWithTitleBar, true);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        setUpFirebaseUi();

        setContentView(R.layout.activity_login);

        noInternetLayout = findViewById(R.id.no_internet);
        retry = findViewById(R.id.btn_retry);

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpFirebaseUi();
            }
        });
    }

    private void setUpFirebaseUi() {
        if (mUser == null){
            launchFirebaseUi();
        }else {
            launchCorrespondingActivity();
        }
    }

    private void launchFirebaseUi() {
        // Choose authentication providers
        if (NetworkUtils.isConnected(this)) {
            List<AuthUI.IdpConfig> providers = Collections.singletonList(
                    new AuthUI.IdpConfig.PhoneBuilder().build());

// Create and launch sign-in intent
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .setTheme(R.style.BlueTheme)
                            .build(),
                    RC_SIGN_IN);
        }else{
            noInternetLayout.setVisibility(View.VISIBLE);
            Toast.makeText(this, "No Internet",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                launchCorrespondingActivity();
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                if (response == null){

                }// response.getError().getErrorCode() and handle the error.
                else {
                    String error = response.getError().getMessage();
                    Log.i("errorMessageFirebaseui", error);
                }
            }
        }
    }



    private void launchCorrespondingActivity() {
         Intent intent = new Intent(this, ProfileInformation.class);
         startActivity(intent);
         finish();
    }
}

