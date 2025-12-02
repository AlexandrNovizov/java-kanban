package ru.yandex.prakticum.gymmaster;

import java.util.Objects;

public class TrainingSession implements Comparable<TrainingSession> {

    //группа
    private final Group group;
    //тренер
    private final Coach coach;
    //день недели
    private final DayOfWeek dayOfWeek;
    //время начала занятия
    private final TimeOfDay timeOfDay;
    //длительность
    private final int duration;

    public TrainingSession(Group group, Coach coach, DayOfWeek dayOfWeek, TimeOfDay timeOfDay, int duration) {
        this.group = group;
        this.coach = coach;
        this.dayOfWeek = dayOfWeek;
        this.timeOfDay = timeOfDay;
        this.duration = duration;
    }

    public Group getGroup() {
        return group;
    }

    public Coach getCoach() {
        return coach;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public TimeOfDay getTimeOfDay() {
        return timeOfDay;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public int compareTo(TrainingSession o) {
        int result = dayOfWeek.compareTo(o.dayOfWeek);
        if (dayOfWeek.compareTo(o.dayOfWeek) > 0)
            result = timeOfDay.compareTo(o.timeOfDay);

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TrainingSession that = (TrainingSession) o;
        return Objects.equals(group, that.group) && Objects.equals(coach, that.coach) && dayOfWeek == that.dayOfWeek
                && Objects.equals(timeOfDay, that.timeOfDay) && duration == that.duration;
    }

    @Override
    public int hashCode() {
        return Objects.hash(group, coach, dayOfWeek, timeOfDay, duration);
    }
}