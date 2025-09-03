package org.example.task;

import org.apache.ignite.lang.IgniteCallable;

import java.time.OffsetDateTime;
import java.util.Date;

public class PrintTask  implements IgniteCallable<Integer> {
    private final String message;

    public PrintTask(String message) {
        this.message = message;
    }

    @Override
    public Integer call() throws Exception {
        System.out.println("Message from thin client: " + message + " at "+ OffsetDateTime.now());
        int result =  message.length();
        System.out.println("Task with string:" + message +" completed with result: " + result + " at "+ OffsetDateTime.now());
        return result;
    }
}
