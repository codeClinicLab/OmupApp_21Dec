package com.wordpress.herovickers.omup.destinations.fragments;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wordpress.herovickers.omup.R;
import com.wordpress.herovickers.omup.adapters.RecentCallAdapter;
import com.wordpress.herovickers.omup.authentication.PhoneVerificationActivity;
import com.wordpress.herovickers.omup.destinations.CallActivty;
import com.wordpress.herovickers.omup.destinations.ContactDetailsActivity;
import com.wordpress.herovickers.omup.destinations.FundingActivity;
import com.wordpress.herovickers.omup.destinations.SettingsActivity;
import com.wordpress.herovickers.omup.destinations.UserProfileActivity;
import com.wordpress.herovickers.omup.models.RecentCall;
import com.wordpress.herovickers.omup.models.User;
import com.wordpress.herovickers.omup.utility.Listeners.FragmentInteractionListener;
import com.wordpress.herovickers.omup.viewmodel.FirestoreViewModel;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class CallsFragment extends Fragment implements FragmentInteractionListener {
    private TextView firstName;
    private CircleImageView profilImage;
    private TextView walletBalance;
    private TextView currencyType;
    private RecyclerView mrecyclerView;
    private RecentCallAdapter adapter;
    private List<RecentCall> recentCallArrayList;
    private LinearLayout emptyView;
    private ImageView addCall;
    private LinearLayout rootLayout;
    public CallsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calls, container, false);

        rootLayout = view.findViewById(R.id.root_layout);
        firstName = view.findViewById(R.id.tv_name);
        profilImage =view.findViewById(R.id.image_view_contact_display);
        walletBalance = view.findViewById(R.id.tv_balance);
        currencyType = view.findViewById(R.id.tv_currency);
        emptyView = view.findViewById(R.id.empty_view);
        addCall = view.findViewById(R.id.img_add_call);
        //TODO currency type should be set to user's preferred in the setting activity
        setUserData( );
        setUpFab(view);
        setUpRecyclerView(view);
        fetchRecentCallsData();

        LinearLayout walletBack = view.findViewById(R.id.wallet_back);
        walletBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), FundingActivity.class);
                startActivity(intent);
            }
        });
        ImageView settings = view.findViewById(R.id.action_settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });
        addCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO move to the contacts fragment, use a listener
            }
        });
        profilImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UserProfileActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    private void fetchRecentCallsData() {
        FirestoreViewModel firestoreViewModel = ViewModelProviders.of(this).get(FirestoreViewModel.class);
        LiveData<List<RecentCall>> listLiveData = firestoreViewModel.getRecentCalls();
        listLiveData.observe(this, new Observer<List<RecentCall>>() {
            @Override
            public void onChanged(List<RecentCall> recentCallList) {
                recentCallArrayList.clear();
                if (recentCallList != null){
                    emptyView.setVisibility(View.GONE);
                    mrecyclerView.setVisibility(View.VISIBLE);
                    recentCallArrayList = recentCallList;
                    adapter.update(recentCallArrayList);
                }else {
                    emptyView.setVisibility(View.VISIBLE);
                    mrecyclerView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setUpRecyclerView(View view) {
        recentCallArrayList = new ArrayList<>();
        mrecyclerView = view.findViewById(R.id.rv_recent_calls);
        mrecyclerView.setHasFixedSize(true);
        mrecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecentCallAdapter(/*view.getContext()*/getContext(), recentCallArrayList, this);
        mrecyclerView.setAdapter(adapter);
    }

    private void setUserData()  {
        FirestoreViewModel firestoreViewModel = ViewModelProviders.of(this).get(FirestoreViewModel.class);
        LiveData<User> userLiveData = firestoreViewModel.getUserData();
        userLiveData.observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null){
                    firstName.setText("Hi, "+user.getFirstName());
                    walletBalance.setText(String.valueOf(user.getWallet().get("balance")));
                    //show layout
                    rootLayout.setVisibility(View.VISIBLE);
                    if (!user.getProfileUrl().isEmpty()){
                        Picasso.get().load(user.getProfileUrl()).into(profilImage);
                    }
                }else {
                     Intent intent = new Intent(getContext(), PhoneVerificationActivity.class);
                     startActivity(intent);
                }
            }
        });
    }

    private void setUpFab(View view) {
        FloatingActionButton rechargeWallet = view.findViewById(R.id.fund_wallet);
        rechargeWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Go to payment Activity
                RechargeDialogFragment dialogFragment = new RechargeDialogFragment();
                dialogFragment.show(getChildFragmentManager(), "");
            }
        });
    }
    @Override
    public void OnContactDetailsFragmentListener(String name, String phoneNumber) {
        Intent intent = new Intent(getContext(), ContactDetailsActivity.class);
        intent.putExtra("CONTACT_NAME", name);
        intent.putExtra("CONTACT_NUMBER", phoneNumber);
        startActivity(intent);
    }

    @Override
    public void OnCallIconClickedListener(String name, String phoneNumber) {
        Intent intent = new Intent(getContext(), CallActivty.class);
        intent.putExtra("CONTACT_NAME", name);
        intent.putExtra("CONTACT_NUMBER", phoneNumber);
        intent.setType("Outgoing");
        startActivity(intent);
    }
}