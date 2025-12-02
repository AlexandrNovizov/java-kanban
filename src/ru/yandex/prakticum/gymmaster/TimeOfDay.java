package ru.yandex.prakticum.gymmaster;

import java.util.Objects;

public class TimeOfDay implements Comparable<TimeOfDay> {

    //часы (от 0 до 23)
    private final int hours;
    //минуты (от 0 до 59)
    private final int minutes;

    private static final int MINUTES_IN_HOUR = 60;

    public TimeOfDay(int hours, int minutes) {
        this.hours = hours;
        this.minutes = minutes;
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public TimeOfDay plusMinutes(int minutes) {
        int totalMinutes = this.minutes + minutes;
        int totalHours = this.hours;
        if (totalMinutes >= MINUTES_IN_HOUR) {
            totalHours += totalMinutes / MINUTES_IN_HOUR;
            totalMinutes %= MINUTES_IN_HOUR;
        }

        return new TimeOfDay(totalHours, totalMinutes);
    }

    @Override
    public int compareTo(TimeOfDay o) {
        int result = Integer.compare(hours, o.hours);
        if (result == 0)
            result = Integer.compare(minutes, o.minutes);

        return result;
    }

    @Override
    public String toString() {
        return hours + ":" + (minutes < 10 ? "0" + minutes : minutes);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TimeOfDay timeOfDay = (TimeOfDay) o;
        return hours == timeOfDay.hours && minutes == timeOfDay.minutes;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hours, minutes);
    }
}