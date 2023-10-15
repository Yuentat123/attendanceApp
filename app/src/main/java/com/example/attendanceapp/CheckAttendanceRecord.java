package com.example.attendanceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CheckAttendanceRecord extends AppCompatActivity {

    ImageView backButton;
    RecyclerView recordView;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    ArrayList<RecordModel> recordModelArrayList;
    RecordAdapter recordAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_attendance_record);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        recordModelArrayList = new ArrayList<>();
        recordView = findViewById(R.id.record_view);
        recordView.setLayoutManager(new LinearLayoutManager(this));
        recordView.setHasFixedSize(true);

        loadData();

        backButton = (ImageView)findViewById(R.id.backBtn);

        //Back to MainActivity
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CheckAttendanceRecord.this, MainActivity.class));
            }
        });
    }

    private void loadData() {
        //Get user Student ID from database
        String email = firebaseAuth.getCurrentUser().getEmail();
        firebaseFirestore.collection("Student").whereEqualTo("email",email).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(DocumentSnapshot document : task.getResult()){

                                String studentID = document.getString("studentID");

                                //Get attendance records of user
                                firebaseFirestore.collection("Attendance").whereEqualTo("studentID",studentID)
                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                for(DocumentSnapshot document : task.getResult()) {
                                                    RecordModel model = new RecordModel(
                                                            document.getString("courseCode"),
                                                            document.getString("classType"),
                                                            document.getString("date"),
                                                            document.getBoolean("attend"));

                                                    recordModelArrayList.add(model);
                                                }

                                                recordAdapter = new RecordAdapter(CheckAttendanceRecord.this, recordModelArrayList);
                                                recordView = findViewById(R.id.record_view);
                                                recordView.setAdapter(recordAdapter);
                                            }
                                        });
                            }
                        }
                    }
                });
    }
}