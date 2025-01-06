package de.roamingthings.model;

import static de.roamingthings.model.Item.getStringAttributeOrNull;
import static de.roamingthings.model.Item.s;
import java.util.Map;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public record User(String userPk, String username, String firstName, String lastName, String email) implements Item {

    enum Keys {
        PK, SK, Username, FirstName, LastName, Email;
    }

    static boolean isSk(AttributeValue sk) {
        var sortKey = SortKey.fromAttribute(sk);
        return sortKey.type().equals(ItemType.USER.name());
    }

    @Override
    public Map<String, AttributeValue> item() {
        return Map.of(
                Keys.PK.name(), pk(),
                Keys.SK.name(), sk().toAttributeValue(),
                Keys.Username.name(), s(username),
                Keys.FirstName.name(), s(firstName),
                Keys.LastName.name(), s(lastName),
                Keys.Email.name(), s(email)
        );
    }

    public static Item fromItem(Map<String, AttributeValue> item) {
        var pk = item.get(Keys.PK.name()).s();
        var username = getStringAttributeOrNull(item, Keys.Username.name());
        var firstName = getStringAttributeOrNull(item, Keys.FirstName.name());
        var lastName = getStringAttributeOrNull(item, Keys.LastName.name());
        var email = getStringAttributeOrNull(item, Keys.Email.name());
        return new User(pk, username, firstName, lastName, email);
    }

    @Override
    public String pkAsString() {
        return userPk;
    }

    @Override
    public ItemType type() {
        return ItemType.USER;
    }
}
