package com.example.donona;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class SubscriptionActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String url = "http://10.0.2.2:5528/";
    private String priceId = "price_1Q6QGjRt4Jb0KcASHTGyC592"; // will fetch database later on
    private String stripePublicKey = "pk_test_51PZTGERt4Jb0KcASqJvZReNwjK5oiWEzZvp27FpjQ8HadaXWg8WKlKO1kfoIcV7AyuI68Trx4ZdTOne1KPrnNP7e00RBCAu2mn";
    private String clientSecret = "";
    private String subscriptionId = "";
    private OkHttpClient client = new OkHttpClient();
    private final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private PaymentSheet paymentSheet;
    private Button cancel;
    private String userDocId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_subscription);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        PaymentConfiguration.init(
                getApplicationContext(),
                stripePublicKey
        );
        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);


        Button btn = (Button) findViewById(R.id.buy_standard);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyStandard(v);
            }
        });

        cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelSubscription();
            }
        });

    }

    public void buyStandard(View view) {
        // Check user has login
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(SubscriptionActivity.this, "You need to login", Toast.LENGTH_SHORT).show();
            return;
        }
        // Check user subscription tier
        db.collection("user")
                .whereEqualTo("userID", user.getUid())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful() || task.getResult().isEmpty()) {
                            return;
                        }
                        DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                        if (doc.getString("tier").equals("standard")) {
                            Toast.makeText(SubscriptionActivity.this, "You are already a pro", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        userDocId = doc.getId();
                        getStripeData(doc.getString("username"), doc.getString("email"));

                    }
                });
    }

    private void getStripeData(String name, String email) {
        JSONObject data = new JSONObject();
        try {
            data.put("name", name);
            data.put("email", email);
        } catch (Exception e) {
            Log.d("TEST", e.toString());
        }
        RequestBody body = RequestBody.create(data.toString(), JSON);

        Request req = new Request.Builder()
                .url(url + "create-customer")
                .method("POST", body)
                .build();
        Call call = client.newCall(req);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("TEST", e.toString());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String body = response.body().string();
                body = body.substring(1, body.length() - 2);
                Log.d("TEST", body);
                getStripeSubscription(body, priceId);
            }
        });
    }

    private void getStripeSubscription(String cusId, String priceId) {
        JSONObject data = new JSONObject();
        try {
            data.put("customerId", cusId);
            data.put("priceId", priceId);
        } catch (Exception e) {
            Log.d("TEST", e.toString());
        }

        RequestBody body = RequestBody.create(data.toString(), JSON);
        Request req = new Request.Builder()
                .url(url + "create-subscription")
                .post(body)
                .build();

        Call call = client.newCall(req);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("TEST", e.toString());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String body = response.body().string();
                Log.d("TEST", body);
                try {
                    JSONObject res = new JSONObject(body);
                    clientSecret = res.getString("clientSecret");
                    subscriptionId = res.getString("subscriptionId");
                    displayPayment(clientSecret);
                } catch (Exception e) {
                    Log.d("TEST", e.toString());
                }
            }
        });
    }

    private void displayPayment(String clientSecret) {
        PaymentSheet.Configuration config = new PaymentSheet.Configuration.Builder("Donona Inc")
                .primaryButtonLabel("Subscribe for 10$/Month (Very cheap)")
                .allowsDelayedPaymentMethods(true)
                .build();
        paymentSheet.presentWithPaymentIntent(clientSecret, config);
    }


    private void onPaymentSheetResult(PaymentSheetResult res) {
        if (res instanceof PaymentSheetResult.Completed) {
            Log.d("TEST", "Payment successful");
            db.collection("user")
                    .document(userDocId)
                    .update("tier", "standard",
                            "subscriptionId", subscriptionId);


        } else if (res instanceof PaymentSheetResult.Canceled) {
            Log.d("TEST", "Cancel payment");
        }
    }

    private void onCancelSubscription() {
        Log.d("TESTING", "Cancel sub clicked");
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(SubscriptionActivity.this, "You need to login", Toast.LENGTH_SHORT).show();
            return;
        }
        // check user current tier
        db.collection("user")
                .whereEqualTo("userID", user.getUid())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful() || task.getResult().isEmpty()) {
                            return;
                        }
                        DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                        if (doc.getString("tier").equals("free")) {
                            Toast.makeText(SubscriptionActivity.this, "You don't have subscription", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        userDocId = doc.getId();
                        cancelSubscription(doc.getString("subscriptionId"));
                    }
                });
    }

    private void cancelSubscription(String subId) {
        JSONObject data = new JSONObject();
        try {
            data.put("subscriptionId", subId);
        } catch (Exception e) {
            Log.d("TEST", "Failed to establish body");
        }

        RequestBody body = RequestBody.create(data.toString(), JSON);
        Request req = new Request.Builder()
                .url(url + "cancel-subscription")
                .post(body)
                .build();
        Call call = client.newCall(req);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                return;
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String body = response.body().string();
                Log.d("TESTING", body);
                try {
                    JSONObject res = new JSONObject(body);
                    if (res.getString("status").equals("canceled")) {
                        Log.d("TESTING", "1");

                        // Update firebase
                        db.collection("user").document(userDocId).update("tier", "free",
                                "subscriptionId", "")
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.d("TESTING", "2");

                                        if (task.isSuccessful()) {
                                            Log.d("TESTING", "Cancel subscription successfuly");
                                        }
                                    }
                                });
                    }
                } catch (Exception e) {
                    Log.d("TEST", e.toString());
                }
            }
        });

    }
}