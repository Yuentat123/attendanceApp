package com.example.attendanceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HybridClass extends AppCompatActivity {

    Button submitBtn;
    ImageView backButton;
    EditText name, id, email, courseCode, reason;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hybrid_class);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getInstance().getCurrentUser() != null){
            String useremail = firebaseAuth.getCurrentUser().getEmail();
            db.collection("Student").whereEqualTo("email",useremail).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for(DocumentSnapshot document : task.getResult()){

                                    String fullname = document.getString("lname") + " " + document.getString("fname");
                                    String studentID = document.getString("studentID");
                                    String email = document.getString("email");

                                    backButton = (ImageView)findViewById(R.id.backBtn);
                                    submitBtn = findViewById(R.id.submitBtn);
                                    courseCode = findViewById(R.id.course_hybrid);
                                    reason = findViewById(R.id.reason_hybrid);

                                    boolean status = false;
                                    String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                                    String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

                                    //Back to MainActivity
                                    backButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            startActivity(new Intent(HybridClass.this, MainActivity.class));
                                        }
                                    });

                                    //save form input to database
                                    submitBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {


                                            String CourseCode = courseCode.getText().toString().trim();
                                            String Reason = reason.getText().toString().trim();


                                            if(TextUtils.isEmpty(CourseCode)){
                                                courseCode.setError("Course Code is required.");
                                                return;
                                            }
                                            if(TextUtils.isEmpty(Reason)){
                                                reason.setError("Elaboration is required.");
                                                return;
                                            }

                                            Map<String,Object> applications = new HashMap<>();
                                            applications.put("name",fullname);
                                            applications.put("id",studentID);
                                            applications.put("email",email);
                                            applications.put("courseCode",CourseCode);
                                            applications.put("reason",Reason);
                                            applications.put("status",status);
                                            applications.put("date",date);
                                            applications.put("time", time);

                                            db.collection("Hybrid").document().set(applications)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(HybridClass.this,"Application Sent.",Toast.LENGTH_SHORT).show();
                                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                            finish();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(HybridClass.this,"Error writing document",Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    });


                                }
                            }
                            else{
                                Toast.makeText(HybridClass.this, "Student Data Not Found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }


    }
}