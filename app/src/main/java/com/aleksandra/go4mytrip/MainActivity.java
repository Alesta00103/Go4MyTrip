package com.aleksandra.go4mytrip;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private GoogleSignInClient googleSignInClient;
    private String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private int RC_SIGN_IN = 1;
    private ProgressBar progressBar;
    private TextView text;
    private ImageView image;
    private Button loginInTEST;
    private String photo;
    private String personEmail;
    private String personName;
    String uid;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        text = findViewById(R.id.text);
        image = findViewById(R.id.image);
        progressBar = findViewById(R.id.progress_bar);


        loginInTEST = findViewById(R.id.login_in_google);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(MainActivity.this, Trips.class);
            startActivity(intent);
        }





        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
        loginInTEST.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View view) {
                                               signIn();

                                           }
                                       }
        );

    }

    private void signIn() {
        progressBar.setVisibility(View.VISIBLE);
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount acc = completedTask.getResult(ApiException.class);
            Toast.makeText(MainActivity.this, "Signed In Successfully", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(acc);

        } catch (ApiException e) {
            Toast.makeText(MainActivity.this, "Signed In Failed", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acct) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    //Toast.makeText(MainActivity.this,"Successful", Toast.LENGTH_SHORT).show();
                    // FirebaseUser user = mAuth.getCurrentUser();
                    updateUI();
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    //Toast.makeText(MainActivity.this,"Failed", Toast.LENGTH_SHORT).show();
                    updateUI();

                }
                Intent intentLog = new Intent(MainActivity.this, Trips.class);
                /*intentLog.putExtra("name", personName);
                intentLog.putExtra("email", personEmail);
                intentLog.putExtra("photo", photo);*/
                startActivity(intentLog);
            }
        });
    }

    private void updateUI() {

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (account != null) {
            personName = account.getDisplayName();
            String personGivenName = account.getGivenName();
            String personFamilyName = account.getFamilyName();
            personEmail = account.getEmail();
            String personId = account.getId();
            Uri personPhoto = account.getPhotoUrl();
            photo = String.valueOf(account.getPhotoUrl());
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            uid = firebaseUser.getUid();
            addUser();

        } else {
            loginInTEST.setVisibility(View.VISIBLE);

        }
    }


    private void addUser() {

        databaseReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    String name = personName;
                    String email = personEmail;
                    String imageUri = photo;
                    String id = databaseReference.push().getKey();
                    User userr = new User(id, name, email, imageUri);
                    databaseReference.child(uid).setValue(userr);
                    Toast.makeText(MainActivity.this, "added user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}