package ch.timonhueppi.m335.carimba.controller;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.File;
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

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_GALLERY = 2;

    Spinner ddAddModCategory;
    EditText tiAddModTitle;
    EditText tiAddModDetails;
    Button btnAddModPhoto;
    Button btnAddModFinished;
    ImageView ivAddModPhoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mod);
        //reference: https://developer.android.com/training/appbar/setting-up
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        if (myToolbar != null) {
            setSupportActionBar(myToolbar);
        }
        //reference: until here

        ddAddModCategory = findViewById(R.id.ddAddModCategory);
        tiAddModTitle = findViewById(R.id.tiAddModTitle);
        tiAddModDetails = findViewById(R.id.tiAddModDetails);
        btnAddModPhoto = findViewById(R.id.btnAddModPhoto);
        btnAddModFinished = findViewById(R.id.btnAddModFinished);
        ivAddModPhoto = findViewById(R.id.ivAddModPhoto);

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

        //reference: https://stackoverflow.com/questions/11920754/android-fill-spinner-from-java-code-programmatically
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modCategories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ddAddModCategory.setAdapter(adapter);
        //reference: until here
        carService.selectedModCategory = ModCategory.values()[0];
        ddAddModCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (id != 0)
                    carService.selectedModCategory = ModCategory.values()[Math.toIntExact(id)];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //reference (method): https://stackoverflow.com/questions/4427608/android-getting-resource-id-from-string
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
            Mod mod = new Mod(carService.selectedCar.getCarId(), carService.selectedModCategory, tiAddModTitle.getText().toString(), tiAddModDetails.getText().toString(), carService.takenPhotoEncoded);
            carService.addModToCar(this, carService.selectedCar.getCarId(), mod);
        });
        btnAddModPhoto.setOnClickListener(v -> {
            selectImage(this);
        });
    }

    //reference (method): https://medium.com/@hasangi/capture-image-or-choose-from-gallery-photos-implementation-for-android-a5ca59bc6883
    private void selectImage(Context context) {
        final CharSequence[] options = {getString(R.string.takePhoto), getString(R.string.fromGallery), getString(R.string.cancel)};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your profile picture");

        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals(getString(R.string.takePhoto))) {
                dispatchTakePictureIntent();

            } else if (options[item].equals(getString(R.string.fromGallery))) {
                /*Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , REQUEST_IMAGE_GALLERY);*/
                Toast galleryNotSupported = Toast.makeText(this, getText(R.string.galleryNotSupported), Toast.LENGTH_SHORT);
                galleryNotSupported.show();
            } else if (options[item].equals(getString(R.string.cancel))) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    //reference (method): https://developer.android.com/training/camera/photobasics
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, requestCode, data);
        //reference: https://developer.android.com/training/camera/photobasics
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                //reference: until here
                //imageBitmap = carService.rotateImage(imageBitmap);
                carService.takenPhotoEncoded = carService.encodeImage(imageBitmap);
                ivAddModPhoto.setImageBitmap(imageBitmap);
            } else if (requestCode == REQUEST_IMAGE_GALLERY) {/*
                try {
                    //reference: https://stackoverflow.com/questions/29803924/android-how-to-set-the-photo-selected-from-gallery-to-a-bitmap/29804953
                    final Uri uri = data.getData();
                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    //reference: until here

                    carService.takenPhotoEncoded = carService.encodeImage(imageBitmap);
                    ivAddModPhoto.setImageBitmap(imageBitmap);
                }catch (Exception e){
                    e.printStackTrace();
                }
*/
            }
        }
    }
}