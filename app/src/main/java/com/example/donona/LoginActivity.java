package com.example.donona;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private String TAG = "Hello";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    Log.v(TAG, result.toString());
                    onSignInResult(result);
                }
            }
    );

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            Log.d("TEST", "Sign in successful");
            // Successfully signed in
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            // Create user in firebase table if not existed
            db.collection("user")
                    .whereEqualTo("userID", user.getUid())
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.getResult().isEmpty()) {
                                Map<String, Object> data = new HashMap<>();
                                data.put("username", user.getEmail());
                                data.put("email", user.getEmail());
                                data.put("userID", user.getUid());
                                data.put("image", "No image");
                                data.put("bookmarks", new ArrayList<String>());
                                data.put("tier", "free");
                                data.put("subscriptionId", "");

                                db.collection("user")
                                        .add(data)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Log.d(TAG, "Document write successfull");
                                                handleSuccessAuthentication(user);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d(TAG, "Document write fail");
                                            }
                                        });

                            } else {
                                handleSuccessAuthentication(user);

                            }
                        }
                    });

        } else {
            Log.d("TEST", "Google sign in failed");
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Sign in failed");
            alert.setMessage("Please sign in again");
            alert.setCancelable(false);
            alert.show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        TextView notice = (TextView) findViewById(R.id.notice);
        notice.setText(Html.fromHtml("To continue, you can review Company’s <b>privacy policy</b> and <b>terms of service</b>."));

        SharedPreferences preferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        boolean isVietnamese = preferences.getBoolean("isVietnamese", false);

        // Áp dụng ngôn ngữ
        if (isVietnamese) {
            setLocale(this, "vi");
        } else {
            setLocale(this, "en");
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav);
        bottomNavigationView.setSelectedItemId(R.id.navigation_account);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            // can't use switch case due to non constant id
            final int itemId = item.getItemId();
            if (itemId == R.id.navigation_account) {
                return true;
            }
            if (itemId == R.id.navigation_setting) {
                startActivity(new Intent(LoginActivity.this, SettingActivity.class));
                finish();
                return true;
            }
            if (itemId == R.id.navigation_home) {
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        EditText password = (EditText) findViewById(R.id.editTextPassword);
        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return false;
            }
        });
    }

    public void onSignin(View view) {
        EditText mEmail = (EditText) findViewById(R.id.editTextTextEmailAddress);
        EditText mPassword = (EditText) findViewById(R.id.editTextPassword);
        if (mEmail.getText().toString().isEmpty() || mPassword.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter Email or Password", Toast.LENGTH_LONG).show();
            return;
        }
        mAuth.signInWithEmailAndPassword(mEmail.getText().toString(), mPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Login successfully", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "Login successfully");
                            FirebaseUser user = mAuth.getCurrentUser();
                            handleSuccessAuthentication(user);
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Email or Password is wrong", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "hello", task.getException());
                        }
                    }
                });
    }

    public void handleSuccessAuthentication(FirebaseUser user) {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("email", user.getEmail());
        intent.putExtra("userID", user.getUid());
        startActivity(intent);
    }

    public void onSignup(View view) {
        startActivity(new Intent(LoginActivity.this, SignupActivity.class));
    }

    public void googleSignin(View view) {
        Log.d(TAG, "googleSignin: Hello world");
        // Pre built UI sign in

        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build();
        signInLauncher.launch(signInIntent);
    }

    // Phương thức để thay đổi ngôn ngữ
    protected void setLocale(Activity activity, String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

}