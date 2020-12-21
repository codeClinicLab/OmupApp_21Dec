package com.wordpress.herovickers.omup.utility;

import android.content.Context;
import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wordpress.herovickers.omup.R;

public class KeyboardView extends FrameLayout implements View.OnClickListener {

    private EditText mCallField;

    public KeyboardView(Context context) {
        super(context);
        init();
    }

    public KeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public KeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.keyboard, this);
        initViews();
    }

    private void initViews() {
        mCallField = $(R.id.call_field);
        $(R.id.t9_key_0).setOnClickListener(this);
        $(R.id.t9_key_1).setOnClickListener(this);
        $(R.id.t9_key_2).setOnClickListener(this);
        $(R.id.t9_key_3).setOnClickListener(this);
        $(R.id.t9_key_4).setOnClickListener(this);
        $(R.id.t9_key_5).setOnClickListener(this);
        $(R.id.t9_key_6).setOnClickListener(this);
        $(R.id.t9_key_7).setOnClickListener(this);
        $(R.id.t9_key_8).setOnClickListener(this);
        $(R.id.t9_key_9).setOnClickListener(this);
        //$(R.id.t9_key_clear).setOnClickListener(this);
        //TODO no longer a text view, mow an ImageView
        $(R.id.t9_key_backspace).setOnClickListener(this);
        $(R.id.t9_key_astericks).setOnClickListener(this);
        $(R.id.t9_key_hash).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        // handle number button click
        if (v.getTag() != null && "number_button".equals(v.getTag())) {
            mCallField.append(((TextView) v).getText());
            return;
        }
        /*case R.id.t9_key_clear: { // handle clear button
                mCallField.setText(null);
            }
            break;*/
        // handle backspace button
        // delete one character
        if (v.getId() == R.id.t9_key_backspace) {
            Editable editable = mCallField.getText();
            int charCount = editable.length();
            if (charCount > 0) {
                editable.delete(charCount - 1, charCount);
            }
        }
    }

    public String getInputText() {
        return mCallField.getText().toString();
    }

    protected <T extends View> T $(@IdRes int id) {
        return (T) super.findViewById(id);
    }
}

