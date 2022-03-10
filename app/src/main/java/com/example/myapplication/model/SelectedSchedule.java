package com.example.myapplication.model;

public class SelectedSchedule {
    private int indexSchedule;
    private String comesFrom;
    private int previousBgColor;
    private boolean previousEnabledState;

    public SelectedSchedule() {
    }

    public int getIndexSchedule() {
        return indexSchedule;
    }

    public void setIndexSchedule(int indexSchedule) {
        this.indexSchedule = indexSchedule;
    }

    public String getComesFrom() {
        return comesFrom;
    }

    public void setComesFrom(String comesFrom) {
        this.comesFrom = comesFrom;
    }

    public int getPreviousBgColor() {
        return previousBgColor;
    }

    public void setPreviousBgColor(int previousBgColor) {
        this.previousBgColor = previousBgColor;
    }

    public boolean isPreviousEnabledState() {
        return previousEnabledState;
    }

    public void setPreviousEnabledState(boolean previousEnabledState) {
        this.previousEnabledState = previousEnabledState;
    }
}
