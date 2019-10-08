package com.example.cyclovillev4;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class NewProductActivity extends AppCompatActivity {

    EditText etProductName, etProductPrice, etQuantity, etCategory, etDescription;
    Button btnUpload;

    ImageView imageView;

    static String json = "";
    String productname, productprice, quantity, category, description, username;

    String imageId, imageUrl, imageName, imagePrice, imageQuantity, imageCategory, imageDescription;

    SharedPreferences sp;
    public static final String SHARED_PREF_NAME = "userdetails";

    public static final String UPLOAD_URL = "http://shiftadmin.tamshi.co.ke/cyclovilleserver/upload.php";
    public static final String UPLOAD_KEY = "image";

    private Uri filePath;
    private Bitmap bitmap;
    private ImageView imageViewMain;

    private int PICK_IMAGE_REQUEST = 101;
    String encodedImage, imageResult;
    boolean update = false;

    JSONObject imageDataParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);

        getIntents();

        imageViewMain = findViewById(R.id.imageView);
        etProductName = findViewById(R.id.etProductName);
        etProductPrice = findViewById(R.id.etProductPrice);
        etQuantity = findViewById(R.id.etQuantity);
        etDescription = findViewById(R.id.etDescription);
        etCategory = findViewById(R.id.etCategory);
        btnUpload = (Button) findViewById(R.id.btnUpload);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        checkState();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(view.getContext(), MainActivity.class);
                //Intent is used to switch from one activity to another.

                startActivity(i);
                //invoke the SecondActivity.

                finish();
                //the current activity will get finished.

            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //make your toast here
                if (bitmap != null){

                    upload();

                } else {
                    Toast.makeText(getApplicationContext(),"Please Select An Image",Toast.LENGTH_LONG).show();
                }

            }
        });

        imageViewMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {

                filePath = data.getData();
                Log.e("Uri Data", filePath.toString());

                bitmap = null;

                try {

                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                    imageViewMain.setImageBitmap(bitmap);

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);

                    byte[] imageBytes = byteArrayOutputStream.toByteArray();
                    encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            }
        }
    }

    public void getIntents(){

        imageId = getIntent().getStringExtra("image_id");
        imageName = getIntent().getStringExtra("image_name");
        imagePrice = getIntent().getStringExtra("image_price");
        imageQuantity = getIntent().getStringExtra("image_quantity");
        imageCategory = getIntent().getStringExtra("image_category");
        imageDescription = getIntent().getStringExtra("image_description");

    }

    public void checkState(){

        if(imageName != null && imagePrice != null && imageQuantity != null && imageCategory != null && imageDescription != null){

            etProductName.setText(imageName);
            etProductPrice.setText(imagePrice);
            etQuantity.setText(imageQuantity);
            etCategory.setText(imageCategory);
            etDescription.setText(imageDescription);

            update = true;

        }

    }

    public void upload() {

        if( TextUtils.isEmpty(etProductName.getText())){

            etProductName.setError( "Enter Product Name" );

        } else if( TextUtils.isEmpty(etProductPrice.getText())){

            etProductPrice.setError( "Enter Product Price" );

        } else if( TextUtils.isEmpty(etQuantity.getText())){

            etQuantity.setError( "Enter Item Quantity" );

        } else if( TextUtils.isEmpty(etCategory.getText())){

            etCategory.setError( "Enter Item Category" );

        } else if( TextUtils.isEmpty(etDescription.getText())){

            etDescription.setError( "Enter Item Description" );

        } else {

            productname = etProductName.getText().toString();
            productprice = etProductPrice.getText().toString();
            quantity = etQuantity.getText().toString();
            category = etCategory.getText().toString();
            description = etDescription.getText().toString();

            sp = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            username = sp.getString("userNameKey", null);

            imageDataParams = new JSONObject();
            try {

                imageDataParams.put("imageName", productname);
                imageDataParams.put("image", encodedImage);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Toast.makeText(this, "Attempting Upload",Toast.LENGTH_SHORT).show();

            new AttemptUpload().execute();

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

    private class AttemptUpload extends AsyncTask<String, String, String> {

        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... args) {

            try {
                imageResult = ImageUploadConnection.makeUploadRequest(imageDataParams);
                Log.e("Image URL", imageResult);

                if(imageResult != null){

                    JSONObject postDataParams = new JSONObject();

                    postDataParams.put("product_name", productname);
                    postDataParams.put("product_price", productprice);
                    postDataParams.put("quantity", quantity);
                    postDataParams.put("category", category);
                    postDataParams.put("description", description);
                    postDataParams.put("imageurl", imageResult);


                    if (!update){

                        postDataParams.put("vendor", username);

                    } else {


                        postDataParams.put("product_id", imageId);
                    }


                    Log.e("params",postDataParams.toString());

                    json = ServerConnection.makeHttpRequest(postDataParams);
                    Log.e("Image URL", "Its DOne");

                } else {
                    Toast.makeText(getApplicationContext(), "Image Upload Error", Toast.LENGTH_SHORT).show();
                }

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

                    String codeString = json_data.getString("success");

                    Integer codeInt = Integer.parseInt(codeString);

                    Integer codeGood = 1;

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
            }


        }

    }
}
