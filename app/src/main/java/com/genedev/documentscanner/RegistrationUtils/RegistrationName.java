package com.genedev.documentscanner.RegistrationUtils;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.genedev.documentscanner.HelperUtil;
import com.genedev.documentscanner.HomeUtils.HomeRegUser;
import com.genedev.documentscanner.R;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class RegistrationName extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextInputLayout fName, mName, lName;
    private Spinner spinner_dept;
    private String selected_department;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_name);

        progressBar = findViewById(R.id.progressBar);
        fName = findViewById(R.id.tl1);
        mName = findViewById(R.id.tl2);
        lName = findViewById(R.id.tl3);
        spinner_dept = findViewById(R.id.spinner_dept);

        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add("DIT");
        spinnerArray.add("DOM");
        spinnerArray.add("DAS");
        spinnerArray.add("TED");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_dept.setAdapter(adapter);
    }

    public void registerName(View v){
        selected_department = spinner_dept.getSelectedItem().toString();
        HelperUtil.registerName(this, HomeRegUser.class,fName,mName,lName,selected_department,progressBar,progressBar);
    }

    @Override
    public void onBackPressed(){
        //super.onBackPressed();
        HelperUtil.backNavigationWithAlert(this, "Alert","You need to finish and fill up the necessary informations, when all fields are filled up, click Finish");
    }
}
