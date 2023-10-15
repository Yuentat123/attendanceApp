package com.example.attendanceapp;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class FetchAddressService extends IntentService {

    private ResultReceiver resultReceiver;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    public FetchAddressService(){
        super("FetchAddressService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        if(intent != null){
            String errorMessage = "";
            resultReceiver = intent.getParcelableExtra(Constants.RECEIVER);
            Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
            if(location == null){
                return;
            };
            Geocoder geocoder = new Geocoder(this,Locale.getDefault());
            List <Address> addresses = null;
            try{
                addresses=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(), 1);
            }catch(Exception exception){
                errorMessage = exception.getMessage();
            }
            if(addresses == null || addresses.isEmpty()){
                deliverResultToReceiver(Constants.FAILURE_RESULT,errorMessage);
            }
            else{
                Address address = addresses.get(0);
                String add = address.toString();
                String[] fulladd = add.split(":");
                String str1 = fulladd[0];
                String str2 = fulladd[1];
                String[] fullstr2= str2.split("]");
                String midaddress=fullstr2[0];
                String[] addres = midaddress.split("\"");
                String str3 = addres[0];
                String fulladdress=addres[1];

                System.out.println(fulladdress+"???????????????????????/");
                ArrayList<String> addressFragments = new ArrayList<>();
                for (int i =0; i<= address.getMaxAddressLineIndex();i++){
                    addressFragments.add(address.getAddressLine(i));
                }
                String email = firebaseAuth.getCurrentUser().getEmail();
                firebaseFirestore.collection("Student").whereEqualTo("email",email).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    for(DocumentSnapshot document : task.getResult()){

                                        String studentID = document.getString("studentID");

                                        CollectionReference attendanceRef=firebaseFirestore.collection("Attendance");
                                        attendanceRef.whereEqualTo("studentID",studentID).
                                                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if(task.isSuccessful()){
                                                            for(QueryDocumentSnapshot document:task.getResult()){
                                                                Map<Object,String> map = new HashMap<>();
                                                                map.put("address", fulladdress);
                                                                attendanceRef.document(document.getId()).set(map, SetOptions.merge());
                                                            }
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }
                        });
                deliverResultToReceiver(
                        Constants.SUCCESS_RESULT,
                        TextUtils.join(
                                Objects.requireNonNull(System.getProperty("line.separator")),
                                addressFragments
                        )
                );
            }
        }
    }

    private void deliverResultToReceiver(int resultCode, String addressMessage){
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY,addressMessage);
        resultReceiver.send(resultCode,bundle);
    }
}
