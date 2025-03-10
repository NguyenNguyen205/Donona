package com.example.donona;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.donona.transformation.CircleTransform;
import com.example.donona.util.NetworkUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private TextInputEditText usernameProfile;
    private TextView tierProfile;
    private TextView emailProfile;
    private Button updateButton;
    private String currentDocumentId;
    private ImageButton imageButton;
    private String imageUri = "";
    private String username = "";
    int SELECT_PICTURE = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage = FirebaseStorage.getInstance("gs://dononacoffee.appspot.com");
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        usernameProfile = findViewById(R.id.username_profile);
        emailProfile = findViewById(R.id.email_profile);
        tierProfile = findViewById(R.id.tier_profile);
        updateButton = findViewById(R.id.update_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUsername = usernameProfile.getText().toString();
                if (newUsername.equals(username)) {
                    Toast.makeText(ProfileActivity.this, R.string.no_update, Toast.LENGTH_LONG).show();
                    return;
                }
                if (currentDocumentId != null) {
                    updateProfile(currentDocumentId, newUsername);
                } else {
                    Log.w("TAG", "No document ID available for updating");
                }
            }
        });
        imageButton = findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav);
        bottomNavigationView.setSelectedItemId(R.id.navigation_account);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            final int itemId = item.getItemId();
            if (itemId == R.id.navigation_account) {
                return true;
            }
            if (itemId == R.id.navigation_home) {
                startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                finish();
                return true;
            }
            if (itemId == R.id.navigation_setting) {
                startActivity(new Intent(ProfileActivity.this, SettingActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userIdToFind = currentUser.getUid(); // Assuming your user ID is the Firebase Auth UID

            db.collection("user")
                    .whereEqualTo("userID", userIdToFind)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("TAG", document.getId() + " => " + document.getString("username"));
                            // View data from FireBase
                            username = document.getString("username");
                            currentDocumentId = document.getId();
                            usernameProfile.setText(document.getString("username"));
                            emailProfile.setText(document.getString("email"));
                            tierProfile.setText(document.getString("tier"));
                            Picasso.get().load(document.getString("image")).transform(new CircleTransform()).into(imageButton);
                        }
                    } else {
                        Log.w("TAG", "Error getting documents.", task.getException());
                    }
                }
            });

        } else {
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    private void updateProfile(String documentId, String newUsername) {
        if (!NetworkUtils.isWifiConnected(this)) {
            // Wi-Fi is not connected, do something here
            Toast.makeText(this, "Wi-Fi is not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        db.collection("user").document(documentId)
                .update("username", newUsername)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ProfileActivity.this, "Update user name successfully", Toast.LENGTH_LONG).show();
                        Log.d("TAG", "Updated successfully");
                        recreate();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error updating ", e);
                        recreate();
                    }
                });

    }

    private void imageChooser() {
        if (!NetworkUtils.isWifiConnected(this)) {
            // Wi-Fi is not connected, do something here
            Toast.makeText(this, "Wi-Fi is not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image to the firebase
                    Log.d("IMAGE HERE", selectedImageUri.toString());
                    imageButton.setImageURI(selectedImageUri);
                    imageUri = selectedImageUri.toString();
                    if (!imageUri.isEmpty()) {
                        Uri file = Uri.parse(imageUri);
                        StorageReference storageRef = storage.getReference();
                        StorageReference imagesRef = storageRef.child("Young_Kreden/" + file.getLastPathSegment());
                        UploadTask uploadTask = imagesRef.putFile(file);

                        // Register observers to listen for when the download is done or if it fails
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                // ...
                                Log.d("TESTING", "Upload successfully");
                                // Set image url
                                imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        db.collection("user").document(currentDocumentId)
                                                .update("image", uri.toString())
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(ProfileActivity.this, "Update avatar successfully", Toast.LENGTH_LONG).show();
                                                        Log.d("TAG", "Updated successfully");
                                                        finish();
                                                        startActivity(getIntent());
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w("TAG", "Error updating ", e);
                                                    }
                                                });
                                    }
                                });
                            }
                        });
                    }
                }
            }
        }
    }

}