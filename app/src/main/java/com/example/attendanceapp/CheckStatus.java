package com.example.attendanceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CheckStatus extends AppCompatActivity {

    ImageView backButton;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    ArrayList<StatusModel> statusModelArrayList;
    StatusAdapter statusAdapter;
    RecyclerView historyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_status);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        statusModelArrayList = new ArrayList<>();
        historyView = findViewById(R.id.history_view);
        historyView.setLayoutManager(new LinearLayoutManager(this));
        historyView.setHasFixedSize(true);

        loadData();

        backButton = (ImageView)findViewById(R.id.backBtn);

        //Back to MainActivity
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CheckStatus.this, MainActivity.class));
            }
        });
    }

    private void loadData() {

        String email = firebaseAuth.getCurrentUser().getEmail();
        firebaseFirestore.collection("Hybrid").whereEqualTo("email",email)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot document : task.getResult()) {
                    StatusModel model = new StatusModel(
                            document.getString("date"),
                            document.getString("time"),
                            document.getString("reason"),
                            document.getBoolean("status"));

                    statusModelArrayList.add(model);
                }

                statusAdapter = new StatusAdapter(CheckStatus.this, statusModelArrayList);
                historyView = findViewById(R.id.history_view);
                historyView.setAdapter(statusAdapter);
            }
        });

    }
}