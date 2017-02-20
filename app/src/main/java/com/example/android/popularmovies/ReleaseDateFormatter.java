package com.example.android.popularmovies;

import android.content.Context;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/*
* Resources used as guidance:
* stackoverflow.com/questions/454315/how-do-you-format-date-and-time-in-android
* https://developer.android.com/reference/java/text/DateFormat.html
* http://stackoverflow.com/questions/9872419/how-to-convert-a-string-to-a-date-using-simpledateformat
*/

//Using for date formatting with the Release Date
class ReleaseDateFormatter {
    public static Date getFormattedDate(String releaseDate, String format) throws ParseException {
        SimpleDateFormat formattedDate = new SimpleDateFormat(format);
        return formattedDate.parse(releaseDate);
    }

    public static String getLocalizedDate(Context context, String date, String format) throws ParseException {
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);
        return dateFormat.format(getFormattedDate(date, format));
    }
}

