package com.dusre.lms.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.dusre.lms.LoginActivity;
import com.dusre.lms.MainActivity;
import com.dusre.lms.R;
import com.dusre.lms.Util.Constants;
import com.dusre.lms.Util.UserPreferences;
import com.dusre.lms.databinding.FragmentAccountBinding;

public class AccountFragment extends Fragment {

    private FragmentAccountBinding binding;
    private NavController navController;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        binding.downloadCoursesLayout.setOnClickListener(v->{
            navController.navigate(R.id.action_navigation_account_to_navigation_downloaded_videos);

        });
        binding.ivLogout.setOnClickListener(v->{
            UserPreferences.setBoolean(Constants.LOGGED_IN, false);
            UserPreferences.setString(Constants.TOKEN, "");
            UserPreferences.setString(Constants.USERNAME, "");
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();

        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}