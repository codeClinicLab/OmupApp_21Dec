package com.wordpress.herovickers.omup.adapters;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.wordpress.herovickers.omup.R;
import com.wordpress.herovickers.omup.authentication.PhoneVerificationActivity;
import com.wordpress.herovickers.omup.models.RecentCall;
import com.wordpress.herovickers.omup.models.User;
import com.wordpress.herovickers.omup.utility.Listeners.FragmentInteractionListener;
import com.wordpress.herovickers.omup.utility.Utils;
import com.wordpress.herovickers.omup.viewmodel.FirestoreViewModel;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class RecentCallAdapter extends RecyclerView.Adapter<RecentCallAdapter.DataObjectHolder> {


    private List<RecentCall> recentCallList;
    private Context context;
    private FragmentInteractionListener mListener;
    String TAG="RecentCallAdapter ";
    public RecentCallAdapter(Context context, List<RecentCall> recentCallList,
                             FragmentInteractionListener listener) {
        this.context = context;
        this.recentCallList = recentCallList;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public DataObjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.calls_item, parent, false);
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
        TextView title;
        TextView phoneNumber;
        ImageView contactImage;
        TextView callDuration;
        TextView callTime;
        ImageView callTypeIndicator;
        DataObjectHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_layout);
            phoneNumber = itemView.findViewById(R.id.number_layout);
            contactImage = itemView.findViewById(R.id.image_view_contact_display);
            callDuration = itemView.findViewById(R.id.call_duration);
            callTime = itemView.findViewById(R.id.call_time);
            callTypeIndicator = itemView.findViewById(R.id.img_call_type);
        }

        void bind(final RecentCall recentCall) {
            final String name = recentCall.getUserInfo().get("fullName");
            final String phone = recentCall.getUserInfo().get("phoneNumber");
            title.setText(name);
            phoneNumber.setText(phone);
            callDuration.setText(recentCall.getLastCallDuration());
            callTime.setText(formatTime(recentCall.getCreatedAt()));
     try {
         Utils.displayContactImage(contactImage, name.charAt(0), recentCallList.size());
     }
     catch (Exception exp)
     {
         Log.e(TAG,"excep="+exp.toString());
     }
     displayProperCallIndicator(callTypeIndicator, recentCall.getCallType());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirestoreViewModel firestoreViewModel = ViewModelProviders.of((FragmentActivity) context).get(FirestoreViewModel.class);
                    LiveData<User> userLiveData = firestoreViewModel.getUserData();
                    userLiveData.observe((FragmentActivity) context, new Observer<User>() {
                        @Override
                        public void onChanged(User user) {
                            if (user != null){
                                if(  String.valueOf(user.getWallet().get("balance")).equals("0.0") ||   String.valueOf(user.getWallet().get("balance")).equals("0"))
                                {
                                    Toast.makeText(context, "You have not sufficient balance", Toast.LENGTH_SHORT).show();
                                }else{
                                    mListener.OnCallIconClickedListener(name, phone);

                                }
                            }else {


                            Intent intent = new Intent(context, PhoneVerificationActivity.class);
                                context.startActivity(intent);

                            }
                        }
                    });
                }
            });
            contactImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.OnContactDetailsFragmentListener(name, phone);
                }
            });
        }
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

    private String formatTime(Long createdAt) {
        long duration = System.currentTimeMillis()-createdAt;
        if (duration < TimeUnit.SECONDS.toMillis(60)){
            int d = (int) Math.ceil(TimeUnit.MILLISECONDS.toSeconds(duration));
            if (d>1){
                return d+" secs ago";
            }else{
                return d+" sec ago";
            }
        }else if (duration < TimeUnit.MINUTES.toMillis(60)){
            int d = (int) Math.ceil(TimeUnit.MILLISECONDS.toMinutes(duration));
            if (d>1){
                return d+" mins ago";
            }else{
                return d+" min ago";
            }
        }else if (duration < TimeUnit.HOURS.toMillis(24)){
            int d = (int) Math.ceil(TimeUnit.MILLISECONDS.toHours(duration));
            if (d>1){
                return d+" hrs ago";
            }else{
                return d+" hr ago";
            }

        }else {
            int d = (int) Math.ceil(TimeUnit.MILLISECONDS.toDays(duration));
            if (d>1){
                return d+" days ago";
            }else {
                return d+" day ago";
            }
        }
    }
}
