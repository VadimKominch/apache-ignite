import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootApplication
public class Runner {
    private static PostgreSQLContainer<?> postgresContainer;

    public static void main(String[] args) {
        if (postgresContainer == null) {
            postgresContainer = new PostgreSQLContainer<>("postgres:15.3")
                    .withDatabaseName("testdb")
                    .withUsername("user")
                    .withPassword("pass");
            postgresContainer.start();
            System.out.println(postgresContainer.getJdbcUrl());
            System.out.println(postgresContainer.getUsername());
            System.out.println(postgresContainer.getPassword());

//            SpringApplication.run(Runner.class, args);
            // Register shutdown hook
//            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//                System.out.println("Stopping PostgreSQL container...");
//                stop();
//            }));
        }
    }

    public static String getJdbcUrl() {
        return postgresContainer.getJdbcUrl();
    }

    public static String getUsername() {
        return postgresContainer.getUsername();
    }

    public static String getPassword() {
        return postgresContainer.getPassword();
    }

    public static void stop() {
        if (postgresContainer != null) {
            postgresContainer.stop();
            postgresContainer = null;
        }
    }
}
