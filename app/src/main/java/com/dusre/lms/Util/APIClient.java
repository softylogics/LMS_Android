package com.dusre.lms.Util;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import android.net.Uri;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class APIClient {
    private RequestQueue requestQueue;
    private Context context;
    int requestTimeoutMs = 30000; // Timeout duration in milliseconds (30 seconds)
    int maxRetries = 3; // Maximum number of retry attempts
    float backoffMultiplier = 2.0f; // Backoff multiplier (2.0 means exponential backoff)

    RetryPolicy customRetryPolicy = new DefaultRetryPolicy(
            requestTimeoutMs,
            maxRetries,
            backoffMultiplier
    );

    public APIClient(Context context) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
    }

    public interface ApiResponseListener {
        void onSuccess(String response);
        void onFailure(VolleyError error);
    }

    public void fetchDataFromApi(String apiUrl, Map<String, String> params, ApiResponseListener listener, String tag) {
        String formattedUrl = formatUrl(apiUrl, params);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, formattedUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        listener.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onFailure(error);
            }


        });
        stringRequest.setTag(tag);
        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

        requestQueue.add(stringRequest);
    }
    public void saveDownloadProgress(String apiUrl, Map<String, String> params, ApiResponseListener listener) {



        // Create a StringRequest with POST method
        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle successful response
                        listener.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error response
                listener.onFailure(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                // Add any necessary headers here
                return headers;
            }
        };
        stringRequest.setRetryPolicy(customRetryPolicy);
        // Add the request to the Volley request queue
        requestQueue.add(stringRequest);
    }


    public void updateServerForDownload(String apiUrl, Map<String, String> params, ApiResponseListener listener, String tag){
        String formattedUrl = formatUrl(apiUrl, params);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, formattedUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        listener.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onFailure(error);
            }


        });
        stringRequest.setTag(tag);
        requestQueue.add(stringRequest);
    }

    public void login(String apiUrl, Map<String, String> params, ApiResponseListener listener) {
        String formattedUrl = formatUrl(apiUrl, params);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, formattedUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        listener.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onFailure(error);
            }


        });

        requestQueue.add(stringRequest);
    }


    private String formatUrl(String baseUrl, Map<String, String> params) {
        Uri.Builder builder = Uri.parse(baseUrl).buildUpon();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.appendQueryParameter(entry.getKey(), entry.getValue());
        }
        return builder.build().toString();
    }

    public void cancelRequest(String tag){
        requestQueue.cancelAll(tag);
    }

    public static String getProgressDisplayLine(long currentBytes, long totalBytes) {
        return getBytesToMBString(currentBytes) + "/" + getBytesToMBString(totalBytes);
    }

    private static String getBytesToMBString(long bytes) {
        return String.format(Locale.ENGLISH, "%.2fMb", bytes / (1024.00 * 1024.00));
    }
}
