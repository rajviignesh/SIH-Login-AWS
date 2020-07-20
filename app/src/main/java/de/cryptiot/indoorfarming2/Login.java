/*
 * Developed by Keivan Kiyanfar on 19/7/20 5:31 AM
 * Last modified 18/7/20 7:38 PM
 * Copyright (c) 2020. All rights reserved.
 */

package de.cryptiot.indoorfarming2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import de.cryptiot.indoorfarming.R;


public class Login extends AppCompatActivity {
    // ############################################################# View Components
    TextView txtNotAccount;     // For creating account
    //TextView txtForgetPass;     // For retrieving password
    Button btnLogin;            // Button for Login
    EditText etUsername;
    EditText etPassword;
    RadioGroup radioGroup;
    Button caretakee;
    Button caretaker;
    int flag = 1;

    private ProgressDialog mProgress;
    // ############################################################# End View Components

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#000000"));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Login");
        initViewComponents();
        mProgress = new ProgressDialog(Login.this);
        mProgress.setTitle("Processing...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);
    }

    private void initViewComponents(){
        txtNotAccount = findViewById(R.id.txtNotAccount);
        //txtForgetPass= findViewById(R.id.txtForgetPass);
        btnLogin = findViewById(R.id.btnLogin);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);

        radioGroup = findViewById(R.id.Selection);
        caretakee = findViewById(R.id.Caretakee);
        caretaker = findViewById(R.id.Caretaker);
        radioGroup.clearCheck();




        txtNotAccount.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, Signup.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cognito authentication = new Cognito(getApplicationContext());
                authentication.userLogin(etUsername.getText().toString().replace(" ", ""), etPassword.getText().toString());
                mProgress.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {

                        Cognito autha = new Cognito(getApplicationContext());
                        String auth = autha.getAuth();
                        Log.d("Auth = ",auth);

                        if(auth.equals("ss")) {

                            int selectedId = radioGroup.getCheckedRadioButtonId();
                            if (selectedId == -1) {
                                flag = 1;
                            }
                            else {

                                RadioButton radioButton
                                        = (RadioButton)radioGroup
                                        .findViewById(selectedId);

                                String sel = radioButton.getText().toString();
                                if(sel.equals("Caretakee")){
                                    flag =0;
                                }

                                // Now display the value of selected item
                                // by the Toast message

                            }
                            if(flag == 1){
                                Intent intent = new Intent(Login.this, AppDashBoard2.class);
                                startActivity(intent);
                            }

                            else{
                                Intent intent = new Intent(Login.this, Caretakee.class);
                                startActivity(intent);
                            }

                        }

                         // yourMethod();
                    }
                }, 1500);
                mProgress.dismiss();
            }
        });
    }
}
