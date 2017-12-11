package com.tpt.borne.tpt.models;

/**
 * Created by Borne on 6/16/2016.
 */
public class Ship {

    private String name;
    private String cbm;
    private String dwt;
    private String loa;
    private String last;
    private String built;
    private String location;
    private String openDate;
    private String status;
    private String comment;
    private String temaSuitable;
    private String sire;
    private String lastUpdate;


    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getSire() {
        return sire;
    }

    public void setSire(String sire) {
        this.sire = sire;
    }

    public String getTemaSuitable() {
        return temaSuitable;
    }

    public void setTemaSuitable(String temaSuitable) {
        this.temaSuitable = temaSuitable;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOpenDate() {
        return openDate;
    }

    public void setOpenDate(String openDate) {
        this.openDate = openDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCbm() {
        return cbm;
    }

    public void setCbm(String cbm) {
        this.cbm = cbm;
    }

    public String getDwt() {
        return dwt;
    }

    public void setDwt(String dwt) {
        this.dwt = dwt;
    }

    public String getLoa() {
        return loa;
    }

    public void setLoa(String loa) {
        this.loa = loa;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getBuilt() {
        return built;
    }

    public void setBuilt(String built) {
        this.built = built;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
