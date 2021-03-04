
package ch.timonhueppi.m335.carimba.service;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Binder;
import android.os.IBinder;
import android.util.Base64;

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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import ch.timonhueppi.m335.carimba.controller.AddCarActivity;
import ch.timonhueppi.m335.carimba.controller.AddModActivity;
import ch.timonhueppi.m335.carimba.controller.CarActivity;
import ch.timonhueppi.m335.carimba.controller.CarsActivity;
import ch.timonhueppi.m335.carimba.controller.ModActivity;
import ch.timonhueppi.m335.carimba.model.Car;
import ch.timonhueppi.m335.carimba.model.Mod;
import ch.timonhueppi.m335.carimba.model.ModCategory;

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

    public Car selectedCar;

    public ModCategory selectedModCategory;

    public Mod selectedMod;

    public ArrayList<Car> carList = new ArrayList<>();

    public void addCar(Car car){
        Map<String, Object> carMap = new HashMap<>();
        carMap.put("userId", car.getUserId());
        carMap.put("year", car.getYear());
        carMap.put("make", car.getMake());
        carMap.put("model", car.getModel());
        carMap.put("trim", car.getTrim());

        mFirestore.collection("cars")
                .add(carMap);
    }

    public void deleteCar(String carId){
        mFirestore.collection("cars").document(carId).collection("mods")
                .get()
                .addOnCompleteListener(task -> {
                    ArrayList<String> modIds = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()){
                        modIds.add(document.getId());
                    }
                    for (String modId : modIds){
                        mFirestore.collection("cars").document(carId).collection("mods").document(modId)
                                .delete();
                    }
                    mFirestore.collection("cars").document(carId)
                            .delete();
                });
    }

    public void getCarsOfUser(CarsActivity currentActivity, String userUid){
        carList = new ArrayList<>();
        mFirestore.collection("cars")
                .get()
                .addOnCompleteListener(task -> {
                    for (QueryDocumentSnapshot document : task.getResult()){
                        Map<String, Object> carMap = document.getData();
                        String user = (String) carMap.get("userId");
                        if (user.equals(userUid)){
                            Car carObject = new Car(document.getId(), user, (String) carMap.get("year"), (String) carMap.get("make"), (String) carMap.get("model"), (String) carMap.get("trim"));
                            carList.add(carObject);
                        }
                    }
                    currentActivity.generateList();
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

    public void getModsOfCar(CarActivity currentActivity, String carUid) {
        int carListId = -1;
        for (int i = 0; i < carList.size(); i++){
            if (carList.get(i).getCarId().equals(carUid)){
                carListId = i;
            }
        }
        final int carListIdFinal = carListId;
        carList.get(carListId).setMods(new ArrayList<Mod>());
        mFirestore.collection("cars").document(carUid).collection("mods")
                .get()
                .addOnCompleteListener(task -> {
                    for (QueryDocumentSnapshot document : task.getResult()){
                        Map<String, Object> modMap = document.getData();
                        Mod modObject = new Mod(document.getId(), carUid, ModCategory.valueOf((String) modMap.get("category")), (String) modMap.get("title"), (String) modMap.get("details"), (String) modMap.get("photo"));
                        carList.get(carListIdFinal).getMods().add(modObject);
                    }
                    currentActivity.generateList(carListIdFinal);
                });
    }

    public void addModToCar(AddModActivity currentActivity, String carId, Mod mod){
        Map<String, Object> modMap = new HashMap<>();
        modMap.put("category", mod.getCategory());
        modMap.put("title", mod.getTitle());
        modMap.put("details", mod.getDetails());
        modMap.put("photo", mod.getPhoto());
        mFirestore.collection("cars").document(carId).collection("mods")
                .add(modMap)
                .addOnCompleteListener(v -> {
                    currentActivity.backToCarActivity();
                });
    }

    public void deleteModFromCar(String carId, String modId){
        mFirestore.collection("cars").document(carId).collection("mods").document(modId)
                .delete();
    }
/*
    public Bitmap rotateImage(Bitmap bitmap){
        //https://stackoverflow.com/questions/9015372/how-to-rotate-a-bitmap-90-degrees
        Matrix matrix = new Matrix();

        matrix.postRotate(90);

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);

        return Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
    }
*/
    public String encodeImage(Bitmap bitmap){
        //https://www.learnhowtoprogram.com/android/gestures-animations-flexible-uis/using-the-camera-and-saving-images-to-firebase
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 1, baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }

    public Bitmap decodeImage(String encodedImage){
        //https://www.learnhowtoprogram.com/android/gestures-animations-flexible-uis/using-the-camera-and-saving-images-to-firebase
        byte[] decodedByteArray = android.util.Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }


}