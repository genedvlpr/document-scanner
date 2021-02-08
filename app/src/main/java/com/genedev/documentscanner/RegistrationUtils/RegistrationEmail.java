package com.genedev.documentscanner.RegistrationUtils;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.genedev.documentscanner.HelperUtil;
import com.genedev.documentscanner.Login;
import com.genedev.documentscanner.R;
import com.google.android.material.textfield.TextInputLayout;

public class RegistrationEmail extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextInputLayout email, pass, con_pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_email);

        progressBar = findViewById(R.id.progressBar);

        email = findViewById(R.id.tl1);
        pass = findViewById(R.id.tl2);
        con_pass = findViewById(R.id.tl3);
    }

    public void registerEmail(View v){
        HelperUtil.registerUser(this,RegistrationName.class,email,pass,con_pass,progressBar,progressBar);
    }

    @Override
    public void onBackPressed(){
        //super.onBackPressed();
        HelperUtil.backNavigationFinish(this, Login.class);
    }
}
