# Single Table Design with Modern Java

This repository contains the sample code and project structure for the talk **"Single Table Design Revisited: Modern Java and Data-Oriented Programming"**. The goal of this repository is to demonstrate how to use modern Java features (e.g., records, sealed classes, pattern matching) in conjunction with Data-Oriented Programming principles to implement a DynamoDB-based Single Table Design.

## Key Features

- **Single Table Design**: Efficiently organizes data using partition and sort keys.
- **Data-Oriented Programming**: Simplifies data representation and operations.
- **Modern Java Constructs**: Uses sealed classes, records, and pattern matching to improve code clarity and maintainability.
- **AWS DynamoDB Integration**: Provides examples for querying, storing, and managing items.

---

## Project Structure

- `src/main/java/de/roamingthings/model`
    - `Item.java`: The base sealed interface representing different entity types.
    - `User.java`: Record representing user entities.
    - `Order.java`: Record representing order entities.
- `src/main/java/de/roamingthings`
    - `UserRepository.java`: Class implementing data access methods for DynamoDB.
- `src/test/java/de/roamingthings`
    - `UserRepositoryTest.java`: JUnit tests for verifying repository functionality.

---

## Prerequisites

- [Java 21](https://docs.aws.amazon.com/corretto/latest/corretto-21-ug/downloads-list.html) or later
- [AWS DynamoDB Local](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.html) or access to AWS DynamoDB
- [Gradle](https://gradle.org/install/) for build and dependency management

---

## Setting Up the Project

1. **Clone the repository**:

   ```bash
   git clone <repository-url>
   cd <repository-folder>
   ```

2. **Configure AWS SDK**:

    - Ensure that you have valid credentials to create, delete tables and read/write data.

3. **Build the project and run tests**:

   ```bash
   ./gradlew build
   ```

---

## Key Concepts Illustrated

### Single Table Design

- Organizes multiple entity types (e.g., `User`, `Order`) in a single DynamoDB table using partition (`PK`) and sort (`SK`) keys.
- Uses a structured sort key naming convention (`<Type>#<Identifier>`).

### Data-Oriented Programming with Modern Java

- **Sealed Interfaces**: `Item` as a sealed interface enforces a controlled hierarchy.
- **Records**: Immutable data representation for entities like `User` and `Order`.
- **Pattern Matching**: Simplifies transformations and type handling.

### AWS DynamoDB Integration

- Example methods for querying and storing data using the AWS SDK for Java.
- Transactional writes to ensure consistency across multiple entities.

---

## Key Takeaways

- How to leverage modern Java features to simplify data access and manipulation.
- Best practices for designing a Single Table in DynamoDB.
- Real-world application of Data-Oriented Programming principles.

---

## Resources

- [DynamoDB Documentation](https://docs.aws.amazon.com/dynamodb/index.html)
- [AWS SDK for Java](https://docs.aws.amazon.com/sdk-for-java/)
- [Java 21 Features](https://openjdk.org/projects/jdk/21/)

