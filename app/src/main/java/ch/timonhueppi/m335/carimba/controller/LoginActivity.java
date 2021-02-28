package ch.timonhueppi.m335.carimba.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;

import ch.timonhueppi.m335.carimba.R;
import ch.timonhueppi.m335.carimba.service.UserService;

public class LoginActivity extends AppCompatActivity {

    UserService userService;
    boolean serviceBound = false;
    final int REQUEST_EXIT = 1;

    EditText tiLoginEmail;
    EditText tiLoginPassword;
    Button btnLoginLogin;
    Button btnLoginSignUp;
    TextView tvLoginException;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tiLoginEmail = findViewById(R.id.tiLoginEmail);
        tiLoginPassword = findViewById(R.id.tiLoginPassword);
        btnLoginLogin = findViewById(R.id.btnLoginLogin);
        btnLoginSignUp = findViewById(R.id.btnLoginSignUp);
        tvLoginException = findViewById(R.id.tvLoginException);
        setButtonHandlers();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EXIT) {
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent(this, CarsActivity.class);
                startActivity(intent);
                finish();
            }
        }
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

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            serviceBound = false;
        }
    };

    private void setButtonHandlers(){
        btnLoginLogin.setOnClickListener(v -> {
            try {
                userService.loginEmailPassword(tiLoginEmail.getText().toString(), tiLoginPassword.getText().toString(), this);
            }catch (Exception e){
                e.printStackTrace();
            }
        });
        btnLoginSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivityForResult(intent, REQUEST_EXIT);
        });

    }

    public void loginCompleted(){
        Intent intent = new Intent(this, CarsActivity.class);
        startActivity(intent);
        finish();
    }

    public void displayException(String error){
        tvLoginException.setText(error != null ? error : "");
    }

}