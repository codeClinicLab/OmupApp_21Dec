package com.wordpress.herovickers.omup.destinations;

import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.wordpress.herovickers.omup.R;

public class AddNewContactActivity extends AppCompatActivity {

    private TextView toolbarTitle;
    private ImageView backArrow;
    private String selectedCountry;
    private String[] codeList;
    private EditText contactName;
    private EditText contactNumber;
    private final int CONTACT_CODE = 6344;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTheme().applyStyle(R.style.AppThemeWithTitleBar, true);
        setContentView(R.layout.activity_add_new_contact);

        toolbarTitle = findViewById(R.id.toolbar_title);
        //Set text of toolbar title to be empty
        toolbarTitle.setText("Create New Contact");
        contactName = findViewById(R.id.ed_contact_name);
        contactNumber = findViewById(R.id.ed_contact_number);
        backArrow = findViewById(R.id.back_btn);
        codeList = getResources().getStringArray(R.array.country_code);
        setUpSpinner();
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Navigate back to the previous activity
                NavigateToPreviousActivity();
            }
        });
        Button createContact = findViewById(R.id.action_create);
        createContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchContactApp();
            }
        });
    }

    private void launchContactApp() {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        intent.putExtra(ContactsContract.Intents.Insert.NAME, contactName.getText().toString());
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, selectedCountry+contactNumber.getText().toString());
        intent.putExtra("finishActivityOnSaveCompleted", true);
        //TODO use start activity for result instead
        startActivityForResult(intent, CONTACT_CODE);
        //TODO Refresh the contact fragment when contacts has been saved successfully
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONTACT_CODE){
            if (resultCode == RESULT_OK){
                finish();
            }
        }
    }

    private void setUpSpinner() {
        selectedCountry = codeList[0];
        SpinnerAdapter adapter = ArrayAdapter.createFromResource(this,
                R.array.country_code, android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = findViewById(R.id.country_code);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 selectedCountry = codeList[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void NavigateToPreviousActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
