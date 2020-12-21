package com.wordpress.herovickers.omup.destinations;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wordpress.herovickers.omup.R;
import com.wordpress.herovickers.omup.adapters.WalletTransactionAdapter;
import com.wordpress.herovickers.omup.destinations.fragments.RechargeDialogFragment;
import com.wordpress.herovickers.omup.models.User;
import com.wordpress.herovickers.omup.models.WalletTransaction;
import com.wordpress.herovickers.omup.viewmodel.FirestoreViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FundingActivity extends AppCompatActivity {

    TextView balance;
    TextView lastRechargeDate;
    TextView currencyType;
    private RecyclerView mrecyclerView;
    private WalletTransactionAdapter adapter;
    private List<WalletTransaction> walletTransactionList;
    private LinearLayout emptyView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_funding);

        TextView toolBarTitle = findViewById(R.id.toolbar_title);
        toolBarTitle.setText("My Wallet");
        balance = findViewById(R.id.tv_balance);
        lastRechargeDate = findViewById(R.id.tv_last_recharge_date);
        currencyType = findViewById(R.id.tv_currency);
        adapter = new WalletTransactionAdapter(this, walletTransactionList);

        setWalletInfo();
        setUpRecyclerView();
        fetchRecentTransaction();

        ImageView rechargeWallet = findViewById(R.id.img_recharge);
        ImageView transferFund = findViewById(R.id.img_transfer);
        rechargeWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Go to payment activity
                RechargeDialogFragment dialogFragment = new RechargeDialogFragment();
                dialogFragment.show(getSupportFragmentManager(), "");
            }
        });
        transferFund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FundingActivity.this, TransferFundActivity.class);
                startActivity(intent);
            }
        });
    }
    private void setWalletInfo() {
        FirestoreViewModel firestoreViewModel = ViewModelProviders.of(this).get(FirestoreViewModel.class);
        LiveData<User> userLiveData = firestoreViewModel.getUserData();
        userLiveData.observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                balance.setText(String.valueOf(user.getWallet().get("balance")));
                lastRechargeDate.setText(formatDate((Long) user.getWallet().get("lastRechargeDate")));
                //TODO set currency type based on user preference
            }
        });
    }

    private String formatDate(Long lastRechargeDate) {
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        return simpleDateFormat.format(new Date(lastRechargeDate));
    }

    private void setUpRecyclerView() {
        walletTransactionList = new ArrayList<>();
        mrecyclerView = findViewById(R.id.rv_wallet_transactions);
        mrecyclerView.setHasFixedSize(true);
        mrecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WalletTransactionAdapter(this, walletTransactionList);
        mrecyclerView.setAdapter(adapter);
    }
    private void fetchRecentTransaction() {
        FirestoreViewModel firestoreViewModel = ViewModelProviders.of(this).get(FirestoreViewModel.class);
        LiveData<List<WalletTransaction>> listLiveData = firestoreViewModel.getRecentTransactions();
        listLiveData.observe(this, new Observer<List<WalletTransaction>>() {
            @Override
            public void onChanged(List<WalletTransaction> walletTransactions) {
                walletTransactionList.clear();
                if (walletTransactions != null){
                    //emptyView.setVisibility(View.INVISIBLE);
                    mrecyclerView.setVisibility(View.VISIBLE);
                    walletTransactionList = walletTransactions;
                    adapter.update(walletTransactionList);
                }else {
                    //emptyView.setVisibility(View.VISIBLE);
                    mrecyclerView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}
