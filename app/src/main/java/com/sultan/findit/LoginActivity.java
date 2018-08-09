package com.sultan.findit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.sultan.findit.helpers.Validation;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    EditText etUsername, etPassword;
    TextView summaryErrorMsg;
    TextInputLayout inputLayoutUsername,inputLayoutPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setVariables();
    }
    private void setVariables() {
        summaryErrorMsg = (TextView) findViewById(R.id.summaryErrorMsg);
        mAuth = FirebaseAuth.getInstance();
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.inputLayoutPassword);
        inputLayoutUsername = (TextInputLayout) findViewById(R.id.inputLayoutUsername);
    }
    public void GotoRegister(View view) {
        Intent i = new Intent(this, RegistrationActivity.class);
        startActivity(i);
    }

    public void login(View view) {
        if (validateEmail() && validatePassword()) {
            progressDialog = ProgressDialog.show(LoginActivity.this,null, getString(R.string.pleaseWait),true);
            firebaseLogin(etUsername.getText().toString(), etPassword.getText().toString());
        }
    }
    private void firebaseLogin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("login", "signInWithEmail:success");

                            progressDialog.hide();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("login", "signInWithEmail:failure", task.getException());
                            progressDialog.hide();
                            summaryErrorMsg.setText(getString(R.string.wrong_login));
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
        if (pwd.length() < 1 ) {
            inputLayoutPassword.setError(getString(R.string.required_password));
            return false;

        } else {
            inputLayoutPassword.setErrorEnabled(false);
            return true;
        }
    }
}
