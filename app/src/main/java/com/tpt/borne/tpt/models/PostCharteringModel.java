package com.tpt.borne.tpt.models;

import java.util.ArrayList;

/**
 * Created by Jovan Milutinović on 11.6.2016..
 */
public class PostCharteringModel {

    private String cgarteringId;
    private String state;
    private String sofComments;
    private String vesselName;
    private String status;
    private String date;
    private ArrayList<Invoice> invoices;

    private String brokerFirstName;
    private String brokerLastName;
    private String brokerEmail;
    private String brokerPosition;
    private String brokerPhoneNumber;
    private String fullDate;
    private String dateFromStatus;
    private int locked;
    private String subsDueDate;


    public String getSubsDueDate() {
        return subsDueDate;
    }

    public void setSubsDueDate(String subsDueDate) {
        this.subsDueDate = subsDueDate;
    }

    public int getLocked() {
        return locked;
    }

    public void setLocked(int locked) {
        this.locked = locked;
    }

    public String getDateFromStatus() {
        return dateFromStatus;
    }

    public void setDateFromStatus(String dateFromStatus) {
        this.dateFromStatus = dateFromStatus;
    }

    public String getFullDate() {
        return fullDate;
    }

    public void setFullDate(String fullDate) {
        this.fullDate = fullDate;
    }

    public String getBrokerFirstName() {
        return brokerFirstName;
    }

    public void setBrokerFirstName(String brokerFirstName) {
        this.brokerFirstName = brokerFirstName;
    }

    public String getBrokerLastName() {
        return brokerLastName;
    }

    public void setBrokerLastName(String brokerLastName) {
        this.brokerLastName = brokerLastName;
    }

    public String getBrokerEmail() {
        return brokerEmail;
    }

    public void setBrokerEmail(String brokerEmail) {
        this.brokerEmail = brokerEmail;
    }

    public String getBrokerPosition() {
        return brokerPosition;
    }

    public void setBrokerPosition(String brokerPosition) {
        this.brokerPosition = brokerPosition;
    }

    public String getBrokerPhoneNumber() {
        return brokerPhoneNumber;
    }

    public void setBrokerPhoneNumber(String brokerPhoneNumber) {
        this.brokerPhoneNumber = brokerPhoneNumber;
    }

    public String getCgarteringId() {
        return cgarteringId;
    }

    public void setCgarteringId(String cgarteringId) {
        this.cgarteringId = cgarteringId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getSofComments() {
        return sofComments;
    }

    public void setSofComments(String sofComments) {
        this.sofComments = sofComments;
    }

    public String getVesselName() {
        return vesselName;
    }

    public void setVesselName(String vesselName) {
        this.vesselName = vesselName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<Invoice> getInvoices() {
        return invoices;
    }

    public void setInvoices(ArrayList<Invoice> invoices) {
        this.invoices = invoices;
    }
}
