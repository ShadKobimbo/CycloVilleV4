package com.example.cyclovillev4;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

public class GalleryViewActivity extends AppCompatActivity {

    private static final String TAG = "GalleryActivity";

    String imageId, imageUrl, imageName, imagePrice, imageQuantity, imageCategory, imageDescription;
    String delete = "delete";
    static String json = "";

    String command = "";
    Button btnDelete, btnEdit;

    public Context mCtx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_view);

        getIncomingIntent();

        btnDelete = (Button)findViewById(R.id.deleteBtn);
        btnEdit = (Button)findViewById(R.id.editBtn);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //make your toast here

                customDialog("Delete","Are you sure you want to delete this record?", "cancel","delete");

            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //make your toast here
                Intent intent = new Intent(GalleryViewActivity.this, NewProductActivity.class);
                intent.putExtra("image_id", imageId);
                intent.putExtra("image_name", imageName);
                intent.putExtra("image_price", imagePrice);
                intent.putExtra("image_quantity", imageQuantity);
                intent.putExtra("image_category", imageCategory);
                intent.putExtra("image_description", imageDescription);

                startActivity(intent);
                finish();
            }
        });

    }

    private void getIncomingIntent(){
        Log.d(TAG, "getIncomingIntent: checking for incoming intents.");

        if(getIntent().hasExtra("image_url") && getIntent().hasExtra("image_name")){
            Log.d(TAG, "getIncomingIntent: found intent extras.");

            imageId = getIntent().getStringExtra("image_id");
            imageUrl = getIntent().getStringExtra("image_url");
            imageName = getIntent().getStringExtra("image_name");
            imagePrice = getIntent().getStringExtra("image_price");
            imageQuantity = getIntent().getStringExtra("image_quantity");
            imageCategory = getIntent().getStringExtra("image_category");
            imageDescription = getIntent().getStringExtra("image_description");

            setImage(imageUrl, imageName, imagePrice, imageQuantity, imageCategory, imageDescription);
        }
    }


    private void setImage(String imageUrl, String imageName, String imagePrice, String imageQuantity, String imageCategory, String imageDescription){
        Log.d(TAG, "setImage: setting te image and name to widgets.");

        TextView name = findViewById(R.id.image_name);
        TextView price = findViewById(R.id.image_price);
        TextView quantity = findViewById(R.id.image_quantity);
        TextView category = findViewById(R.id.image_category);
        TextView description = findViewById(R.id.image_description);

        name.setText(imageName);
        price.setText(imagePrice);
        quantity.setText(imageQuantity);
        category.setText(imageCategory);
        description.setText(imageDescription);


        ImageView image = findViewById(R.id.image);
        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(image);
    }

    public class AttempAlter extends AsyncTask<String, String, String> {

        @Override

        protected void onPreExecute() {

            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... args) {

            try {

                JSONObject postDataParams = new JSONObject();

                postDataParams.put("product_id", imageId);
                postDataParams.put("delete", delete);

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

                    if (codeInt == codeGood){

                        Toast.makeText(getApplicationContext(),json_data.getString("message"),Toast.LENGTH_LONG).show();

                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        //Intent is used to switch from one activity to another.

                        startActivity(i);
                        //invoke the SecondActivity.

                        finish();
                        //the current activity will get finished.
                    } else {

                        Toast.makeText(getApplicationContext(), json_data.getString("message"), Toast.LENGTH_LONG).show();

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
                "DELETE",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(okMethod.equals("delete")){
                            delete();
                        }
                    }
                });


        builderSingle.show();
    }

    private void cancel(){
        Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_LONG).show();
    }
    private void delete(){

        new AttempAlter().execute();

    }

}
