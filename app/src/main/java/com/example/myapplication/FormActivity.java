package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class FormActivity extends AppCompatActivity {

    private EditText mNombres;
    private EditText mApellidos;
    private EditText mCedula;
    private EditText mEmail;
    private EditText mTelf;
    private EditText passw1;
    private EditText passw2;
    private Button bRegister;

    private String Nombres;
    private String Apellidos;
    private String Cedula;
    private String Email;
    private String Telf;
    private String Pwd1;
    private String Pwd2;

    FirebaseAuth mReg;
    DatabaseReference mDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        mReg = FirebaseAuth.getInstance();
        mDB = FirebaseDatabase.getInstance().getReference();

        mNombres = (EditText) findViewById(R.id.txt_Name);
        mApellidos = (EditText) findViewById(R.id.txt_Apellidos);
        mCedula = (EditText) findViewById(R.id.txt_ID);
        mEmail = (EditText) findViewById(R.id.edit_email);
        mTelf = (EditText) findViewById(R.id.edit_Phone);
        passw1 = (EditText) findViewById(R.id.edit_Pwd1);
        passw2 = (EditText) findViewById(R.id.edit_Pwd2);
        bRegister = (Button) findViewById(R.id.btn_Guardar);

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Nombres = mNombres.getText().toString();
                Apellidos = mApellidos.getText().toString();
                Cedula = mCedula.getText().toString();
                Email = mEmail.getText().toString();
                Telf = mTelf.getText().toString();
                Pwd1 = passw1.getText().toString();
                Pwd2 = passw2.getText().toString();

                if (!Nombres.isEmpty() && !Apellidos.isEmpty() && !Cedula.isEmpty() && !Email.isEmpty() && !Telf.isEmpty() && !Pwd1.isEmpty() && !Pwd2.isEmpty()) {

                    if (Pwd1.length() <= 6 || Pwd2.length() <= 6) {
                        Toast.makeText(FormActivity.this, "CONTRASEÑA DEBE TENER AL MENOS 6 CARACTERES", Toast.LENGTH_LONG).show();

                    } else if (Pwd1.equals(Pwd2)) {
                         Pwd1 = Pwd1;
                    } else {
                        onStop();
                        onRestart();
                        Toast.makeText(FormActivity.this, "CONTRASEÑAS NO COINCIDEN", Toast.LENGTH_LONG).show();
                    }
                    registerUser();
                } else {
                    Toast.makeText(FormActivity.this, "FAVOR LLENE TODOS LOS DATOS", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void registerUser () {

        mReg.createUserWithEmailAndPassword(Email, Pwd1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    Map<String, Object> map = new HashMap<>();
                    map.put("Names", Nombres);
                    map.put("LastName", Apellidos);
                    map.put("Email", Email);
                    map.put("Identity", Cedula);
                    map.put("Telf", Telf);
                    map.put("Password", Pwd1);

                    String id = mReg.getCurrentUser().getUid();

                    mDB.child("Users").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if (task2.isSuccessful()) {
                                Toast.makeText(FormActivity.this, "DATOS GUARDADOS CON ÉXITO", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(FormActivity.this, MainActivity.class));
                                finish();
                            }
                            else {
                                Toast.makeText(FormActivity.this, "ERROR AL GUARDAR DATOS", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
            }
                else {
                    Toast.makeText(FormActivity.this, "USUARIO YA EXISTENTE", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}