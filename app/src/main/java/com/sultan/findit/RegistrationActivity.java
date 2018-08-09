package com.sultan.findit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.sultan.findit.helpers.Validation;
import com.sultan.findit.models.User;

public class RegistrationActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    EditText etUsername, etPassword, etConfirmPassword;
    TextInputLayout inputLayoutUsername,inputLayoutPassword, inputLayoutConfirmPassword;
    private FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        setVariables();
    }
    private void setVariables() {
        mAuth = FirebaseAuth.getInstance();
        registerBtn = (Button) findViewById(R.id.register_btn);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.inputLayoutPassword);
        inputLayoutUsername = (TextInputLayout) findViewById(R.id.inputLayoutUsername);
        inputLayoutConfirmPassword = (TextInputLayout) findViewById(R.id.inputLayoutConfirmPassword);

    }
    public void Register(View view) {
        // register new user
        if (validateEmail() && validatePassword()) {
            progressDialog = ProgressDialog.show(RegistrationActivity.this,null, getString(R.string.pleaseWait),true);
            firebaseCreateUser(etUsername.getText().toString(), etPassword.getText().toString());
        }
    }

    //region Firebase
    private void firebaseCreateUser(final String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("login", "createUserWithEmail:success");

                            User user = new User();
                            user.setName(email.substring(0, email.indexOf("@")));
                            user.setProfile_image("");
                            user.setPhone("1");
                            user.setSecurity_level("1");
                            user.setUser_id(mAuth.getCurrentUser().getUid());

                            FirebaseDatabase.getInstance().getReference()
                                    .child(getString(R.string.dbnode_users))
                                    .child(mAuth.getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mAuth.getInstance().signOut();
                                    startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    mAuth.getInstance().signOut();
                                    startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                                    finish();
                                    Toast.makeText(RegistrationActivity.this, "Registration Error!", Toast.LENGTH_LONG).show();
                                }
                            });

                            progressDialog.hide();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("login", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegistrationActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //region Validation
    private boolean validateEmail() {
        if (etUsername.getText().toString().isEmpty()) {
            inputLayoutUsername.setError(getString(R.string.required_username));
            return false;

        } else if (!Validation.isEmailValid(etUsername.getText().toString())) {
            inputLayoutUsername.setError(getString(R.string.invalidEmail));
            return false;
        } else {
            inputLayoutUsername.setErrorEnabled(false);
            return true;
        }
    }
    private boolean validatePassword() {
        String pwd = etPassword.getText().toString().trim();
        String conf_pwd = etConfirmPassword.getText().toString().trim();
        if (pwd.length() < 1 ) {
            inputLayoutPassword.setError(getString(R.string.required_password));
            return false;

        } else if (!pwd.equals(conf_pwd)){
            inputLayoutConfirmPassword.setError(getString(R.string.confirmPassword_error));
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
            return true;
        }
    }

    public void GotoLogin(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }
}
