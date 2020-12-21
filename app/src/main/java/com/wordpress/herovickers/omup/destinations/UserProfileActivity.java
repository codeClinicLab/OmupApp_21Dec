package com.wordpress.herovickers.omup.destinations;

import android.Manifest;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.wordpress.herovickers.omup.R;
import com.wordpress.herovickers.omup.Repository.FirestoreRepository;
import com.wordpress.herovickers.omup.authentication.WelcomeActivity;
import com.wordpress.herovickers.omup.models.User;
import com.wordpress.herovickers.omup.utility.PrefsManager;
import com.wordpress.herovickers.omup.utility.Utils;
import com.wordpress.herovickers.omup.viewmodel.FirestoreViewModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.READ_CONTACTS;
import static com.wordpress.herovickers.omup.utility.Utils.CAMERA;
import static com.wordpress.herovickers.omup.utility.Utils.GALLERY;

public class UserProfileActivity extends AppCompatActivity {
    private String path = null;
    private TextView firstName;
    private TextView lastName;
    private TextView email;
    private TextView phoneNumber;
    private TextView country;
    private TextView toolbarTitle;
    private ImageView backArrow;
    private CircleImageView profilImage;
    private Button saveChanges;
    private Boolean saveStatus = false;
    private StorageReference storageReference;
    private ImageView selectPhoto;
    private FirebaseUser mUser;
    private PrefsManager manager;
    private final int MY_PERMISSON_REQUEST = 984;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTheme().applyStyle(R.style.AppThemeWithTitleBar, true);
        setContentView(R.layout.activity_user_profile);
        setTitle("");

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        manager = new PrefsManager(this);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        toolbarTitle = findViewById(R.id.toolbar_title);
        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        email = findViewById(R.id.email);
        phoneNumber = findViewById(R.id.phone_number);
        country = findViewById(R.id.country);
        profilImage = findViewById(R.id.photo_view);
        backArrow = findViewById(R.id.back_btn);
        saveChanges = findViewById(R.id.btn_save);
        selectPhoto = findViewById(R.id.select_picture);
        setButtonStatus(saveStatus);

        storageReference = FirebaseStorage.getInstance().getReference();
        //Disable button Initially
        //Set text of toolbar title to be empty
        toolbarTitle.setText("My Profile");
        setUserData();
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Navigate back to the previous activity
                NavigateToPreviousActivity();
            }
        });
        selectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestAllPermission();

            }
        });
        firstName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO create a dialog to edit user first name and update as necessary. DO it for all other profile fields too
                //name of the dialog layout is dia_edit_profile_field
                editFieldDaialog("First Name", firstName.getText().toString(), firstName);

            }
        });
        lastName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO create a dialog to edit user first name and update as necessary. DO it for all other profile fields too
                //name of the dialog layout is dia_edit_profile_field
                editFieldDaialog("Last Name", lastName.getText().toString(), lastName);

            }
        });
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO create a dialog to edit user first name and update as necessary. DO it for all other profile fields too
                //name of the dialog layout is dia_edit_profile_field
                editFieldDaialog("Email", email.getText().toString(), email);

            }
        });
        phoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Do another phoneNumber verification/This is quite tricky, come back later

            }
        });
        country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO create a dialog to edit user first name and update as necessary. DO it for all other profile fields too
                //name of the dialog layout is dia_edit_profile_field
                editFieldDaialog("Country", country.getText().toString(), country);

            }
        });
        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //disable button after updating
                if (saveStatus){
                    if (path != null){
                        UploadUserInformation(path, mUser.getUid());
                    }else {
                        saveInfoToDatabase(null, null);
                    }
                }
            }
        });
    }

    private void setButtonStatus(boolean b) {
        if (b){
            saveChanges.setBackgroundResource(R.drawable.round_button_blue);
            saveChanges.setTextColor(getResources().getColor(R.color.white));
        }else{
            saveChanges.setBackgroundResource(R.drawable.round_button_white);
            saveChanges.setTextColor(getResources().getColor(R.color.black));
        }

    }

    private void editFieldDaialog(String name, String value, final TextView field){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dia_edit_profile_field, null);
        builder.setView(view);
        TextView fieldName = view.findViewById(R.id.field_name);
        final EditText fieldValue = view.findViewById(R.id.field_value);
        Button cancel = view.findViewById(R.id.btn_cancel);
        Button okay = view.findViewById(R.id.btn_okay);
        fieldName.setText(name);
        fieldValue.setText(value);
        final AlertDialog dialog = builder.create();
        dialog.show();
        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                field.setText(fieldValue.getText().toString());
                //Enable button after edit
                saveStatus = true;
                setButtonStatus(saveStatus);
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void setUserData() {
        FirestoreViewModel firestoreViewModel = ViewModelProviders.of(this).get(FirestoreViewModel.class);
        LiveData<User> userLiveData = firestoreViewModel.getUserData();
        userLiveData.observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                firstName.setText(user.getFirstName());
                lastName.setText(user.getLastName());
                email.setText(user.getEmail());
                phoneNumber.setText(user.getPhoneNumber());
                country.setText(user.getCountry());
                if (!user.getProfileUrl().isEmpty()){
                    Picasso.get().load(user.getProfileUrl()).into(profilImage);
                }
            }
        });
    }
    private void NavigateToPreviousActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sign_out){
            SignOut();
        }
        return super.onOptionsItemSelected(item);
    }

    private void SignOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(UserProfileActivity.this, WelcomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                            this.getContentResolver(), contentURI);
                    profilImage.setImageBitmap(bitmap);
