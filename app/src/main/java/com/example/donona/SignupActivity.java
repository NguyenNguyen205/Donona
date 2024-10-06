package com.example.donona;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String TAG = "Hello";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        TextView notice = (TextView) findViewById(R.id.notice);
        notice.setText(Html.fromHtml("To continue, you can review Company’s <b>privacy policy</b> and <b>terms of service</b>."));
    }

    public void onSignUp(View view) {

        EditText mEmail = (EditText) findViewById(R.id.editTextTextEmailAddress);
        EditText mPassword = (EditText) findViewById(R.id.editTextPassword);
        EditText mPasswordConfirm = (EditText) findViewById(R.id.editTextPasswordConfirm);

        // Handle password and confirm password
        if(mPassword.equals(mPasswordConfirm)){
            Toast.makeText(SignupActivity.this, "Please re-enter your password. The confirmation does not match.",Toast.LENGTH_LONG).show();
            return;
        }

        //Handle password is less than 8 character
        if(mPassword.getTextSize() < 8){
            Toast.makeText(SignupActivity.this, "Password must be more than 8 characters.",Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(mEmail.getText().toString(), mPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "Sign up successfully");
                        FirebaseUser user = mAuth.getCurrentUser();
                        handleSuccessAuthentication(user);
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Sign up fail");
                        handleFailAuthentication();
                    }
                });

    }

    private void handleSuccessAuthentication(FirebaseUser user) {
        // Get data
        EditText mUsername = (EditText) findViewById(R.id.editTextTextUsername);
        EditText mPassword = (EditText) findViewById(R.id.editTextPassword);

        // store user into database
        Map<String, Object> data = new HashMap<>();
        data.put("username", mUsername.getText().toString());
        data.put("email", user.getEmail());
        data.put("userID", user.getUid());
        data.put("password", mPassword.getText().toString());
        data.put("image", "No Image");
        data.put("bookmarks", new ArrayList<String>());

        db.collection("user")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(SignupActivity.this, "Sign up successfully", Toast.LENGTH_LONG).show();
                        // navigate to home page
                        startActivity(new Intent(SignupActivity.this, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        Log.d(TAG, "Document write successfull");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Document write fail");
                    }
                });
    }

    private void handleFailAuthentication() {
        startActivity(new Intent(SignupActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    public void onSignIn(View view) {
        startActivity(new Intent(SignupActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }
}

