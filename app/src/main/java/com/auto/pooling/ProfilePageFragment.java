package com.auto.pooling;

import static androidx.core.app.ActivityCompat.finishAffinity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.auto.pooling.databinding.FragmentProfilePageBinding;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


public class ProfilePageFragment extends Fragment {

    private FragmentProfilePageBinding binding;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private GoogleSignInClient googleSignInClient;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfilePageBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.nameTxt.setText(mAuth.getCurrentUser().getDisplayName());
        binding.emailTxt.setText(mAuth.getCurrentUser().getEmail());
        Glide.with(requireContext())
                .load(mAuth.getCurrentUser().getPhotoUrl())
                .placeholder(R.drawable.example_image)
                .error(R.drawable.example_image)
                .into(binding.userImage);

        binding.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "Hey! You can Create An Auto Pooling With Your Family and Friends. Download Our App Now. \n  http://play.google.com/store/apps/details?id=com.auto.pooling";
                onWhatsAppMessageButtonClicked(message);
            }
        });
        binding.logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                googleSignInClient = GoogleSignIn.getClient(requireContext(), GoogleSignInOptions.DEFAULT_SIGN_IN);

                googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            mAuth.signOut();
                            Intent i = new Intent(requireContext(), LoginActivity.class);
                            startActivity(i);
                            finishAffinity(requireActivity());
                        }
                    }
              });
            }
        });
    }


    private void onWhatsAppMessageButtonClicked(String message) {
        String sendUriString = "https://api.whatsapp.com/send?text=" + Uri.encode(message); // Use encode to ensure special characters are handled
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(sendUriString));
        startActivity(intent);
    }

}