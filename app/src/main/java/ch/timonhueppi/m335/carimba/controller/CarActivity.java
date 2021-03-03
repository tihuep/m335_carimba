package ch.timonhueppi.m335.carimba.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import ch.timonhueppi.m335.carimba.R;
import ch.timonhueppi.m335.carimba.model.Car;
import ch.timonhueppi.m335.carimba.service.CarService;
import ch.timonhueppi.m335.carimba.service.UserService;

public class CarActivity extends AppCompatActivity {

    UserService userService;
    CarService carService;
    boolean userServiceBound = false;
    boolean carServiceBound = false;

    //final int CAR_ADDED = 1;

    LinearLayout svModsLayout;
    Button btnModsAdd;
    TextView tvCarTitlePrimary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        if (myToolbar != null) {
            setSupportActionBar(myToolbar);
        }

        svModsLayout = findViewById(R.id.svModsLayout);
        btnModsAdd = findViewById(R.id.btnModsAdd);
        tvCarTitlePrimary = findViewById(R.id.tvCarTitlePrimary);

        //setButtonHandlers();
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
            Car car = carService.selectedCar;
            tvCarTitlePrimary.setText(car.getModel());
            //loadCars();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            userServiceBound = false;
        }
    };
/*
    private void loadCars(){
        carService.getCarsOfUser(this, userService.getCurrentUser().getUid());
    }

    public void generateList(){
        removeListItems();
        for (Car car : carService.carList){
            generateListItem(car.getMake(), car.getModel(), car.getYear(), car.getTrim());
        }
    }

    private LinearLayout generateListItem(String make, String model, String year, String trim){
        LinearLayout svLayout = findViewById(R.id.svLayout);
        LinearLayout newItem = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.car_list_item, null);
        TextView tvCarPrimary = newItem.findViewById(R.id.tvCarPrimary);
        TextView tvCarSecondary = newItem.findViewById(R.id.tvCarSecondary);
        tvCarPrimary.setText(make + " " + model);
        tvCarSecondary.setText(year + ", " + trim);

        newItem.setPadding(0, 0, 0, 5);

        svLayout.addView(newItem);
        return newItem;
    }

    private void removeListItems(){
        LinearLayout svLayout = findViewById(R.id.svLayout);
        svLayout.removeAllViews();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAR_ADDED) {
            if (resultCode == RESULT_OK) {
                loadCars();
            }
        }
    }

    private void setButtonHandlers(){
        btnCarsAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddCarActivity.class);
            startActivityForResult(intent, CAR_ADDED);
        });
    }*/
}