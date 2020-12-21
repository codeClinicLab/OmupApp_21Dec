package com.wordpress.herovickers.omup.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wordpress.herovickers.omup.R;
import com.wordpress.herovickers.omup.models.RecentCall;
import com.wordpress.herovickers.omup.utility.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RecentCallAdapterSP extends RecyclerView.Adapter<RecentCallAdapterSP.DataObjectHolder> {


    private List<RecentCall> recentCallList;
    private Context context;
    public RecentCallAdapterSP(Context context, List<RecentCall> recentCallList) {
        this.context = context;
        this.recentCallList = recentCallList;
    }

    @NonNull
    @Override
    public DataObjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.call_items_specific, parent, false);
        return new DataObjectHolder(view);
    }

    @Override
    public int getItemCount() {
        return recentCallList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull DataObjectHolder holder, int position) {
        RecentCall recentCall = recentCallList.get(position);
        if(recentCall != null){
            holder.bind(recentCall);
        }
    }

    public void update(List<RecentCall> recentCallArrayList) {
        recentCallList = recentCallArrayList;
        notifyDataSetChanged();
    }

    class DataObjectHolder extends RecyclerView.ViewHolder{
        TextView date;
        TextView callType;
        TextView callTimeAndDuration;
        ImageView callTypeIndicator;
        DataObjectHolder(@NonNull View itemView) {
            super(itemView);
            callType = itemView.findViewById(R.id.tv_call_type);
            callTimeAndDuration = itemView.findViewById(R.id.tv_call_time);
            callTypeIndicator = itemView.findViewById(R.id.img_call_type);
            date = itemView.findViewById(R.id.tv_date);
        }

        void bind(RecentCall recentCall) {
            callType.setText(recentCall.getCallType());
            callTimeAndDuration.setText(formatTime(recentCall.getCreatedAt(), recentCall.getLastCallDuration()));
            date.setText(formatDate(recentCall.getCreatedAt()));
            displayProperCallIndicator(callTypeIndicator, recentCall.getCallType());
        }
    }

    private String formatDate(Long createdAt) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(createdAt);
        return DateFormat.format("MMMM dd", calendar).toString();
    }


    private void displayProperCallIndicator(ImageView callTypeIndicator, String callType) {
        switch (callType) {
            case "Outgoing":
                callTypeIndicator.setImageResource(R.drawable.ic_call_made_green_24dp);
                break;
            case "Received":
                callTypeIndicator.setImageResource(R.drawable.ic_call_received_blue_24dp);
                break;
            case "Missed":
                callTypeIndicator.setImageResource(R.drawable.ic_call_missed_red_24dp);
                break;
        }
    }

    private String formatTime(Long createdAt, String callDuration) {;
        //TODO complete this method. d formatting here is long
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(createdAt);
        String date =  DateFormat.format("hh:mm", calendar).toString();
        return date + " (" + callDuration + ")";
    }
}