package com.wordpress.herovickers.omup.destinations.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wordpress.herovickers.omup.R;
import com.wordpress.herovickers.omup.destinations.BuyCreditActivity;

public class RechargeDialogFragment extends DialogFragment {

    private EditText amount;
    private Button next;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_recharge_dialog, container, false);
        amount = view.findViewById(R.id.ed_amount);
        next = view.findViewById(R.id.btn_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCorrespondingActivity(view);

            }
        });
        amount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount.setBackground(getResources().getDrawable(R.drawable.blue_border_background));
            }
        });
        return view;
    }

    private void launchCorrespondingActivity(View view) {
        String amountHere = amount.getText().toString();
        if (amountHere.isEmpty()){
            Toast.makeText(view.getContext(), "Enter amount", Toast.LENGTH_SHORT).show();
            amount.setBackground(getResources().getDrawable(R.drawable.red_border_background));
        }else if (Integer.valueOf(amountHere) < 5){
            amount.setBackground(getResources().getDrawable(R.drawable.red_border_background));
            Toast.makeText(view.getContext(), "must be at least US 5", Toast.LENGTH_SHORT).show();
        }else {
            Intent intent = new Intent(view.getContext(), BuyCreditActivity.class);
            intent.putExtra("amount", Double.valueOf(amountHere));
            startActivity(intent);
        }
    }
}
