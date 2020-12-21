package com.wordpress.herovickers.omup.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.wordpress.herovickers.omup.R;

public class WelcomeActivity extends AppCompatActivity {

    private FirebaseUser mUSer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTheme().applyStyle(R.style.AppThemeWithTitleBar, true);
        setContentView(R.layout.activity_welcome);

        mUSer = FirebaseAuth.getInstance().getCurrentUser();
        Button login = findViewById(R.id.btn_login);
        Button register = findViewById(R.id.btn_register);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchLoginActivity();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Sign existing user out
                if(mUSer != null){
                    AuthUI.getInstance()
                            .signOut(WelcomeActivity.this)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    launchLoginActivity();
                                }
                            });
                } else{
                    launchLoginActivity();
                }

            }
        });
    }

    private void launchLoginActivity(){
        Intent intent = new Intent(this, PhoneVerificationActivity.class);
        startActivity(intent);
    }
}
