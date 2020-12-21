package com.wordpress.herovickers.omup.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wordpress.herovickers.omup.R;
import com.wordpress.herovickers.omup.models.WalletTransaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class WalletTransactionAdapter extends RecyclerView.Adapter<WalletTransactionAdapter.DataObjectHolder> {
    private Context context;
    List<WalletTransaction> walletTransactions;

    public WalletTransactionAdapter(Context context, List<WalletTransaction> walletTransactions) {
        this.context = context;
        this.walletTransactions = walletTransactions;
    }

    @NonNull
    @Override
    public DataObjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.transactions_item, parent, false);
        return new DataObjectHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataObjectHolder holder, int position) {
        WalletTransaction walletTransaction = walletTransactions.get(position);
        if (walletTransaction != null){
            holder.bind(walletTransaction);
        }

    }

    @Override
    public int getItemCount() {
        return walletTransactions.size();
    }

    public void update(List<WalletTransaction> walletTransactionList) {
        walletTransactions = walletTransactionList;
        notifyDataSetChanged();
    }

    class DataObjectHolder extends RecyclerView.ViewHolder{

        private TextView transactionDate;
        private TextView transactionAmount;
        private TextView currencyType;
        private TextView transactionType;
        private ImageView transIndicator;
        DataObjectHolder(@NonNull View itemView) {
            super(itemView);
            transactionDate = itemView.findViewById(R.id.tv_transaction_date);
            transactionAmount = itemView.findViewById(R.id.transaction_charge);
            currencyType = itemView.findViewById(R.id.tv_currency);
            transactionType = itemView.findViewById(R.id.title_layout);
            transIndicator = itemView.findViewById(R.id.transaction_icon);
        }

        void bind(WalletTransaction walletTransaction) {
            transactionAmount.setText(String.valueOf(walletTransaction.getAmount()));
            transactionDate.setText(formatDate(walletTransaction.getCreatedAt()));
            transactionType.setText(walletTransaction.getTransactionType());
            //TODO set currency type based on user preference
            formatTransactionIndicator(transIndicator);
        }
    }

    private void formatTransactionIndicator(ImageView transIndicator) {
    }

    private String formatDate(long createdAt) {
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        return simpleDateFormat.format(new Date(createdAt));
    }
}
