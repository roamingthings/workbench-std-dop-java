package de.roamingthings.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public sealed interface Item permits User, Order {

    enum GlobalKeys {
        PK, SK
    }

    enum ItemType {
        USER,
        ORDER,
    }

    Map<String, Function<Map<String, AttributeValue>, Item>> ITEM_FACTORIES = Map.of(
            ItemType.USER.name(), User::fromItem,
            ItemType.ORDER.name(), Order::fromItem
    );

    Map<String, AttributeValue> item();

    record SortKey(String type, String value) {

        static SortKey fromAttribute(AttributeValue value) {
            var stringValue = value.s();
            if (stringValue.contains("#")) {
                var parts = stringValue.split("#");
                return new SortKey(parts[0], parts[1]);
            } else {
                return new SortKey(stringValue, null);
            }
        }

        AttributeValue toAttributeValue() {
            return (value != null) ? AttributeValue.fromS(type + "#" + value) : AttributeValue.fromS(type);
        }
    }

    static AttributeValue s(String value) {
        return AttributeValue.fromS(value);
    }

    static AttributeValue n(String value) {
        return AttributeValue.fromN(value);
    }

    static String getStringAttributeOrNull(Map<String, AttributeValue> item, String key) {
        return Optional.ofNullable(item.get(key))
                .map(AttributeValue::s)
                .orElse(null);
    }

    static LocalDate getLocalDateAttributeOrNull(Map<String, AttributeValue> item, String key) {
        return Optional.ofNullable(item.get(key))
                .map(AttributeValue::s)
                .map(LocalDate::parse)
                .orElse(null);
    }

    static BigDecimal getBigDecimalAttributeOrNull(Map<String, AttributeValue> item, String key) {
        return Optional.ofNullable(item.get(key))
                .map(AttributeValue::n)
                .map(BigDecimal::new)
                .orElse(null);
    }

    static Item fromItem(Map<String, AttributeValue> item) {
        var skType = SortKey.fromAttribute(item.get(GlobalKeys.SK.name())).type();
        return Optional.ofNullable(ITEM_FACTORIES.get(skType))
                .orElseThrow(() -> new IllegalArgumentException("Unknown item type: " + skType))
                .apply(item);
    }

    default String pkAsString() {
        return pk().s();
    }

    ItemType type();

    default AttributeValue pk() {
        return AttributeValue.fromS(pkAsString());
    }

    default SortKey sk() {
        return new SortKey(type().name(), null);
    }
}
