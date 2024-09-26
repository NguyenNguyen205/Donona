package com.example.donona;

import android.content.Intent;
//import androidx.credentials.exceptions.GetCredentialException;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
//import androidx.credentials.Credential;
//import androidx.credentials.CredentialManager;
//import androidx.credentials.CredentialManagerCallback;
//import androidx.credentials.GetCredentialRequest;
//import androidx.credentials.GetCredentialResponse;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
//import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
//import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;

public class LoginActivity extends AppCompatActivity {

    private String TAG = "Hello";
    private FirebaseAuth mAuth;

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    Log.v(TAG, result.toString());
                }
            }
    );


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

        TextView notice = (TextView) findViewById(R.id.notice);
        notice.setText(Html.fromHtml("To continue, you need to <b>Hello world</b>"));

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav);
        bottomNavigationView.setSelectedItemId(R.id.navigation_account);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            // can't use switch case due to non constant id
            final int itemId = item.getItemId();
            if (itemId == R.id.navigation_account) {
                return true;
            }
            if (itemId == R.id.navigation_streaming) {
                startActivity(new Intent(LoginActivity.this, StreamingActivity.class));
                finish();
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
        EditText mEmail = (EditText)findViewById(R.id.editTextTextEmailAddress);
        EditText mPassword = (EditText) findViewById(R.id.editTextPassword);
        if (mEmail.getText().toString().isEmpty() || mPassword.getText().toString().isEmpty()) {
            return;
        }
        mAuth.signInWithEmailAndPassword(mEmail.getText().toString(), mPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Login successfully");
                            FirebaseUser user = mAuth.getCurrentUser();
                            handleSuccessAuthentication(user);
                        }
                        else {
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
//        CredentialManager cm = CredentialManager.create(getApplicationContext());
//        String WEB_CLIENT_ID = "815387647289-cvs26lf9ftlcu4rhude58b1bqbu2ucg6.apps.googleusercontent.com";
//        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
//                .setFilterByAuthorizedAccounts(false)
//                .setServerClientId(WEB_CLIENT_ID)
//                .build();
//        GetCredentialRequest request = new GetCredentialRequest.Builder()
//                .addCredentialOption(googleIdOption)
//                .build();
//        cm.getCredentialAsync(
//                LoginActivity.this,
//                request,
//                new CancellationSignal(),
//                getMainExecutor(),
//                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
//                    @Override
//                    public void onError(@NonNull GetCredentialException e) {
//                        Log.e(TAG, e.toString());
//                        return;
//                    }
//                    @Override
//                    public void onResult(GetCredentialResponse result) {
////                        Credential credential = result.getCredential();
////                        Log.v(TAG, credential.toString());
//                        Log.v(TAG, "Another world");
//                    }
//                });
        // Pre built UI sign in

        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build()
//                new AuthUI.IdpConfig.FacebookBuilder().build()
        );
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build();
        signInLauncher.launch(signInIntent);
    }

}