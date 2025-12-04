package ru.yandex.prakticum.gummaster;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.prakticum.gymmaster.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Set;

public class TimetableTest {

    Timetable timetable;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void init() {
        timetable = new Timetable();
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void recover() {
        System.setOut(originalOut);
    }

    @Test
    void testGetTrainingSessionsForDaySingleSession() {

        Group group = new Group("Акробатика для детей", Age.CHILD);
        Coach coach = new Coach("Васильев", "Николай", "Сергеевич");
        TrainingSession singleTrainingSession = new TrainingSession(group, coach,
                DayOfWeek.MONDAY, new TimeOfDay(13, 0), 60);

        timetable.addNewTrainingSession(singleTrainingSession);

        //Проверить, что за понедельник вернулось одно занятие
        assertEquals(1, timetable.getTrainingSessionsForDay(DayOfWeek.MONDAY).size());
        //Проверить, что за вторник не вернулось занятий
        assertEquals(0, timetable.getTrainingSessionsForDay(DayOfWeek.TUESDAY).size());
    }

    @Test
    void testGetTrainingSessionsForDayMultipleSessions() {
        Coach coach = new Coach("Васильев", "Николай", "Сергеевич");

        Group groupAdult = new Group("Акробатика для взрослых", Age.ADULT);
        TrainingSession thursdayAdultTrainingSession = new TrainingSession(groupAdult, coach,
                DayOfWeek.THURSDAY, new TimeOfDay(20, 0), 90);

        timetable.addNewTrainingSession(thursdayAdultTrainingSession);

        Group groupChild = new Group("Акробатика для детей", Age.CHILD);
        TrainingSession mondayChildTrainingSession = new TrainingSession(groupChild, coach,
                DayOfWeek.MONDAY, new TimeOfDay(13, 0), 60);
        TrainingSession thursdayChildTrainingSession = new TrainingSession(groupChild, coach,
                DayOfWeek.THURSDAY, new TimeOfDay(13, 0), 60);
        TrainingSession saturdayChildTrainingSession = new TrainingSession(groupChild, coach,
                DayOfWeek.SATURDAY, new TimeOfDay(10, 0), 60);

        timetable.addNewTrainingSession(mondayChildTrainingSession);
        timetable.addNewTrainingSession(thursdayChildTrainingSession);
        timetable.addNewTrainingSession(saturdayChildTrainingSession);

        // Проверить, что за понедельник вернулось одно занятие
        assertEquals(1, timetable.getTrainingSessionsForDay(DayOfWeek.MONDAY).size());
        // Проверить, что за четверг вернулось два занятия в правильном порядке: сначала в 13:00, потом в 20:00
        var trainings = timetable.getTrainingSessionsForDay(DayOfWeek.THURSDAY);
        assertEquals(2, trainings.size());
        TrainingSession[] sessions = new TrainingSession[2];

        int index = 0;
        for (var key : trainings.navigableKeySet())
            sessions[index++] = trainings.get(key).iterator().next();

        assertEquals("13:00", sessions[0].getTimeOfDay().toString());
        assertEquals("20:00", sessions[1].getTimeOfDay().toString());
        // Проверить, что за вторник не вернулось занятий
        assertEquals(0, timetable.getTrainingSessionsForDay(DayOfWeek.TUESDAY).size());
    }

    @Test
    void testGetTrainingSessionsForDayAndTime() {
        Group group = new Group("Акробатика для детей", Age.CHILD);
        Coach coach = new Coach("Васильев", "Николай", "Сергеевич");
        TrainingSession singleTrainingSession = new TrainingSession(group, coach,
                DayOfWeek.MONDAY, new TimeOfDay(13, 0), 60);

        timetable.addNewTrainingSession(singleTrainingSession);

        //Проверить, что за понедельник в 13:00 вернулось одно занятие
        assertEquals(1, timetable.getTrainingSessionsForDayAndTime(DayOfWeek.MONDAY, new TimeOfDay(13, 0)).size());
        //Проверить, что за понедельник в 14:00 не вернулось занятий
        assertTrue(timetable.getTrainingSessionsForDayAndTime(DayOfWeek.MONDAY, new TimeOfDay(14, 0)).isEmpty());
    }

    @Test
    void testAddMultipleTrainingsForDayAndTime() {
        Coach firstCoach = new Coach("Васильев", "Николай", "Сергеевич");
        Coach secondCoach = new Coach("Смольников", "Виталий", "Алексеевич");
        Group groupChild = new Group("Акробатика для детей", Age.CHILD);
        Group groupAdult = new Group("Гимнастика для взрослых", Age.ADULT);

        DayOfWeek trainDay = DayOfWeek.MONDAY;
        TimeOfDay trainTime = new TimeOfDay(13, 0);

        TrainingSession childTraining = new TrainingSession(groupChild, firstCoach, trainDay, trainTime, 60);
        TrainingSession adultTraining = new TrainingSession(groupAdult, secondCoach, trainDay, trainTime, 60);

        timetable.addNewTrainingSession(childTraining);
        timetable.addNewTrainingSession(adultTraining);

        Set<TrainingSession> trainings = timetable.getTrainingSessionsForDayAndTime(trainDay, trainTime);

        assertEquals(2, trainings.size());

        assertTrue(trainings.contains(childTraining));
        assertTrue(trainings.contains(adultTraining));
    }

    @Test
    void testAddTrainingStartTimeInsideExistingCoachTrain() {
        Coach coach = new Coach("Васильев", "Николай", "Сергеевич");
        Group groupChild = new Group("Акробатика для детей", Age.CHILD);
        Group groupAdult = new Group("Гимнастика для взрослых", Age.ADULT);
        DayOfWeek trainDay = DayOfWeek.MONDAY;
        TimeOfDay firstTrainTime = new TimeOfDay(13, 0);
        TimeOfDay secondTrainTime = new TimeOfDay(13, 50);

        TrainingSession childTraining = new TrainingSession(groupChild, coach, trainDay, firstTrainTime, 60);
        timetable.addNewTrainingSession(childTraining);
        TrainingSession adultTraining = new TrainingSession(groupAdult, coach, trainDay, secondTrainTime, 15);
        timetable.addNewTrainingSession(adultTraining);

        assertEquals(1, timetable.getTrainingSessionsForDay(trainDay).size());

        String expected = "Невозможно добавить тренировку, тренер занят" + System.lineSeparator();
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void testAddTrainingEndTimeInsideExistingCoachTrain() {
        Coach coach = new Coach("Васильев", "Николай", "Сергеевич");
        Group groupChild = new Group("Акробатика для детей", Age.CHILD);
        Group groupAdult = new Group("Гимнастика для взрослых", Age.ADULT);
        DayOfWeek trainDay = DayOfWeek.MONDAY;
        TimeOfDay firstTrainTime = new TimeOfDay(13, 0);
        TimeOfDay secondTrainTime = new TimeOfDay(12, 55);

        TrainingSession childTraining = new TrainingSession(groupChild, coach, trainDay, firstTrainTime, 60);
        timetable.addNewTrainingSession(childTraining);
        TrainingSession adultTraining = new TrainingSession(groupAdult, coach, trainDay, secondTrainTime, 15);
        timetable.addNewTrainingSession(adultTraining);

        assertEquals(1, timetable.getTrainingSessionsForDay(trainDay).size());
        String expected = "Невозможно добавить тренировку, тренер занят" + System.lineSeparator();
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void testAddTrainingCompletelyOverlapsExistingCoachTrain() {
        Coach coach = new Coach("Васильев", "Николай", "Сергеевич");
        Group groupChild = new Group("Акробатика для детей", Age.CHILD);
        Group groupAdult = new Group("Гимнастика для взрослых", Age.ADULT);
        DayOfWeek trainDay = DayOfWeek.MONDAY;
        TimeOfDay firstTrainTime = new TimeOfDay(13, 0);
        TimeOfDay secondTrainTime = new TimeOfDay(12, 59);

        TrainingSession childTraining = new TrainingSession(groupChild, coach, trainDay, firstTrainTime, 60);
        timetable.addNewTrainingSession(childTraining);
        TrainingSession adultTraining = new TrainingSession(groupAdult, coach, trainDay, secondTrainTime, 62);
        timetable.addNewTrainingSession(adultTraining);

        assertEquals(1, timetable.getTrainingSessionsForDay(trainDay).size());
        String expected = "Невозможно добавить тренировку, тренер занят" + System.lineSeparator();
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void testAddTrainingIsCompletelyInsideExistingCoachTrain() {
        Coach coach = new Coach("Васильев", "Николай", "Сергеевич");
        Group groupChild = new Group("Акробатика для детей", Age.CHILD);
        Group groupAdult = new Group("Гимнастика для взрослых", Age.ADULT);
        DayOfWeek trainDay = DayOfWeek.MONDAY;
        TimeOfDay firstTrainTime = new TimeOfDay(13, 0);
        TimeOfDay secondTrainTime = new TimeOfDay(13, 15);

        TrainingSession childTraining = new TrainingSession(groupChild, coach, trainDay, firstTrainTime, 60);
        timetable.addNewTrainingSession(childTraining);
        TrainingSession adultTraining = new TrainingSession(groupAdult, coach, trainDay, secondTrainTime, 15);
        timetable.addNewTrainingSession(adultTraining);

        assertEquals(1, timetable.getTrainingSessionsForDay(trainDay).size());
        String expected = "Невозможно добавить тренировку, тренер занят" + System.lineSeparator();
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void testAddTrainingWithSameStartTimeWithExistingCoachTrain() {
        Coach coach = new Coach("Васильев", "Николай", "Сергеевич");
        Group firstGroup = new Group("Акробатика для детей", Age.CHILD);
        Group secondGroup = new Group("Гимнастика для взрослых", Age.ADULT);
        DayOfWeek trainDay = DayOfWeek.MONDAY;
        TimeOfDay trainTime = new TimeOfDay(13, 0);

        timetable.addNewTrainingSession(
                new TrainingSession(firstGroup, coach, trainDay, trainTime, 60)
        );

        timetable.addNewTrainingSession(
                new TrainingSession(secondGroup, coach, trainDay, trainTime, 60)
        );

        assertEquals(1, timetable.getTrainingSessionsForDayAndTime(trainDay, trainTime).size());
        String expected = "Невозможно добавить тренировку, тренер занят" + System.lineSeparator();
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void testAddTrainingStartTimeInsideExistingGroupTrain() {
        Coach firstCoach = new Coach("Васильев", "Николай", "Сергеевич");
        Coach secondCoach = new Coach("Смольников", "Виталий", "Алексеевич");
        Group group = new Group("Акробатика для детей", Age.CHILD);
        DayOfWeek trainDay = DayOfWeek.MONDAY;
        TimeOfDay firstTrainTime = new TimeOfDay(13, 0);
        TimeOfDay secondTrainTime = new TimeOfDay(13, 50);

        TrainingSession childTraining = new TrainingSession(group, firstCoach, trainDay, firstTrainTime, 60);
        timetable.addNewTrainingSession(childTraining);
        TrainingSession adultTraining = new TrainingSession(group, secondCoach, trainDay, secondTrainTime, 15);
        timetable.addNewTrainingSession(adultTraining);

        assertEquals(1, timetable.getTrainingSessionsForDay(trainDay).size());

        String expected = "Невозможно добавить тренировку, группа занята" + System.lineSeparator();
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void testAddTrainingEndTimeInsideExistingGroupTrain() {
        Coach firstCoach = new Coach("Васильев", "Николай", "Сергеевич");
        Coach secondCoach = new Coach("Смольников", "Виталий", "Алексеевич");
        Group group = new Group("Акробатика для детей", Age.CHILD);
        DayOfWeek trainDay = DayOfWeek.MONDAY;
        TimeOfDay firstTrainTime = new TimeOfDay(13, 0);
        TimeOfDay secondTrainTime = new TimeOfDay(12, 55);

        TrainingSession childTraining = new TrainingSession(group, firstCoach, trainDay, firstTrainTime, 60);
        timetable.addNewTrainingSession(childTraining);
        TrainingSession adultTraining = new TrainingSession(group, secondCoach, trainDay, secondTrainTime, 15);
        timetable.addNewTrainingSession(adultTraining);

        assertEquals(1, timetable.getTrainingSessionsForDay(trainDay).size());

        String expected = "Невозможно добавить тренировку, группа занята" + System.lineSeparator();
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void testAddTrainingCompletelyOverlapsExistingGroupTrain() {
        Coach firstCoach = new Coach("Васильев", "Николай", "Сергеевич");
        Coach secondCoach = new Coach("Смольников", "Виталий", "Алексеевич");
        Group group = new Group("Акробатика для детей", Age.CHILD);
        DayOfWeek trainDay = DayOfWeek.MONDAY;
        TimeOfDay firstTrainTime = new TimeOfDay(13, 0);
        TimeOfDay secondTrainTime = new TimeOfDay(12, 59);

        TrainingSession childTraining = new TrainingSession(group, firstCoach, trainDay, firstTrainTime, 60);
        timetable.addNewTrainingSession(childTraining);
        TrainingSession adultTraining = new TrainingSession(group, secondCoach, trainDay, secondTrainTime, 15);
        timetable.addNewTrainingSession(adultTraining);

        assertEquals(1, timetable.getTrainingSessionsForDay(trainDay).size());

        String expected = "Невозможно добавить тренировку, группа занята" + System.lineSeparator();
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void testAddTrainingIsCompletelyInsideExistingGroupTrain() {
        Coach firstCoach = new Coach("Васильев", "Николай", "Сергеевич");
        Coach secondCoach = new Coach("Смольников", "Виталий", "Алексеевич");
        Group group = new Group("Акробатика для детей", Age.CHILD);
        DayOfWeek trainDay = DayOfWeek.MONDAY;
        TimeOfDay firstTrainTime = new TimeOfDay(13, 0);
        TimeOfDay secondTrainTime = new TimeOfDay(13, 15);

        TrainingSession childTraining = new TrainingSession(group, firstCoach, trainDay, firstTrainTime, 60);
        timetable.addNewTrainingSession(childTraining);
        TrainingSession adultTraining = new TrainingSession(group, secondCoach, trainDay, secondTrainTime, 15);
        timetable.addNewTrainingSession(adultTraining);

        assertEquals(1, timetable.getTrainingSessionsForDay(trainDay).size());

        String expected = "Невозможно добавить тренировку, группа занята" + System.lineSeparator();
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void testAddTrainingWithSameStartTimeWithExistingGroupTrain() {
        Coach firstCoach = new Coach("Васильев", "Николай", "Сергеевич");
        Coach secondCoach = new Coach("Смольников", "Виталий", "Алексеевич");
        Group group = new Group("Акробатика для детей", Age.CHILD);
        DayOfWeek trainDay = DayOfWeek.MONDAY;
        TimeOfDay trainTime = new TimeOfDay(13, 0);

        timetable.addNewTrainingSession(
                new TrainingSession(group, firstCoach, trainDay, trainTime, 60)
        );

        timetable.addNewTrainingSession(
                new TrainingSession(group, secondCoach, trainDay, trainTime, 60)
        );

        assertEquals(1, timetable.getTrainingSessionsForDayAndTime(trainDay, trainTime).size());
        String expected = "Невозможно добавить тренировку, группа занята" + System.lineSeparator();
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void testGetCountByCoachesSortsByTrainingCountDescending() {
        Coach firstCoach = new Coach("Васильев", "Николай", "Сергеевич");
        Coach secondCoach = new Coach("Смольников", "Виталий", "Алексеевич");
        Coach thirdCoach = new Coach("Крестоносцева", "Светлана", "Олеговна");
        Group firstGroup = new Group("Акробатика для детей", Age.CHILD);
        Group secondGroup = new Group("Гимнастика для взрослых", Age.ADULT);
        Group thirdGroup = new Group("Гимнастика для пенсионеров", Age.ADULT);


        TimeOfDay trainTime = new TimeOfDay(13, 0);

        for (DayOfWeek day : DayOfWeek.values()) {
            switch (day) {
                case MONDAY:
                case THURSDAY:
                case WEDNESDAY:
                    timetable.addNewTrainingSession(
                            new TrainingSession(firstGroup, firstCoach, day, trainTime, 60)
                    );
                case TUESDAY:
                case SATURDAY:
                    timetable.addNewTrainingSession(
                            new TrainingSession(secondGroup, secondCoach, day, trainTime, 60)
                    );
                case SUNDAY:
                    timetable.addNewTrainingSession(
                            new TrainingSession(thirdGroup, thirdCoach, day, trainTime, 60)
                    );
            }
        }

        Set<CounterOfTrainings> sortedTrainings = timetable.getCountByCoaches();

        Iterator<CounterOfTrainings> iterator = sortedTrainings.iterator();

        CounterOfTrainings counter = iterator.next();

        assertEquals(thirdCoach, counter.getCoach());
        assertEquals(6, counter.getCountOfTrains());

        counter = iterator.next();

        assertEquals(secondCoach, counter.getCoach());
        assertEquals(5, counter.getCountOfTrains());

        counter = iterator.next();

        assertEquals(firstCoach, counter.getCoach());
        assertEquals(3, counter.getCountOfTrains());
    }
}
