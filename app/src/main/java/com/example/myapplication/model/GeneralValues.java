package com.example.myapplication.model;

public class GeneralValues {
    private int maxSchedules;
    private float localCapacity;
    private float permittedCapacity;

    public GeneralValues() {
    }

    public int getMaxSchedules() {
        return maxSchedules;
    }

    public void setMaxSchedules(int maxSchedules) {
        this.maxSchedules = maxSchedules;
    }

    public float getLocalCapacity() {
        return localCapacity;
    }

    public void setLocalCapacity(float localCapacity) {
        this.localCapacity = localCapacity;
    }

    public float getPermittedCapacity() {
        return permittedCapacity;
    }

    public void setPermittedCapacity(float permittedCapacity) {
        this.permittedCapacity = permittedCapacity;
    }
}
