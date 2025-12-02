package ru.yandex.prakticum.gymmaster;

import java.util.*;

public class Timetable {

    private enum CheckType { COACH, GROUP }

    private final HashMap<Coach, Integer> countOfTrainings = new HashMap<>();

    private final HashMap<DayOfWeek, TreeMap<TimeOfDay, HashSet<TrainingSession>>> timetable = new HashMap<>();

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

    public HashSet<TrainingSession> getTrainingSessionsForDayAndTime(DayOfWeek dayOfWeek, TimeOfDay timeOfDay) {
        TreeMap<TimeOfDay, HashSet<TrainingSession>> trainingSessionsForDay = timetable.getOrDefault(
                dayOfWeek, new TreeMap<>());
        return trainingSessionsForDay.getOrDefault(timeOfDay, new HashSet<>());
    }

    public List<CounterOfTrainings> getCountByCoaches() {
        return CounterOfTrainings.toSortedList(countOfTrainings);
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
                boolean condition;
                String errorMessage;
                switch (type) {
                    case GROUP -> {
                        condition = train.getGroup().equals(session.getGroup());
                        errorMessage = "Невозможно добавить тренировку, группа занята";
                    }
                    case null, default -> {
                        condition = train.getCoach().equals(session.getCoach());
                        errorMessage = "Невозможно добавить тренировку, тренер занят";
                    }
                }
                if (condition) {
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
        countOfTrainings.put(coach, countOfTrainings.getOrDefault(coach, 0) + 1);
    }
}
