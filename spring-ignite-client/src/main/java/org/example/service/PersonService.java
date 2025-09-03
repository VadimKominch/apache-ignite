package org.example.service;

import org.apache.ignite.client.IgniteClient;
import org.example.model.Person;
import org.example.repository.PersonRepository;
import org.springframework.stereotype.Service;

@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final IgniteClient client;

    public PersonService(PersonRepository personRepository, IgniteClient client) {
        this.personRepository = personRepository;
        this.client = client;
    }

    public void savePerson(Person p) {
        this.personRepository.save(p.getId(), p); // попадет и в Ignite, и в БД
    }

    public Person findByName(String name) {
        return personRepository.findByName(name);
    }

//    public void runTask(String message) {
//        var tasks = Arrays.stream(message.split(" ")).map(PrintTask::new).toList();
//        client.compute().
//    }
}
