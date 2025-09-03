package org.example.runner;

import org.example.model.Person;
import org.example.service.PersonService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class IgniteCommandLineRunner implements CommandLineRunner {
    private final PersonService service;

    public IgniteCommandLineRunner(PersonService service) {
        this.service = service;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Executing command line runner");
//        Person employeeDTO = new Person();
//        employeeDTO.setId(2);
//        employeeDTO.setName("Ivan");
//        employeeDTO.setCityId(2);
//
//        System.out.println("Here");
//        service.savePerson(employeeDTO);
//        Person personFromDb = service.findByName("John");
//        Person personFromDbNotFound = service.findByName("John1");

        long start = System.currentTimeMillis();
        System.out.println(service.getUserById("1")); // slow
        System.out.println("First call: " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        System.out.println(service.getUserById("1")); // cached
        System.out.println("Second call: " + (System.currentTimeMillis() - start) + "ms");
    }
}
