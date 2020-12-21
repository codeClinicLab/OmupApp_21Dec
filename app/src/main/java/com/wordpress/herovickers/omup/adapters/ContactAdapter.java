package com.wordpress.herovickers.omup.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.wordpress.herovickers.omup.data.Contact;
import com.wordpress.herovickers.omup.R;

import java.util.ArrayList;

public class ContactAdapter extends ArrayAdapter<Contact> {

    public ContactAdapter(Activity context, ArrayList<Contact> contacts) {
        super(context, 0, contacts);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null ) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.contact_list_item, parent, false);
        }

        Contact currentContact = getItem(position);

        TextView contactName = (TextView) listItemView.findViewById(R.id.contact_name);
        contactName.setText(currentContact.getmContactName());

        TextView contactNumber = (TextView) listItemView.findViewById(R.id.contact_number);
        contactNumber.setText(currentContact.getContactNumber());

//Todo Add image to contact object and implement the code below
//        if (currentWord.hasImage()) {
//            imageView.setImageResource(currentWord.getImageResourceID());
//            imageView.setVisibility(View.VISIBLE);
//        }
//        else {
//            imageView.setVisibility(View.GONE);
//        }

        return listItemView;
    }
}
