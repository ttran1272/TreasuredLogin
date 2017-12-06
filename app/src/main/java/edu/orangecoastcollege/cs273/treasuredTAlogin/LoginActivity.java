package edu.orangecoastcollege.cs273.treasuredTAlogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "TreasuredLogin";

    private EditText mEmailEditText;
    private EditText mPasswordEditText;

    //TODO (1): Add Firebase member variables (auth and user)
    // Connect to the Firebase service
    private FirebaseAuth mAuth;
    // The current user
    private FirebaseUser mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Views
        mEmailEditText = (EditText) findViewById(R.id.emailEditText);
        mPasswordEditText = (EditText) findViewById(R.id.passwordEditText);

        //TODO (2): Initialize Firebase authentication
        mAuth = FirebaseAuth.getInstance();

        // TODO (3): Get the current user.  If not null (already signed in), go directly to TreasureActivity
        mUser = mAuth.getCurrentUser();
        // If user is signed in, go to TreasureActivity
        if (mUser != null)
            goToTreasure();
    }

    // TODO (4): Create a private void goToTreasure() method that finishes this activity
    // TODO (4): then creates a new Intent to the TreasureActivity.class and starts the intent.
    private void goToTreasure()
    {
        finish();
        Intent treasureActivity = new Intent(this, TreasureActivity.class);
        startActivity(treasureActivity);
    }

    // TODO (5): Create a private boolean isValidInput() method that checks to see whether
    // TODO (5): the email address or password is empty.  Return false if either is empty, true otherwise.
    private boolean isValidInput()
    {
        boolean valid = true;
        if (TextUtils.isEmpty(mEmailEditText.getText()))
        {
            mEmailEditText.setError("Required.");
            valid = false;
        }
        if (TextUtils.isEmpty(mPasswordEditText.getText()))
        {
            mPasswordEditText.setError("Required.");
            valid = false;
        }
        return valid;
    }

    // TODO (6): Create a private void createAccount(String email, String password) method
    // TODO (6): that checks for valid input, then uses Firebase authentication to create the user with email and password.
    private void createAccount(String email, String password)
    {
        if (!isValidInput())
            return;

        // Create a new account with Firebase
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // Check the result (task)
                        if (task.isSuccessful())
                        {
                            // Alert user account was created successfully
                            Toast.makeText(LoginActivity.this, "Account created successfully. Please verify account in your email.", Toast.LENGTH_LONG).show();
                            mUser = mAuth.getCurrentUser();
                            mUser.sendEmailVerification();
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this, "Account already exists. Please sign in, or use different email.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    // TODO (7): Create a private void signIn(String email, String password) method
    // TODO (7): that checks for valid input, then uses Firebase authentication to sign in user with email and password entered.
    private void signIn(String email, String password)
    {
        if (!isValidInput())
            return;

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task ){
                        if (task.isSuccessful()) {
                            mUser = mAuth.getCurrentUser();
                            if (mUser.isEmailVerified())
                                goToTreasure();
                            else
                                Toast.makeText(LoginActivity.this, "Please verify your account in the email: " + mUser.getEmail(), Toast.LENGTH_LONG).show();
                        }
                        else
                            Toast.makeText(LoginActivity.this,
                                    "Sign in failed. Please try again.", Toast.LENGTH_LONG).show();
                    }
                });

    }

    // TODO (8): Create a public void handleLoginButtons(View v) that checks the id of the button clicked.
    // TODO (8): If the button is createAccountButton, call the createAccount() method, else if it's signInButton, call the signIn() method.
    public void handleLoginButtons(View v)
    {
        switch (v.getId())
        {
            case R.id.createAccountButton:
                createAccount(mEmailEditText.getText().toString(), mPasswordEditText.getText().toString());
                break;

            case R.id.signInButton:
                signIn(mEmailEditText.getText().toString(), mPasswordEditText.getText().toString());
                break;
        }
    }
}
