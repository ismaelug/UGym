package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.model.Reservation;
import com.example.myapplication.model.Schedule;
import com.example.myapplication.model.ScheduleItem;
import com.example.myapplication.model.SelectedSchedule;
import com.example.myapplication.model.adapters.ScheduleAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class ClockActivity extends AppCompatActivity {
    DatabaseReference dbReference;

    private static final float localCapacity = 4;
    private static final float permittedCapacity = 50;
    private static final float availableCapacity = (localCapacity * permittedCapacity) / 100;

    private ArrayList<Schedule> scheduleList = new ArrayList<Schedule>();
    private ListView scheduleListView;
    private ScheduleAdapter scheduleAdapter;
    private LinearLayout linearLayoutButtonActions;
    private Button buttonCancelSchedule, buttonSaveSchedule;

    private String reservationDate = "";
    private long reservationIndex = 0;
    private String userLogged;
    private ArrayList<SelectedSchedule> selectedScheduleList = new ArrayList<SelectedSchedule>();
    private ArrayList<ScheduleItem> uploadedScheduleList = new ArrayList<ScheduleItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);

        userLogged = getIntent().getExtras().getString("userLogged");
        reservationDate = getIntent().getExtras().getString("reservationDate");

        dbReference = FirebaseDatabase.getInstance().getReference();

        scheduleListView = (ListView)findViewById(R.id.listViewScheduleButtons);
        linearLayoutButtonActions = (LinearLayout) findViewById(R.id.buttonActions);
        buttonCancelSchedule = (Button) findViewById(R.id.buttonCancelSchedule);
        buttonSaveSchedule = (Button) findViewById(R.id.buttonSaveSchedule);
        linearLayoutButtonActions.setVisibility(View.INVISIBLE);

        buttonCancelSchedule.setOnClickListener(buttonCancelScheduleEvent());
        buttonSaveSchedule.setOnClickListener(buttonSaveScheduleEvent());

        getScheduleList();
    }

    private View.OnClickListener buttonCancelScheduleEvent(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayoutButtonActions.setVisibility(View.INVISIBLE);
                for(int selectedIndex = 0; selectedIndex < selectedScheduleList.size(); selectedIndex = selectedIndex + 1){
                    SelectedSchedule currentSchedule = selectedScheduleList.get(selectedIndex);
                    View currentView = scheduleListView.getChildAt(currentSchedule.getIndexSchedule());
                    Button currentButton = currentView.findViewById(R.id.buttonScheduleRight);
                    if(currentSchedule.getComesFrom().equals("left")){
                        currentButton = currentView.findViewById(R.id.buttonScheduleLeft);
                    }

                    currentButton.setEnabled(currentSchedule.isPreviousEnabledState());
                    currentButton.setBackgroundColor(getResources().getColor(currentSchedule.getPreviousBgColor()));

                }
                selectedScheduleList.clear();
                uploadedScheduleList.clear();
            }
        };
    }

    private void getScheduleList () {
        dbReference.child("Schedules").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                scheduleList.clear();
                for(DataSnapshot currentSchedule : snapshot.getChildren()) {
                    Schedule scheduleItem = currentSchedule.getValue(Schedule.class);
                    scheduleList.add(scheduleItem);
                }

                scheduleAdapter = new ScheduleAdapter(ClockActivity.this, scheduleList, reservationDate, userLogged);
                scheduleListView.setAdapter(scheduleAdapter);

                getScheduleAvailability();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getScheduleAvailability () {
        dbReference.child("Reservation").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reservationIndex = 0;
                if(snapshot.exists()) {
                    reservationIndex = snapshot.getChildrenCount() + 1;
                }

                for(int scheduleIndex = 0; scheduleIndex < scheduleList.size(); scheduleIndex = scheduleIndex + 1) {
                    try {
                         boolean isAvalaibleLeft = true;
                         boolean isAvalaibleRight = true;
                        int bgButtonColor = R.color.red;

                        View currentView = scheduleListView.getChildAt(scheduleIndex);
                        Button buttonLeft = currentView.findViewById(R.id.buttonScheduleLeft);
                        Button buttonRight = currentView.findViewById(R.id.buttonScheduleRight);

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        Date selectedDate = simpleDateFormat.parse(reservationDate + " " + scheduleList.get(scheduleIndex).getItem1().getStartTime());

                        if (new Date().after(selectedDate)) {
                            isAvalaibleLeft = false;
                            isAvalaibleRight = false;
                        }else {
                            isAvalaibleLeft = validateAvailability(snapshot, scheduleList.get(scheduleIndex).getItem1());
                            isAvalaibleRight = validateAvailability(snapshot, scheduleList.get(scheduleIndex).getItem2());
                        }

                        buttonLeft.setEnabled(isAvalaibleLeft);
                        buttonRight.setEnabled(isAvalaibleRight);

                        if(isAvalaibleLeft){
                            bgButtonColor = R.color.green;
                        }
                        buttonLeft.setBackgroundColor(getResources().getColor(bgButtonColor));

                        bgButtonColor = R.color.red;
                        if(isAvalaibleRight){
                            bgButtonColor = R.color.green;
                        }
                        buttonRight.setBackgroundColor(getResources().getColor(bgButtonColor));

                        buttonLeft.setOnClickListener(scheduleButtonClickEvent(scheduleList.get(scheduleIndex).getItem1(), scheduleIndex, "left", isAvalaibleLeft, bgButtonColor));
                        buttonRight.setOnClickListener(scheduleButtonClickEvent(scheduleList.get(scheduleIndex).getItem2(), scheduleIndex,"right", isAvalaibleRight, bgButtonColor));
                    }catch (Exception error){
                        showAlertDialog("Error", error.getMessage(), SweetAlertDialog.ERROR_TYPE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private boolean validateAvailability (DataSnapshot listOfReservation, ScheduleItem currentSchedule) {
        float countReservations = 0;
        for (DataSnapshot currentReservation : listOfReservation.getChildren()) {
            Reservation reservationItem = currentReservation.getValue(Reservation.class);

            if(reservationItem.getReservationDate().equals(reservationDate)) {
                if (currentSchedule.getId().equals(reservationItem.getReservationSchedule())) {
                    countReservations = countReservations + 1;
                    if (userLogged.equals(reservationItem.getReservationUser())) {
                        countReservations = availableCapacity;
                        break;
                    }
                }
            }
        }

        return countReservations < availableCapacity;
    }

    private void showAlertDialog (String title, String message, int icon) {
        SweetAlertDialog sweetAlert = new SweetAlertDialog(ClockActivity.this, icon);
        sweetAlert.setTitleText(title);
        sweetAlert.setContentText(message);
        sweetAlert.show();
    }

    private View.OnClickListener scheduleButtonClickEvent (ScheduleItem currentSchedule, int comesFromIndex, String comesFrom, boolean enabledState, int bgColor) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayoutButtonActions.setVisibility(View.VISIBLE);
                SelectedSchedule selectedSchedule = new SelectedSchedule();

                selectedSchedule.setIndexSchedule(comesFromIndex);
                selectedSchedule.setPreviousBgColor(bgColor);
                selectedSchedule.setPreviousEnabledState(enabledState);
                selectedSchedule.setComesFrom(comesFrom);

                selectedScheduleList.add(selectedSchedule);

                if(comesFrom.equals("left")){
                    view.setBackgroundColor(getResources().getColor(R.color.white));
                }else{
                    view.setBackgroundColor(getResources().getColor(R.color.white));
                }
            }
        };
    }

    private View.OnClickListener buttonSaveScheduleEvent() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SweetAlertDialog(ClockActivity.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Confirmar Reservación")
                .setContentText(getSaveMessage() +"?")
                .showCancelButton(true)
                .setCancelText("Cancelar")
                .setConfirmText("Confirmar")
                .setConfirmClickListener(
                        new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                saveReservation();
                                sDialog.dismissWithAnimation();
                            }
                        }
                )
                .show();
            }
        };
    }

    private String getSaveMessage () {
        String saveMessage = "¿Desea reservar su turno para la fecha " + reservationDate + " en los horarios:";
        String scheduleMessage = "";

        for(SelectedSchedule currentSelectedtSchedule : selectedScheduleList){
            Schedule currentSchedule = scheduleList.get(currentSelectedtSchedule.getIndexSchedule());
            ScheduleItem currentScheduleItem = currentSchedule.getItem2();

            if(currentSelectedtSchedule.getComesFrom().equals("left")){
                currentScheduleItem = currentSchedule.getItem1();
            }

            String currentTime = " De " + currentScheduleItem.getStartTime() + " a " + currentScheduleItem.getEndTime();
            if(!scheduleMessage.isEmpty()){
                currentTime = "," + currentTime;
            }

            scheduleMessage = scheduleMessage + currentTime;
        }

        return saveMessage + scheduleMessage;
    }

    private void saveReservation () {
        for(SelectedSchedule currentSelectedtSchedule : selectedScheduleList){
            Schedule currentSchedule = scheduleList.get(currentSelectedtSchedule.getIndexSchedule());

            ScheduleItem currentScheduleItem = currentSchedule.getItem2();
            if(currentSelectedtSchedule.getComesFrom().equals("left")){
                currentScheduleItem = currentSchedule.getItem1();
            }

            saveReservationItem(currentScheduleItem);
            reservationIndex = reservationIndex + 1;
        }

        afterUploadReservation();
    }

    private void afterUploadReservation() {
        new SweetAlertDialog(ClockActivity.this, SweetAlertDialog.SUCCESS_TYPE)
        .setTitleText("Reservación Registrada")
        .setContentText("Su reservación ha sido generada con éxito")
        .showCancelButton(false)
        .setConfirmText("Ver Código QR")
        .setConfirmClickListener(
                new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {


                        Intent qrCodeActivity = new Intent(ClockActivity.this, QRCodeActivity.class);

                        qrCodeActivity.putExtra("reservationUser", userLogged);
                        qrCodeActivity.putExtra("reservationDate", reservationDate);
                        qrCodeActivity.putExtra("reservationCreatingDate", getCreationDate());
                        qrCodeActivity.putExtra("numReservation", uploadedScheduleList.size());

                        for(int scheduleIndex = 0; scheduleIndex < uploadedScheduleList.size(); scheduleIndex = scheduleIndex + 1){
                            ScheduleItem currentSchedule = uploadedScheduleList.get(scheduleIndex);
                            int newIndex = scheduleIndex + 1;
                            qrCodeActivity.putExtra("reservationStartTime"+newIndex, currentSchedule.getStartTime());
                            qrCodeActivity.putExtra("reservationEndTime"+newIndex, currentSchedule.getEndTime());
                        }

                        startActivity(qrCodeActivity);

                        selectedScheduleList.clear();
                        uploadedScheduleList.clear();
                        sDialog.dismissWithAnimation();
                    }
                }
        )
        .show();
    }

    private void saveReservationItem (ScheduleItem currentSchedule) {
        Reservation currentReservation = new Reservation();
        currentReservation.setCreated(getCreationDate());
        currentReservation.setReservationDate(reservationDate);
        currentReservation.setReservationUser(userLogged);
        currentReservation.setReservationSchedule(currentSchedule.getId());

        String childIndex = reservationIndex + "-reservation";

        dbReference.child("Reservation").child(childIndex).setValue(currentReservation).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    uploadedScheduleList.add(currentSchedule);
                }else{
                    Toast.makeText(ClockActivity.this, "Error: No se pudo reservar el Horario de " + currentSchedule.getStartTime() + " a " + currentSchedule.getEndTime(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String getCreationDate () {
        String creationDate = "";
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormatrmat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        creationDate = simpleDateFormatrmat.format(calendar.getTime());

        return creationDate;
    }
}