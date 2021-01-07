package com.wordpress.herovickers.omup.destinations.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wordpress.herovickers.omup.R;
import com.wordpress.herovickers.omup.destinations.CallActivty;
import com.wordpress.herovickers.omup.destinations.ContactDetailsActivity;
import com.wordpress.herovickers.omup.models.User;
import com.wordpress.herovickers.omup.viewmodel.FirestoreViewModel;


public class KeypadFragment extends Fragment{

    SharedPreferences sharedPreferences;
    String phoneStr;

    EditText phoneNumber;

    public KeypadFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_keypad, container, false);

        Button callButton = rootView.findViewById(R.id.call_button);

        phoneNumber = rootView.findViewById(R.id.call_field);

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View rootView) {
                /*FirestoreViewModel firestoreViewModel = ViewModelProviders.of((FragmentActivity) getContext()).get(FirestoreViewModel.class);
                LiveData<User> userLiveData = firestoreViewModel.getUserData();
                userLiveData.observe((FragmentActivity) getContext(), new Observer<User>() {
                    @Override
                    public void onChanged(User user) {
                        if (user != null) {
                            if (String.valueOf(user.getWallet().get("balance")).equals("0.0") || String.valueOf(user.getWallet().get("balance")).equals("0")) {
                                Toast.makeText( getContext(), "You have not sufficient balance", Toast.LENGTH_SHORT).show();
                            } else {
                            }
                        }
                    }
                });*/
                Intent intent = new Intent(getActivity(), CallActivty.class);
                intent.putExtra("CONTACT_NUMBER", phoneNumber.getText().toString());
                intent.putExtra("CONTACT_NAME", phoneNumber.getText().toString());
                intent.setType("Outgoing");
                startActivity(intent);

            }
        });

        return rootView;
    }
}