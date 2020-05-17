package com.busra.reminder.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateTimeUtils {

    private static String resultDate = "";

    public static String validTime(int hour, int minute) {
        Calendar time = new GregorianCalendar();
        time.set(Calendar.HOUR_OF_DAY, hour);
        time.set(Calendar.MINUTE, minute);

        SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        return formatTime.format(time.getTime());
    }

    public static String validDate(int year, int monthOfYear, int dayOfMonth) {
        Calendar date = new GregorianCalendar();
        date.set(Calendar.YEAR, year);
        date.set(Calendar.MONTH, monthOfYear);
        date.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        return format.format(date.getTime());
    }

    public static String dateManager(String rowDate) {
        Calendar today = Calendar.getInstance();
        if (rowDate.length() == 5 && resultDate.isEmpty()) {
            SimpleDateFormat resData = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
            String todayDate = resData.format(today.getTime());
            resultDate += todayDate + ", " + rowDate;
        } else if (rowDate.length() == 10 && resultDate.isEmpty()) {
            SimpleDateFormat resData = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
            String todayTime = resData.format(today.getTime());
            resultDate += rowDate + ", " + todayTime;
        } else {
            if (rowDate.length() == 5) {
                resultDate = resultDate.substring(0, 12) + rowDate;
            } else {
                resultDate = rowDate + ", " + resultDate.substring(12);
            }
        }

        return displayDateTime(resultDate);
    }


    private static String displayDateTime(String resultDate) {
        //TODO logic of correct display func: validDateTime(resultDate)

        String date = resultDate.substring(0, 10);
        String time = resultDate.substring(12);

        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.MONTH, Integer.parseInt(date.substring(3, 5)) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date.substring(0, 2)));

        String correctDateTime = new SimpleDateFormat("dd MMMM", Locale.ENGLISH)
                .format(calendar.getTime()).substring(0, 6);

        // Date time format at this moment 06 May 05:00
        return validDateTime(correctDateTime, time);
    }

    private static String validDateTime(String correctDateTime, String time) {
        Calendar currentDateTime = Calendar.getInstance();
        String currentDate = new SimpleDateFormat("dd MMMM", Locale.ENGLISH).format(currentDateTime.getTime());
        String currentTime = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(currentDateTime.getTime());

        //TODO "tomorrow" and other logic impl here
        if (currentDate.equals(correctDateTime))
            correctDateTime = "Today\n" + time;
        else
            correctDateTime += '\n' + time;

        if (correctDateTime.startsWith("0"))
            correctDateTime = correctDateTime.substring(1);

        return correctDateTime;
    }
}
