package org.example.model;

import org.apache.ignite.cache.query.annotations.QuerySqlField;
import javax.persistence.*;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "person")
public class Person implements Serializable {

    @Serial
    private static final long serialVersionUID = -3964651601523124638L;
    @Id
    @QuerySqlField(index = true) // индекс для Ignite SQL
    private Integer id;

    @QuerySqlField(index = true)
    private String name;

    @QuerySqlField
    private Integer cityId;

    public Person() {
    }

    public Person(Integer id, String name, Integer cityId) {
        this.id = id;
        this.name = name;
        this.cityId = cityId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }
}

