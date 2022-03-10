package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog ;


public class CalendarActivity extends AppCompatActivity {
    //DatabaseReference DBFecha;
    CalendarView cal;
    private String reservationDate;
    private Button buttonLogout;
    String userLogged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        userLogged = getIntent().getExtras().getString("userLogged");
        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(buttonLogoutClickEvent());

        cal = findViewById(R.id.CalendarIn);

        cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                int currentMonth = month + 1;

                int newDay = day + 1;
                String selectedMonth = Integer.toString(currentMonth);
                String selectedDay = Integer.toString(day);
                String selectedDayValidation = Integer.toString(newDay);

                if (currentMonth < 10) {
                    selectedMonth = "0" + Integer.toString(currentMonth);
                }

                if (day < 10) {
                    selectedDay = "0" + Integer.toString(day);
                    selectedDayValidation = "0" + Integer.toString(newDay);
                }

                String currentStringDate = Integer.toString(year) + "-" + selectedMonth + "-" + selectedDay;
                String currentStringDateValidation = Integer.toString(year) + "-" + selectedMonth + "-" + selectedDayValidation;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date selectedDate = simpleDateFormat.parse(currentStringDateValidation);
                    if(new Date().after(selectedDate)){
                        reservationDate = "";
                        showAlertDialog("Error", "Seleccione una fecha válida.", SweetAlertDialog.ERROR_TYPE);
                    }else{
                        reservationDate = currentStringDate;
                    }
                }catch (ParseException error){
                    showAlertDialog("Error", error.getMessage(), SweetAlertDialog.ERROR_TYPE);
                }
            }
        });
    }

    private void showAlertDialog (String title, String message, int icon) {
        SweetAlertDialog sweetAlert = new SweetAlertDialog(CalendarActivity.this, icon);
        sweetAlert.setTitleText(title);
        sweetAlert.setContentText(message);
        sweetAlert.show();
    }

    public void onClickSiguienteClock (View view) {
        if (reservationDate == null || reservationDate.isEmpty()) {
            showAlertDialog("Error", "Seleccione una fecha", SweetAlertDialog.ERROR_TYPE);
            return;
        }
        goToClockActivity();
    }

    private void goToClockActivity () {
        Intent clockActivity = new Intent(CalendarActivity.this, ClockActivity.class);
        clockActivity.putExtra("userLogged", userLogged);
        clockActivity.putExtra("reservationDate", reservationDate);
        startActivity(clockActivity);
    }

    private View.OnClickListener buttonLogoutClickEvent () {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sessionAndroid = getSharedPreferences("appSession", Context.MODE_PRIVATE);
                SharedPreferences.Editor sessionEditor = sessionAndroid.edit();
                sessionEditor.clear();
                sessionEditor.commit();

                startActivity(new Intent(CalendarActivity.this, MainActivity.class));
                finish();
            }
        };
    }
}