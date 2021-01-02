package tech.sudarakas.digitallecture.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

import tech.sudarakas.digitallecture.R;
import tech.sudarakas.digitallecture.entities.User;

public class Register extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText email;
    private EditText password;
    private EditText confirmpassowrd;
    private ProgressBar progressBar;

    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        email = (EditText) findViewById(R.id.emailreg);
        password = (EditText) findViewById(R.id.passwordreg);
        confirmpassowrd = (EditText) findViewById(R.id.confirmpassword);
        progressBar = (ProgressBar) findViewById(R.id.progressBarreg);

        registerButton =(Button) findViewById(R.id.registerreg);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regitserUser();
            }
        });
    }

    public void regitserUser(){

        //Validate the data
        final String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();
        String userConfirmPassword = confirmpassowrd.getText().toString().trim();

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

        if(userPassword.length() < 6){
            confirmpassowrd.setError("Password should be longer than 6 characters");
            confirmpassowrd.requestFocus();
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            if(userConfirmPassword.isEmpty()){
                confirmpassowrd.setError("Confirm Password is required");
                confirmpassowrd.requestFocus();
                return;
            }
        }

        if(!userPassword.equals(userConfirmPassword)){
            confirmpassowrd.setError("Password is not matched");
            confirmpassowrd.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    User user = new User(userEmail);
                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(Register.this, "Registration is successful", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                                Intent intent = new Intent(getApplicationContext(), Login.class);
                                startActivity(intent);
                            }else{
                                Toast.makeText(Register.this, "Try again! Registration is not successful", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }else{
                    Toast.makeText(Register.this, "Try again! Registration is not successful", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}
