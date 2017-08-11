package com.example.xyzreader.util;

import android.content.Context;
import android.content.res.Resources;
import android.text.format.DateUtils;
import android.util.Log;

import com.example.xyzreader.R;
import com.example.xyzreader.data.Item;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Collection of static utility functions
 */

public class Util {
    // Most time functions can only handle 1902 - 2037
    private static GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2,1,1);
    private static java.text.DateFormat inDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");

    public static String getSubtitle(Context context, Item item) {
        Resources resources = context.getResources();
        java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(
                context.getApplicationContext());
        Date date = item.publishDate;
        String dateText;
        if (date.before(START_OF_EPOCH.getTime())) {
            dateText = dateFormat.format(date);
        }
        else {
            dateText = DateUtils.getRelativeTimeSpanString(
                    date.getTime(),
                    System.currentTimeMillis(),
                    DateUtils.HOUR_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_ALL)
                    .toString();
            Log.d(Util.class.getName(), dateText);
        }
        String author = item.author;
        return resources.getString(R.string.subtitle, dateText, author);
    }

    public static Date parseDateString(String date) {
        try {
            return inDateFormat.parse(date);
        }
        catch (ParseException e) {
            return new Date();
        }
    }
}
