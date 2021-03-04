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
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import ch.timonhueppi.m335.carimba.R;
import ch.timonhueppi.m335.carimba.model.Car;
import ch.timonhueppi.m335.carimba.service.CarService;
import ch.timonhueppi.m335.carimba.service.UserService;

public class CarsActivity extends AppCompatActivity {

    UserService userService;
    CarService carService;
    boolean userServiceBound = false;
    boolean carServiceBound = false;

    final int CAR_ADDED = 1;

    LinearLayout svLayout;
    Button btnCarsAdd;
    ImageButton btnCarDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cars);
        //reference: https://developer.android.com/training/appbar/setting-up
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        if (myToolbar != null) {
            setSupportActionBar(myToolbar);
        }
        //reference: until here

        svLayout = findViewById(R.id.svLayout);
        btnCarsAdd = findViewById(R.id.btnCarsAdd);
        btnCarDetail = findViewById(R.id.btnCarDetail);

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

    //reference (method): https://developer.android.com/guide/components/bound-services
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
            loadCars();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            userServiceBound = false;
        }
    };

    private void loadCars(){
        carService.getCarsOfUser(this, userService.getCurrentUser().getUid());
    }

    public void generateList(){
        removeListItems();
        for (Car car : carService.carList){
            generateListItem(car);
        }
    }

    private LinearLayout generateListItem(Car car){
        LinearLayout svLayout = findViewById(R.id.svLayout);
        LinearLayout newItem = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.car_list_item, null);
        TextView tvCarPrimary = newItem.findViewById(R.id.tvCarPrimary);
        TextView tvCarSecondary = newItem.findViewById(R.id.tvCarSecondary);
        tvCarPrimary.setText(car.getMake() + " " + car.getModel());
        tvCarSecondary.setText(car.getYear() + ", " + car.getTrim());

        ImageButton btnCarDetail = newItem.findViewById(R.id.btnCarDetail);
        btnCarDetail.setOnClickListener(v -> {
            carService.selectedCar = car;
            Intent intent = new Intent(this, CarActivity.class);
            startActivity(intent);
        });

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
    }
}