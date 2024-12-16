package com.auto.extensions;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class extension {
    public static String getTimeFromDate(Date date) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a"); // Use "hh:mm:ss a" for 12-hour format
        return timeFormat.format(date);
    }

    public static String getDateTimeWithExtraHour(Date date) {
        // Initialize Calendar with the provided date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        // Add one hour
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        // Format the updated date and time
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("hh:mm a");
        return dateTimeFormat.format(calendar.getTime());
    }

    // Function to set date and time as a string from a Date object
    public static String getDateTimeFromDate(Date date) {
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd-MM-yyyy   hh:mm a"); // Customize format as needed
        return dateTimeFormat.format(date);
    }

    public static String getDate(Date date) {
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd-MM-yyyy"); // Customize format as needed
        return dateTimeFormat.format(date);
    }

    public static String getImageUrl() {
        Random random = new Random();
        return "https://api.dicebear.com/9.x/avataaars/png?seed="+(random.nextInt(100));

    }

    public static String generateRandomName() {
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "Diana", "Ethan", "Fiona", "George", "Hannah");
        List<String> lastNames = Arrays.asList("Smith", "Johnson", "Brown", "Williams", "Jones", "Garcia", "Miller", "Davis", "Martinez", "Lopez");
        Random random = new Random();
        return names.get(random.nextInt(names.size())) +" "+lastNames.get(random.nextInt(lastNames.size()));
    }

    public static boolean validateNumberPlate(String plate) {
        String regex = "^[A-Z]{2}-[0-9]{2}-[A-Z]{1,2}-[0-9]{4}$"; // Adjust based on your format
        return plate.matches(regex);
    }

}
