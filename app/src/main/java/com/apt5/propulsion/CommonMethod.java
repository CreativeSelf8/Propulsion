package com.apt5.propulsion;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Van Quyen on 5/15/2017.
 */

public class CommonMethod {
    public static String convertToDate(long time) {
        Date date = new Date(time);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(date);
    }
}
