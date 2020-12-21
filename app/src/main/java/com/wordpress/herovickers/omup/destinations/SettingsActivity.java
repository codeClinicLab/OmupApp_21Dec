package com.wordpress.herovickers.omup.destinations;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wordpress.herovickers.omup.R;

public class SettingsActivity extends AppCompatActivity {

    private ImageView backArrow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        TextView toolBarTitle = findViewById(R.id.toolbar_title);
        toolBarTitle.setText("Settings");

        backArrow = findViewById(R.id.back_btn);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Navigate back to the previous activity
                NavigateToPreviousActivity();
            }
        });

        //Todo handle multiple button clicks better
        //Todo change where clicking log out leads to
    }
    private void NavigateToPreviousActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
