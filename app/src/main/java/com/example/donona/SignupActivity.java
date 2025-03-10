package com.example.donona;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.Patterns;
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

import com.example.donona.util.NetworkUtils;
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
    private String TAG = "TESTINGHELLO";

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
        // Get data
        EditText mUsername = (EditText) findViewById(R.id.editTextTextUsername);
        EditText mEmail = (EditText) findViewById(R.id.editTextTextEmailAddress);
        EditText mPassword = (EditText) findViewById(R.id.editTextPassword);
        EditText mPasswordConfirm = (EditText) findViewById(R.id.editTextPasswordConfirm);

        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        String passwordConfirm = mPasswordConfirm.getText().toString().trim();
        String username = mUsername.getText().toString().trim();

        if(username.isEmpty()){
            Toast.makeText(SignupActivity.this, "Username is empty", Toast.LENGTH_LONG).show();
            return; // Không tiếp tục nếu có lỗi
        }

        if(email.isEmpty()){
            Toast.makeText(SignupActivity.this, "Email is empty", Toast.LENGTH_LONG).show();
            return; // Không tiếp tục nếu có lỗi
        }

        if(passwordConfirm.isEmpty()){
            Toast.makeText(SignupActivity.this, "Password confirm is empty", Toast.LENGTH_LONG).show();
            return; // Không tiếp tục nếu có lỗi
        }

        if(password.isEmpty()){
            Toast.makeText(SignupActivity.this, "Password is empty", Toast.LENGTH_LONG).show();
            return; // Không tiếp tục nếu có lỗi
        }

        // Kiểm tra mật khẩu dưới 6 ký tự
        if (password.length() < 6) {
            Toast.makeText(SignupActivity.this, "Password contains at least 6 characters", Toast.LENGTH_LONG).show();
            return; // Không tiếp tục nếu có lỗi
        }

        // Kiểm tra mật khẩu và xác nhận mật khẩu có trùng nhau không
        if (!password.equals(passwordConfirm)) {
            Toast.makeText(SignupActivity.this, "Password do not match", Toast.LENGTH_LONG).show();
            return; // Không tiếp tục nếu có lỗi
        }

        if (!(email.endsWith("@gmail.com") ||
                email.endsWith("@yahoo.com") ||
                email.endsWith("@outlook.com") ||
                email.endsWith("@icloud.com") ||
                Patterns.EMAIL_ADDRESS.matcher(email).matches()
        )
        ) {
            Toast.makeText(SignupActivity.this, "Invalid email format", Toast.LENGTH_LONG).show();
            return; // Không tiếp tục nếu có lỗi
        }
        if (!NetworkUtils.isWifiConnected(this)) {
            // Wi-Fi is not connected, do something here
            Toast.makeText(this, "Wi-Fi is not connected", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(mEmail.getText().toString(), mPassword.getText().toString())
                .addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.d(TAG, "Sign up successfully");
                        FirebaseUser user = mAuth.getCurrentUser();
                        handleSuccessAuthentication(user);
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.getMessage());
                        handleFailAuthentication(e.getMessage());
                    }
                });
    }

    private void handleSuccessAuthentication(FirebaseUser user) {
        // Get data
        EditText mUsername = (EditText) findViewById(R.id.editTextTextUsername);
        EditText mEmail = (EditText) findViewById(R.id.editTextTextEmailAddress);
        EditText mPassword = (EditText) findViewById(R.id.editTextPassword);
        EditText mPasswordConfirm = (EditText) findViewById(R.id.editTextPasswordConfirm);



        // store user into database
        Map<String, Object> data = new HashMap<>();
        data.put("username", mUsername.getText().toString());
        data.put("email", user.getEmail());
        data.put("userID", user.getUid());
        data.put("password", mPassword.getText().toString());
        data.put("image", "No Image");
        data.put("bookmarks", new ArrayList<String>());
        data.put("tier", "free");
        data.put("subscriptionId", "");

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

    private void handleFailAuthentication(String error) {
        // Get data
        EditText mUsername = (EditText) findViewById(R.id.editTextTextUsername);
        EditText mEmail = (EditText) findViewById(R.id.editTextTextEmailAddress);
        EditText mPassword = (EditText) findViewById(R.id.editTextPassword);
        EditText mPasswordConfirm = (EditText) findViewById(R.id.editTextPasswordConfirm);

        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        String passwordConfirm = mPasswordConfirm.getText().toString().trim();
        String username = mUsername.getText().toString().trim();

        if(username.isEmpty()){
            Toast.makeText(SignupActivity.this, "Username is empty", Toast.LENGTH_LONG).show();
            return; // Không tiếp tục nếu có lỗi
        }

        if(email.isEmpty()){
            Toast.makeText(SignupActivity.this, "Email is empty", Toast.LENGTH_LONG).show();
            return; // Không tiếp tục nếu có lỗi
        }

        if(passwordConfirm.isEmpty()){
            Toast.makeText(SignupActivity.this, "Password confirm is empty", Toast.LENGTH_LONG).show();
            return; // Không tiếp tục nếu có lỗi
        }

        if(password.isEmpty()){
            Toast.makeText(SignupActivity.this, "Password is empty", Toast.LENGTH_LONG).show();
            return; // Không tiếp tục nếu có lỗi
        }

        // Kiểm tra mật khẩu dưới 6 ký tự
        if (password.length() < 6) {
            Toast.makeText(SignupActivity.this, "Password contains at least 6 characters", Toast.LENGTH_LONG).show();
            return; // Không tiếp tục nếu có lỗi
        }

        // Kiểm tra mật khẩu và xác nhận mật khẩu có trùng nhau không
        if (!password.equals(passwordConfirm)) {
            Toast.makeText(SignupActivity.this, "Password do not match", Toast.LENGTH_LONG).show();
            return; // Không tiếp tục nếu có lỗi
        }

        if (!(email.endsWith("@gmail.com") ||
                email.endsWith("@yahoo.com") ||
                email.endsWith("@outlook.com") ||
                email.endsWith("@icloud.com") ||
                Patterns.EMAIL_ADDRESS.matcher(email).matches()
        )
        ) {
            Toast.makeText(SignupActivity.this, "Invalid email format", Toast.LENGTH_LONG).show();
            return; // Không tiếp tục nếu có lỗi
        }
        if (error.endsWith("another account.")) {
            Toast.makeText(SignupActivity.this, R.string.used_email, Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(SignupActivity.this, "Failed", Toast.LENGTH_LONG).show();

    }

    public void onSignIn(View view) {
        startActivity(new Intent(SignupActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }
}

