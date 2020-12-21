package com.wordpress.herovickers.omup.authentication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.wordpress.herovickers.omup.R;
import com.wordpress.herovickers.omup.Views.MyEditText;
import com.wordpress.herovickers.omup.destinations.HomeActivity;
import com.wordpress.herovickers.omup.models.User;
import com.wordpress.herovickers.omup.utility.Listeners.DrawableClickListener;
import com.wordpress.herovickers.omup.utility.PrefsManager;
import com.wordpress.herovickers.omup.viewmodel.FirestoreViewModel;

import static com.wordpress.herovickers.omup.utility.PrefsManager.STATUS_SIGNED_IN;

public class PasswordAuthentication extends AppCompatActivity {

    private EditText mPassword;
    private TextView forgotPassword;
    private TextView welcomeText;
    private PrefsManager manager;
    private Boolean visible = false;
    private RelativeLayout pBarLayout;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTheme().applyStyle(R.style.AppThemeWithTitleBar, true);
        setContentView(R.layout.activity_password_authentication);

        manager = new PrefsManager(this);
        mPassword = findViewById(R.id.ed_password);
        forgotPassword = findViewById(R.id.tv_forgot_password);
        welcomeText = findViewById(R.id.tv_welcome_text);
        pBarLayout = findViewById(R.id.progress_bar_layout);
        String firstName = getIntent().getStringExtra("firstName");
        welcomeText.setText("Welcome\n"+firstName);
        login = findViewById(R.id.btn_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableActions();
                verifyPassword();
            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO handle retrieving password via email
            }
        });
        //setPasswordEyeIconListener();
    }

    private void setPasswordEyeIconListener() {
        /*mPassword.setDrawableClickListener(new DrawableClickListener() {
            public void onClick(DrawablePosition target) {
                if (target == DrawablePosition.RIGHT) {//Do something here
                    if (visible) {
                        mPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                                R.drawable.ic_hide_white, 0);
                        mPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        visible = false;
                    } else {
                        mPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_remove_red_eye_white_24dp, 0);
                        mPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        visible = true;
                    }
                    mPassword.setSelection(mPassword.getText().length());
                }
            }
        });*/
    }

    private void verifyPassword() {
        FirestoreViewModel firestoreViewModel = ViewModelProviders.of(this).get(FirestoreViewModel.class);
        LiveData<String> stringLiveData = firestoreViewModel.verifyPassword(mPassword.getText().toString().trim());
        stringLiveData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s != null){
                    if (s.equals("match")){
                        setUserData();
                    }else if (s.equals("no match")){
                        //Password do not match
                        pBarLayout.setVisibility(View.INVISIBLE);
                        mPassword.setBackgroundResource(R.drawable.red_border_background);
                        Toast.makeText(PasswordAuthentication.this, "Incorrect Password",
                                Toast.LENGTH_SHORT).show();
                        mPassword.setText("");
                        enableActions();
                    }
                }else {
                    //Error fetching Data online
                    //TODO handle properly
                    Toast.makeText(PasswordAuthentication.this, "Error while Signing In",
                            Toast.LENGTH_SHORT).show();
                    pBarLayout.setVisibility(View.GONE);
                    enableActions();
                }
            }
        });

    }
    private void setUserData( ) {
        FirestoreViewModel firestoreViewModel = ViewModelProviders.of(this).get(FirestoreViewModel.class);
        LiveData<User> userLiveData = firestoreViewModel.getUserData();
        userLiveData.observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null){
                    manager.saveUserData(user);
                    manager.saveRegistrationProgress(STATUS_SIGNED_IN);
                    launchCorrespondingActivity();
                }
            }
        });
    }

    private void launchCorrespondingActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
    private void disableActions(){
        //mPassword.setInputType(InputType.TYPE_NULL);
        login.setVisibility(View.INVISIBLE);
        pBarLayout.setVisibility(View.VISIBLE);
    }
    private void enableActions(){
        //mPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        login.setVisibility(View.VISIBLE);
        pBarLayout.setVisibility(View.INVISIBLE);
    }
}
