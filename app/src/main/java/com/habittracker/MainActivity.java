package com.habittracker;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

        if (savedInstanceState == null) {
            loadFragment(new TodayFragment());
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment;
            int id = item.getItemId();
            if (id == R.id.nav_today) {
                fragment = new TodayFragment();
            } else if (id == R.id.nav_habits) {
                fragment = new HabitsFragment();
            } else if (id == R.id.nav_activities) {
                fragment = PlaceholderFragment.newInstance("Attività");
            } else if (id == R.id.nav_categories) {
                fragment = PlaceholderFragment.newInstance("Categorie");
            } else {
                fragment = PlaceholderFragment.newInstance("Timer");
            }
            return loadFragment(fragment);
        });
    }

    private boolean loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
        return true;
    }
}
