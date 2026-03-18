package reneakar.model.entities.items;

import reneakar.model.enums.ItemSubType;
import reneakar.model.enums.ItemType;
import reneakar.model.valueobjects.Position;

public class Treasure extends Item {
    private int value;
    public Treasure(Position position, ItemSubType subType, int value) {
        super(position, ItemType.TREASURE, subType);
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