//                    path = ProfileImageUtils.saveImage(bitmap, this);
                    new saveImageTask().execute(bitmap);
                    setButtonStatus(true);
                    saveStatus = true;

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(UserProfileActivity.this,
                            "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            profilImage.setImageBitmap(thumbnail);
            new saveImageTask().execute(thumbnail);
            setButtonStatus(true);
            saveStatus = true;
        }
    }

    class saveImageTask extends AsyncTask<Bitmap, Void, String> {

        @Override
        protected String doInBackground(Bitmap... bitmaps) {
            Bitmap bitmap = bitmaps[0];
            String filePath = Utils.saveImage(bitmap
                    , UserProfileActivity.this);
            return filePath;
        }

        @Override
        protected void onPostExecute(String s) {
            path = s;
            super.onPostExecute(s);
        }
    }
    public void UploadUserInformation(final String path, final String userId) {
        //This creates the path where the image will be saved on cloud storage
        final StorageReference ref = storageReference.child(
                "images/" + userId + ".jpg");
        if (path != null) {
            /*disableAllViews();
            progressDialog.startDeterminate();
            progressDialog.setPercent(63);
            progressDialog.setVisibility(View.VISIBLE);*/
            Uri file = Uri.fromFile(new File(path));
            UploadTask uploadTask = ref.putFile(file);

            Task<Uri> urlTask = uploadTask.continueWithTask(
                    new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(
                                @NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            // Continue with the task to get the download URL
                            return ref.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        saveInfoToDatabase(null, downloadUri);
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });
        }
    }
    /*public void uploadThumbnail(Bitmap bitmap,
                                String userId,
                                final Uri downloadUri ){
        //progressDialog.setPercent(82);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        final StorageReference ref = storageReference.child(
                "images/" + userId + "_thumbnail.jpg");
        UploadTask uploadTask = ref.putBytes(data);
        Task<Uri> urlTask = uploadTask.continueWithTask(
                new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(
                            @NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return ref.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {

                    Uri thumbnailUri = task.getResult();
                    saveInfoToDatabase(thumbnailUri, downloadUri);
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }*/
    private void saveInfoToDatabase(final Uri thumbnailUri,
                                    final Uri downloadUri ) {
        //progressDialog.setPercent(99);
        //TODO save thumbnail if necessary
        //user.setThumbnail(String.valueOf(thumbnailUri));
        Map<String, Object> userData = new HashMap<>();
        userData.put("firstName",firstName.getText().toString());
        userData.put("lastName",lastName.getText().toString());
        userData.put("country",country.getText().toString());
        userData.put("email",email.getText().toString());
        //Update user data in SharedPreferences
        final User updateUser = manager.getUserData();
        updateUser.setFirstName(firstName.getText().toString());
        updateUser.setLastName(lastName.getText().toString());
        updateUser.setCountry(country.getText().toString());
        updateUser.setEmail(email.getText().toString());
        if (downloadUri != null){
            userData.put("profileUrl", String.valueOf(downloadUri));
            updateUser.setProfileUrl(String.valueOf(downloadUri));
        }

        FirestoreViewModel firestoreViewModel =
                ViewModelProviders.of(UserProfileActivity.this).get(FirestoreViewModel.class);
        LiveData<Boolean> booleanLiveData = firestoreViewModel.updateUserProfile(userData);
        booleanLiveData.observe(UserProfileActivity.this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean){
                    saveStatus = false;
                    setButtonStatus(saveStatus);
                    manager.saveUserData(updateUser);
                    Toast.makeText(UserProfileActivity.this, "Success",
                            Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(UserProfileActivity.this, "Try again",
                            Toast.LENGTH_SHORT).show();
                    saveStatus = true;
                    setButtonStatus(saveStatus);
                }
            }
        });
    }
    public void requestAllPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED |
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ){
                // Permission is not granted
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_CONTACTS)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                } else {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSON_REQUEST);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }else {
                //Permission already granted
                Utils.showPictureDialog(UserProfileActivity.this);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if (requestCode == MY_PERMISSON_REQUEST) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
                Utils.showPictureDialog(UserProfileActivity.this);
            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                Toast.makeText(this,
                        "All Permission needs to be granted",
                        Toast.LENGTH_SHORT).show();
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}
