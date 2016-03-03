package com.ubi.ubibeacons.beacons;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.connection.MotionState;

import java.util.List;

/**
 * @author João Pedro Pedrosa, SE on 23-02-2016.
 */
public interface OnBeaconsAction {

    void onDiscoveryBeacon(List<Beacon> beacons);

    void showBeaconNotificationEnter(String title, String message);

    void showBeaconNotificationExit(String title, String message);

    void onBeaconTemperature(float temperature);

    void onBeaconMotionListener(MotionState motionState);
}
