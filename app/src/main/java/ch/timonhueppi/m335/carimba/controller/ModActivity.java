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
import android.widget.Toast;

import java.lang.reflect.Field;

import ch.timonhueppi.m335.carimba.R;
import ch.timonhueppi.m335.carimba.service.CarService;
import ch.timonhueppi.m335.carimba.service.UserService;

public class ModActivity extends AppCompatActivity {

    UserService userService;
    CarService carService;

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
        //reference: https://developer.android.com/training/appbar/setting-up
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        if (myToolbar != null) {
            setSupportActionBar(myToolbar);
        }
        //reference: until here

        btnModDelete = findViewById(R.id.btnModDelete);
        tvModSubtitle = findViewById(R.id.tvModSubtitle);
        tvModCategory = findViewById(R.id.tvModCategory);
        tvModTitle = findViewById(R.id.tvModTitle);
        tvModDetailsContent = findViewById(R.id.tvModDetailsContent);
        ivModPhoto = findViewById(R.id.ivModPhoto);

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

    //reference (object): https://developer.android.com/guide/components/bound-services
    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection userConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            UserService.LocalBinder binder = (UserService.LocalBinder) service;
            userService = binder.getService();

            //put actions here
            userService.initFirebaseAuth();
            bindCarService();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {}
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

            //put actions here
            carService.initFirebaseFirestore();
            setTexts();
            setImage();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {}
    };

    //reference: https://stackoverflow.com/questions/4427608/android-getting-resource-id-from-string
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

    public void backToCarActivityAfterDelete(){
        setResult(RESULT_OK, null);
        finish();
    }

    private void setButtonHandlers(){
        btnModDelete.setOnClickListener(v -> {
            carService.deleteModFromCar(this, carService.selectedCar.getCarId(), carService.selectedMod.getModId());
        });
    }
}