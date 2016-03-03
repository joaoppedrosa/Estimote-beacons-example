package com.ubi.ubibeacons.beacons;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;
import com.estimote.sdk.cloud.model.BeaconInfo;
import com.estimote.sdk.connection.BeaconConnection;
import com.estimote.sdk.connection.MotionState;
import com.estimote.sdk.connection.Property;
import com.estimote.sdk.exception.EstimoteDeviceException;
import com.ubi.ubibeacons.utils.PreferencesManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author João Pedro Pedrosa, UbiBeacons on 29/02/2016.
 */
public class EstimoteBeaconsManager implements BeaconManager.ErrorListener{

    private static final String TAG = "EstimoteBeacons";
    private static Map<String, List<String>> PLACES_BY_BEACONS;
    private Context mContext;
    private BeaconManager beaconManagerMonitoring;
    private BeaconManager beaconManagerRanging;
    private BeaconConnection connection;
    private OnBeaconsAction onBeaconsAction;
    private BeaconConnection beaconConnection;
    private PreferencesManager preferencesManager = PreferencesManager.getPreferencesManager();

    public EstimoteBeaconsManager(Context mContext, OnBeaconsAction onBeaconsAction){
        this.mContext = mContext;
        this.onBeaconsAction = onBeaconsAction;
        this.beaconManagerMonitoring = new BeaconManager(mContext);
        this.beaconManagerRanging = new BeaconManager(mContext);
        this.beaconManagerMonitoring.setErrorListener(this);
        this.beaconManagerRanging.setErrorListener(this);
        populateList();
    }

    public EstimoteBeaconsManager(Context mContext){
        this.mContext = mContext;
        populateList();
    }

    private void populateList(){
        Map<String, List<String>> placesByBeacons = new HashMap<>();
        placesByBeacons.put(Constants.BLUBERRY, Constants.getPlacesToBlueberryBeacons());
        placesByBeacons.put(Constants.ICE, Constants.getPlacesToIceBeacons());
        placesByBeacons.put(Constants.MINT, Constants.getPlacesToMintBeacons() );
        PLACES_BY_BEACONS = Collections.unmodifiableMap(placesByBeacons);
    }

    public void startMonitoringBeacons(final Region region) {
        if(beaconManagerMonitoring !=null){
            beaconManagerMonitoring.connect(new BeaconManager.ServiceReadyCallback() {
                @Override
                public void onServiceReady() {
                    beaconManagerMonitoring.startMonitoring(region);
                }
            });
            beaconManagerMonitoring.setMonitoringListener(new BeaconManager.MonitoringListener() {
                @Override
                public void onEnteredRegion(Region region, List<Beacon> list) {
                    if (onBeaconsAction != null) {
                        onBeaconsAction.showBeaconNotificationEnter("Welcome to Ubiwhere", "You want to make Check-in?");
                    }
                }

                @Override
                public void onExitedRegion(Region region) {
                    onBeaconsAction.showBeaconNotificationExit("Goodbye!", "You want to make Check-out?");
                }
            });
        }
    }

