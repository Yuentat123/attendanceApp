package com.example.attendanceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getInstance().getCurrentUser() != null){
            String email = firebaseAuth.getCurrentUser().getEmail();
            db.collection("Student").whereEqualTo("email",email).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for(DocumentSnapshot document : task.getResult()){
                                    //find the field for displaying the data
                                    TextView dname = (TextView)findViewById(R.id.name);
                                    TextView dstuID = (TextView)findViewById(R.id.stuID);
                                    TextView demail = (TextView)findViewById(R.id.email);

                                    String fullname = document.getString("lname") + " " + document.getString("fname");
                                    String studentID = document.getString("studentID");
                                    String email = document.getString("email");

                                    dname.setText(fullname);
                                    dstuID.setText(studentID);
                                    demail.setText(email);
                                }
                            }
                            else{
                                Toast.makeText(MainActivity.this, "Student Data Not Found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        ImageView logOutBtn = findViewById(R.id.logOutBtn);

        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });

        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId())
                {
                    case R.id.home:
                        return true;
                    case R.id.qr:
                        startActivity(new Intent(getApplicationContext(),Attendance_QR.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        CardView hybridBtn = findViewById(R.id.hybridBtn);
        hybridBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, HybridClass.class);
                startActivity(i);
            }
        });

        CardView checkStatusBtn = findViewById(R.id.checkStatusBtn);
        checkStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, CheckStatus.class);
                startActivity(i);
            }
        });

        CardView checkAttendanceBtn = findViewById(R.id.checkAttendanceBtn);
        checkAttendanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, CheckAttendanceRecord.class);
                startActivity(i);
            }
        });
    }
}