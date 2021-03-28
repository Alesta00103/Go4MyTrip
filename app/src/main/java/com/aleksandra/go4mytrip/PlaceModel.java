package com.aleksandra.go4mytrip;

public class PlaceModel {
    String Id;
    String Name;
    String Day;
    String LatLang;
    String Address;
    String Date;
    String Time;


    public PlaceModel() {
    }

    public PlaceModel(String id, String name, String day, String latLang, String address, String date, String time) {
        this.Id = id;
        this.Name = name;
        this.Day = day;
        this.LatLang = latLang;
        this.Address = address;
        this.Date = date;
        this.Time = time;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getLatLang() {
        return LatLang;
    }

    public void setLatLang(String latLang) {
        LatLang = latLang;
    }

    public String getDay() {
        return Day;
    }

    public void setDay(String day) {
        Day = day;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
