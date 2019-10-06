package watermelon.watchblock.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import watermelon.watchblock.R;

public class ProfileActivity extends AppCompatActivity
{

    SharedPreferences sharedpreferences;
    TextView name_field;
    TextView email_field;
    TextView phoneNumber_field;
    public static final String NAME = "nameKey";
    public static final String EMAIL = "emailKey";
    public static final String PHONE_NUMBER = "phoneKey";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        sharedpreferences = getSharedPreferences("mainprefs", Context.MODE_PRIVATE);

        name_field = findViewById(R.id.nameField);
        email_field = findViewById(R.id.emailField);
        phoneNumber_field = findViewById(R.id.phoneField);

        if (sharedpreferences.contains(NAME)) {
            name_field.setText(sharedpreferences.getString(NAME, ""));
        }
        if (sharedpreferences.contains(EMAIL)) {
            email_field.setText(sharedpreferences.getString(EMAIL, ""));
        }
        if (sharedpreferences.contains(PHONE_NUMBER)) {
            phoneNumber_field.setText(sharedpreferences.getString(PHONE_NUMBER, ""));
        }


        final Button saveButton = findViewById(R.id.saveProfile);
        saveButton.setEnabled(false);

        name_field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                saveButton.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        email_field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                saveButton.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        phoneNumber_field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                saveButton.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name =  name_field.getText().toString();
                String email = email_field.getText().toString();
                String phoneNumber = phoneNumber_field.getText().toString();

                SharedPreferences.Editor editor = sharedpreferences.edit();

                editor.putString(NAME, name);
                editor.putString(EMAIL, email);
                editor.putString(PHONE_NUMBER, phoneNumber);
                editor.apply();
                editor.commit();
                finish();
            }
        });

        Button cancelButton = findViewById(R.id.cancelProfile);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

}
