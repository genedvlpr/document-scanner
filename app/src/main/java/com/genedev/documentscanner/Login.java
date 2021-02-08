package com.genedev.documentscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.genedev.documentscanner.HomeUtils.HomeGuest;
import com.genedev.documentscanner.HomeUtils.HomeRegUser;
import com.genedev.documentscanner.RegistrationUtils.RegistrationEmail;
import com.google.android.material.textfield.TextInputLayout;

public class Login extends AppCompatActivity {

    public HelperUtil helperUtil;
    private TextInputLayout tl1,tl2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tl1 = findViewById(R.id.tl1);
        tl2 = findViewById(R.id.tl2);

        HelperUtil.firebaseInitialization(this);
    }

    public void login(View v){
        HelperUtil.authentication(this, HomeRegUser.class,tl1,tl2);
    }

    public void register(View v){
        HelperUtil.nextActivity(this, RegistrationEmail.class);
    }

    public void guest(View v){
        HelperUtil.nextActivity(this, HomeGuest.class);
    }

}
