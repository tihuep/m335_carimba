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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Field;

import ch.timonhueppi.m335.carimba.R;
import ch.timonhueppi.m335.carimba.service.CarService;
import ch.timonhueppi.m335.carimba.service.UserService;

public class ModActivity extends AppCompatActivity {

    UserService userService;
    CarService carService;
    boolean userServiceBound = false;
    boolean carServiceBound = false;

    ImageButton btnModDelete;
    TextView tvModSubtitle;
    TextView tvModCategory;
    TextView tvModTitle;
    TextView tvModDetailsContent;
    ImageView ivModPhoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mod);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        if (myToolbar != null) {
            setSupportActionBar(myToolbar);
        }


        btnModDelete = findViewById(R.id.btnModDelete);
        tvModSubtitle = findViewById(R.id.tvModSubtitle);
        tvModCategory = findViewById(R.id.tvModCategory);
        tvModTitle = findViewById(R.id.tvModTitle);
        tvModDetailsContent = findViewById(R.id.tvModDetailsContent);
        ivModPhoto = findViewById(R.id.ivModPhoto);

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
            setTexts();
            setImage();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            userServiceBound = false;
        }
    };

    private int getResId(String resName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void setTexts(){
        tvModSubtitle.setText(carService.selectedCar.getYear() + " " + carService.selectedCar.getMake() + " " + carService.selectedCar.getModel());
        tvModCategory.setText(getResId(carService.selectedMod.getCategory().name(), R.string.class));
        tvModTitle.setText(carService.selectedMod.getTitle());
        tvModDetailsContent.setText(carService.selectedMod.getDetails());
    }

    private void setImage(){
        ivModPhoto.setImageBitmap(carService.decodeImage(carService.selectedMod.getPhoto()));
    }

    private void setButtonHandlers(){
        btnModDelete.setOnClickListener(v -> {
            carService.deleteModFromCar(carService.selectedCar.getCarId(), carService.selectedMod.getModId());
            finish();
        });
    }
}