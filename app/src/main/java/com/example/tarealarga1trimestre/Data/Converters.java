package com.example.tarealarga1trimestre.Data;

import androidx.room.TypeConverter;

import java.time.LocalDate;

public class Converters {
    @TypeConverter
    public static LocalDate fromString(String value) {
        return value == null ? LocalDate.now() : LocalDate.parse(value);
    }

    @TypeConverter
    public static String toString(LocalDate date) {
        return date == null ? LocalDate.now().toString() : date.toString();
    }
}
