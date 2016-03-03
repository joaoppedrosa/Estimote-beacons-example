package com.ubi.ubibeacons.model;

import io.realm.RealmObject;

/**
 * @author João Pedro Pedrosa, UbiBeacons on 29/02/2016.
 */
public class Notification extends RealmObject{

    private String id;
    private String title;
    private String message;

    public Notification(){}

    public Notification(String id, String title, String message) {
        this.id = id;
        this.title = title;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
