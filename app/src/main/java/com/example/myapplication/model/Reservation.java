package com.example.myapplication.model;

public class Reservation {
    private String created;
    private String reservationDate;
    private String reservationSchedule;
    private String reservationUser;

    public Reservation() {
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(String reservationDate) {
        this.reservationDate = reservationDate;
    }

    public String getReservationSchedule() {
        return reservationSchedule;
    }

    public void setReservationSchedule(String reservationSchedule) {
        this.reservationSchedule = reservationSchedule;
    }

    public String getReservationUser() {
        return reservationUser;
    }

    public void setReservationUser(String reservationUser) {
        this.reservationUser = reservationUser;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "created='" + created + '\'' +
                ", reservationDate='" + reservationDate + '\'' +
                ", reservationSchedule='" + reservationSchedule + '\'' +
                ", reservationUser='" + reservationUser + '\'' +
                '}';
    }
}
