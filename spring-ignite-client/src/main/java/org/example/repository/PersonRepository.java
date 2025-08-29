package org.example.repository;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlQuery;
import org.example.model.Person;
import org.springframework.stereotype.Repository;

import javax.cache.Cache;

@Repository
public class PersonRepository {
    private final IgniteCache<Integer, Person> cache;

    public PersonRepository(Ignite ignite) {
        this.cache = ignite.cache("person-cache");
    }

    public Person save(Integer id, Person person) {
        cache.put(id, person);
        return person;
    }

    public Person findById(Integer id) {
        return cache.get(id);
    }

    public void deleteById(Integer id) {
        cache.remove(id);
    }

    public Person findByName(String name) {
        SqlQuery<Integer, Person> query =
                new SqlQuery<>(Person.class, "name = ?");
        try (QueryCursor<Cache.Entry<Integer, Person>> cursor =
                     cache.query(query.setArgs(name))) {
            var result  = cursor.getAll()
                    .stream()
                    .map(Cache.Entry::getValue)
                    .toList();
            if(result.size() == 1) return result.get(0);
            else return null;
        }
    }
}
