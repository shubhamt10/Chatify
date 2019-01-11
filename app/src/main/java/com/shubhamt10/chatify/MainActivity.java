package com.shubhamt10.chatify;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    public final static String ANONYMOUS = "anonymous";

    private EditText nameText;
    private EditText emailText;
    private EditText passwordText;
    private Button signUpButton;
    private TextView loginOrSignupTextView;
    private TextView questionTextView;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private String mUsername;
    private boolean loginModeIsActive = true;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsername = ANONYMOUS;
        intent = new Intent(MainActivity.this, UserListActivity.class);

        nameText = findViewById(R.id.nameEditText);
        emailText = findViewById(R.id.emailEditText);
        passwordText = findViewById(R.id.passwordEditText);
        signUpButton = findViewById(R.id.loginButton);
        loginOrSignupTextView = findViewById(R.id.loginOrSignUp);
        questionTextView = findViewById(R.id.question);

        loginOrSignupTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loginModeIsActive) {
                    signUpButton.setText("SIGNUP");
                    loginModeIsActive = false;
                    nameText.setVisibility(View.VISIBLE);
                    questionTextView.setText("Already have an account?");
                    loginOrSignupTextView.setText("Login");
                } else {
                    signUpButton.setText("LOGIN");
                    nameText.setVisibility(View.GONE);
                    questionTextView.setText("Don't have an account yet?");
                    loginOrSignupTextView.setText("SignUp");
                    loginModeIsActive = true;
                }

            }
        });

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("users");

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    startActivity(intent);
                    finish();
                }
            }
        };

        firebaseAuth.addAuthStateListener(authStateListener);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);

    }

    public void signUpOrLogin(View view) {

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        final String name = nameText.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(MainActivity.this,"Enter valid Email and Password",Toast.LENGTH_SHORT).show();
        } else {

            if (loginModeIsActive) {

                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "Login unsuccessful", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } else {

                if (name.isEmpty()){
                    Toast.makeText(MainActivity.this,"Enter name",Toast.LENGTH_SHORT).show();
                }else {

                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                final FirebaseUser mUser = firebaseAuth.getCurrentUser();

                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name).build();

                                mUser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            System.out.println(mUser.getDisplayName());
                                            User user = new User(mUser.getDisplayName(), mUser.getUid());
                                            databaseReference.push().setValue(user);
                                        } else {
                                            System.out.println(task.getException().toString());
                                        }
                                    }
                                });

                                Toast.makeText(MainActivity.this, "SignUp successful", Toast.LENGTH_SHORT).show();
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(MainActivity.this, "SignUp unsuccessful", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }
    }


}

