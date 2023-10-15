package com.example.attendanceapp;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.attendanceapp.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText firstName, lastName, stuID, email_signUp, pw_signUp;
    TextView signUpIntent;
    Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        stuID = findViewById(R.id.stuID);
        email_signUp = findViewById(R.id.email_signUp);
        pw_signUp = findViewById(R.id.pw_signUp);
        signUpIntent = findViewById(R.id.signUpIntent);
        registerBtn = findViewById(R.id.registerBtn);


        signUpIntent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fname = firstName.getText().toString().trim();
                String lname = lastName.getText().toString().trim();
                String id = stuID.getText().toString().trim();
                String email = email_signUp.getText().toString().trim();
                String password = pw_signUp.getText().toString().trim();


                if(TextUtils.isEmpty(fname)){
                    firstName.setError("First Name is required.");
                    return;
                }
                if(TextUtils.isEmpty(lname)){
                    lastName.setError("Last Name is required.");
                    return;
                }
                if(TextUtils.isEmpty(id)){
                    stuID.setError("Student ID is required.");
                    return;
                }
                if(TextUtils.isEmpty(email)){
                    email_signUp.setError("Email is required.");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    pw_signUp.setError("Password is required.");
                    return;
                }

                //Assign registered to a map, to store into Firestore later
                Map<String,Object> students = new HashMap<>();
                students.put("studentID",id);
                students.put("fname",fname);
                students.put("lname",lname);
                students.put("email",email);

                db.collection("Student").document(id).set(students)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(Register.this,"Students Data Has Been Successfully Saved",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Register.this,"Error writing document",Toast.LENGTH_SHORT).show();
                            }
                        });

                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user = new User(fname, lname, id, email);
                            Toast.makeText(Register.this, "User Created.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Register.this,MainActivity.class);
                            intent.putExtra("studentID",id);
                            startActivity(intent);
                        }else {
                        Toast.makeText(Register.this, "Register Unsuccessful!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


                    }
                });
            }
        }