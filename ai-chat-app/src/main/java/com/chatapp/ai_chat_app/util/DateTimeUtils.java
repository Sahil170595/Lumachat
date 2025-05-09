package com.chatapp.ai_chat_app.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for handling date and time operations in the application.
 */
public class DateTimeUtils {
    
    private static final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("HH:mm:ss");
    private static final SimpleDateFormat DATE_TIME_FORMATTER = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
    private static final SimpleDateFormat DATE_ONLY_FORMATTER = new SimpleDateFormat("MMM dd, yyyy");
    
    /**
     * Gets the current time as a formatted string.
     * @return Current time in HH:mm:ss format
     */
    public static String getCurrentTimeFormatted() {
        return TIME_FORMATTER.format(new Date());
    }
    
    /**
     * Gets the current date and time as a formatted string.
     * @return Current date and time in "MMM dd, yyyy HH:mm:ss" format
     */
    public static String getCurrentDateTimeFormatted() {
        return DATE_TIME_FORMATTER.format(new Date());
    }
    
    /**
     * Gets only the date part from current date.
     * @return Current date in "MMM dd, yyyy" format
     */
    public static String getCurrentDateFormatted() {
        return DATE_ONLY_FORMATTER.format(new Date());
    }
    
    /**
     * Formats a date object into a string with format "MMM dd, yyyy HH:mm:ss".
     * @param date The date to format
     * @return Formatted date and time string
     */
    public static String formatDateTime(Date date) {
        return DATE_TIME_FORMATTER.format(date);
    }
    
    /**
     * Attempts to parse a date string in "MMM dd, yyyy HH:mm:ss" format.
     * @param dateTimeStr The date string to parse
     * @return Date object, or null if parsing fails
     */
    public static Date parseDateTime(String dateTimeStr) {
        try {
            return DATE_TIME_FORMATTER.parse(dateTimeStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Groups messages by date for displaying date separators in the chat.
     * @param dateTimeStr The date-time string from a message
     * @return Only the date portion in "MMM dd, yyyy" format
     */
    public static String extractDatePart(String dateTimeStr) {
        try {
            Date date = DATE_TIME_FORMATTER.parse(dateTimeStr);
            return DATE_ONLY_FORMATTER.format(date);
        } catch (ParseException e) {
            return "Unknown Date";
        }
    }
    
    /**
     * Determines if a message should have a date separator based on the previous message.
     * @param currentMessageDate Date of the current message in string format
     * @param previousMessageDate Date of the previous message in string format, or null if no previous message
     * @return true if a date separator should be displayed, false otherwise
     */
    public static boolean shouldShowDateSeparator(String currentMessageDate, String previousMessageDate) {
        if (previousMessageDate == null) {
            return true; // First message should always show date
        }
        
        try {
            Date current = DATE_TIME_FORMATTER.parse(currentMessageDate);
            Date previous = DATE_TIME_FORMATTER.parse(previousMessageDate);
            
            // Compare just the date portions
            String currentDateOnly = DATE_ONLY_FORMATTER.format(current);
            String previousDateOnly = DATE_ONLY_FORMATTER.format(previous);
            
            return !currentDateOnly.equals(previousDateOnly);
        } catch (ParseException e) {
            return true; // If there's an error, show the separator to be safe
        }
    }
}