package com.tpt.borne.tpt.models;

/**
 * Created by Borne on 6/10/2016.
 */
public class MarketReport {

    private String newsletter_id;
    private String title;
    private String date;
    private String file;

    public String getNewsletter_id() {
        return newsletter_id;
    }

    public void setNewsletter_id(String newsletter_id) {
        this.newsletter_id = newsletter_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
