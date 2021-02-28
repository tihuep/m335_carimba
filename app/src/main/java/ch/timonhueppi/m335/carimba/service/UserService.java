package ch.timonhueppi.m335.carimba.service;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ch.timonhueppi.m335.carimba.controller.LoginActivity;
import ch.timonhueppi.m335.carimba.controller.SignUpActivity;

public class UserService extends Service {
    // Binder given to clients
    private final IBinder binder = new LocalBinder();

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public UserService getService() {
            // Return this instance of LocalService so clients can call public methods
            return UserService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    private FirebaseAuth mAuth;

    public void initFirebaseAuth(){
        mAuth = FirebaseAuth.getInstance();
    }

    public boolean loggedIn(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        return currentUser != null;
    }

    public FirebaseUser getCurrentUser(){
        return mAuth.getCurrentUser();
    }


    public void signUpEmailPassword(String email, String password, SignUpActivity currentActivity){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(currentActivity, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    currentActivity.signUpCompleted();
                    currentActivity.displayException(null);
                }else{
                    currentActivity.displayException(task.getException().getClass().getSimpleName());
                }
            }
        });
    }

    public void loginEmailPassword(String email, String password, LoginActivity currentActivity){
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(currentActivity, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    currentActivity.loginCompleted();
                    currentActivity.displayException(null);
                }else{
                    currentActivity.displayException(task.getException().getClass().getSimpleName());
                }
            }
        });
    }

    public void logout(){
        mAuth.signOut();
    }

}