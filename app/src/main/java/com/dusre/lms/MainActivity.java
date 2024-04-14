package com.dusre.lms;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.dusre.lms.R;

import com.dusre.lms.Util.Constants;
import com.dusre.lms.Util.UserPreferences;
import com.dusre.lms.viewmodel.CoursesViewModel;
import com.dusre.lms.viewmodel.DownloadedVideoViewModel;
import com.dusre.lms.viewmodel.LessonsViewModel;
import com.dusre.lms.viewmodel.SectionsViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.dusre.lms.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    public ActivityMainBinding binding;
    private CoursesViewModel coursesViewModel;
    private SectionsViewModel sectionsViewModel;
    private LessonsViewModel lessonsViewModel;
    private DownloadedVideoViewModel downloadedVideoViewModel;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        UserPreferences.init(this);
        if(!UserPreferences.getBoolean(Constants.LOGGED_IN)){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }else {
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            setSupportActionBar(binding.toolbar);


            coursesViewModel = new ViewModelProvider(this).get(CoursesViewModel.class);
            sectionsViewModel = new ViewModelProvider(this).get(SectionsViewModel.class);
            lessonsViewModel = new ViewModelProvider(this).get(LessonsViewModel.class);
            downloadedVideoViewModel = new ViewModelProvider(this).get(DownloadedVideoViewModel.class);


            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(

                    R.id.navigation_my_courses, R.id.navigation_account)
                    .build();
            navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
    @Override
    public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
        if(navDestination.getId()==R.id.navigation_account||navDestination.getId()==R.id.navigation_my_courses){
            binding.navView.setVisibility(View.VISIBLE);
        }
        else{
            binding.navView.setVisibility(View.GONE);
        }
    }
});

            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(binding.navView, navController);
        }
    }
    public CoursesViewModel getCoursesViewModel() {
        return coursesViewModel;
    }


public void disableBottomNav(){
    Log.d("bottom","disable");
    for (int i = 0; i < binding.navView.getMenu().size(); i++) {
        binding.navView.getMenu().getItem(i).setEnabled(false);
    }

}
    public void enableBottomNav(){
        Log.d("bottom","enable");
        for (int i = 0; i < binding.navView.getMenu().size(); i++) {
            binding.navView.getMenu().getItem(i).setEnabled(true);
        }
    }



    @Override
    public boolean onSupportNavigateUp() {

        return navController.navigateUp();

    }
}