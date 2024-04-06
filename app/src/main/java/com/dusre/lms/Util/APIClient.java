package com.dusre.lms.Util;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import android.content.Context;
import android.net.Uri;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Locale;
import java.util.Map;

public class APIClient {
    private RequestQueue requestQueue;
    private Context context;

    public APIClient(Context context) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
    }

    public interface ApiResponseListener {
        void onSuccess(String response);
        void onFailure(VolleyError error);
    }

    public void fetchDataFromApi(String apiUrl, Map<String, String> params, ApiResponseListener listener) {
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


    public void updateServerForDownload(String apiUrl, Map<String, String> params, ApiResponseListener listener){

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

    public static String getProgressDisplayLine(long currentBytes, long totalBytes) {
        return getBytesToMBString(currentBytes) + "/" + getBytesToMBString(totalBytes);
    }

    private static String getBytesToMBString(long bytes) {
        return String.format(Locale.ENGLISH, "%.2fMb", bytes / (1024.00 * 1024.00));
    }
}
