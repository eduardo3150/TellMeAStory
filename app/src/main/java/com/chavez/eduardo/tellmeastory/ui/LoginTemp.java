package com.chavez.eduardo.tellmeastory.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chavez.eduardo.tellmeastory.R;
import com.chavez.eduardo.tellmeastory.network.NetworkUtils;
import com.chavez.eduardo.tellmeastory.utils.ConfigurationUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginTemp extends AppCompatActivity {


    private boolean loggedIn = false;

    public static final String LOG_TAG = LoginTemp.class.getSimpleName();

    private static final int RC_SIGN_IN = 9001;

    private FirebaseAuth auth;
    private GoogleSignInClient googleSignInClient;

    @BindView(R.id.sign_in)
    SignInButton signInButton;

    @BindView(R.id.helloLabel)
    TextView helloUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_temp);
        ButterKnife.bind(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        auth = FirebaseAuth.getInstance();
        /*button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!input.getText().toString().isEmpty()){
                    String value = "http://"+ input.getText().toString()+":82";
                    SharedPreferences sharedPreferences = getSharedPreferences(ConfigurationUtils.PREF_KEY, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(ConfigurationUtils.IP_SET_KEY, true);
                    editor.putString(ConfigurationUtils.IP_VALUE_KEY, value);
                    editor.apply();
                    Intent intent = new Intent(LoginTemp.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        defaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences(ConfigurationUtils.PREF_KEY, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(ConfigurationUtils.IP_SET_KEY, true);
                editor.putString(ConfigurationUtils.IP_VALUE_KEY, NetworkUtils.IMG_BASE_URL);
                editor.apply();
                Intent intent = new Intent(LoginTemp.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });*/

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            startActivity(new Intent(LoginTemp.this, MainActivity.class));
            LoginTemp.this.finish();
        }
    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        // Firebase sign out
        auth.signOut();

        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    private void revokeAccess() {
        // Firebase sign out
        auth.signOut();

        // Google revoke access
        googleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(LOG_TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(LOG_TAG, "firebaseAuthWithGoogle:" + acct.getId());


        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(LOG_TAG, "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(LOG_TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginTemp.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }


                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
