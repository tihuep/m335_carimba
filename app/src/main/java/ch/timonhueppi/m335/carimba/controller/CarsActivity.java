package ch.timonhueppi.m335.carimba.controller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import ch.timonhueppi.m335.carimba.R;
import ch.timonhueppi.m335.carimba.service.UserService;

public class CarsActivity extends AppCompatActivity {


    UserService userService;
    boolean serviceBound = false;

    LinearLayout svLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cars);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        if (myToolbar != null) {
            setSupportActionBar(myToolbar);
        }


        svLayout = findViewById(R.id.svLayout);

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
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
        serviceBound = false;
    }


    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            UserService.LocalBinder binder = (UserService.LocalBinder) service;
            userService = binder.getService();
            serviceBound = true;

            //put actions here
            userService.initFirebaseAuth();

            removeListItems();
            generateList();

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            serviceBound = false;
        }
    };

    private void generateList(){



        generateListItem();
        generateListItem();
        generateListItem();
        generateListItem();
        generateListItem();
        generateListItem();
        generateListItem();
        generateListItem();
        generateListItem();
        generateListItem();
        generateListItem();
        generateListItem();
        generateListItem();
        generateListItem();
        generateListItem();
        generateListItem();
        generateListItem();
    }

    private LinearLayout generateListItem(){
        LinearLayout svLayout = findViewById(R.id.svLayout);
        LinearLayout newItem = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.car_list_item, null);
        TextView text7 = newItem.findViewById(R.id.textView7);
        text7.setText("moiga");

        newItem.setPadding(0, 0, 0, 5);


        svLayout.addView(newItem);



        return newItem;
    }

    private void removeListItems(){
        LinearLayout svLayout = findViewById(R.id.svLayout);
        svLayout.removeAllViews();
    }

    private void setButtonHandlers(){

    }
}