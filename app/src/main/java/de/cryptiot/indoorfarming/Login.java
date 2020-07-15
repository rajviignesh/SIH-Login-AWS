/*
 * Developed by Keivan Kiyanfar on 10/9/18 11:38 PM
 * Last modified 10/9/18 11:38 PM
 * Copyright (c) 2018. All rights reserved.
 */

package de.cryptiot.indoorfarming;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;



public class Login extends AppCompatActivity {
    // ############################################################# View Components
    TextView txtNotAccount;     // For creating account
    //TextView txtForgetPass;     // For retrieving password
    Button btnLogin;            // Button for Login
    EditText etUsername;
    EditText etPassword;
    private ProgressDialog mProgress;
    // ############################################################# End View Components

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

                            Intent intent = new Intent(Login.this, AppDashboard.class);
                            startActivity(intent);
                        }

                         // yourMethod();
                    }
                }, 1500);
                mProgress.dismiss();
            }
        });
    }
}
