package com.zgriesinger.bowlpicks;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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
import java.text.DateFormat;
import java.util.Date;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "EmailPassword";

    private FirebaseAuth mAuth;
    //private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mPasswordConfirmField;
    private Button mSignUp;
    public void showHome(String uid) {
        // When a new user signs up we can initialize their db entry here for later
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("Users").child(uid).child("lastLogin").setValue(DateFormat.getDateTimeInstance().format(new Date()));

        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
    public void showLanding() {
        Intent intent = new Intent(this, LandingActivity.class);
        startActivity(intent);
    }
//    @Override
//    public void onStart() {
//        super.onStart();
//        mAuth.addAuthStateListener(mAuthListener);
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        if (mAuthListener != null) {
//            mAuth.removeAuthStateListener(mAuthListener);
//        }
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Intent intent = getIntent();

        mAuth = FirebaseAuth.getInstance();

        mEmailField = (EditText) findViewById(R.id.email_field);
        mPasswordField = (EditText) findViewById(R.id.password_field);
        mPasswordConfirmField = (EditText) findViewById(R.id.password_field_confirm);

        mSignUp = (Button) findViewById(R.id.sign_up_act_button);
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmailField.getText().toString();
                String pass = mPasswordField.getText().toString();
                String passConfirm = mPasswordConfirmField.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    mEmailField.setError("Required.");
                } else if (TextUtils.isEmpty(pass)) {
                    mPasswordField.setError("Required.");
                } else if(TextUtils.isEmpty(passConfirm)){
                    mPasswordConfirmField.setError("Required.");
                } else if(!pass.equals(passConfirm)) {
                    mPasswordConfirmField.setError("Passwords Must Match");
                } else {
                    //This is a successful form and now we go to the authentication
                    mAuth.createUserWithEmailAndPassword(email, pass)
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "createUserWithEmail: success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        showHome(user.getUid());
                                    } else {
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(SignUpActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

            }
        });

//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                if (user != null) {
//                    // User is signed in
//                    showHome();
//                } else {
//                    // User is signed out
//                    showLanding();
//                }
//                // ...
//            }
//        };
    }
}
