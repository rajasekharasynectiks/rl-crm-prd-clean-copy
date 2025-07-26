package com.rlabs.crm.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
public class DateTimeUtil {

    public long getTimeDifferenceInSeconds(long startTime, long endTime){
        long differenceInMillis = endTime - startTime;
        // Convert milliseconds to seconds
        long differenceInSeconds = differenceInMillis / 1000;
        log.debug("Difference in seconds: {}", differenceInSeconds);
        return differenceInSeconds;
    }

    public long convertSecondsToMinutes(long seconds){
        long minutes = seconds / 60;
        log.debug("{} seconds are equals to {}",seconds, minutes);
        return minutes;
    }

    public String getDateDifferenceInReadableFormat(LocalDateTime startDate, LocalDateTime endDate) {
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);

        if (daysBetween == 0) {
            return "today";
        } else if (daysBetween == 1) {
            return "1 day ago";
        } else if (daysBetween < 7) {
            return daysBetween + " days ago";
        } else if (daysBetween < 30) {
            long weeksBetween = daysBetween / 7;
            return weeksBetween == 1 ? "1 week ago" : weeksBetween + " weeks ago";
        } else if (daysBetween < 365) {
            long monthsBetween = daysBetween / 30;
            return monthsBetween == 1 ? "1 month ago" : monthsBetween + " months ago";
        } else {
            long yearsBetween = daysBetween / 365;
            return yearsBetween == 1 ? "1 year ago" : yearsBetween + " years ago";
        }
    }

    public String convertLocalDateTimeToString(LocalDateTime localDateTime, String format){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return localDateTime.format(formatter);
    }

    public LocalDate convertStringToLocalDate(String localDate, String format){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDate.parse(localDate, formatter);
    }

    public String convertLocalDateToString(LocalDate localDate, String format){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return localDate.format(formatter);
    }
}
