package com.example.attendanceapp;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.zxing.Result;

import java.util.HashMap;
import java.util.Map;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QrCodeScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler{
    private static final int REQUEST_CAMERA=1;
    private ZXingScannerView ScannerView;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScannerView =new ZXingScannerView(this);
        setContentView(ScannerView);

        int currentapiVersion= Build.VERSION.SDK_INT;
        if(currentapiVersion>=Build.VERSION_CODES.M){
            if(checkPermission()){
                Toast.makeText(getApplicationContext(),"Permission Granted", Toast.LENGTH_LONG).show();
            }
            else{
                requestPermission();
            }
        }

    }

    private boolean checkPermission(){
        return(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(QrCodeScanner.this,new String[]{Manifest.permission.CAMERA},REQUEST_CAMERA);
    }

    public void onRequestPermission(int requestCode, String permission[],int[] grantResult){
        switch(requestCode){
            case REQUEST_CAMERA:
                if(grantResult.length>0){
                    boolean cameraAccept = grantResult[0] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccept){
                        Toast.makeText(getApplicationContext(),"Permission Granted",Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Permission Not Granted",Toast.LENGTH_LONG).show();
                        if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                            showMessageOKCancel("You need to grant permission",
                                    new DialogInterface.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
                                                requestPermissions(new String[]{Manifest.permission.CAMERA},REQUEST_CAMERA);
                                            }
                                        }
                                    });
                            return;
                        }
                    }
                }
                break;
        }
    }

    public void onResume(){
        super.onResume();
        int currentapiVersion = Build.VERSION.SDK_INT;
        if(currentapiVersion>= Build.VERSION_CODES.N){
            if(checkPermission()){
                if(ScannerView==null){
                    ScannerView = new ZXingScannerView(this);
                    setContentView(ScannerView);
                }
                ScannerView.setResultHandler(this);
                ScannerView.startCamera();
            }
        }
    }

    public void onDestroy(){
        super.onDestroy();
        ScannerView.stopCamera();
        ScannerView=null;
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener oklistener){
        new AlertDialog.Builder(QrCodeScanner.this)
                .setMessage(message)
                .setPositiveButton("OK",oklistener)
                .setNegativeButton("Cancel",null)
                .create()
                .show();

    }

    @Override
    public void handleResult(Result result) {
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        final String rawresult=result.getText();

        Intent intent = getIntent();
        String totallatitude = intent.getStringExtra("latitude");
        String totallongitude = intent.getStringExtra("longitude");

        //Set area for the fict block
        double minLat= 4.338000;
        double maxLat= 4.339275;
        double minLong=101.136275;
        double maxLong=101.137153;

        double lat = Double.parseDouble(totallatitude);
        double longi = Double.parseDouble(totallongitude);
        AlertDialog.Builder builder= new AlertDialog.Builder(this);

        if(lat >= minLat && lat<=maxLat && longi >= minLong && longi<= maxLong){
        builder.setTitle("Attendance");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean status=true;

                String email = firebaseAuth.getCurrentUser().getEmail();

                firebaseFirestore.collection("Student").whereEqualTo("email",email).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    for(DocumentSnapshot document : task.getResult()){

                                        String studentID = document.getString("studentID");

                                        CollectionReference attendanceRef=firebaseFirestore.collection("Attendance");
                                        attendanceRef.whereEqualTo("studentID",studentID).whereEqualTo("classID",rawresult)
                                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if(task.isSuccessful()){
                                                            for(QueryDocumentSnapshot document:task.getResult()){

                                                                    Map<Object,Boolean> map = new HashMap<>();
                                                                    map.put("attend", status);
                                                                    attendanceRef.document(document.getId()).set(map, SetOptions.merge());//                                                                    Toast.makeText(QrCodeScanner.this, "Attendance taken successfully.", Toast.LENGTH_SHORT).show();
                                                                    Toast.makeText(QrCodeScanner.this, "Attendance taken successfully.", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                    }
                                                });
                                    }
                                }
                            }
                        });

                ScannerView.resumeCameraPreview(QrCodeScanner.this);
            }
        });

        }
        else{
            builder.setTitle("Check In Failed");
            Toast.makeText(QrCodeScanner.this,"You are not within the school area",Toast.LENGTH_SHORT).show();
            builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ScannerView.resumeCameraPreview(QrCodeScanner.this);
                }
            });
        }

        builder.setMessage(result.getText());
        AlertDialog alertdialog = builder.create();
        alertdialog.show();
    }

}