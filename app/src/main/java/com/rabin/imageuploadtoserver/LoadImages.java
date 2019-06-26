package com.rabin.imageuploadtoserver;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LoadImages extends AppCompatActivity {

    private RequestQueue requestQueue;
    private String uploadUrl = "http://192.168.0.107:8080/image/images";
    private String loadImages = "http://192.168.0.107:8080/image/displayImage";
    private List<String> imagesId = new ArrayList<>();
    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_images);
        imageView = findViewById(R.id.loadImg);

        requestQueue = Volley.newRequestQueue(this);
//
        loadImages();


        Picasso.with(getApplicationContext()).
                load(loadImages).into(imageView);
    }

    private void loadImages() {
        JsonRequest jsonRequest = new JsonArrayRequest(Request.Method.GET, uploadUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("Response", response.toString());
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        System.out.println("Images name: " + jsonObject.getString("path"));
                        imagesId.add(jsonObject.getString("id"));


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonRequest);
    }
}
