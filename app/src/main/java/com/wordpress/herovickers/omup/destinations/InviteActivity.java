package com.wordpress.herovickers.omup.destinations;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.wordpress.herovickers.omup.R;

public class InviteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getTheme().applyStyle(R.style.AppThemeWithTitleBar, true);
        setContentView(R.layout.activity_invite);

    }
}
