package ru.yandex.prakticum.gymmaster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CounterOfTrainings implements Comparable<CounterOfTrainings> {

    private final Coach coach;
    private final int countOfTrains;

    public CounterOfTrainings(Coach coach, int countOfTrains) {
        this.coach = coach;
        this.countOfTrains = countOfTrains;
    }

    public Coach getCoach() {
        return coach;
    }

    public int getCountOfTrains() {
        return countOfTrains;
    }

    @Override
    public int compareTo(CounterOfTrainings o) {
        return countOfTrains - o.countOfTrains;
    }

    public static List<CounterOfTrainings> toSortedList(Map<Coach, Integer> map) {
        ArrayList<CounterOfTrainings> result = new ArrayList<>(map.size());
        for (Map.Entry<Coach, Integer> entry : map.entrySet())
            result.add(new CounterOfTrainings(entry.getKey(), entry.getValue()));

        Collections.sort(result);
        return result.reversed();
    }
}
