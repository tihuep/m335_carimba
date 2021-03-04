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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Field;

import ch.timonhueppi.m335.carimba.R;
import ch.timonhueppi.m335.carimba.model.Car;
import ch.timonhueppi.m335.carimba.model.Mod;
import ch.timonhueppi.m335.carimba.model.ModCategory;
import ch.timonhueppi.m335.carimba.service.CarService;
import ch.timonhueppi.m335.carimba.service.UserService;

public class CarActivity extends AppCompatActivity {

    UserService userService;
    CarService carService;
    boolean userServiceBound = false;
    boolean carServiceBound = false;

    final int MOD_ADDED = 1;

    LinearLayout svModsLayout;
    Button btnModsAdd;
    TextView tvCarTitlePrimary;
    TextView tvCarTitleSecondary;
    ImageButton btnCarDelete;

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
        tvCarTitleSecondary = findViewById(R.id.tvCarTitleSecondary);
        btnCarDelete = findViewById(R.id.btnCarDelete);

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
        unbindService(carConnection);
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
            setTitleTexts();
            loadMods();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            userServiceBound = false;
        }
    };

    private void setTitleTexts(){
        Car car = carService.selectedCar;
        tvCarTitlePrimary.setText(car.getMake() + " " + car.getModel());
        tvCarTitleSecondary.setText(car.getYear() + "; " + car.getTrim());
    }

    private void loadMods(){
        carService.getModsOfCar(this, carService.selectedCar.getCarId());
    }

    public void generateList(int carListId){
        removeListItems();
        for (Mod mod : carService.carList.get(carListId).getMods()){
            generateListItem(mod);
        }
    }

    private LinearLayout generateListItem(Mod mod){
        LinearLayout svModsLayout = findViewById(R.id.svModsLayout);
        LinearLayout newItem = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.mod_list_item, null);
        TextView tvModPrimary = newItem.findViewById(R.id.tvModPrimary);
        TextView tvModSecondary = newItem.findViewById(R.id.tvModSecondary);
        tvModPrimary.setText(getResId(mod.getCategory().name(), R.string.class));
        tvModSecondary.setText(mod.getTitle());

        ImageButton btnModDetail = newItem.findViewById(R.id.btnModDetail);
        btnModDetail.setOnClickListener(v -> {
            carService.selectedMod = mod;
            Intent intent = new Intent(this, ModActivity.class);
            startActivity(intent);
        });

        newItem.setPadding(0, 0, 0, 5);

        svModsLayout.addView(newItem);
        return newItem;
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

    private void removeListItems(){
        LinearLayout svModsLayout = findViewById(R.id.svModsLayout);
        svModsLayout.removeAllViews();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MOD_ADDED) {
            if (resultCode == RESULT_OK) {
                //loadMods();
            }
        }
    }

    private void setButtonHandlers(){
        btnModsAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddModActivity.class);
            startActivityForResult(intent, MOD_ADDED);
        });
        btnCarDelete.setOnClickListener(v -> {
            carService.deleteCar(carService.selectedCar.getCarId());
            Intent intent = new Intent(this, CarsActivity.class);
            startActivity(intent);
            finish();
        });
    }
}