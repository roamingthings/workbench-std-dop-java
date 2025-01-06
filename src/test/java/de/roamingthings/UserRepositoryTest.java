package de.roamingthings;

import static org.assertj.core.api.Assertions.assertThat;
import de.roamingthings.model.Item;
import de.roamingthings.model.Order;
import de.roamingthings.model.User;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;
import software.amazon.awssdk.services.dynamodb.model.TableStatus;

@Slf4j
class UserRepositoryTest {

    private static final String TABLE_NAME = "std-dop-java-test-table-" + UUID.randomUUID();
    private static final DynamoDbClient dynamoDbClient = DynamoDbClient.create();

    private UserRepository userRepository;

    @BeforeAll
    static void setUpTable() {
        dynamoDbClient.createTable(builder -> builder
                .tableName(TABLE_NAME)
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .keySchema(
                        keySchemaBuilder -> keySchemaBuilder.attributeName("PK").keyType("HASH"),
                        keySchemaBuilder -> keySchemaBuilder.attributeName("SK").keyType("RANGE")
                )
                .attributeDefinitions(
                        attributeDefinitionBuilder -> attributeDefinitionBuilder.attributeName("PK").attributeType(ScalarAttributeType.S),
                        attributeDefinitionBuilder -> attributeDefinitionBuilder.attributeName("SK").attributeType(ScalarAttributeType.S)
                )
        );
        var retryConfig = RetryConfig.custom()
                .maxAttempts(30)
                .waitDuration(Duration.ofSeconds(5))
                .retryOnResult(Boolean.class::cast)
                .build();
        Retry.decorateSupplier(Retry.of("waitForTableCreated", retryConfig), () -> {
            log.info("🔄 - Waiting for table to be created: {}", TABLE_NAME);
            return dynamoDbClient.describeTable(builder -> builder.tableName(TABLE_NAME)).table().tableStatus() != TableStatus.ACTIVE;
        }).get();
        log.info("🛠  - Created test table {}.", TABLE_NAME);
    }

    @AfterAll
    static void deleteTable() {
        dynamoDbClient.deleteTable(builder -> builder.tableName(TABLE_NAME));
        log.info("🗑  - Deleted test table {}", TABLE_NAME);
    }

    @BeforeEach
    void setTableName() {
        userRepository = new UserRepository(TABLE_NAME, dynamoDbClient);
    }

    @Test
    void should_create_and_retrieve_item() {
        var userPk = "USER#" + UUID.randomUUID();
        var orderId = UUID.randomUUID().toString();
        var user = new User(userPk, "username", "firstName", "lastName", "toni@tester.de");
        var order = new Order(userPk, orderId, LocalDate.now(), BigDecimal.valueOf(new Random().nextDouble()));
        var expectedItem = List.<Item>of(user, order);
        userRepository.storeItemCollection(expectedItem);

        var actualItem = userRepository.findItemCollectionByUserPk(userPk);

        assertThat(actualItem).containsExactlyInAnyOrderEntriesOf(
                expectedItem.stream()
                        .collect(Collectors.groupingBy(Item::type))
        );
    }
}
