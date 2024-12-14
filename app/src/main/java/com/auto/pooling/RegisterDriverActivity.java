package com.auto.pooling;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.auto.pooling.databinding.ActivityRegisterDriverBinding;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class RegisterDriverActivity extends AppCompatActivity {

    private ActivityRegisterDriverBinding binding;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private ActivityResultLauncher<Intent> pickImageLauncher;

    private Uri imageUri;

    private String documentUrl;

    private String numberPlate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterDriverBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        documentUrl = intent.getStringExtra("documentUrl");
        numberPlate = intent.getStringExtra("numberPlate");

        binding.numberPlateTxt.setText(numberPlate);
        Glide.with(RegisterDriverActivity.this)
                .load(documentUrl) // Load the profile image URL
                .into(binding.fileImage);

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            imageUri = result.getData().getData();
                            documentUrl = "";
                            binding.fileImage.setImageURI(imageUri);
                        }
                        else{
                            Toast.makeText(RegisterDriverActivity.this,"Choose Correct File",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        binding.uploadFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });

        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.submitBtn.setVisibility(View.GONE);
                if((imageUri != null || !documentUrl.isEmpty()) && !binding.numberPlateTxt.getText().toString().trim().isEmpty()){
                    if(documentUrl.isEmpty() && imageUri != null){
                        uploadImageToStorage(imageUri,binding.numberPlateTxt.getText().toString().trim());
                    }
                    else{
                        updateDriverDetails(documentUrl,binding.numberPlateTxt.getText().toString().trim());
                    }
                }
                else if(imageUri == null){
                    binding.progressBar.setVisibility(View.GONE);
                    binding.submitBtn.setVisibility(View.VISIBLE);
                    Toast.makeText(RegisterDriverActivity.this,"Licence Should Uploaded",Toast.LENGTH_SHORT).show();
                }
                else{
                    binding.progressBar.setVisibility(View.GONE);
                    binding.submitBtn.setVisibility(View.VISIBLE);
                    Toast.makeText(RegisterDriverActivity.this,"Enter Registered Number Plate",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    private void uploadImageToStorage(Uri imageUri,String numberPlate) {
        if (imageUri != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference profilePicRef = storageRef.child("licence/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg");

            profilePicRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        profilePicRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            Toast.makeText(RegisterDriverActivity.this,"Uploaded On FireBase",Toast.LENGTH_SHORT).show();
//                            updateProfilePicture(uri.toString());
                            updateDriverDetails(uri.toString(),numberPlate);
                        });
                    })
                    .addOnFailureListener(e -> {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.submitBtn.setVisibility(View.VISIBLE);
                        Toast.makeText(RegisterDriverActivity.this,"Failed To Uploaded On FireBase",Toast.LENGTH_SHORT).show();
                        Toast.makeText(RegisterDriverActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                        Log.e("ImageUpload", "Failed to upload image", e);
                    });
        }
    }

    private void updateDriverDetails(String documentUrl,String numberPlate) {
        Map<String, String> details = new HashMap<>();
        details.put("documentUrl", documentUrl);
        details.put("numberPlate", numberPlate);
        db.collection("users").document(auth.getCurrentUser().getUid()).update(
                "driver", true,
                "details", details // Add the details object to the array
        ).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                binding.progressBar.setVisibility(View.GONE);
                binding.submitBtn.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Updated As Drived", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}