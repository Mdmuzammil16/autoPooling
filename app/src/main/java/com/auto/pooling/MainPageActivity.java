package com.auto.pooling;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.auto.pooling.R;
import com.auto.pooling.databinding.ActivityMainPageBinding;

public class MainPageActivity extends AppCompatActivity {
    private ActivityMainPageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentview);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(binding.bottomtabs, navController);
            navController.addOnDestinationChangedListener((@NonNull NavController controller, @NonNull androidx.navigation.NavDestination destination, Bundle arguments) -> {
                if (destination.getId() == R.id.homePageFragment ||
                        destination.getId() == R.id.profilePageFragment ) {
                    binding.bottomtabs.setVisibility(View.VISIBLE);
                } else {
                    binding.bottomtabs.setVisibility(View.GONE);
                }
            });
        }
    }
}
