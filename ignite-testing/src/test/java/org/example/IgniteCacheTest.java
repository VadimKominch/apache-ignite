package org.example;

import net.bytebuddy.utility.RandomString;
import org.example.service.PersonService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class IgniteCacheTest {

    @Autowired
    PersonService userService;

    @Test
    void testCache() {
        String idToFetch = RandomString.make(3);
        long start = System.currentTimeMillis();
        System.out.println(userService.getUserById(idToFetch)); // slow
        System.out.println("First call: " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        System.out.println(userService.getUserById(idToFetch)); // cached
        System.out.println("Second call: " + (System.currentTimeMillis() - start) + "ms");

        userService.clearCache();
    }
}
