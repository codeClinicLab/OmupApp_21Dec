package com.wordpress.herovickers.omup.destinations;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.widget.ImageView;

import com.wordpress.herovickers.omup.R;
import com.wordpress.herovickers.omup.adapters.RecentCallAdapter;
import com.wordpress.herovickers.omup.adapters.RecentCallAdapterSP;
import com.wordpress.herovickers.omup.models.RecentCall;
import com.wordpress.herovickers.omup.models.User;
import com.wordpress.herovickers.omup.utility.PrefsManager;
import com.wordpress.herovickers.omup.viewmodel.FirestoreViewModel;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class ContactDetailsActivity extends AppCompatActivity {

    private RecyclerView mrecyclerView;
    private RecentCallAdapterSP adapter;
    private List<RecentCall> recentCallArrayList;
    private String contact_name;
    private String contact_number;
    private ImageView actionCall;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTheme().applyStyle(R.style.AppThemeWithTitleBar, true);
        setContentView(R.layout.activity_contact_details);
        setTitle("");

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        android.content.Intent intent = getIntent();
        contact_name = intent.getStringExtra("CONTACT_NAME");
        contact_number = intent.getStringExtra("CONTACT_NUMBER");

        android.widget.TextView title_text = findViewById(R.id.toolbar_title);
        android.widget.TextView number_text = findViewById(R.id.number_text);
        actionCall = findViewById(R.id.action_call);
        ImageView backArrow = findViewById(R.id.back_btn);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigateToPreviousActivity();
            }
        });
        actionCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactDetailsActivity.this, CallActivty.class);
                intent.putExtra("CONTACT_NAME", contact_name);
                intent.putExtra("CONTACT_NUMBER", contact_number);
                intent.setType("Outgoing");
                startActivity(intent);
            }
        });

        title_text.setText(contact_name);
        number_text.setText(contact_number);

        setUpRecyclerView();
        fetchRecentCallsDataSP();

    }
    private void setUpRecyclerView() {
        recentCallArrayList = new ArrayList<>();
        mrecyclerView = findViewById(R.id.rv_recent_calls);
        mrecyclerView.setHasFixedSize(true);
        mrecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecentCallAdapterSP(this, recentCallArrayList);
        mrecyclerView.setAdapter(adapter);
    }
    private void fetchRecentCallsDataSP() {
        FirestoreViewModel firestoreViewModel = ViewModelProviders.of(this).get(FirestoreViewModel.class);
        LiveData<List<RecentCall>> listLiveData = firestoreViewModel.getRecentCallsSP(getCombinedId());
        listLiveData.observe(this, new Observer<List<RecentCall>>() {
            @Override
            public void onChanged(List<RecentCall> recentCallList) {
                recentCallArrayList.clear();
                if (recentCallList != null){
                    recentCallArrayList = recentCallList;
                    adapter.update(recentCallArrayList);
                }
            }
        });
    }

    private String getCombinedId() {
        User user = new PrefsManager(this).getUserData();
        String userPhoneNumber = user.getPhoneNumber();
        String receiverPhoneNumber = contact_number.replaceAll("\\s","");;
        if (receiverPhoneNumber.charAt(0) == '+'){
            receiverPhoneNumber = "0"+receiverPhoneNumber.substring(4);
        }
        if (userPhoneNumber.charAt(0) == '+'){
            userPhoneNumber = "0"+userPhoneNumber.substring(4);
        }
        if (Long.valueOf(userPhoneNumber) > Long.valueOf(receiverPhoneNumber)){
            return userPhoneNumber+receiverPhoneNumber;
        }else{
            return receiverPhoneNumber+userPhoneNumber;
        }
    }

    private void NavigateToPreviousActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contact_details, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit){
            //handle action
        }else if (id == R.id.action_delete){
            //handle action
        }else if (id == R.id.action_share){
            //handle action
        }else if (id == R.id.action_add_to_fav){
            //handle action
        }
        return super.onOptionsItemSelected(item);
    }
}