    public void startRangingBeacons(final Region region) {
        if(beaconManagerRanging!=null){
            beaconManagerRanging.connect(new BeaconManager.ServiceReadyCallback() {
                @Override
                public void onServiceReady() {
                    beaconManagerRanging.startRanging(region);
                }
            });
            beaconManagerRanging.setRangingListener(new BeaconManager.RangingListener() {
                @Override
                public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                    Log.e(TAG, "onBeaconsDiscovered: " + list.toString());

                    for (Beacon b : list) {
                        beaconConnection = new BeaconConnection(mContext, b, new BeaconConnection.ConnectionCallback() {
                            @Override
                            public void onAuthorized(BeaconInfo beaconInfo) {

                            }

                            @Override
                            public void onConnected(BeaconInfo beaconInfo) {
                                connection.edit().set(connection.motionDetectionEnabled(), true).commit(new BeaconConnection.WriteCallback() {
                                    @Override public void onSuccess() {
                                        Log.e(TAG, "onSuccess: " + (connection.motionDetectionEnabled().get() ? connection.motionState().get() : null));
                                        Log.e(TAG, "onSuccess: " + (connection.temperature().get()));
                                    }

                                    @Override
                                    public void onError(EstimoteDeviceException e) {
                                        Log.e(TAG, "onError: ",e);
                                    }
                                });
                            }

                            @Override
                            public void onAuthenticationError(EstimoteDeviceException e) {

                            }

                            @Override
                            public void onDisconnected() {

                            }
                        });
                    }

                    onBeaconsAction.onDiscoveryBeacon(list);
                }
            });
        }
    }

    public void stopMonitoringBeacons(Region region) {
        if(beaconManagerMonitoring !=null){
            beaconManagerMonitoring.stopMonitoring(region);
        }
    }

    public void stopRangingBeacons(Region region) {
        if(beaconManagerRanging!=null){
            beaconManagerRanging.stopRanging(region);
        }
    }

    public Region createRegion(String identifier, UUID UUID, int major, int minor) {
        if(identifier!=null){
            return new Region(identifier,UUID,major,minor);
        }else{
            return null;
        }
    }

    public Region createRegionForAllBeacons(String identifier) {
        if(identifier!=null){
            return new Region(identifier,null,null,null);
        }else{
            return null;
        }
    }

    public void setBeaconsActionListener(OnBeaconsAction onBeaconsAction) {
        this.onBeaconsAction = onBeaconsAction;
    }

    public void startListenTemperatureBeacon(Beacon beacon) {
        connection = new BeaconConnection(mContext, beacon, new BeaconConnection.ConnectionCallback() {
            @Override
            public void onAuthenticationError(EstimoteDeviceException e) {
                Log.e(TAG, "onAuthenticationError",e);
            }

            @Override
            public void onConnected(BeaconInfo beaconInfo) {
                refreshTemperature(onBeaconsAction);
            }

            @Override
            public void onDisconnected() {
                Log.e(TAG, "onDisconnected");
            }

            @Override
            public void onAuthorized(BeaconInfo beaconInfo) {
                Log.e(TAG, "onAuthorized");
            }
        });
    }


    private void refreshTemperature(final OnBeaconsAction onBeaconsAction) {
        connection.temperature().getAsync(new Property.Callback<Float>() {
            @Override
            public void onValueReceived(final Float value) {
                onBeaconsAction.onBeaconTemperature(value);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshTemperature(onBeaconsAction);
                    }
                }, 200000);
            }

            @Override
            public void onFailure() {
                Log.e(TAG, "onFailure: Unable to read temperature from beacon");
            }
        });
    }

    public void startListenMotionBeacon(Beacon beacon) {
        connection = new BeaconConnection(mContext, beacon, new BeaconConnection.ConnectionCallback() {
            @Override
            public void onAuthenticationError(EstimoteDeviceException e) {
                Log.e(TAG, "onAuthenticationError",e);
            }

            @Override
            public void onConnected(BeaconInfo beaconInfo) {
                connection.edit().set(connection.motionDetectionEnabled(), true).commit(new BeaconConnection.WriteCallback() {
                    @Override
                    public void onSuccess() {
                        enableMotionListner(onBeaconsAction);
                    }

                    @Override
                    public void onError(EstimoteDeviceException exception) {
                        Log.e(TAG, "onError: Failed to enable motion detection",exception);
                    }
                });
            }

            @Override
            public void onDisconnected() {
                Log.e(TAG, "onDisconnected");
            }

            @Override
            public void onAuthorized(BeaconInfo beaconInfo) {
                Log.e(TAG, "onAuthorized");
            }
        });
    }

    public BeaconConnection getBeaconConnections() {
        return this.connection;
    }

    private void enableMotionListner(final OnBeaconsAction onBeaconsAction) {
        connection.setMotionListener(new Property.Callback<MotionState>() {
            @Override
            public void onValueReceived(final MotionState value) {
                onBeaconsAction.onBeaconMotionListener(value);
            }

            @Override
            public void onFailure() {
                Log.e(TAG, "onFailure: Unable to register motion listener");
            }
        });
    }

    @Override
    public void onError(Integer integer) {
        Log.e(TAG, "onError: "+ integer);
    }

    public List<String> placesNearBeacon(Beacon beacon) {
        String beaconKey = String.format("%d:%d", beacon.getMajor(), beacon.getMinor());
        if (PLACES_BY_BEACONS.containsKey(beaconKey)) {
            return PLACES_BY_BEACONS.get(beaconKey);
        }
        return Collections.emptyList();
    }
}
