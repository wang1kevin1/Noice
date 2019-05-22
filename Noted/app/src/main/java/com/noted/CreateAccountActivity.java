package com.noted;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.noted.utils.AccountUtil;

/**
 * A login screen that offers login via email/password.
 */
public class CreateAccountActivity extends AppCompatActivity {

    private static final String TAG = "Sign In";

    /**
     * Firebase Connection
     */
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    // UI references.
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mConfirmPasswordEditText;
    private Button mCreateAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        // Text Inputs
        mEmailEditText = findViewById(R.id.editTextEmail);
        mPasswordEditText = findViewById(R.id.editTextPassword);
        mConfirmPasswordEditText = findViewById(R.id.editTextConfirmPassword);

        // Buttons
        mCreateAccountButton = findViewById(R.id.buttonCreateAccount);

        mCreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Set errors to null
                mPasswordEditText.setError(null);
                mEmailEditText.setError(null);

                if(mPasswordEditText.getText().toString().equals(mConfirmPasswordEditText.getText().toString())) {
                    createAccount(mEmailEditText.getText().toString().trim(), mPasswordEditText.getText().toString());
                } else {
                    mPasswordEditText.setError("Passwords do not match");
                    mConfirmPasswordEditText.setError("Passwords do not match");
                }



            }
        });

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    private void createAccount(String email, String password){

        if(email.isEmpty())
            mEmailEditText.setError("Email field is required.");

        else if(!AccountUtil.isEmailValid(email))
            mEmailEditText.setError("Please enter a valid email");


        else if(AccountUtil.isPasswordValid(password, mPasswordEditText)){

            //Attempt to create an account
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                AccountUtil.writeUser(user.getUid(), user.getEmail());

                                Intent myIntent = new Intent(CreateAccountActivity.this,
                                        DashboardActivity.class);
                                startActivity(myIntent);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(CreateAccountActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
