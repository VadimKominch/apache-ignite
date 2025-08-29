package org.example.controller;

import org.example.model.Person;
import org.example.service.PersonService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class PersonController {
    private final PersonService service;

    public PersonController(PersonService service) {
        this.service = service;
    }

    @PostMapping
    public String add(@RequestBody Person user) {
        service.savePerson(user);
        return "Saved " + user.getName();
    }

    @GetMapping("/search")
    public Person search(@RequestParam String name) {
        return service.findByName(name);
    }
}
