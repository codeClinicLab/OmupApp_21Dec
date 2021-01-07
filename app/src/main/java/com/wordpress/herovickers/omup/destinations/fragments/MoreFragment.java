package com.wordpress.herovickers.omup.destinations.fragments;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wordpress.herovickers.omup.R;
import com.wordpress.herovickers.omup.authentication.PhoneVerificationActivity;
import com.wordpress.herovickers.omup.destinations.FundingActivity;
import com.wordpress.herovickers.omup.destinations.InviteActivity;
import com.wordpress.herovickers.omup.destinations.RatesActivity;
import com.wordpress.herovickers.omup.destinations.UserProfileActivity;
import com.wordpress.herovickers.omup.models.User;
import com.wordpress.herovickers.omup.utility.PrefsManager;
import com.wordpress.herovickers.omup.viewmodel.FirestoreViewModel;

import de.cketti.mailto.EmailIntentBuilder;
import de.hdodenhof.circleimageview.CircleImageView;

public class MoreFragment extends Fragment  {
    private View v;
    private CircleImageView profilePic;
    private TextView fullName;
    private PrefsManager manager;


    public MoreFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_more, container, false);

        manager = new PrefsManager(rootView.getContext());
        View fundingButton = rootView.findViewById(R.id.funding_layout);
        Button editProfile = rootView.findViewById(R.id.btn_edit_profile);
        profilePic = rootView.findViewById(R.id.image_view_contact_display);
        fullName = rootView.findViewById(R.id.user_name);
        setUserData();
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //navigate to user profile activity
                Intent intent = new Intent(getContext(), UserProfileActivity.class);
                startActivity(intent);
            }
        });
                fundingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FundingActivity.class);
                startActivity(intent);
            }
        });

        View inviteButton = (View) rootView.findViewById(R.id.invite_layout);
        inviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), InviteActivity.class);
                startActivity(intent);
            }
        });

        View callingRateButton = (View) rootView.findViewById(R.id.calling_rate_layout);
        callingRateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), RatesActivity.class);
                startActivity(intent);
            }
        });

//        View settingsButton = (View) rootView.findViewById(R.id.settings_layout);
//        settingsButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), SettingsActivity.class);
//                startActivity(intent);
//            }
//        });

        View supportButton = (View) rootView.findViewById(R.id.support_layout);
        supportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                v = getActivity().findViewById(android.R.id.content);
                boolean success = EmailIntentBuilder.from(getContext())
                        .to("support@omuppcall.com")
                        .start();

                if (!success) {
                    Snackbar.make(v, "No email app found", Snackbar.LENGTH_LONG).show();
                }


            }
        });
        return rootView;
    }
    private void setUserData() {
        FirestoreViewModel firestoreViewModel = ViewModelProviders.of(this).get(FirestoreViewModel.class);
        LiveData<User> userLiveData = firestoreViewModel.getUserData();
        userLiveData.observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                fullName.setText(user.getFirstName()+" "+user.getLastName());
                if (!user.getProfileUrl().isEmpty()){
                    Picasso.get().load(user.getProfileUrl()).into(profilePic);
                }
            }
        });
    }
}
