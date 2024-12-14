package com.auto.pooling;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.auto.pooling.R;
import com.auto.pooling.databinding.ActivityMainPageBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainPageActivity extends AppCompatActivity {
    private ActivityMainPageBinding binding;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainPageBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // POST_NOTIFICATIONS is required from Android 13+
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        101); // Request code can be any integer
                return;
            }
        }
        db.collection("users").document(""+mAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        boolean isDriver = Boolean.TRUE.equals(documentSnapshot.getBoolean("driver"));
                        if(isDriver){
                            binding.bottomtabs.getMenu().clear();
                            binding.bottomtabs.inflateMenu(R.menu.driver_tabs);
                        } else {
                            binding.bottomtabs.getMenu().clear();
                            binding.bottomtabs.inflateMenu(R.menu.tab_items);
                        }
                    } else {
                        Log.d("Document", "No such document exists!");
                    }
                })
                .addOnFailureListener(e -> Log.d("Document", "Failed to fetch document: " + e.getMessage()));
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION}, 101);
//            }
//        }
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.d("FCM", "Fetching FCM registration token failed");
                        return;
                    }
                    // Get the token
                    String token = task.getResult();
                    db.collection("users").document(""+mAuth.getCurrentUser().getUid())
                            .update("fcmToken",token).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("FCM", "FCM Token: " + token);
                                    Toast.makeText(MainPageActivity.this,"Login Successfully",Toast.LENGTH_SHORT).show();
                                }
                            }
                            );
                });
        FirebaseMessaging.getInstance().subscribeToTopic(mAuth.getCurrentUser().getUid())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("FCM", "Subscribed to topic: " + mAuth.getCurrentUser().getUid());
                    } else {
                        Log.d("FCM", "Failed to subscribe to topic: " + mAuth.getCurrentUser().getUid());
                    }
                });
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentview);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(binding.bottomtabs, navController);
            navController.addOnDestinationChangedListener((@NonNull NavController controller, @NonNull NavDestination destination, Bundle arguments) -> {
                if (destination.getId() == R.id.homePageFragment ||
                        destination.getId() == R.id.profilePageFragment || destination.getId() == R.id.bookingsFragment || destination.getId() == R.id.createPoolingFragment) {
                    binding.bottomtabs.setVisibility(View.VISIBLE);
                } else {
                    binding.bottomtabs.setVisibility(View.GONE);
                }
            });
        }
    }
}
