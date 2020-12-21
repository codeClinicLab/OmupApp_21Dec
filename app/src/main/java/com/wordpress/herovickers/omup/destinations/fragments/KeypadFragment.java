package com.wordpress.herovickers.omup.destinations.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.wordpress.herovickers.omup.R;
import com.wordpress.herovickers.omup.destinations.CallActivty;


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
                Intent intent = new Intent(getActivity(), CallActivty.class);
                intent.putExtra("phone", phoneNumber.getText().toString());
                startActivity(intent);
            }
        });

        return rootView;
    }
}