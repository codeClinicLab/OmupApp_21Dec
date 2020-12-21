package com.wordpress.herovickers.omup.destinations.fragments;

import android.content.Intent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.widget.Toast;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.util.Log;
import android.view.ViewGroup;
import android.provider.ContactsContract;

import com.wordpress.herovickers.omup.R;
import com.wordpress.herovickers.omup.adapters.ContactsRvAdapter;
import com.wordpress.herovickers.omup.destinations.AddNewContactActivity;
import com.wordpress.herovickers.omup.destinations.CallActivty;
import com.wordpress.herovickers.omup.destinations.ContactDetailsActivity;
import com.wordpress.herovickers.omup.models.ModelContacts;
import com.wordpress.herovickers.omup.utility.Listeners.FragmentInteractionListener;
import com.wordpress.herovickers.omup.utility.RecyclerViewEmptySupport;

import java.util.ArrayList;
import java.util.List;

import android.Manifest;

public class ContactsFragment extends Fragment implements FragmentInteractionListener {
    private List<ModelContacts> userList;
    private RecyclerViewEmptySupport mRecycler;
    private LinearLayoutManager mManager;
    private FloatingActionButton addNewContact;
    Cursor cursor;

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;


    public ContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_contacts, container, false);

        addNewContact = rootView.findViewById(R.id.fab_add_contact);
        addNewContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Navigate to add new Contact Activity
                Intent intent = new Intent(getContext(), AddNewContactActivity.class);
                startActivity(intent);
            }
        });

        return rootView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        userList = new ArrayList<>();

        mRecycler = getActivity().findViewById(R.id.contacts_list);
        mRecycler.setHasFixedSize(true);
        mRecycler.setItemAnimator(new DefaultItemAnimator());

        mManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecycler.setLayoutManager(mManager);

        Log.d("check for request", "contact should read" );

        if(!hasPhoneContactsPermission(Manifest.permission.READ_CONTACTS))
        {
            requestPermission(Manifest.permission.READ_CONTACTS);
            Log.d("request permission done", "contact should read" );
        }else {
            Log.d("no request", "contact should read" );
            ContactsRvAdapter adapter = new ContactsRvAdapter(getContext(), getContacts(), this);
            mRecycler.setAdapter(adapter);
                
        }
    }
    private boolean hasPhoneContactsPermission(String permission)
    {
        boolean ret = false;

        // If android sdk version is bigger than 23 the need to check run time permission.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // return phone read contacts permission grant status.
            int hasPermission = ContextCompat.checkSelfPermission(getContext(), permission);
            // If permission is granted then return true.
            if (hasPermission == PackageManager.PERMISSION_GRANTED) {
                ret = true;
            }
        }else
        {
            ret = true;
        }
        return ret;
    }

    // Request a runtime permission to app user.
    private void requestPermission(String permission)
    {
        String requestPermissionArray[] = {permission};
        ActivityCompat.requestPermissions(getActivity(), requestPermissionArray, 1);
    }

    
    @Override
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                       @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == 1) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(),"permission granted", Toast.LENGTH_SHORT).show();  
             // perform your action here
             Log.d("request", "permission granted" );

        } else {
            Toast.makeText(getActivity(),"permission not granted", Toast.LENGTH_SHORT).show();
            Log.d("request", "permission not granted" );
        }
    }

}
    
    
    
   private List<ModelContacts> getContacts() {

        List<ModelContacts> list = new ArrayList<>();

        cursor = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC");
            



        cursor.moveToFirst();


        while (cursor.moveToNext()) {

            try {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                String phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));


                list.add(new ModelContacts(name, phonenumber));

        } catch(Exception e){
            e.printStackTrace();
        }

    }

        cursor.close();
        return list;
}
    @Override
    public void OnContactDetailsFragmentListener(String name, String phoneNumber) {
        Intent intent = new Intent(getContext(), ContactDetailsActivity.class);
        intent.putExtra("CONTACT_NAME", name);
        intent.putExtra("CONTACT_NUMBER", phoneNumber);
        startActivity(intent);
    }

    @Override
    public void OnCallIconClickedListener(String name, String phoneNumber) {
        Intent intent = new Intent(getContext(), CallActivty.class);
        intent.putExtra("CONTACT_NAME", name);
        intent.putExtra("CONTACT_NUMBER", phoneNumber);
        intent.setType("Outgoing");
        startActivity(intent);
    }
}

