package com.example.mapofflinej;

public class placemark {

    String origin,title,type;
    double latitude,longitude ;

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public String getOrigin() {
        return origin;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
