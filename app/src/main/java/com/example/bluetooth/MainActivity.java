package com.example.bluetooth;

import android.os.Bundle;
import android.widget.Toast;

import com.example.bluetooth.service.bt.BtClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_server, R.id.navigation_client, R.id.navigation_peripheral, R.id.navigation_central)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);


        // android default bottom bar, fragment is recreated when switching between
        // use distinct view ID, or refactor will change views with same ID

        // request permission
        APP.requestPermission(this);

        // enable bluetooth
        if (!BtClient.enableAdapter()) {
            Toast.makeText(this, getString(R.string.blue_no_adaptor), Toast.LENGTH_SHORT);
        }

    }

}
