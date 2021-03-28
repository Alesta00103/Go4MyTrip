package com.aleksandra.go4mytrip;

public interface PlaceListener {
    void onLongClicked(PlaceModel placeModel, int position);

    void onClickDelete(PlaceModel placeModel, int position);
}
