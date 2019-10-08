package com.example.cyclovillev4;

import com.example.cyclovillev4.R;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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
import java.net.URL;
import java.util.ArrayList;


/**
    * Created by Shad Kobimbo on 7/4/2019.
            */

public class RegFragment extends Fragment {

    private static final String TAG = "Registration Fragment";

    EditText etEmail, etUserName, etFirstName, etSecondName, etPhone, etPassword, etPasswordConfirm;
    Button btnReg;

    static String json = "";
    static String email, username, firstName, secondName, password, phone, passwordConfirm;

    public RegFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_reg, container, false);
        etEmail = v.findViewById(R.id.etEmail);
        etUserName = v.findViewById(R.id.etUserName);
        etFirstName = v.findViewById(R.id.etFirstName);
        etSecondName = v.findViewById(R.id.etSecondName);
        etPassword = v.findViewById(R.id.etPassword);
        etPasswordConfirm = v.findViewById(R.id.etPasswordConfirm);
        etPhone = v.findViewById(R.id.etPhone);
        btnReg = (Button) v.findViewById(R.id.btnReg);

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //make your toast here
                register();
            }
        });

        return v;
    }

    public void register() {

        if( TextUtils.isEmpty(etFirstName.getText())){

            etFirstName.setError( "Enter First Name" );

        } else if( TextUtils.isEmpty(etSecondName.getText())){

            etSecondName.setError( "Enter Second Name" );

        } else if( TextUtils.isEmpty(etUserName.getText())){

            etUserName.setError( "Enter Username" );

        } else if( TextUtils.isEmpty(etEmail.getText())){

            etEmail.setError( "Enter Email Address" );

        } else if( TextUtils.isEmpty(etPhone.getText())) {

            etPhone.setError("Enter Phone Number");
        } else {

            email = etEmail.getText().toString();
            username = etUserName.getText().toString();
            firstName = etFirstName.getText().toString();
            secondName = etSecondName.getText().toString();
            password = etPassword.getText().toString();
            phone = etPhone.getText().toString();
            passwordConfirm = etPasswordConfirm.getText().toString();

            if (!password.equals(passwordConfirm)){

                etPasswordConfirm.setError("Password Does Not Match");

            } else {
                etPasswordConfirm.setError(null);

                Toast.makeText(getActivity(), "Attempting Registration",Toast.LENGTH_SHORT).show();

                new AttemptReg().execute();
            }

        }

    }

    private class AttemptReg extends AsyncTask<String, String, String> {

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

                postDataParams.put("email", email);
                postDataParams.put("username", username);
                postDataParams.put("firstname", firstName);
                postDataParams.put("secondname", secondName);
                postDataParams.put("phone", phone);
                postDataParams.put("password", password);


                Log.e("params",postDataParams.toString());

                json = ServerConnection.makeHttpRequest(postDataParams);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return json;
        }

        protected void onPostExecute(String result) {

            // dismiss the dialog once product deleted
            //Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();

            try {
                if (result != null) {

                    JSONObject json_data = new JSONObject(result);

                    Toast.makeText(getActivity().getApplicationContext(),json_data.getString("message"),Toast.LENGTH_LONG).show();

                    String codeString = json_data.getString("success");

                    Integer codeInt = Integer.parseInt(codeString);

                    Integer codeGood = 1;

                    if (codeInt == codeGood){

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                            Intent i = new Intent(getActivity().getApplicationContext(), LandingActivity.class);
                            //Intent is used to switch from one activity to another.

                            startActivity(i);
                            //invoke the SecondActivity.

                            }
                        }, 1000);

                    }

                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Unable to retrieve any data from server", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }

}
