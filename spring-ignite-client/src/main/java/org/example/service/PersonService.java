package org.example.service;

import org.example.model.Person;
import org.example.repository.PersonRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class PersonService {

    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public void savePerson(Person p) {
        this.personRepository.save(p.getId(), p); // попадет и в Ignite, и в БД
    }

    @Cacheable(cacheNames = "user-cache")
    public String getUserById(String id) {
        // Simulate slow DB
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        return "User-" + id;
    }

    @CacheEvict(cacheNames = "person-cache")
    public void clearCache() { }

    @Cacheable(cacheNames = "person-cache")
    public Person findByName(String name) {
        return personRepository.findByName(name);
    }
}
