package com.wordpress.herovickers.omup.onboarding;

import android.content.Intent;
import android.os.Build;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.wordpress.herovickers.omup.R;
import com.wordpress.herovickers.omup.authentication.PhoneVerificationActivity;
import com.wordpress.herovickers.omup.authentication.WelcomeActivity;
import com.wordpress.herovickers.omup.utility.PageAdapter;
import com.wordpress.herovickers.omup.utility.PrefsManager;

public class AppIntro extends AppCompatActivity {

    public static final int RC_SIGN_IN = 1;
    private PagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private Integer layouts[] = {R.layout.on_screen_one, R.layout.on_screen_two,
    R.layout.on_screen_three, R.layout.on_screen_four};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTheme().applyStyle(R.style.AppThemeWithTitleBar, true);
        if(new PrefsManager(this).checkPref()){
            //Go to Login
            goToLogin();
        }

        //Trying to hide the toolbar

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        if (Build.VERSION.SDK_INT >= 19){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        
        setContentView(R.layout.activity_app_intro);

        viewPager = findViewById(R.id.view_pager);

        pagerAdapter = new PageAdapter(layouts, this);
        viewPager.setAdapter(pagerAdapter);

        final Button next = findViewById(R.id.btn_next);
        TextView skip = findViewById(R.id.btn_skip);
        /**Listen for chnages on the page and trigger a call back*/
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == layouts.length-1){
                     next.setText("Get Started");
                }else{
                    next.setText("NEXT");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int nextScreen= viewPager.getCurrentItem() +1;//Index of the currently displayed page
                if(nextScreen < layouts.length){
                    viewPager.setCurrentItem(nextScreen);
                }else if (nextScreen > layouts.length-1){
                    /**Handle navigation here because it will always get greater than it*/
                    //Go to Login
                    goToLogin();
                }
                if(nextScreen == layouts.length-1){
                    /**Do something when they exactly equal*/
                }

            }
        });
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Go to Login
                goToLogin();
            }
        });
    }
    private void goToLogin() {
        new PrefsManager(this).writePref();
        Intent i = new Intent(this, WelcomeActivity.class);
        startActivity(i);
        finish();
    }
}
