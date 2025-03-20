# Capital Gains - Study Project

## About the Technical Decisions
  
- Java (21)
  - Chosen because it's the language I have the most experience with.
  - Benefits of version 21: support for Virtual Threads, performance improvements, and greater language maturity.
- Java Streams API for efficient data handling.
- BufferedReader used to improve input reading performance:
  - Reduces I/O calls when reading large volumes of data.
- Implementation of SOLID principles for better code organization and maintenance.
- Docker Containers
  - Facilitates running the project without requiring additional setup on the machine.
  - Provides environment isolation and consistency between executions.

## Libraries Used

- Jackson
  - For JSON object serialization and deserialization, making it easy to convert between Java objects and JSON.
- JUnit
  - For creating and running unit tests, ensuring code quality and functionality.
- AssertJ
  - Provides a fluent and readable API for assertions in tests, making result verification more intuitive.
- Mockito
  - For mocking dependencies in tests, allowing the simulation of object behavior and facilitating isolated component testing.

## How to Run the Project

    Note: Only Docker installation is required.
    Docker version used in this project: 28.0.1

### 1 - Build the Project
``` shell
    docker compose run --rm capital-gains-maven mvn clean install
```

### 2 - Run the Project

#### Example 1: Passing operations inline
``` shell  
    docker compose run --rm -T capital-gains-app java -jar target/capital-gains.jar \
    '[{"operation":"buy", "unit-cost":10.00, "quantity": 10000}]' \
    '[{"operation":"buy", "unit-cost":10.00, "quantity": 10000}]'
```  
#### Example 2: Passing operations inline with multiple lines
``` shell  
    docker compose run --rm -T capital-gains-app java -jar target/capital-gains.jar \
    '[{"operation":"buy", "unit-cost":10.00, "quantity": 10000}]
    [{"operation":"buy", "unit-cost":10.00, "quantity": 10000}]'
``` 
#### Example 3: Passing operations from a JSON file
``` shell  
    docker compose run --rm -T capital-gains-app java -jar target/capital-gains.jar < input.txt        
```

## How to Run the Tests
``` shell
    docker compose run --rm capital-gains-maven mvn test
```

