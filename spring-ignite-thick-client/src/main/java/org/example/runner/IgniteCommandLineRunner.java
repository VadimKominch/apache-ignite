package org.example.runner;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCompute;
import org.example.task.PrintTask;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class IgniteCommandLineRunner implements CommandLineRunner {
    private final Ignite ignite;

    public IgniteCommandLineRunner(Ignite ignite) {
        this.ignite = ignite;
    }

    @Override
    public void run(String... args) throws Exception {
        ignite.cache("person-cache").metrics().
        System.out.println("Executing compute task command line runner");
        IgniteCompute compute = ignite.compute();
        List<PrintTask> computeList = new ArrayList<>();
        for (String word : "Print words on different cluster nodes".split(" ")) {
            computeList.add(new PrintTask(word));
        }
        String result = ignite.compute().call(computeList).stream().map(String::valueOf).collect(Collectors.joining(" "));
        System.out.println("Computing result is " + result);
    }
}
