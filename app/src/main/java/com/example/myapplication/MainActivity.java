package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mLog;

    private EditText mUsername;
    private EditText mPassword;

    private String email;
    private String password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        validateSession();

        mLog = FirebaseAuth.getInstance();

        mUsername = (EditText) findViewById(R.id.Username);
        mPassword = (EditText) findViewById(R.id.Password);
    }



    //Aquí se abre la actividad Form para registrar nuevo usuario al hacer click en el texto "Crear cuenta"
    public void onClickCrearCuenta (View view) {
        Intent intent = new Intent(this, FormActivity.class);
        startActivity(intent);
    }



    //Aquí se abre la actividad Calendar para mostrar calendario cuando el usuario está identificado
    public void onClickIniciarSesion (View view) {

        //Hace referencia al cuadro de Nombre para iniciar sesión

        email = mUsername.getText().toString();
        password = mPassword.getText().toString();

        if (!email.isEmpty() && !password.isEmpty()){

            loginUser();

        } else {
            Toast.makeText(this, "INGRESE DATOS CORRECTAMENTE", Toast.LENGTH_LONG).show();
        }

    }

    private void loginUser () {
        mLog.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    SharedPreferences sessionAndroid = getSharedPreferences("appSession", Context.MODE_PRIVATE);
                    SharedPreferences.Editor sessionEditor = sessionAndroid.edit();
                    sessionEditor.putString("userLogged", email);
                    sessionEditor.commit();

                    goToCalendar(email);
                } else {
                    Toast.makeText(MainActivity.this, "DATOS INCORRECTOS", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void validateSession(){
        SharedPreferences sessionAndroid = getSharedPreferences("appSession", Context.MODE_PRIVATE);

        String userLogged = sessionAndroid.getString("userLogged", "");
        if(!(userLogged == null || userLogged.isEmpty())) {
            goToCalendar(userLogged);
        }
    }

    private void goToCalendar (String userLogged) {
        Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
        intent.putExtra("userLogged", userLogged);
        startActivity(intent);
        finish();
    }

}