package com.dusre.lms;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.android.volley.VolleyError;
import com.dusre.lms.Util.APIClient;
import com.dusre.lms.Util.Constants;
import com.dusre.lms.Util.UserPreferences;
import com.dusre.lms.databinding.FragmentLoginBinding;
import com.dusre.lms.model.Section;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    FragmentLoginBinding binding;
    APIClient apiClient;
    private Gson gson;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        binding = FragmentLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        UserPreferences.init(getApplicationContext());
        apiClient = new APIClient(getApplicationContext());
        gson = new Gson();

        binding.btnLogin.setOnClickListener(v->{
            if(!TextUtils.isEmpty(binding.username.getText().toString()) && !TextUtils.isEmpty(binding.password.getText().toString())) {
                if (checkInternet() != 0) {

                    showPB();
                    login(binding.username.getText().toString(), binding.password.getText().toString());


                } else {
                    Toast.makeText(getApplicationContext(), "No Internet", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(getApplicationContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void showPB() {
        binding.progressBar.setAlpha(1f);
        binding.btnLogin.setEnabled(false);
        binding.btnLogin.setAlpha(0.5f);
        binding.logo.setAlpha(0.5f);
        binding.passwordLayout.setEnabled(false);
        binding.passwordLayout.setAlpha(0.5f);
        binding.usernameLayout.setEnabled(false);
        binding.usernameLayout.setAlpha(0.5f);
        binding.txtview.setAlpha(0.5f);
    }

    private void login(String username, String password ) {


        APIClient.ApiResponseListener listener = new APIClient.ApiResponseListener() {
            @Override
            public void onSuccess(String response) {
                // Handle successful response
                JsonElement jsonElement = JsonParser.parseString(response);
                if(jsonElement.isJsonObject()){
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    if(jsonObject.get("validity").getAsString().equals("0")){
                        Toast.makeText(getApplicationContext(),"Invalid username/password", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        UserPreferences.setBoolean(Constants.LOGGED_IN, true);
                        UserPreferences.setString(Constants.TOKEN, jsonObject.get("token").getAsString());
                        UserPreferences.setString(Constants.USERNAME, jsonObject.get("first_name").getAsString() + " " + jsonObject.get("last_name").getAsString());
//                        hidePB();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                }
                Log.d("API Response", response);
            }

            @Override
            public void onFailure(VolleyError error) {
                // Handle failure
                Log.d("API Response", error.toString());
                hidePB();
                Toast.makeText(getApplicationContext(),"Some error occurred", Toast.LENGTH_SHORT).show();
            }
        };


        Map<String, String> params = new HashMap<>();
        params.put("email", username);
        params.put("password", password);

        apiClient.login(Constants.url+"login", params, listener);

    }

    private void hidePB() {
        binding.progressBar.setAlpha(0f);
        binding.btnLogin.setEnabled(true);
        binding.btnLogin.setAlpha(1f);
        binding.logo.setAlpha(1f);
        binding.passwordLayout.setEnabled(true);
        binding.passwordLayout.setAlpha(1);
        binding.usernameLayout.setEnabled(true);
        binding.usernameLayout.setAlpha(1);
        binding.txtview.setAlpha(1);

    }

    public List<Section> parseJsonToCourseList(String jsonString) {
        Type listType = new TypeToken<List<Section>>(){}.getType();
        return gson.fromJson(jsonString, listType);
    }


    private int checkInternet() {
        int result = 0; // Returns connection type. 0: none; 1: mobile data; 2: wifi
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    result = 2;
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    result = 1;
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                    result = 3;
                }
            }
        }
        return result;


    }

    private class Login {

            public String user_id;
            public String first_name;
            public String last_name;
            public String email;
            public String role;
            public int validity;
            public String token;
        }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
        crashlytics.setCustomKey("Activity", "Login");

    }
}
