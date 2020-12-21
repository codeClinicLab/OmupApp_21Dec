package com.wordpress.herovickers.omup.destinations;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.wordpress.herovickers.omup.R;
import com.wordpress.herovickers.omup.models.WalletTransaction;
import com.wordpress.herovickers.omup.utility.NetworkUtils;
import com.wordpress.herovickers.omup.viewmodel.FirestoreViewModel;

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;

public class BuyCreditActivity extends AppCompatActivity {

    private EditText cardName;
    private EditText cardNumber;
    private EditText expiryDate;
    private EditText cvv;
    private Boolean error = false;
    private Button pay;
    private Button back;
    private FirebaseUser user;
    private Boolean isDeductionSuccessful = false;
    private TextView toolBarTitile;
    private Double rechargeAmount;
    private LinearLayout successPage;
    private ScrollView scrollView;
    private AppBarLayout appBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_credit);

        user = FirebaseAuth.getInstance().getCurrentUser();
        cardName = findViewById(R.id.card_name);
        cardNumber = findViewById(R.id.card_number);
        expiryDate = findViewById(R.id.expiry_date);
        cvv = findViewById(R.id.cvv);
        pay = findViewById(R.id.btn_pay);
        back = findViewById(R.id.btn_back);
        toolBarTitile = findViewById(R.id.toolbar_title);
        successPage = findViewById(R.id.success_layout);
        scrollView = findViewById(R.id.scroll_View);
        appBarLayout = findViewById(R.id.toolbar_layout);
        toolBarTitile.setText("Make Payment");
        rechargeAmount = getIntent().getDoubleExtra("amount", 0.0);

        setEditTextsOnClicks();
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtils.isConnected(BuyCreditActivity.this)) {
                    isInputValid();
                    if (!error){
                        //Simulate payment for now
                        simulatePayment();
                        //initPayment();
                    }
                    error = false;
                }else{
                    Toast.makeText(BuyCreditActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        expiryDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = expiryDate.getText().toString();
                if ((text.length() == 2) && !text.contains("/")) {
                    expiryDate.setText(text+"/");
                    expiryDate.setSelection(expiryDate.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Button btnHome = findViewById(R.id.btn_home);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BuyCreditActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void simulatePayment() {
        final WalletTransaction transaction = new WalletTransaction(rechargeAmount,
                System.currentTimeMillis(), "Recharge");
        final FirestoreViewModel viewModel = ViewModelProviders.of(this).get(FirestoreViewModel.class);

        LiveData<Boolean> updateBalance = viewModel.updateWalletBalance(rechargeAmount);
        //TODO remember to save lastRecharge date
        updateBalance.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean){
                    LiveData<Boolean> liveData = viewModel.saveWalletTransction(transaction);
                    liveData.observe(BuyCreditActivity.this, new Observer<Boolean>() {
                        @Override
                        public void onChanged(Boolean aBoolean) {
                            if (aBoolean){
                                //Payment Successful
                                appBarLayout.setVisibility(View.GONE);
                                scrollView.setVisibility(View.GONE);
                                successPage.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            }
        });
    }

    private void setEditTextsOnClicks() {
        cardName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardName.setBackground(getResources().getDrawable(R.drawable.red_border_background));
            }
        });
        cardNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardNumber.setBackground(getResources().getDrawable(R.drawable.red_border_background));
            }
        });

        expiryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expiryDate.setBackground(getResources().getDrawable(R.drawable.red_border_background));
            }
        });
        cvv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cvv.setBackground(getResources().getDrawable(R.drawable.red_border_background));
            }
        });

    }

    private void isInputValid() {
        if (TextUtils.isEmpty(expiryDate.getText().toString())) {
            expiryDate.setBackground(getResources().getDrawable(R.drawable.red_border_background));
            error = true;
        } else {
            if (expiryDate.getText().length() < 5) {
                expiryDate.setBackground(getResources().getDrawable(R.drawable.red_border_background));
                error = true;
            } else {
                expiryDate.setBackground(getResources().getDrawable(R.drawable.blue_border_background));
            }
        }
        if (TextUtils.isEmpty(cvv.getText().toString())) {
            cvv.setBackground(getResources().getDrawable(R.drawable.red_border_background));
            error = true;
        } else {
            if (cvv.getText().length() < 3) {
                cvv.setBackground(getResources().getDrawable(R.drawable.red_border_background));
                error = true;
            } else {
                cvv.setBackground(getResources().getDrawable(R.drawable.blue_border_background));
            }
        }
        if (TextUtils.isEmpty(cardNumber.getText().toString())) {
            cardNumber.setBackground(getResources().getDrawable(R.drawable.red_border_background));
            error = true;
        } else {
            if (cardNumber.getText().length() < 16) {
                cardNumber.setBackground(getResources().getDrawable(R.drawable.red_border_background));
                error = true;
            } else {
                cardNumber.setBackground(getResources().getDrawable(R.drawable.blue_border_background));
            }
        }
    }

    private void initPayment() {
        String mCardNumber = cardNumber.getText().toString();
        String mExpiryDate = expiryDate.getText().toString();
        String[] mExpiryDetails = mExpiryDate.split("/");
        String mExpiryMonth = mExpiryDetails[0];
        String mExpiryYear = mExpiryDetails[1];
        String mCVV = cvv.getText().toString();
        String mAmount = String.valueOf(rechargeAmount);

        PaymentTask paymentTaskRunner = new PaymentTask();
        paymentTaskRunner.execute(mCardNumber, mExpiryMonth, mExpiryYear, mCVV, mAmount);
    }
    class PaymentTask extends AsyncTask<String, String, Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //TODO show load indicator here
        }

        @Override
        protected Void doInBackground(String... transactionDetails) {
            try {
                String cardNumber = transactionDetails[0];
                int expiryMonth = Integer.parseInt(transactionDetails[1]);
                int expiryYear = Integer.parseInt(transactionDetails[2]);
                String cvv = transactionDetails[3];
                int amount = Integer.parseInt(transactionDetails[4]);

                Card myCard = userCard(cardNumber, expiryMonth, expiryYear, cvv);
                if (myCard.isValid()) {
                    deductDonation(myCard, amount);
                }
            } catch (Exception e) {
            }
            return null;
        }

    }
    Card userCard(String cardNumber ,int expiryMonth , int expiryYear , String cvv ) {
        return new Card.Builder(cardNumber, expiryMonth, expiryYear, cvv).build();
        //Paystack javadocs advise that its better to use the Card.build
    }

    void deductDonation(Card card, final int amount) {
        String email = user.getEmail();

        Charge charge = new Charge();

        charge.setCard(card);
        String newAmount = amount  + "00";
        charge.setAmount(Integer.valueOf(newAmount));
        charge.setEmail(email);  //this should be the email the user used in registering for the app

        PaystackSdk.chargeCard(this, charge, new Paystack.TransactionCallback() {
            @Override
            public void onSuccess(Transaction transaction) {
                //TODO show payment success screen
                //progressDialog.setTitle("Payment Success");
                //progressDialog.setMessage("Topping up your wallet");
                updateWallet(amount);
            }

            @Override
            public void beforeValidate(Transaction transaction) {

            }

            @Override
            public void onError(Throwable error, Transaction transaction) {
                //progressDialog.dismiss();
            }
        });
    }
    private void updateWallet(int amount){

    }
}
