package com.example.hairsalon.activity.employee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.hairsalon.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeEmployee extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_employee);
        bottomNavigationView = findViewById(R.id.bottomNavBarEmployee);
        frameLayout = findViewById(R.id.frameLayoutEmployee);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemID = item.getItemId();
                if( itemID == R.id.navScheduleEm){
                    loadFragment(new OrderListEmployeeFragment(),false);
                } else {
                    loadFragment(new AccountEmployeeFragment(), false);
                }
                return true;
            }
        });
        loadFragment(new OrderListEmployeeFragment(), true);
    }


    private void loadFragment(Fragment fragment, boolean isAppInitialized) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if(isAppInitialized){
            fragmentTransaction.add(R.id.frameLayoutEmployee,fragment);
        }
        else {
            fragmentTransaction.replace(R.id.frameLayoutEmployee, fragment);
        }
        fragmentTransaction.commit();
    }
}