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
import android.widget.Toast;

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
        //reference: https://developer.android.com/training/appbar/setting-up
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        if (myToolbar != null) {
            setSupportActionBar(myToolbar);
        }
        //reference: until here

        ddCarYear = findViewById(R.id.ddCarYear);
        ddCarMake = findViewById(R.id.ddCarMake);
        ddCarModel = findViewById(R.id.ddCarModel);
        tiCarAddTrim = findViewById(R.id.tiCarAddTrim);
        tiCarAddTrim.setEnabled(false);
        btnCarAddFinished = findViewById(R.id.btnCarAddFinished);
        btnCarAddFinished.setEnabled(false);

        setButtonHandlers();
    }

    //reference (method): https://www.vogella.com/tutorials/AndroidActionBar/article.html
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }

    //reference (method): https://www.vogella.com/tutorials/AndroidActionBar/article.html
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnMenuSearch:
                Toast searchNotSupported = Toast.makeText(this, getString(R.string.searchNotSupported), Toast.LENGTH_SHORT);
                searchNotSupported.show();
                return true;
            case R.id.btnMenuLogout:
                userService.logout();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //reference (method): https://developer.android.com/guide/components/bound-services
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

    //reference (method): https://developer.android.com/guide/components/bound-services
    @Override
    protected void onStop() {
        super.onStop();
        unbindService(userConnection);
        unbindService(carConnection);
        userServiceBound = false;
        carServiceBound = false;
    }

    //reference (object): https://developer.android.com/guide/components/bound-services
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

    //reference (object): https://developer.android.com/guide/components/bound-services
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
        //reference: https://stackoverflow.com/questions/11920754/android-fill-spinner-from-java-code-programmatically
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, carYears);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ddCarYear.setAdapter(adapter);
        //reference: until here
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
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    public void populateMakeDropdown(String year, String[] carMakes){
        //reference: https://stackoverflow.com/questions/11920754/android-fill-spinner-from-java-code-programmatically
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, carMakes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ddCarMake.setAdapter(adapter);
        //reference: until here
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
        //reference: https://stackoverflow.com/questions/11920754/android-fill-spinner-from-java-code-programmatically
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[0]);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ddCarMake.setAdapter(adapter);
        //reference: until here
    }

    public void populateModelDropdown(String[] carModels){
        //reference: https://stackoverflow.com/questions/11920754/android-fill-spinner-from-java-code-programmatically
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, carModels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ddCarModel.setAdapter(adapter);
        //reference: until here
        ddCarModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (id != 0)
                    carService.selectedModel = carModels[Math.toIntExact(id)];
                tiCarAddTrim.setEnabled(id != 0);
                btnCarAddFinished.setEnabled(id != 0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    public void emptyModelDropdown(){
        //reference: https://stackoverflow.com/questions/11920754/android-fill-spinner-from-java-code-programmatically
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[0]);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ddCarModel.setAdapter(adapter);
        // reference: until here
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