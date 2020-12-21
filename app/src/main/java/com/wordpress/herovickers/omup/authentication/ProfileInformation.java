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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.wordpress.herovickers.omup.R;
import com.wordpress.herovickers.omup.Views.MyEditText;
import com.wordpress.herovickers.omup.models.User;
import com.wordpress.herovickers.omup.utility.Listeners.DrawableClickListener;
import com.wordpress.herovickers.omup.utility.PasswordUtils;
import com.wordpress.herovickers.omup.utility.PrefsManager;
import com.wordpress.herovickers.omup.viewmodel.FirestoreViewModel;

import java.util.HashMap;
import java.util.Map;

import static com.wordpress.herovickers.omup.utility.PrefsManager.STATUS_SIGNED_IN;

public class ProfileInformation extends AppCompatActivity {
    private EditText mFirstName;
    private EditText mLastName;
    private MyEditText mPassword;
    private EditText mCountry;
    private EditText mEmail;
    private TextView toolBarTitle;
    private boolean visible = false;
    private RelativeLayout pBarLayout;
    private FirebaseUser mUser;
    private Button proceed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTheme().applyStyle(R.style.AppThemeWithTitleBar, true);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser == null){
            launchPhoneVerificationActivity();
        }

        FirestoreViewModel viewModel = ViewModelProviders.of(this).get(FirestoreViewModel.class);
        LiveData<User> liveData = viewModel.getUserDataOnce();
        liveData.observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null){
                    String data = user.getFirstName();
                    launchCorrespondingActivity(data);

                }else {
                    setContentView(R.layout.activity_profile_information);

                    toolBarTitle = findViewById(R.id.toolbar_title);
                    toolBarTitle.setText("Kindly fill all fields");
                    mFirstName = findViewById(R.id.ed_first_name);
                    mLastName = findViewById(R.id.ed_last_name);
                    mPassword = findViewById(R.id.ed_password);
                    mCountry = findViewById(R.id.ed_country);
                    mEmail = findViewById(R.id.ed_email);
                    pBarLayout = findViewById(R.id.progress_bar_layout);

                    proceed = findViewById(R.id.btn_proceed);
                    proceed.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            enableActions();
                            pBarLayout.setVisibility(View.VISIBLE);
                            saveUserInfoToDatabase(mUser);
                        }
                    });
                }
            }
        });

    }

    private void saveUserInfoToDatabase(FirebaseUser user) {
        final String firstName = mFirstName.getText().toString();
        String lastName = mLastName.getText().toString();
        String country = mCountry.getText().toString();
        String email = mEmail.getText().toString();
        String salt = PasswordUtils.getSalt(30);
        String password = PasswordUtils.generateSecurePassword(mPassword.getText().toString(), salt);
        Map<String, Object> wallet= new HashMap<>();
        wallet.put("balance", 0.00);
        wallet.put("lastRechargeDate", System.currentTimeMillis());
        Map<String, String> passwordMap = new HashMap<>();
        passwordMap.put("password", password);
        passwordMap.put("salt", salt);
        User userInfo = new User(email, firstName, lastName,
                user.getPhoneNumber(), "", user.getUid(),
                wallet, country, passwordMap);
        FirestoreViewModel firestoreViewModel = ViewModelProviders.of(this).get(FirestoreViewModel.class);
        LiveData<Boolean> booleanLiveData = firestoreViewModel.saveUserData(userInfo);
        booleanLiveData.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean){
                    new PrefsManager(ProfileInformation.this).saveRegistrationProgress(STATUS_SIGNED_IN);
                    Log.e("dddddddd", "success");
                    pBarLayout.setVisibility(View.GONE);
                    disableActions();
                    launchCorrespondingActivity(firstName);
                }else{
                    //User signed it successfully but data couldn't be added to database
                    //TODO Do something
                    pBarLayout.setVisibility(View.GONE);
                    disableActions();
                    Log.e("Saving Data Error", "User signed it successfully but data couldn't be added to database");
                }
            }
        });
        setPasswordEyeIconListener();

    }
    private void launchCorrespondingActivity(String data) {
        Intent intent = new Intent(this, PasswordAuthentication.class);
        intent.putExtra("firstName", data);
        startActivity(intent);
        finish();
    }

    private void launchPhoneVerificationActivity() {
        Intent intent = new Intent(this, PhoneVerificationActivity.class);
        startActivity(intent);
        finish();
    }

    private void setPasswordEyeIconListener() {
        mPassword.setDrawableClickListener(new DrawableClickListener() {
            public void onClick(DrawablePosition target) {
                if (target == DrawablePosition.RIGHT) {//Do something here
                    if (visible) {
                        mPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                                R.drawable.ic_hide, 0);
                        mPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        visible = false;
                    } else {
                        mPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_remove_red_eye_black_24dp, 0);
                        mPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        visible = true;
                    }
                    mPassword.setSelection(mPassword.getText().length());
                }
            }
        });
    }
    private void disableActions(){
        mPassword.setInputType(InputType.TYPE_NULL);
        mLastName.setInputType(InputType.TYPE_NULL);
        mEmail.setInputType(InputType.TYPE_NULL);
        mCountry.setInputType(InputType.TYPE_NULL);
        mFirstName.setInputType(InputType.TYPE_NULL);
        proceed.setVisibility(View.INVISIBLE);
    }
    private void enableActions(){
        mPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mLastName.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mEmail.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mCountry.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mFirstName.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        proceed.setVisibility(View.VISIBLE);
    }
}
