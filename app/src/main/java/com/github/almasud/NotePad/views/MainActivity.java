package com.github.almasud.NotePad.views;

import android.os.Build;
import android.os.Bundle;

import com.github.almasud.NotePad.R;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.ArraySet;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.github.almasud.NotePad.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mViewBinding;
    private AppBarConfiguration mAppBarConfiguration;
    private BottomNavigationView mBottomNavigationView;
//    private BottomAppBar mBottomAppBar;
    private NavController mNavController;
    private Set<Integer> mTopLevelDestinations = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        // Setup toolbar as an action bar
        setSupportActionBar(mViewBinding.toolbar);

        // Initialize the bottom navigation view and inflate the menus
        mBottomNavigationView = mViewBinding.bottomNavigation;
//        mBottomNavigationView.inflateMenu(R.menu.menu_bottom_navigation);
        mBottomNavigationView.setBackground(null);
        // Initialize the bottom app bar and replace the menus
//        mBottomAppBar = mViewBinding.bottomAppBar;
//        mBottomAppBar.replaceMenu(R.menu.menu_bottom_navigation);

        // Initialize the navigation controller
        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

        // Add the top level destinations
        mTopLevelDestinations.add(R.id.navCalendar);
        mTopLevelDestinations.add(R.id.navNote);
        mTopLevelDestinations.add(R.id.navHome);
        mTopLevelDestinations.add(R.id.navFavorite);
        mTopLevelDestinations.add(R.id.navSettings);
        // Set the top level destination in app bar
        mAppBarConfiguration = new AppBarConfiguration.Builder(mTopLevelDestinations).build();

        // Setup navigation controller with action bar and bottom navigation view
        NavigationUI.setupActionBarWithNavController(this, mNavController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(mBottomNavigationView, mNavController);
//        NavigationUI.setupWithNavController(mBottomAppBar, mNavController);

        // Set the bottom app bar menu item in navigation controller
//        mBottomAppBar.setOnMenuItemClickListener(item ->
//                NavigationUI.onNavDestinationSelected(item, mNavController)
//        );

        // Listen the navigation destination change and take necessary actions
        mNavController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            // Make uncheckable the first bottom nav menu after click
            mBottomNavigationView.getMenu().getItem(0).setCheckable(true);

            // Set the navigation back icon in action bar for non top level
            // destination fragment
            if (mTopLevelDestinations.contains(destination.getId())) {
                // Hide the toolbar
                mViewBinding.toolbar.setVisibility(View.GONE);
            } else {
                // Show the toolbar and change the back arrow icon
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_ios);
                }
                mViewBinding.toolbar.setVisibility(View.VISIBLE);
            }
        });

        // Make initially uncheckable the first bottom nav menu
        mBottomNavigationView.getMenu().getItem(0).setCheckable(false);

        // Navigate to home screen after clicking on the FAB
        mViewBinding.buttonFloatingAction.setOnClickListener(view -> {
            // Uncheck the bottom nav menus before navigate
            mBottomNavigationView.getMenu().setGroupCheckable(0, true, false);
            for (int i = 0; i < mBottomNavigationView.getMenu().size(); i++) {
                mBottomNavigationView.getMenu().getItem(i).setChecked(false);
            }
            mBottomNavigationView.getMenu().setGroupCheckable(0, true, true);
            // Navigate the the home screen
            mNavController.navigate(R.id.navHome);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return NavigationUI.onNavDestinationSelected(item, mNavController)
                || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(mNavController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}