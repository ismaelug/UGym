package com.example.myapplication.model;

public class Schedule {
    private ScheduleItem item1;
    private ScheduleItem item2;

    public Schedule() {
    }

    public ScheduleItem getItem1() {
        return item1;
    }

    public void setItem1(ScheduleItem item1) {
        this.item1 = item1;
    }

    public ScheduleItem getItem2() {
        return item2;
    }

    public void setItem2(ScheduleItem item2) {
        this.item2 = item2;
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "item1=" + item1.toString() +
                ", item2=" + item2.toString() +
                '}';
    }
}
