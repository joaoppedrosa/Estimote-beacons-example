package com.ubi.ubibeacons.utils;

/**
 * @author João Pedro Pedrosa, UbiBeacons on 01/03/2016.
 */
public interface OnRegist {

    void onCheckIn(String id, String title, String message, String date);

    void onCheckOut(String id, String title, String message, String date);

}
