package com.example.instagram.ui.profile;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log; // Add this import for logging
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;
import com.example.instagram.Model.UserModel;
import com.example.instagram.PostActivity;
import com.example.instagram.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity"; // Tag for logging

    private ImageView close, imageProfile;
    private Uri imageUri;
    private String myUrl = "";
    private TextView save, avatarChange;
    private com.google.android.material.textfield.TextInputEditText username, bio;
    private ProgressBar progressBar;
    private FirebaseUser firebaseUser;
    private Uri mImageUri;
    private StorageTask<UploadTask.TaskSnapshot> uploadTask;
    private StorageReference storageRef;
    private final ActivityResultLauncher<CropImageContractOptions> cropImageLauncher = registerForActivityResult(
            new CropImageContract(), result -> {
                if (result.isSuccessful()) {
                    imageUri = result.getUriContent();
                    Log.d(TAG, "Image cropped successfully: " + imageUri);
                    imageProfile.setImageURI(imageUri);
                } else {
                    Exception exception = result.getError();
                    Log.e(TAG, "Crop failed: " + exception.getMessage());
                    Toast.makeText(EditProfileActivity.this, "Crop failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        close = findViewById(R.id.close);
        imageProfile = findViewById(R.id.image_editprofile);
        save = findViewById(R.id.save);
        avatarChange = findViewById(R.id.changeAvatar);
        username = findViewById(R.id.editUsername);
        bio = findViewById(R.id.editBio);
        progressBar = findViewById(R.id.indeterminateBar);

        // Log the firebase user status
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            Log.d(TAG, "Current User ID: " + firebaseUser.getUid());
        } else {
            Log.e(TAG, "FirebaseUser is null");
        }

        storageRef = FirebaseStorage.getInstance().getReference("uploads");

        // Fetch user details from Firebase
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel user = dataSnapshot.getValue(UserModel.class);
                if (user != null) {
                    Log.d(TAG, "Retrieved User ID: " + firebaseUser.getUid());
                    Log.d(TAG, "User Data: Username - " + user.getUsername() + ", Bio - " + user.getBio());
                    username.setText(user.getUsername());
                    bio.setText(user.getBio());
                    Glide.with(getApplicationContext()).load(user.getImageUrl()).into(imageProfile);
                } else {
                    Log.e(TAG, "UserModel is null for User ID: " + firebaseUser.getUid());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to load user data: " + databaseError.getMessage());
                Toast.makeText(EditProfileActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });

        close.setOnClickListener(view -> {
            Log.d(TAG, "Close button clicked, finishing activity");
            finish();
        });

        save.setOnClickListener(view -> {
            Log.d(TAG, "Save button clicked");
            updateProfile(username.getText().toString(), bio.getText().toString());
        });

        avatarChange.setOnClickListener(view -> {
            Log.d(TAG, "Avatar change button clicked");
            startCrop();
        });

        imageProfile.setOnClickListener(view -> {
            Log.d(TAG, "Profile image clicked for cropping");
            startCrop();
        });
    }

    private void startCrop() {
        Log.d(TAG, "Starting image crop");
        CropImageOptions options = new CropImageOptions();
        options.guidelines = CropImageView.Guidelines.ON;
        cropImageLauncher.launch(new CropImageContractOptions(null, options));
    }

    private void updateProfile(String username, String bio) {
        Log.d(TAG, "Updating profile with Username: " + username + ", Bio: " + bio);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String, Object> map = new HashMap<>();
        map.put("username", username);
        map.put("bio", bio);
        reference.updateChildren(map).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Profile updated successfully for User ID: " + firebaseUser.getUid());
                Toast.makeText(EditProfileActivity.this, "Successfully updated!", Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "Profile update failed for User ID: " + firebaseUser.getUid());
                Toast.makeText(EditProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImage() {
        if (mImageUri != null) {
            Log.d(TAG, "Uploading image: " + mImageUri);
            progressBar.setVisibility(View.VISIBLE);
            final StorageReference fileReference = storageRef.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));
            uploadTask = fileReference.putFile(mImageUri);
            uploadTask.continueWithTask((Continuation<UploadTask.TaskSnapshot, Task<Uri>>) task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return fileReference.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String gottenImageUrl = downloadUri.toString();
                    Log.d(TAG, "Image uploaded successfully: " + gottenImageUrl);

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                    HashMap<String, Object> map1 = new HashMap<>();
                    map1.put("imageurl", gottenImageUrl);
                    reference.updateChildren(map1).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Log.d(TAG, "Image URL updated successfully in Firebase for User ID: " + firebaseUser.getUid());
                            Toast.makeText(EditProfileActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "Failed to update image URL in Firebase for User ID: " + firebaseUser.getUid());
                        }
                    });
                } else {
                    Log.e(TAG, "Image upload failed for User ID: " + firebaseUser.getUid());
                    Toast.makeText(EditProfileActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Image upload error: " + e.getMessage());
                Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            });
        } else {
            Log.w(TAG, "No image selected for upload");
            Toast.makeText(EditProfileActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String extension = mime.getExtensionFromMimeType(cR.getType(uri));
        Log.d(TAG, "File extension: " + extension);
        return extension;
    }
}
