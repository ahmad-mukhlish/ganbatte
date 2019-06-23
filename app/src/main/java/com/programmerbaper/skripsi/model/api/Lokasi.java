package com.programmerbaper.skripsi.model.api;

public class Lokasi {

    private double latitude ;
    private double longitude ;
    private int id ;
    private boolean moving;

    public Lokasi(double latitude, double longitude, int id, boolean moving) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.id = id;
        this.moving = moving;
    }

    public boolean isMoving() {
        return moving;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }


    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
