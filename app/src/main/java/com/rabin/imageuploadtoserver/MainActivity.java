package com.rabin.imageuploadtoserver;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button uploadBtn, chooseBtn, loadImages;
    private EditText NAME;
    private ImageView imgView;
    private final int IMG_REQUEST = 1;
    private Bitmap bitmap;
    private String uploadUrl = "http://192.168.0.107:8080/image/upload/img";
    private RequestQueue requestQueue;
//..
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uploadBtn = findViewById(R.id.uploadBtn);
        chooseBtn = findViewById(R.id.chooseBtn);
        loadImages = findViewById(R.id.loadImages);
        NAME = findViewById(R.id.name);
        imgView = findViewById(R.id.imageView);

        //setting action for button
        chooseBtn.setOnClickListener(this);
        uploadBtn.setOnClickListener(this);
        loadImages.setOnClickListener(this);
        requestQueue = Volley.newRequestQueue(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chooseBtn:
                selectImage();
                break;

            case R.id.uploadBtn:
                uploadImage();
                break;

            case R.id.loadImages:
                startActivity(new Intent(MainActivity.this, LoadImages.class));
                break;

        }
    }

    // method for uploading the image
    private void uploadImage() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                uploadUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("success")) {
                    Toast.makeText(MainActivity.this, "Image Uploaded Successfully",
                            Toast.LENGTH_LONG).show();
                    imgView.setVisibility(View.GONE);
                    NAME.setVisibility(View.GONE);
                } else if (response.equals("failure")) {
                    Toast.makeText(MainActivity.this, "Something Went Wrong",
                            Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Server Down or NetWork Error:",
                        Toast.LENGTH_LONG).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", NAME.getText().toString().trim());
                params.put("image", imageToString(bitmap));

                return params;
            }
        };
        //add Stringrequest to requestqueue
        requestQueue.add(stringRequest);

    }

    // method to convert image to String
    private String imageToString(Bitmap bitmap) {
        //converting bitmap into String
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        //compress bitmap into jpg format
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream);
        // convert bytearrayoutputstream to byte
        byte[] imgByres = byteArrayOutputStream.toByteArray();
        //encode byte into streams
        //return bitmap into string value
        return Base64.encodeToString(imgByres, Base64.DEFAULT);
    }

    // select image from gallery if user click choose button
    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMG_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMG_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                imgView.setImageBitmap(bitmap);
                imgView.setVisibility(View.VISIBLE);
                NAME.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}