package com.aleksandra.go4mytrip;

public class PackingModel {
    String itemId;
    String name;
    String category;
    Boolean isCheckedS;
    Boolean isChecked;
    Boolean isToBuy;


    public PackingModel() {

    }

    public PackingModel(String itemId, String name, String category, Boolean ischecked, Boolean isToBuy, Boolean isCheckedS) {
        this.itemId = itemId;
        this.name = name;
        this.category = category;
        this.isChecked = ischecked;
        this.isToBuy = isToBuy;
        this.isCheckedS = isCheckedS;

    }


    public Boolean getCheckedS() {
        return isCheckedS;
    }

    public void setCheckedS(Boolean checkedS) {
        isCheckedS = checkedS;
    }

    public Boolean getToBuy() {
        return isToBuy;
    }

    public void setToBuy(Boolean toBuy) {
        isToBuy = toBuy;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Boolean getChecked() {
        return isChecked;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }
}
