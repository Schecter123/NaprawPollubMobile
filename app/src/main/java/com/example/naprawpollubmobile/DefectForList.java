package com.example.naprawpollubmobile;

public class DefectForList {
    String place;
    String room;
    String defectType;
    String description;

    public DefectForList(String place, String room, String defectType, String description) {
        this.place = place;
        this.room = room;
        this.defectType = defectType;
        this.description = description;
    }

    public DefectForList() {
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getDefectType() {
        return defectType;
    }

    public void setDefectType(String defectType) {
        this.defectType = defectType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
