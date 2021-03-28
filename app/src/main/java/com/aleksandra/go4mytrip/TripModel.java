package com.aleksandra.go4mytrip;


public class TripModel {
    private String TripId;
    private String Title;
    private String NamePlace;
    private String Coordinate;
    private int ImageTrip;
    private String TripDate;
    private String TripDateEnd;
    private String TimeStart;
    private String TimeEnd;


    public TripModel() {

    }

    public TripModel(String id, String title, String namePlace, String coordinate, int imageTrip, String tripDate, String tripDateEnd, String timeStart, String timeEnd) {
        TripId = id;
        Title = title;
        NamePlace = namePlace;
        Coordinate = coordinate;
        ImageTrip = imageTrip;
        TripDate = tripDate;
        TripDateEnd = tripDateEnd;
        TimeStart = timeStart;
        TimeEnd = timeEnd;

    }

    public String getTimeStart() {
        return TimeStart;
    }

    public void setTimeStart(String timeStart) {
        TimeStart = timeStart;
    }

    public String getTimeEnd() {
        return TimeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        TimeEnd = timeEnd;
    }

    public String getTripId() {
        return TripId;
    }

    public void setTripId(String tripId) {
        TripId = tripId;
    }

    public String getNamePlace() {
        return NamePlace;
    }

    public void setNamePlace(String namePlace) {
        NamePlace = namePlace;
    }

    public String getCoordinate() {
        return Coordinate;
    }

    public void setCoordinate(String coordinate) {
        Coordinate = coordinate;
    }

    public String getTripDateEnd() {
        return TripDateEnd;
    }

    public void setTripDateEnd(String tripDateEnd) {
        TripDateEnd = tripDateEnd;
    }

    public String getTripDate() {
        return TripDate;
    }

    public void setTripDate(String tripDate) {
        TripDate = tripDate;
    }

    public void setImageTrip(int imageTrip) {
        ImageTrip = imageTrip;
    }

    public int getImageTrip() {
        return ImageTrip;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }


}
