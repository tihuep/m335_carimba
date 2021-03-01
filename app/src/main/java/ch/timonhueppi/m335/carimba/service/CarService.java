
package ch.timonhueppi.m335.carimba.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import ch.timonhueppi.m335.carimba.controller.CarsActivity;
import ch.timonhueppi.m335.carimba.controller.LoginActivity;
import ch.timonhueppi.m335.carimba.controller.SignUpActivity;
import ch.timonhueppi.m335.carimba.model.Car;

public class CarService extends Service {
    // Binder given to clients
    private final IBinder binder = new LocalBinder();

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public CarService getService() {
            // Return this instance of LocalService so clients can call public methods
            return CarService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    private FirebaseFirestore mFirestore;

    public void initFirebaseFirestore() {mFirestore = FirebaseFirestore.getInstance();}

    public void addCar(Car car, UUID userId){
        Map<String, Object> carMap = new HashMap<>();
        carMap.put("userId", userId);
        carMap.put("year", car.getYear());
        carMap.put("make", car.getMake());
        carMap.put("model", car.getModel());
        carMap.put("trim", car.getTrim());

        mFirestore.collection("cars")
                .add(carMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        //TODO
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //TODO
                    }
                });
    }

    public void getCarsOfUser(CarsActivity currentActivity, String userUid){
        mFirestore.collection("cars")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document : task.getResult()){
                            Map<String, Object> carMap = document.getData();
                            String user = (String) carMap.get("userId");
                            if (user.equals(userUid)){
                                Car carObject = new Car(document.getId(), user, (String) carMap.get("year"), (String) carMap.get("make"), (String) carMap.get("model"), (String) carMap.get("trim"));
                                carList.add(carObject);
                            }
                        }
                        currentActivity.generateList();
                    }
                });
    }

    public ArrayList<Car> carList = new ArrayList<>();

}