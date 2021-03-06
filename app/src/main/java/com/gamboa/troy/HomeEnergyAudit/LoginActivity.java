package com.gamboa.troy.HomeEnergyAudit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Troygbv on 2/1/17.
 */

public class LoginActivity extends AppCompatActivity{

    EditText etUsername, etPassword;
    Button btnLogin, btnRegister, btnAbout;
    Toolbar loginToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //custom login toolbar
        loginToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(loginToolbar);
        getSupportActionBar().setTitle(getString(R.string.please_login));
        loginToolbar.setTitleTextColor(Color.WHITE);

        //editTexts (set the keyboard to go away when user touches outside of keyboard)
         etUsername = (EditText) findViewById(R.id.ETusername);
         etPassword = (EditText) findViewById(R.id.ETpassword);

        etUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        etPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        //Buttons
         btnLogin = (Button) findViewById(R.id.BTsubmit);
         btnRegister = (Button) findViewById(R.id.BTregister);
         btnAbout = (Button) findViewById(R.id.btAbout);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openRegister = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(openRegister);
            }
        });


        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openAbout = new Intent(LoginActivity.this, AboutActivity.class);
                startActivity(openAbout);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = etUsername.getText().toString();
                final String password = etPassword.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //Call JSON "response" as shown in PHP API
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            //if user is registered, go to Main Activity, and put Extra string "username" for welcome message
                            if (success){
                                String userID = jsonResponse.getString("user_id");
                                String username = jsonResponse.getString("username");
                                String pass = jsonResponse.getString("password");
                                String email = jsonResponse.getString("email");


                                Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
                                loginIntent.putExtra("userID", userID);
                                loginIntent.putExtra("username", username);
                                loginIntent.putExtra("password", pass);
                                loginIntent.putExtra("email", email);
                                //toast if successful login
                                Toast loginSuccess = Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT);
                                loginSuccess.show();
                                startActivity(loginIntent);
                            }

                            //if there is no connection to db
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage("Invalid Login Credentials.")
                                        .setNegativeButton("Retry", null)
                                        .create()
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                };

                LoginRequest loginRequest = new LoginRequest(username, password, responseListener);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);
            }
        });


    }


//method to hide keyboard
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        etUsername.setText("");
        etPassword.setText("");
    }

    @Override
    public void onBackPressed() {
    }
}
