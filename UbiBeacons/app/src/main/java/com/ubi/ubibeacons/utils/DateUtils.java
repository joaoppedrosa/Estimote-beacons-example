package com.ubi.ubibeacons.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author João Pedro Pedrosa, SE on 26/02/2016.
 */
public class DateUtils {

    public static String getCurrentDateTime(){
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        return df.format(Calendar.getInstance().getTime());
    }

    public static int getCurrentMiliseconds(){
        return Calendar.getInstance().get(Calendar.MILLISECOND);
    }

    public static Date currentDate(){
        return new Date(System.currentTimeMillis());
    }
}
