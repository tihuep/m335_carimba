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

import java.util.ArrayList;
import java.util.List;

import ch.timonhueppi.m335.carimba.R;
import ch.timonhueppi.m335.carimba.model.Car;
import ch.timonhueppi.m335.carimba.service.CarService;
import ch.timonhueppi.m335.carimba.service.UserService;

public class AddCarActivity extends AppCompatActivity {

    UserService userService;
    CarService carService;
    boolean userServiceBound = false;
    boolean carServiceBound = false;

    Spinner ddCarYear;
    Spinner ddCarMake;
    Spinner ddCarModel;
    EditText tiCarAddTrim;
    Button btnCarAddFinished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        if (myToolbar != null) {
            setSupportActionBar(myToolbar);
        }

        ddCarYear = findViewById(R.id.ddCarYear);
        ddCarMake = findViewById(R.id.ddCarMake);
        ddCarModel = findViewById(R.id.ddCarModel);
        tiCarAddTrim = findViewById(R.id.tiCarAddTrim);
        tiCarAddTrim.setEnabled(false);
        btnCarAddFinished = findViewById(R.id.btnCarAddFinished);
        btnCarAddFinished.setEnabled(false);

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
            populateDropdowns();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            userServiceBound = false;
        }
    };

    private void populateDropdowns(){
        carService.loadYears(this);
    }

    public void populateYearDropdown(String[] carYears){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, carYears);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ddCarYear.setAdapter(adapter);
        AddCarActivity that = this;
        ddCarYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (id != 0) {
                    carService.loadMakes(carYears[Math.toIntExact(id)], that);
                    carService.selectedYear = carYears[Math.toIntExact(id)];
                }
                emptyMakeDropdown();
                emptyModelDropdown();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void emptyYearDropdown(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, new String[0]);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ddCarYear.setAdapter(adapter);
    }

    public void populateMakeDropdown(String year, String[] carMakes){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, carMakes);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ddCarMake.setAdapter(adapter);
        AddCarActivity that = this;
        ddCarMake.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (id != 0) {
                    carService.loadModels(year, carMakes[Math.toIntExact(id)], that);
                    carService.selectedMake = carMakes[Math.toIntExact(id)];
                }
                emptyModelDropdown();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void emptyMakeDropdown(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, new String[0]);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ddCarMake.setAdapter(adapter);
    }

    public void populateModelDropdown(String year, String make, String[] carModels){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, carModels);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ddCarModel.setAdapter(adapter);
        AddCarActivity that = this;
        ddCarModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (id != 0)
                    carService.selectedModel = carModels[Math.toIntExact(id)];
                tiCarAddTrim.setEnabled(id != 0);
                btnCarAddFinished.setEnabled(id != 0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void emptyModelDropdown(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, new String[0]);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ddCarModel.setAdapter(adapter);
        tiCarAddTrim.setEnabled(false);
        btnCarAddFinished.setEnabled(false);
    }


    private void setButtonHandlers(){
        btnCarAddFinished.setOnClickListener(v -> {
            carService.addCar(new Car(userService.getCurrentUser().getUid(), carService.selectedYear, carService.selectedMake, carService.selectedModel, tiCarAddTrim.getText().toString()));

            setResult(RESULT_OK, null);
            finish();
        });
    }

}