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

import ch.timonhueppi.m335.carimba.R;
import ch.timonhueppi.m335.carimba.service.UserService;

public class SignUpActivity extends AppCompatActivity {

    UserService userService;
    boolean serviceBound = false;

    EditText tiSignUpEmail;
    EditText tiSignUpPassword;
    Button btnSignUpSignUp;
    TextView tvSignUpException;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        tiSignUpEmail = findViewById(R.id.tiSignUpEmail);
        tiSignUpPassword = findViewById(R.id.tiSignUpPassword);
        btnSignUpSignUp = findViewById(R.id.btnSignUpSignUp);
        tvSignUpException = findViewById(R.id.tvSignUpException);
        setButtonHandlers();
    }

    //reference (method): https://developer.android.com/guide/components/bound-services
    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, UserService.class);
        bindService(intent, userConnection, Context.BIND_AUTO_CREATE);
    }

    //reference (method): https://developer.android.com/guide/components/bound-services
    @Override
    protected void onStop() {
        super.onStop();
        unbindService(userConnection);
        serviceBound = false;
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
        btnSignUpSignUp.setOnClickListener(v -> {
            try {
                userService.signUpEmailPassword(tiSignUpEmail.getText().toString(), tiSignUpPassword.getText().toString(), this);
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    public void signUpCompleted(){
        setResult(RESULT_OK, null);
        finish();
    }

    public void displayException(String error){
        tvSignUpException.setText(error != null ? error : "");
    }

}