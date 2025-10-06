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
        var key = ignite.binary().builder(Object.class.getSimpleName())
                .setField("iteration", 1)
                .build();
        var value = ignite.binary().builder(Object.class.getSimpleName())
                .setField("element", "elementStr")
                .setField("exception","exception")
                .build();
        ignite.getOrCreateCache("errors").put(key,value);

        System.out.println("Executing compute task command line runner");
        IgniteCompute compute = ignite.compute();
        List<PrintTask> computeList = new ArrayList<>();
        for (String word : "Print words on different cluster nodes".split(" ")) {
            computeList.add(new PrintTask(word));
        }
        String result = compute.call(computeList).stream().map(String::valueOf).collect(Collectors.joining(" "));
        System.out.println("Computing result is " + result);

//        ignite.services().deployClusterSingleton("eventHandlerService", new MyCustomService());

    }
}
