package com.dara.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;
    private ImageView profileImage;
    private Uri imageUri;
    private FirebaseAuth mAuth;
    private SharedViewModel sharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        // UI Components
        TextView userName = findViewById(R.id.user_name);
        TextView userEmail = findViewById(R.id.user_email);
        TextView abaId = findViewById(R.id.aba_id);
        profileImage = findViewById(R.id.profile_image_view);

        if (currentUser != null) {
            userName.setText(currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "New User");
            userEmail.setText(currentUser.getEmail());
            abaId.setText("UID: " + currentUser.getUid().substring(0, 8) + "...");
        }

        // Action Buttons
        Button editProfileBtn = findViewById(R.id.btn_edit_profile);
        Button settingsBtn = findViewById(R.id.btn_settings);
        Button logoutBtn = findViewById(R.id.btn_logout);
        Button changeLangBtn = findViewById(R.id.btn_change_language);

        editProfileBtn.setOnClickListener(v -> 
            openGallery()
        );

        changeLangBtn.setOnClickListener(v -> {
            toggleLanguage();
        });

        settingsBtn.setOnClickListener(v -> 
            startActivity(new Intent(ProfileActivity.this, SettingsActivity.class))
        );

        logoutBtn.setOnClickListener(v -> {
            // Clear local data before logging out
            sharedViewModel.clearLocalData();
            
            mAuth.signOut();
            Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            profileImage.setImageURI(imageUri);
            Toast.makeText(this, "Profile Picture Updated!", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleLanguage() {
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        String lang = prefs.getString("My_Lang", "en");
        
        if (lang.equals("en")) {
            setLocale("km");
        } else {
            setLocale("en");
        }
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();

        // Restart activity to apply changes
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
