
package ch.timonhueppi.m335.carimba.service;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import ch.timonhueppi.m335.carimba.controller.AddCarActivity;
import ch.timonhueppi.m335.carimba.controller.CarsActivity;
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

    public String selectedYear;
    public String selectedMake;
    public String selectedModel;
    public String selectedTrim;

    public ArrayList<Car> carList = new ArrayList<>();

    public void addCar(Car car){
        Map<String, Object> carMap = new HashMap<>();
        carMap.put("userId", car.getUserId());
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


    public void loadYears(AddCarActivity currentActivity){
        try {
            Properties properties = new Properties();
            AssetManager assetManager = getBaseContext().getAssets();
            InputStream inputStream = assetManager.open("app.properties");
            properties.load(inputStream);
            String carApiToken = properties.getProperty("carApiToken");

            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "https://carmakemodeldb.com/api/v1/car-lists/get/years/desc?api_token=" + carApiToken;

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Gson gson = new Gson();
                            JsonObject[] carYears = gson.fromJson(response, JsonObject[].class);
                            ArrayList<String> carYearsStringList = new ArrayList<>();
                            carYearsStringList.add("");
                            for (JsonObject year : carYears){
                                carYearsStringList.add(year.get("year").getAsString());
                            }
                            String[] carYearsString = new String[carYearsStringList.size()];
                            currentActivity.populateYearDropdown(carYearsStringList.toArray(carYearsString));
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });

            queue.add(stringRequest);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadMakes(String year, AddCarActivity currentActivity){
        try {
            Properties properties = new Properties();
            AssetManager assetManager = getBaseContext().getAssets();
            InputStream inputStream = assetManager.open("app.properties");
            properties.load(inputStream);
            String carApiToken = properties.getProperty("carApiToken");

            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "https://carmakemodeldb.com/api/v1/car-lists/get/makes/" + year + "?api_token=" + carApiToken;

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Gson gson = new Gson();
                            JsonObject[] carMakes = gson.fromJson(response, JsonObject[].class);
                            ArrayList<String> carMakesStringList = new ArrayList<>();
                            carMakesStringList.add("");
                            for (JsonObject make : carMakes){
                                carMakesStringList.add(make.get("make").getAsString());
                            }
                            String[] carMakesString = new String[carMakesStringList.size()];
                            currentActivity.populateMakeDropdown(year, carMakesStringList.toArray(carMakesString));
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });

            queue.add(stringRequest);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadModels(String year, String make, AddCarActivity currentActivity){
        try {
            Properties properties = new Properties();
            AssetManager assetManager = getBaseContext().getAssets();
            InputStream inputStream = assetManager.open("app.properties");
            properties.load(inputStream);
            String carApiToken = properties.getProperty("carApiToken");

            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "https://carmakemodeldb.com/api/v1/car-lists/get/models/" + year + "/" + make + "?api_token=" + carApiToken;

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Gson gson = new Gson();
                            JsonObject[] carModels = gson.fromJson(response, JsonObject[].class);
                            ArrayList<String> carModelsStringList = new ArrayList<>();
                            carModelsStringList.add("");
                            for (JsonObject model : carModels){
                                carModelsStringList.add(model.get("model").getAsString());
                            }
                            String[] carModelsString = new String[carModelsStringList.size()];
                            currentActivity.populateModelDropdown(year, make, carModelsStringList.toArray(carModelsString));
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });

            queue.add(stringRequest);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}