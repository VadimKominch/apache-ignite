# Spring Boot  + Apache Ignite + postgres + test containers

## Goals

- Run a **Spring Boot app** with TestContainers PostgreSQL database
- Run **Apache Ignite** server node to use it as a cache
- Run thick or thin client to run compute tasks or to use a cache

---

## Stack

- **Spring Boot** (Java, Maven)
- **PostgreSQL** database
- **TestContainers** for postgres env
- **Apache Ignite** as a calculation platform

---

## Running

Before running need to add VM options for env of JAva 17

```bash
java
--add-opens=java.base/jdk.internal.access=ALL-UNNAMED
--add-opens=java.base/jdk.internal.misc=ALL-UNNAMED
--add-opens=java.base/sun.nio.ch=ALL-UNNAMED
--add-opens=java.base/sun.util.calendar=ALL-UNNAMED
--add-opens=java.management/com.sun.jmx.mbeanserver=ALL-UNNAMED
--add-opens=jdk.internal.jvmstat/sun.jvmstat.monitor=ALL-UNNAMED
--add-opens=java.base/sun.reflect.generics.reflectiveObjects=ALL-UNNAMED
--add-opens=jdk.management/com.sun.management.internal=ALL-UNNAMED
--add-opens=java.base/java.io=ALL-UNNAMED
--add-opens=java.base/java.nio=ALL-UNNAMED
--add-opens=java.base/java.net=ALL-UNNAMED
--add-opens=java.base/java.util=ALL-UNNAMED
--add-opens=java.base/java.util.concurrent=ALL-UNNAMED
--add-opens=java.base/java.util.concurrent.locks=ALL-UNNAMED
--add-opens=java.base/java.util.concurrent.atomic=ALL-UNNAMED
--add-opens=java.base/java.lang=ALL-UNNAMED
--add-opens=java.base/java.lang.invoke=ALL-UNNAMED
--add-opens=java.base/java.math=ALL-UNNAMED
--add-opens=java.sql/java.sql=ALL-UNNAMED
--add-opens=java.base/java.lang.reflect=ALL-UNNAMED
--add-opens=java.base/java.time=ALL-UNNAMED
--add-opens=java.base/java.text=ALL-UNNAMED
--add-opens=java.management/sun.management=ALL-UNNAMED
--add-opens
java.desktop/java.awt.font=ALL-UNNAMED
```

### 1️⃣ Run spring-ignite module as application

### 2️⃣ Run spring-ignite-client module as a thin client

### 3️⃣ Run spring-ignite-thick-client as a thick client


## Testing

### 4️⃣ Run ignite-testing module tests