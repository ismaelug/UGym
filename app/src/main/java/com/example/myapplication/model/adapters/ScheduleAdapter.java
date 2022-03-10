package com.example.myapplication.model.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.myapplication.CalendarActivity;
import com.example.myapplication.ClockActivity;
import com.example.myapplication.QRCodeActivity;
import com.example.myapplication.R;
import com.example.myapplication.model.Reservation;
import com.example.myapplication.model.Schedule;
import com.example.myapplication.model.ScheduleItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog ;

public class ScheduleAdapter extends BaseAdapter {
    DatabaseReference dbReference;

    private Context context;
    private ArrayList<Schedule> scheduleList;

    private Button scheduleLeftButton;
    private Button scheduleRightButton;

    private String reservationdDate = "";
    private String userLogged = "";

    public ScheduleAdapter(Context context, ArrayList<Schedule> scheduleList, String reservationDate, String userLogged) {
        this.context = context;
        this.scheduleList = scheduleList;
        this.reservationdDate = reservationDate;
        this.userLogged = userLogged;
        dbReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public int getCount() {
        return scheduleList.size();
    }

    @Override
    public Object getItem(int index) {
        return scheduleList.get(index);
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public View getView(int index, View view, ViewGroup viewGroup) {
        Schedule currentSchedule = (Schedule) getItem(index);

        view = LayoutInflater.from(this.context).inflate(R.layout.schedule_button_item, null);
        scheduleLeftButton = (Button) view.findViewById(R.id.buttonScheduleLeft);
        scheduleRightButton = (Button) view.findViewById(R.id.buttonScheduleRight);

        scheduleLeftButton.setText(currentSchedule.getItem1().getStartTime() + " - " + currentSchedule.getItem1().getEndTime());
        scheduleRightButton.setText(currentSchedule.getItem2().getStartTime() + " - " + currentSchedule.getItem2().getEndTime());

        return view;
    }
}
