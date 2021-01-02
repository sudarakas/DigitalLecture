package tech.sudarakas.digitallecture.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.prefs.AbstractPreferences;

import tech.sudarakas.digitallecture.R;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button registerButton;
    private  Button loginButton;

    private EditText email;
    private EditText password;

    private ProgressBar progressBar;
    private static final String LOGGED_USER = "LOGGED";
    SharedPreferences pref = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pref = getSharedPreferences(LOGGED_USER, 0);
        //Update shared logged variable
        if(pref.getBoolean(LOGGED_USER, false)){
            pref.edit().putBoolean(LOGGED_USER, true).commit();
        }

        boolean previouslyLogged = pref.getBoolean(LOGGED_USER, false);

        if(!previouslyLogged){
            registerButton = (Button) findViewById(R.id.register);
            registerButton.setVisibility(View.VISIBLE);
            registerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), Register.class));
                }
            });
        }

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //Login
        loginButton = (Button) findViewById(R.id.login);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {

        //Validate the data
        final String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            if(userEmail.isEmpty()){
                email.setError("Email is required");
                email.requestFocus();
                return;
            }
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
            email.setError("Email is invalid");
            email.requestFocus();
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            if(userPassword.isEmpty()){
                password.setError("Password is required");
                password.requestFocus();
                return;
            }
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    pref.edit().putBoolean(LOGGED_USER, true).commit();
                    
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    progressBar.setVisibility(View.GONE);

                }else {
                    Toast.makeText(Login.this, "Try again! Invalid login attempt", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}
