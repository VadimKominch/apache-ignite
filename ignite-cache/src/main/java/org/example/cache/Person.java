package org.example.cache;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

import java.io.Serializable;

public class Person implements Serializable {
    @QuerySqlField(index=true)
    private int id;
    @QuerySqlField
    private String name;
    @QuerySqlField
    private int age;

    public Person(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return "Person{id=" + id + ", name='" + name + "', age=" + age + '}';
    }
}
