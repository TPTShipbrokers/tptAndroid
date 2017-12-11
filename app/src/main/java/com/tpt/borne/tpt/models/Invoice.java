package com.tpt.borne.tpt.models;

/**
 * Created by Jovan Milutinović on 11.6.2016..
 */
public class Invoice {

    private String id;
    private String file;
    private String fileName;


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
