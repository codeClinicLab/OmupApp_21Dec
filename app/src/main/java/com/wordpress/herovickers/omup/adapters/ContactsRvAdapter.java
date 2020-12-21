package com.wordpress.herovickers.omup.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Picasso;
import com.wordpress.herovickers.omup.authentication.PhoneVerificationActivity;
import com.wordpress.herovickers.omup.destinations.ContactDetailsActivity;
import com.wordpress.herovickers.omup.R;
import com.wordpress.herovickers.omup.models.ModelContacts;
import com.wordpress.herovickers.omup.models.User;
import com.wordpress.herovickers.omup.utility.Listeners.FragmentInteractionListener;
import com.wordpress.herovickers.omup.utility.RecyclerViewEmptySupport;
import com.wordpress.herovickers.omup.utility.Utils;
import com.wordpress.herovickers.omup.viewmodel.FirestoreViewModel;

import java.util.List;
import java.util.Random;

public class ContactsRvAdapter extends RecyclerViewEmptySupport.Adapter<ContactsRvAdapter.ViewHolder> {

    private Context mContext;

    private LayoutInflater inflater;
    private List<ModelContacts> mListContacts;
    private FragmentInteractionListener mListener;


    public ContactsRvAdapter(Context context, List<ModelContacts> listContacts,
                             FragmentInteractionListener listener) {
        mListContacts = listContacts;
        mListener = listener;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(mContext);
        View view =  inflater.inflate(R.layout.contact_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final TextView contact_name;
        final TextView contact_number;


        final String contact_name_string = mListContacts.get(position).getName();
        final String contact_number_string = mListContacts.get(position).getNumber();

        contact_name = holder.contact_name;
        contact_name.setText(contact_name_string);

        contact_number = holder.contact_number;
        contact_number.setText(contact_number_string);

        char firstLetter = contact_name_string.charAt(0);
        if (!Character.isLetter(firstLetter)){
            firstLetter = 'O';
        }
        Utils.displayContactImage(holder.contactDp, firstLetter, mListContacts.size());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.OnContactDetailsFragmentListener(contact_name_string, contact_number_string);
            }

        } );
        holder.callAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Perform Calling Action here

                FirestoreViewModel firestoreViewModel = ViewModelProviders.of((FragmentActivity) mContext).get(FirestoreViewModel.class);
                LiveData<User> userLiveData = firestoreViewModel.getUserData();
                userLiveData.observe((FragmentActivity) mContext, new Observer<User>() {
                    @Override
                    public void onChanged(User user) {
                        if (user != null){
 if(  String.valueOf(user.getWallet().get("balance")).equals("0.0"))
 {
     Toast.makeText(mContext, "You have not sufficient balance", Toast.LENGTH_SHORT).show();
 }else{
     mListener.OnCallIconClickedListener(contact_name_string, contact_number_string);

 }
                        }else {


                            Intent intent = new Intent(mContext, PhoneVerificationActivity.class);
                            mContext.startActivity(intent);

                        }
                                              }
                });
             }
        });

    }

    @Override
    public int getItemCount() {
        return mListContacts.size();
    }

    class ViewHolder extends RecyclerViewEmptySupport.ViewHolder{
        TextView contact_name;
        TextView contact_number;
        ImageView callAction;
        ImageView contactDp;

        ViewHolder(View itemView){
            super (itemView);
            contact_name = itemView.findViewById(R.id.title_layout);
            contact_number = itemView.findViewById(R.id.number_layout);
            callAction = itemView.findViewById(R.id.action_call);
            contactDp = itemView.findViewById(R.id.image_view_contact_display);
        }
    }

}
