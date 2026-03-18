package reneakar.model.entities.items;

import reneakar.model.entities.Player;
import reneakar.model.enums.ItemSubType;
import reneakar.model.enums.ItemType;
import reneakar.model.valueobjects.Position;

public abstract class Item {
    private final Position position;
    private final ItemType type;
    private final ItemSubType subType;

    public Item(Position position, ItemType type, ItemSubType subType) {
        this.position = position;
        this.type = type;
        this.subType = subType;
    }

    public Position getPosition() {
        return position;
    }

    public ItemType getType() {
        return type;
    }

    public ItemSubType getSubType() {
        return subType;
    }
}
