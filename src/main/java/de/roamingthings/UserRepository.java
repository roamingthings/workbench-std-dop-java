package de.roamingthings;

import de.roamingthings.model.Item;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItem;

public class UserRepository {

    private final String tableName;
    private final DynamoDbClient dynamoDbClient;

    public UserRepository(String tableName, DynamoDbClient dynamoDbClient) {
        this.tableName = tableName;
        this.dynamoDbClient = dynamoDbClient;
    }

    public Map<Item.ItemType, List<Item>> findItemCollectionByUserPk(String userPk) {
        var response = dynamoDbClient.query(builder -> builder
                .tableName(tableName)
                .keyConditionExpression("PK = :v_pk")
                .expressionAttributeValues(Map.of(":v_pk", Item.s(userPk)))
        );
        return response.items()
                .stream()
                .map(Item::fromItem)
                .collect(Collectors.groupingBy(Item::type));
    }

    public void storeItemCollection(List<Item> items) {
        dynamoDbClient.transactWriteItems(builder -> builder.transactItems(
                items.stream()
                        .map(Item::item)
                        .map(item -> TransactWriteItem.builder()
                                .put(put -> put.tableName(tableName).item(item))
                                .build())
                        .collect(Collectors.toList())));
    }
}
