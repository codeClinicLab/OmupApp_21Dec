package com.wordpress.herovickers.omup.destinations;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.annotation.NonNull;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.wordpress.herovickers.omup.adapters.ViewPagerAdapter;
import com.wordpress.herovickers.omup.authentication.WelcomeActivity;
import com.wordpress.herovickers.omup.destinations.fragments.CallsFragment;
import com.wordpress.herovickers.omup.destinations.fragments.ContactsFragment;
import com.wordpress.herovickers.omup.destinations.fragments.KeypadFragment;
import com.wordpress.herovickers.omup.destinations.fragments.MoreFragment;
import com.wordpress.herovickers.omup.R;

import static android.Manifest.permission.READ_CONTACTS;

public class HomeActivity extends AppCompatActivity{
    private ViewPager viewPager;
    private BottomNavigationView bottomNavigationView;

    private MenuItem prevMenuItem;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    Handler handler;
    Runnable r;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        requestAllPermission();
        viewPager = findViewById(R.id.viewpager);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        //toolBarTitle = findViewById(R.id.toolbar_title);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.action_home:
                                viewPager.setCurrentItem(0);
                                //toolBarTitle.setText("Home");
                                break;
                            case R.id.action_keypad:
                                viewPager.setCurrentItem(1);
                                //toolBarTitle.setText("Phone");
                                break;
                            case R.id.action_contact:
                                viewPager.setCurrentItem(2);
                                //toolBarTitle.setText("Contacts");
                                break;
                            case R.id.action_more:
                                viewPager.setCurrentItem(3);
                                //toolBarTitle.setText("More");
                                break;
                        }
                        return false;
                    }
                });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                Log.d("page", "onPageSelected: "+position);
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        setupViewPager(viewPager);

        handler = new Handler();
        r = new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Toast.makeText(HomeActivity.this, "user is inactive from last 3 minute",Toast.LENGTH_SHORT).show();
                AuthUI.getInstance()
                        .signOut(HomeActivity.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                               /* Intent intent = new Intent(HomeActivity.this, WelcomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);                                startActivity(intent);
                                finish();*/
                            }
                        });   }
        };
        startHandler();
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        CallsFragment homeFragment = new CallsFragment();
        KeypadFragment keypadFragment = new KeypadFragment();
        ContactsFragment contactsFragments = new ContactsFragment();
        MoreFragment moreFragment = new MoreFragment();
        adapter.addFragment(homeFragment);
        adapter.addFragment(keypadFragment);
        adapter.addFragment(contactsFragments);
        adapter.addFragment(moreFragment);
        if (viewPager != null) {
            viewPager.setAdapter(adapter);
            viewPager.setOffscreenPageLimit(4);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
    public void requestAllPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ){
                // Permission is not granted
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_CONTACTS)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                } else {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(this,
                            new String[]{ Manifest.permission.READ_CONTACTS},
                            REQUEST_CODE_ASK_PERMISSIONS);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }else {
                setupViewPager(viewPager);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
                //setupViewPager(viewPager);
            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                Toast.makeText(this,
                        "All Permission needs to be granted",
                        Toast.LENGTH_SHORT).show();
                finish();
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }



    @Override
    public void onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction();
        stopHandler();//stop first and then start
        startHandler();
    }
    public void stopHandler() {
        handler.removeCallbacks(r);
    }
    public void startHandler() {
        handler.postDelayed(r, 3*60*1000);
    }

}

