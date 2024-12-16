package com.auto.pooling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.auto.pooling.databinding.ActivitySignUpBinding;
import com.auto.pooling.databinding.DialogBoxBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.signUpBtn.setVisibility(View.GONE);
                if(!binding.emailEdtTxt.getText().toString().isEmpty() && binding.emailEdtTxt.getText().toString().contains("@gmail.com") && !binding.passwordEdtTxt.getText().toString().isEmpty()){
                    auth.createUserWithEmailAndPassword(binding.emailEdtTxt.getText().toString(), binding.passwordEdtTxt.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    FirebaseUser user = auth.getCurrentUser();
                                    db = FirebaseFirestore.getInstance();
                                    Map<String, Object> users = new HashMap<>();
                                    users.put("uid", user.getUid() + "");
                                    users.put("name", binding.nameEdtTxt.getText().toString());
                                    users.put("email", binding.emailEdtTxt.getText().toString());
                                    users.put("driver", false);

                                    // Update the FirebaseAuth display name
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(binding.nameEdtTxt.getText().toString()) // Set the display name to the name entered
                                            .build();

                                    user.updateProfile(profileUpdates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        db.collection("users").document(user.getUid()).set(users, SetOptions.merge())
                                                                .addOnSuccessListener(a -> {
                                                                    binding.progressBar.setVisibility(View.GONE);
                                                                    binding.signUpBtn.setVisibility(View.VISIBLE);
                                                                    Toast.makeText(getApplicationContext(), "User Created Successfully. Please Sign In and Continue", Toast.LENGTH_SHORT).show();
                                                                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                                                    finishAffinity();
                                                                })
                                                                .addOnFailureListener(e -> {
                                                                    Toast.makeText(getApplicationContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                    binding.progressBar.setVisibility(View.GONE);
                                                                    binding.signUpBtn.setVisibility(View.VISIBLE);
                                                                });
                                                    } else {
                                                        // Handle the case when profile update fails
                                                        binding.progressBar.setVisibility(View.GONE);
                                                        binding.signUpBtn.setVisibility(View.VISIBLE);
                                                        Toast.makeText(getApplicationContext(), "Failed to update profile: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    binding.progressBar.setVisibility(View.GONE);
                                    binding.signUpBtn.setVisibility(View.VISIBLE);
                                    Toast.makeText(SignUpActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                                    Toast.makeText(SignUpActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                else if(binding.nameEdtTxt.getText().toString().isEmpty()){
                    Toast.makeText(SignUpActivity.this,"Enter Your Name",Toast.LENGTH_SHORT).show();
                    binding.progressBar.setVisibility(View.GONE);
                    binding.signUpBtn.setVisibility(View.VISIBLE);

                }
                else if(binding.emailEdtTxt.getText().toString().isEmpty() || !binding.emailEdtTxt.getText().toString().contains("@gmail.com")){
                    Toast.makeText(SignUpActivity.this,"Enter Your Email Correctly",Toast.LENGTH_SHORT).show();
                    binding.progressBar.setVisibility(View.GONE);
                    binding.signUpBtn.setVisibility(View.VISIBLE);
                }
                else{
                    Toast.makeText(SignUpActivity.this,"Enter Password",Toast.LENGTH_SHORT).show();
                    binding.progressBar.setVisibility(View.GONE);
                    binding.signUpBtn.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}