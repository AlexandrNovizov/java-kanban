package ru.yandex.prakticum.gymmaster;

import java.util.*;

public class Timetable {

    private enum CheckType { COACH, GROUP }

    private final Map<Coach, Integer> countOfTrainings = new HashMap<>();

    private final Map<DayOfWeek, TreeMap<TimeOfDay, HashSet<TrainingSession>>> timetable = new HashMap<>();

    public void addNewTrainingSession(TrainingSession trainingSession) {
        DayOfWeek dayOfWeek = trainingSession.getDayOfWeek();
        TimeOfDay timeOfDay = trainingSession.getTimeOfDay();
        TreeMap<TimeOfDay, HashSet<TrainingSession>> trainingsForDay = timetable.getOrDefault(dayOfWeek, new TreeMap<>());

        // Проверка, не пересекается ли это занятие с другими занятиями тренера/группы
        if (isIntersections(trainingSession, trainingsForDay, CheckType.COACH))
            return;
        if (isIntersections(trainingSession, trainingsForDay, CheckType.GROUP))
            return;

        HashSet<TrainingSession> trainingsForTime = trainingsForDay.getOrDefault(timeOfDay, new HashSet<>());

        trainingsForTime.add(trainingSession);
        trainingsForDay.put(trainingSession.getTimeOfDay(), trainingsForTime);
        incrementCounter(trainingSession.getCoach());

        timetable.put(dayOfWeek, trainingsForDay);
    }

    public TreeMap<TimeOfDay, HashSet<TrainingSession>> getTrainingSessionsForDay(DayOfWeek dayOfWeek) {
        return timetable.getOrDefault(dayOfWeek, new TreeMap<>());
    }

    public Set<TrainingSession> getTrainingSessionsForDayAndTime(DayOfWeek dayOfWeek, TimeOfDay timeOfDay) {
        TreeMap<TimeOfDay, HashSet<TrainingSession>> trainingSessionsForDay = timetable.getOrDefault(
                dayOfWeek, new TreeMap<>());
        return trainingSessionsForDay.getOrDefault(timeOfDay, new HashSet<>());
    }

    public Set<CounterOfTrainings> getCountByCoaches() {
        return toSortedSet(countOfTrainings);
    }

    private boolean isIntersections(TrainingSession session,
                                    TreeMap<TimeOfDay, HashSet<TrainingSession>> trainingsForDay,
                                    CheckType type) {
        TimeOfDay startTime = session.getTimeOfDay();
        int trainDuration = session.getDuration();
        TimeOfDay endTime = startTime.plusMinutes(trainDuration);

        for (var trainTime : trainingsForDay.navigableKeySet()) {
            if (trainTime.compareTo(endTime) > 0)
                break;

            // отбираем тренировки конкретного тренера
            for (TrainingSession train : trainingsForDay.get(trainTime)) {
                boolean isSameGroupOrCoach;
                String errorMessage;
                switch (type) {
                    case GROUP -> {
                        isSameGroupOrCoach = train.getGroup().equals(session.getGroup());
                        errorMessage = "Невозможно добавить тренировку, группа занята";
                    }
                    case null, default -> {
                        isSameGroupOrCoach = train.getCoach().equals(session.getCoach());
                        errorMessage = "Невозможно добавить тренировку, тренер занят";
                    }
                }
                if (isSameGroupOrCoach) {
                    TimeOfDay currTrainStart = train.getTimeOfDay();
                    TimeOfDay currTrainEnd = train.getTimeOfDay().plusMinutes(train.getDuration());

                    boolean startsInside = (startTime.compareTo(currTrainStart) >= 0 &&
                            startTime.compareTo(currTrainEnd) <= 0);

                    boolean endsInside = (endTime.compareTo(currTrainStart) >= 0 &&
                            endTime.compareTo(currTrainEnd) <= 0);

                    boolean fullContain = (startTime.compareTo(currTrainStart) <= 0 &&
                            endTime.compareTo(currTrainEnd) >= 0);

                    if (startsInside || endsInside || fullContain) {
                        System.out.println(errorMessage);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void incrementCounter(Coach coach) {
        countOfTrainings.compute(coach, (k, v) -> (v == null) ? 1 : v + 1);
    }

    public static Set<CounterOfTrainings> toSortedSet(Map<Coach, Integer> map) {
        TreeSet<CounterOfTrainings> result = new TreeSet<>();
        for (Map.Entry<Coach, Integer> entry : map.entrySet())
            result.add(new CounterOfTrainings(entry.getKey(), entry.getValue()));

        return result.reversed();
    }
}
