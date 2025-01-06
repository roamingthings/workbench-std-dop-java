package de.roamingthings.model;

import static de.roamingthings.model.Item.getBigDecimalAttributeOrNull;
import static de.roamingthings.model.Item.getLocalDateAttributeOrNull;
import static de.roamingthings.model.Item.getStringAttributeOrNull;
import static de.roamingthings.model.Item.n;
import static de.roamingthings.model.Item.s;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public record Order(String userPk, String orderId, LocalDate orderDate, BigDecimal totalAmount) implements Item {

    enum Keys {
        PK, SK, OrderId, OrderDate, TotalAmount;
    }

    static boolean isSk(AttributeValue sk) {
        var sortKey = SortKey.fromAttribute(sk);
        return sortKey.type().equals(ItemType.ORDER.name());
    }

    @Override
    public Map<String, AttributeValue> item() {
        return Map.of(
                Keys.PK.name(), pk(),
                Keys.SK.name(), sk().toAttributeValue(),
                Keys.OrderId.name(), s(orderId),
                Keys.OrderDate.name(), s(orderDate.toString()),
                Keys.TotalAmount.name(), n(totalAmount.toString())
        );
    }

    public static Item fromItem(Map<String, AttributeValue> item) {
        var pk = item.get(Keys.PK.name()).s();
        var orderId = getStringAttributeOrNull(item, Keys.OrderId.name());
        var orderDate = getLocalDateAttributeOrNull(item, Keys.OrderDate.name());
        var totalAmount = getBigDecimalAttributeOrNull(item, Keys.TotalAmount.name());
        return new Order(pk, orderId, orderDate, totalAmount);
    }

    @Override
    public String pkAsString() {
        return userPk;
    }

    @Override
    public ItemType type() {
        return ItemType.ORDER;
    }

    @Override
    public SortKey sk() {
        return new SortKey(type().name(), orderId);
    }
}
