package com.ubi.ubibeacons.model;

import io.realm.RealmObject;

/**
 * @author João Pedro Pedrosa, UbiBeacons on 29/02/2016.
 */
public class Place extends RealmObject{

    private String id;
    private String name;

    public Place(){}

    public Place(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
