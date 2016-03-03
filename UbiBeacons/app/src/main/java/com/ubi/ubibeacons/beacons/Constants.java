package com.ubi.ubibeacons.beacons;

import com.estimote.sdk.Beacon;

import java.util.ArrayList;

/**
 * @author João Pedro Pedrosa, UbiBeacons on 29/02/2016.
 */
public class Constants {

    //TODO - Insert the correct major and minor of your beacons

    public static final String UUID_BEACONS = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";

    public static final String BLUBERRY = "27592:49298";

    public static final String ICE = "7524:45085";

    public static final String MINT = "15621:39312";

    public static ArrayList<String> getPlacesToBlueberryBeacons(){
        ArrayList list = new ArrayList();
        list.add("Reunions Room");
        return list;
    }

    public static ArrayList<String> getPlacesToIceBeacons(){
        ArrayList list = new ArrayList();
        list.add("Coffe");
        return list;
    }

    public static ArrayList<String> getPlacesToMintBeacons(){
        ArrayList list = new ArrayList();
        list.add("Developers and Design Room");
        return list;
    }

    public static String getBeaconsType(Beacon beacon){
        String value = String.format("%d:%d", beacon.getMajor(), beacon.getMinor());
        if(value.equals(BLUBERRY)){
            return BLUBERRY;
        }else if(value.equals(ICE)){
            return ICE;
        }else if(value.equals(MINT)){
            return MINT;
        }else{
            return "";
        }
    }
}
