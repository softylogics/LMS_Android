package com.dusre.lms;

import android.os.Bundle;

import com.dusre.lms.viewmodel.CoursesViewModel;
import com.dusre.lms.viewmodel.DownloadedVideoViewModel;
import com.dusre.lms.viewmodel.LessonsViewModel;
import com.dusre.lms.viewmodel.SectionsViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.dusre.lms.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private CoursesViewModel coursesViewModel;
    private SectionsViewModel sectionsViewModel;
    private LessonsViewModel lessonsViewModel;
    private DownloadedVideoViewModel downloadedVideoViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        coursesViewModel = new ViewModelProvider(this).get(CoursesViewModel.class);
        sectionsViewModel = new ViewModelProvider(this).get(SectionsViewModel.class);
        lessonsViewModel = new ViewModelProvider(this).get(LessonsViewModel.class);
        downloadedVideoViewModel = new ViewModelProvider(this).get(DownloadedVideoViewModel.class);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_my_courses, R.id.navigation_account)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }
    public CoursesViewModel getCoursesViewModel() {
        return coursesViewModel;
    }
}