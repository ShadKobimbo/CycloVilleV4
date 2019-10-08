package com.example.cyclovillev4;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import android.content.DialogInterface;

import static android.app.AlertDialog.*;


/**
    * Created by Shad Kobimbo on 7/4/2019.
        */

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView recycler;
    MyRecyclerViewAdapter adapter;

    ArrayList<Product> products = new ArrayList<>();

    SharedPreferences sp;
    public static final String SHARED_PREF_NAME = "userdetails";

    static String json = "";
    String userName;

    DrawerLayout drawer;
    ConstraintLayout content;

    public MainActivity() {

        //download();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        download();

        // set up the RecyclerView
        recycler = findViewById(R.id.rvData);
        recycler.setLayoutManager(new GridLayoutManager(this, 2));
        //adapter.setClickListener((MyRecyclerViewAdapter.ItemClickListener) this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        content = (ConstraintLayout) findViewById(R.id.content_main);

        adapter = new MyRecyclerViewAdapter(MainActivity.this,products);
        recycler.setAdapter(adapter);

    }

    public void download() {

        sp = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        userName = sp.getString("userNameKey", null);

        new AttemptLoad().execute();

        Log.d("Frag Report",json);

    }

    @Override
    public void onBackPressed() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            // Handle the camera action
            Intent i = new Intent(this, ProfileActivity.class);
            this.startActivity(i);

        }  else if (id == R.id.nav_upload) {

            Intent i = new Intent(this, NewProductActivity.class);
            this.startActivity(i);

        } else if (id == R.id.nav_home) {
            Intent i = new Intent(this, MainActivity.class);
            this.startActivity(i);

        } else if (id == R.id.nav_logout) {

            Intent i = new Intent(this, SplashActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            this.startActivity(i);
            this.finish();
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onItemClick(View view, int position) {
        //Toast.makeText(this, "You clicked " + adapter.getItem(position) + " product number " + position, Toast.LENGTH_SHORT).show();
    }

    private class AttemptLoad extends AsyncTask<String, String, String> {

        @Override

        protected void onPreExecute() {

            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... args) {

            try {

                JSONObject postDataParams = new JSONObject();

                postDataParams.put("username", userName);

                Log.e("params",postDataParams.toString());

                json = ServerConnection.makeHttpRequest(postDataParams);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return json;

        }

        protected void onPostExecute(String result) {

            Log.e("Result 1", result);

            try {

                if (result != null) {

                    Log.e("Result 2", result);

                    JSONObject json_data = new JSONObject(result);

                    String codeString = json_data.getString("success");
                    Integer codeInt = Integer.parseInt(codeString);

                    Integer codeGood = 1;

                    Log.e("Code Int", String.valueOf(codeInt));

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        Toast.makeText(getApplicationContext(),json_data.getString("message"),Toast.LENGTH_LONG).show();
                    }

                    if (codeInt == codeGood){

                        String dataString = json_data.getString("data");
                        Log.e("Data", dataString);

                        populate(dataString);

                    } else {

                        customDialog("UPLOAD NEW",json_data.getString("message") + ". \n\n\nAdd one now?", "cancel","upload");

                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Unable to retrieve any data from server", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }


        }

    }

    public void populate(String result) {

        try {

            JSONArray jArray = new JSONArray(result);

            Log.e("jArray", jArray.toString());

            for (int i = 0; i < jArray.length(); i++) {

                JSONObject json_data2 = jArray.getJSONObject(i);

                products.add(new Product(
                        json_data2.getString("product_id"),
                        json_data2.getString("product_name"),
                        json_data2.getString("product_price"),
                        json_data2.getString("quantity"),
                        json_data2.getString("description"),
                        json_data2.getString("category"),
                        json_data2.getString("image")

                ));

                System.out.println(json_data2);

            }

            System.out.println(String.valueOf(products));

            adapter = new MyRecyclerViewAdapter(MainActivity.this,products);
            recycler.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void customDialog(String title, String message, final String cancelMethod, final String okMethod){
        final android.support.v7.app.AlertDialog.Builder builderSingle = new android.support.v7.app.AlertDialog.Builder(this);
        builderSingle.setIcon(R.mipmap.ic_notification);
        builderSingle.setTitle(title);
        builderSingle.setMessage(message);

        builderSingle.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(cancelMethod.equals("cancel")){
                            cancel();
                        }

                    }
                });

        builderSingle.setPositiveButton(
                "UPLOAD NEW",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(okMethod.equals("upload")){
                            upload();
                        }
                    }
                });


        builderSingle.show();
    }

    private void cancel(){
        Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_LONG).show();
    }
    private void upload(){

        Intent i = new Intent(getApplicationContext(), NewProductActivity.class);
        //Intent is used to switch from one activity to another.

        startActivity(i);
        //invoke the SecondActivity.

    }

}
