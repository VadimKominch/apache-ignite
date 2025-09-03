package org.example.task;

import org.apache.ignite.lang.IgniteCallable;

public class PrintTask implements IgniteCallable<Integer> {
    private final String message;

    public PrintTask(String message) {
        this.message = message;
    }

    @Override
    public Integer call() throws Exception {
        System.out.println("Message from thin client: " + message);
        return message.length();
    }
}
