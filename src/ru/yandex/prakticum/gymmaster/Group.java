package ru.yandex.prakticum.gymmaster;

import java.util.Objects;

public class Group {
    //название группы
    private String title;
    //тип (взрослая или детская)
    private Age age;

    public Group(String title, Age age) {
        this.title = title;
        this.age = age;
    }

    public String getTitle() {
        return title;
    }

    public Age getAge() {
        return age;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return Objects.equals(title, group.title) && age == group.age;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, age);
    }
}
