package com.example.donona;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.donona.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Init auth instance
        mAuth = FirebaseAuth.getInstance();

//        binding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        BottomNavigationView navView = findViewById(R.id.nav_view);
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
////        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration); // Remove action bar
//        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    @Override
    public void onStart() {
        super.onStart();

//        Log.v("Test", "Helloworld");
//        temporarily sign user out every time re run emulator
//        mAuth.signOut();
//        FirebaseUser user = mAuth.getCurrentUser();
//
//        // Direct user to home page if user is sign in, else to login/signup
//        if (user != null) {
//            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
//            intent.putExtra("userID", user.getUid());
//            startActivity(intent);
//            return;
//        }
//        startActivity(new Intent(MainActivity.this, LoginActivity.class));
//        return;

        startActivity(new Intent(MainActivity.this, HomeActivity.class));
        return;
    }
}