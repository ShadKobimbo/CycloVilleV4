package com.example.cyclovillev4;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Objects;

/**
    * Created by Shad Kobimbo on 7/4/2019.
        */

public class LoginFragment extends Fragment {

    SharedPreferences sp;

    private static final String TAG = "Login Fragment";
    public static final String SHARED_PREF_NAME = "userdetails";
    public static final String firstNameKey = "firstNameKey";
    public static final String secondNameKey = "secondNameKey";
    public static final String emailKey = "emailKey";
    public static final String userNameKey = "userNameKey";

    EditText etEmail, etPassword;
    Button btnSignIn;

    static String json = "";
    String email, passwrd;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_login, container, false);
        etEmail = v.findViewById(R.id.etDescription);
        etPassword = v.findViewById(R.id.etPassword);
        btnSignIn = v.findViewById(R.id.btnSignIn);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //make your toast here
                signIn();
            }
        });
        return v;
    }

    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {

            e.printStackTrace();
        }
        return false;
    }

    public void signIn() {

        if( TextUtils.isEmpty(etEmail.getText())){

            Toast.makeText(getActivity().getApplicationContext(), "Enter Username Or Email", Toast.LENGTH_LONG).show();


            etEmail.setError( "Username Or Email is Required" );

        } else if( TextUtils.isEmpty(etPassword.getText())){

            Toast.makeText(getActivity().getApplicationContext(), "Enter Password", Toast.LENGTH_LONG).show();


            etPassword.setError( "Password is Required" );

        } else {

            email = etEmail.getText().toString();
            passwrd = etPassword.getText().toString();

            Toast.makeText(getActivity(), "Attempting Login",Toast.LENGTH_SHORT).show();

            new AttemptLogin().execute();

        }

    }

    private class AttemptLogin extends AsyncTask<String, String, String> {

        HttpURLConnection conn;
        URL url = null;

        @Override

        protected void onPreExecute() {

            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... args) {

            try {

                JSONObject postDataParams = new JSONObject();

                postDataParams.put("username", email);
                postDataParams.put("password", passwrd);

                Log.e("params",postDataParams.toString());

                json = ServerConnection.makeHttpRequest(postDataParams);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return json;

        }

        protected void onPostExecute(String result) {

            Log.e("Result", result);

            try {

                if (result != null) {

                    Log.e("Result", result);

                    JSONObject json_data = new JSONObject(result);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),json_data.getString("message"),Toast.LENGTH_LONG).show();
                    }

                    String codeString = json_data.getString("success");
                    Integer codeInt = Integer.parseInt(codeString);

                    if (codeInt == 1){

                        String firstname = json_data.getString("firstname");
                        String secondname = json_data.getString("secondname");
                        String email = json_data.getString("email");
                        String username = json_data.getString("username");

                        Log.e("SP Data", firstname + secondname);

                        sp = getActivity().getApplicationContext().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();

                        editor.putString(firstNameKey, firstname);
                        editor.putString(secondNameKey, secondname);
                        editor.putString(emailKey, email);
                        editor.putString(userNameKey, username);

                        editor.apply();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent i = new Intent(getActivity(), MainActivity.class);
                                //Intent is used to switch from one activity to another.

                                getActivity().startActivity(i);
                                //invoke the SecondActivity.

                            }
                        }, 1500);


                    }

                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Unable to retrieve any data from server", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }


        }

    }

}