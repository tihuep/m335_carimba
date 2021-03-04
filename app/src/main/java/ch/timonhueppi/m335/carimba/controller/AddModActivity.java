package ch.timonhueppi.m335.carimba.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.lang.reflect.Field;

import ch.timonhueppi.m335.carimba.R;
import ch.timonhueppi.m335.carimba.model.Car;
import ch.timonhueppi.m335.carimba.model.Mod;
import ch.timonhueppi.m335.carimba.model.ModCategory;
import ch.timonhueppi.m335.carimba.service.CarService;
import ch.timonhueppi.m335.carimba.service.UserService;

public class AddModActivity extends AppCompatActivity {

    UserService userService;
    CarService carService;
    boolean userServiceBound = false;
    boolean carServiceBound = false;

    Spinner ddAddModCategory;
    EditText tiAddModTitle;
    EditText tiAddModDetails;
    Button btnAddModPhoto;
    Button btnAddModFinished;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mod);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        if (myToolbar != null) {
            setSupportActionBar(myToolbar);
        }

        ddAddModCategory = findViewById(R.id.ddAddModCategory);
        tiAddModTitle = findViewById(R.id.tiAddModTitle);
        tiAddModDetails = findViewById(R.id.tiAddModDetails);
        btnAddModPhoto = findViewById(R.id.btnAddModPhoto);
        btnAddModFinished = findViewById(R.id.btnAddModFinished);

        setButtonHandlers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.btnMenuSearch:
                return true;
            case R.id.btnMenuLogout:
                // This is a method, that performs logging user out
                userService.logout();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            // Here could be other cases, if you added more than one menu
            // item
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, UserService.class);
        bindService(intent, userConnection, Context.BIND_AUTO_CREATE);
    }

    private void bindCarService(){
        Intent intent = new Intent(this, CarService.class);
        bindService(intent, carConnection, Context.BIND_AUTO_CREATE);
    }


    @Override
    protected void onStop() {
        super.onStop();
        unbindService(userConnection);
        userServiceBound = false;
        carServiceBound = false;
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection userConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            UserService.LocalBinder binder = (UserService.LocalBinder) service;
            userService = binder.getService();
            userServiceBound = true;

            //put actions here
            userService.initFirebaseAuth();
            bindCarService();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            userServiceBound = false;
        }
    };

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection carConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            CarService.LocalBinder binder = (CarService.LocalBinder) service;
            carService = binder.getService();
            carServiceBound= true;

            //put actions here
            carService.initFirebaseFirestore();
            populateDropdown();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            userServiceBound = false;
        }
    };

    private void populateDropdown(){
        String[] modCategories = new String[ModCategory.values().length];
        for (int i = 0; i < modCategories.length; i++){
            String categoryValue = ModCategory.values()[i].name();

            modCategories[i] = getString(getResId(categoryValue, R.string.class));
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, modCategories);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ddAddModCategory.setAdapter(adapter);
        AddModActivity that = this;
        carService.selectedModCategory = ModCategory.values()[0];
        ddAddModCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (id != 0) {/*
                    carService.loadMakes(modCategories[Math.toIntExact(id)], that);
                    carService.selectedYear = modCategories[Math.toIntExact(id)];*/
                    carService.selectedModCategory = ModCategory.values()[Math.toIntExact(id)];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private int getResId(String resName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void backToCarActivity(){
        setResult(RESULT_OK, null);
        finish();
    }

    private void setButtonHandlers(){
        btnAddModFinished.setOnClickListener(v -> {
            Mod mod = new Mod(carService.selectedCar.getCarId(), carService.selectedModCategory, tiAddModTitle.getText().toString(), tiAddModDetails.getText().toString(), "--");
            carService.addModToCar(this, carService.selectedCar.getCarId(), mod);
        });
    }
}