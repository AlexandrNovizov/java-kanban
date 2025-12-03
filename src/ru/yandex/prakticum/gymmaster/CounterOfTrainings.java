package ru.yandex.prakticum.gymmaster;

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
}
