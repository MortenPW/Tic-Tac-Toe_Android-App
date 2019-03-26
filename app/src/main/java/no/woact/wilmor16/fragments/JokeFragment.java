package no.woact.wilmor16.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import no.woact.wilmor16.R;
import no.woact.wilmor16.network.QueueSingleton;

/**
 * Created by Morten
 */

public class JokeFragment extends Fragment {

    private Button btn_joke;
    private TextView mTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_joke, container, false);

        mTextView = view.findViewById(R.id.joke);
        mTextView.setText("");

        // Button joke
        btn_joke = view.findViewById(R.id.btn_joke);
        btn_joke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestJoke();
            }
        });

        requestJoke();

        return view;
    }

    // Request joke by external API
    public void requestJoke() {
        String url = "https://icanhazdadjoke.com/";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            mTextView.setText(response.getString("joke"));
                        } catch (JSONException e) {
                            // Just ignore if it goes wrong- the joke's not that important
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError e) {
                        e.printStackTrace();
                    }
                }) {

            // Replace headers
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("User-agent", "Tic Tac Toe - wilmor16@student.westerdals.no");
                return headers;
            }
        };
        // Access the RequestQueue through singleton class
        QueueSingleton.getInstance(this.getContext()).addToRequestQueue(jsonObjectRequest, getContext());
    }
}
