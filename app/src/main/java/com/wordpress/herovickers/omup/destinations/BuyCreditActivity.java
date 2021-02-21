package com.wordpress.herovickers.omup.destinations;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.wordpress.herovickers.omup.R;
import com.wordpress.herovickers.omup.api.AllApiResponse;
import com.wordpress.herovickers.omup.api.HttpModule;
import com.wordpress.herovickers.omup.api.interfaces.ApiService;
import com.wordpress.herovickers.omup.models.User;
import com.wordpress.herovickers.omup.models.WalletTransaction;
import com.wordpress.herovickers.omup.utility.NetworkUtils;
import com.wordpress.herovickers.omup.viewmodel.FirestoreViewModel;

import javax.inject.Inject;

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sqip.CardDetails;
import sqip.CardEntry;

import static android.view.View.GONE;
import static sqip.CardEntry.DEFAULT_CARD_ENTRY_REQUEST_CODE;

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
    View llSucessPayment;
    RelativeLayout progressBarLayout;
    @Inject
    ApiService  apiService;

    String strUserId="",strUserEmail="";

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
        llSucessPayment= findViewById(R.id.ll_sucessPayment);
        progressBarLayout= findViewById(R.id.progress_bar_layout);
    toolBarTitile.setText("Make Payment");
        rechargeAmount = getIntent().getDoubleExtra("amount", 0.0);

        setEditTextsOnClicks();
        pay.setText("Pay $"+rechargeAmount);
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtils.isConnected(BuyCreditActivity.this)) {
                    isInputValid();
                    if (!error){
                        //Simulate payment for now
                        //simulatePayment(); it was used as in got code
          initPayment();
                   /*     CardEntry.startCardEntryActivity(CheckoutActivity.this, true,
                                DEFAULT_CARD_ENTRY_REQUEST_CODE);
                   */ }
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

        getUserData();

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
      //  llSucessPayment.setVisibility(View.VISIBLE);
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
     /*   Card card =new Card();


        CardBuilder cardBuilder = new CardBuilder()
                .cardNumber("4111111111111111")
                .expirationDate("09/2018");

        Card.tokenize(mBraintreeFragment, cardBuilder);*/


  /*      PaymentTask paymentTaskRunner = new PaymentTask();
        paymentTaskRunner.execute(mCardNumber, mExpiryMonth, mExpiryYear, mCVV, mAmount);*/

        CardEntry.startCardEntryActivity(BuyCreditActivity.this, true,
                DEFAULT_CARD_ENTRY_REQUEST_CODE);
    }
    class PaymentTask extends AsyncTask<String, String, Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //TODO show load indicator here
Log.e("TAG","PaymentTask onpre");
        }

        @Override
        protected Void doInBackground(String... transactionDetails) {
            Log.e("TAG","PaymentTask doInBackground");
            try {
                String cardNumber = transactionDetails[0];
                int expiryMonth = Integer.parseInt(transactionDetails[1]);
                int expiryYear = Integer.parseInt(transactionDetails[2]);
                String cvv = transactionDetails[3];


                String[] strAmount=transactionDetails[4].split(".");
                int amount = Integer.parseInt(strAmount[0]);

                Card myCard = userCard(cardNumber, expiryMonth, expiryYear, cvv);
                if (myCard.isValid()) {
                    deductDonation(myCard, amount);
                }
            } catch (Exception e) {

Log.e("TAG","doInBackground excecption ="+e.toString());

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        CardEntry.handleActivityResult(data, result -> {
            if (result.isSuccess()) {
                CardDetails cardResult = result.getSuccessValue();
                sqip.Card card = cardResult.getCard();
                String nonce = cardResult.getNonce();
                Log.e("nonce",""+nonce);
                callSendOTPApi(nonce);

            } else if (result.isCanceled()) {
                Log.e("cancelled",""+result.isCanceled());
                Toast.makeText(this,
                        "Canceled",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void callSendOTPApi(String strNonce) {
        if ( strNonce.equals("")) {
            Toast.makeText(BuyCreditActivity.this,"Please try again",Toast.LENGTH_LONG).show();
        }
        else if (checkConnection(BuyCreditActivity.this)) {
             progressBarLayout.setVisibility(View.VISIBLE);
            ApiService apiService = HttpModule.getAppClient().create(ApiService.class);
            /*app_user_id:1
email:paras@gmail.com
amount:50
nonce:cnon:CBASELci-gZjfn8q-8zH_jBqKI0
bank_name:bnkTst
bank_ac_holder_name:acHolderTst
bank_ac_number:acNmbrTst*/

            apiService.callPayApiForFundTransfer("fund_transfer" ,""+strUserId,""+strUserEmail,""+rechargeAmount,""+ strNonce,
"strTstBnk","acHolderTst","acNmbrTst"            )
                    .enqueue(new Callback<AllApiResponse.PayFundTransferModel>() {
                        @Override
                        public void onResponse(Call<AllApiResponse.PayFundTransferModel> call, Response<AllApiResponse.PayFundTransferModel> response) {
                            Log.d("callApi response",""+  new Gson().toJson(response.body()) );
                            progressBarLayout.setVisibility(GONE);
                            Toast.makeText(BuyCreditActivity.this, ""+response.body().isVaild, Toast.LENGTH_SHORT).show();
                            if(response.isSuccessful() && response.body().isVaild!=0){


                            }
else{

                            }
                        }

                        @Override
                        public void onFailure(Call<AllApiResponse.PayFundTransferModel> call, Throwable t) {
                            progressBarLayout.setVisibility(GONE);
                            Toast.makeText(BuyCreditActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();

                        }

                    });

        } else {
            Toast.makeText(BuyCreditActivity.this,"Please Check Internet Connection",Toast.LENGTH_LONG).show();
        }
    }
    private void getUserData() {
        FirestoreViewModel firestoreViewModel = ViewModelProviders.of(this).get(FirestoreViewModel.class);
        LiveData<User> userLiveData = firestoreViewModel.getUserData();
        userLiveData.observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {

          strUserId=user.getUserId();
                 strUserEmail=user.getEmail();
 /*type:fund_transfer
app_user_id:1
email:paras@gmail.com
amount:50
nonce:cnon:CBASELci-gZjfn8q-8zH_jBqKI0
bank_name:bnkTst
bank_ac_holder_name:acHolderTst
bank_ac_number:acNmbrTst*/
            }
        });
    }


    public static boolean checkConnection(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

        if (activeNetworkInfo != null) { // connected to the internet
            Toast.makeText(context, activeNetworkInfo.getTypeName(), Toast.LENGTH_SHORT).show();

            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                return true;
            } else if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                return true;
            }
        }
        return false;
    }

}
