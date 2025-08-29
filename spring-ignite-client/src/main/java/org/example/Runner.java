package org.example;

//import org.apache.ignite.springdata.repository.config.EnableIgniteRepositories;
import org.example.model.Person;
import org.example.service.PersonService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//@ComponentScan(basePackages = "org.example")
public class Runner {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(Runner.class, args);
        PersonService service = context.getBean(PersonService.class);

        Person employeeDTO = new Person();
        employeeDTO.setId(1);
        employeeDTO.setName("John");
        employeeDTO.setCityId(1);

        service.savePerson(employeeDTO);
        Person personFromDb = service.findByName("John");
        Person personFromDbNotFound = service.findByName("John1");
    }
}
