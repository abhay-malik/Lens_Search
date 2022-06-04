package com.example.lens_search;

import static androidx.recyclerview.widget.LinearLayoutManager.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private RecyclerView recyclerView;
    private Button btn_snap,btn_search;
    private String title, link, displayed_link, snippet;

    private searchRVAdapter searchRVAdapter;
    private ArrayList<searchRVModal> searchRVModalArrayList;
    int REQUEST_CODE=1;
    ProgressBar pb;
    private Bitmap imgBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.image);
        recyclerView = findViewById(R.id.rv_search);
        btn_snap = findViewById(R.id.btn_snap);
        btn_search = findViewById(R.id.btn_search);
        pb = findViewById(R.id.pb_load);

        searchRVModalArrayList = new ArrayList<>();
        searchRVAdapter = new searchRVAdapter(this,searchRVModalArrayList);

        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, HORIZONTAL,false));
        recyclerView.setAdapter(searchRVAdapter);

        btn_snap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchRVModalArrayList.clear();
                searchRVAdapter.notifyDataSetChanged();
                takePictureIntent();
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchRVModalArrayList.clear();
                searchRVAdapter.notifyDataSetChanged();
                pb.setVisibility(View.VISIBLE);
                getResults();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            imgBitmap = (Bitmap)extras.get("data");
            imageView.setImageBitmap(imgBitmap);
        }

    }

    private  void getResults() {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(imgBitmap);
        FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance().getOnDeviceImageLabeler();

        labeler.processImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
            @Override
            public void onSuccess(List<FirebaseVisionImageLabel> firebaseVisionImageLabels) {
                String searchQuery = firebaseVisionImageLabels.get(0).getText();
                getSearchResults(searchQuery);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,"Failed to detect Image!",Toast.LENGTH_SHORT);
            }
        });
    }

    private void getSearchResults(String searchQuery) {
        String apiKey = "de85524bcb6c962c657b24e1feb4beebdd5e268cb6833e114977410d96db5269";
        String url = "https://serpapi.com/search.json?q=" + searchQuery.trim() + "&location=Delhi,India&hl=en&gl=us&google_domain=google.com&api_key=" + apiKey;

        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pb.setVisibility(View.GONE);
                try {
                    // on below line we are extracting data from our json.
                    JSONArray organicResultsArray = response.getJSONArray("organic_results");
                    for (int i = 0; i < organicResultsArray.length(); i++) {
                        JSONObject organicObj = organicResultsArray.getJSONObject(i);
                        if (organicObj.has("title")) {
                            title = organicObj.getString("title");
                        }
                        if (organicObj.has("link")) {
                            link = organicObj.getString("link");
                        }
                        if (organicObj.has("displayed_link")) {
                            displayed_link = organicObj.getString("displayed_link");
                        }
                        if (organicObj.has("snippet")) {
                            snippet = organicObj.getString("snippet");
                        }
                        // on below line we are adding data to our array list.
                        searchRVModalArrayList.add(new searchRVModal(title, link, displayed_link, snippet));
                    }
                    // notifying our adapter class
                    // on data change in array list.
                    searchRVAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // displaying error message.
                Toast.makeText(MainActivity.this, "No Result found for the search query..", Toast.LENGTH_SHORT).show();
            }
        });
        // adding json object request to our queue.
        queue.add(jsonObjectRequest);

    }
    private void takePictureIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(intent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(intent,REQUEST_CODE);
        }
    }
}