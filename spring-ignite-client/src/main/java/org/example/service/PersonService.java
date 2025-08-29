package org.example.service;

import org.example.model.Person;
import org.example.repository.PersonRepository;
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

    public Person findByName(String name) {
        return personRepository.findByName(name);
    }
}
