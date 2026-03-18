package reneakar.model.enums;


import java.util.ArrayList;
import java.util.List;

public enum ItemSubType {

  COPPER_COIN(ItemType.TREASURE),
  GOLD_COINS(ItemType.TREASURE),
  SILVER_BARS(ItemType.TREASURE),
  DIAMOND(ItemType.TREASURE),

  BREAD(ItemType.FOOD),
  APPLE(ItemType.FOOD),
  MEAT(ItemType.FOOD),

  HEALTH_ELIXIR(ItemType.ELIXIR),
  STRENGTH_ELIXIR(ItemType.ELIXIR),
  AGILITY_ELIXIR(ItemType.ELIXIR),

  HEALTH_SCROLL(ItemType.SCROLL),
  STRENGTH_SCROLL(ItemType.SCROLL),
  AGILITY_SCROLL(ItemType.SCROLL),


  SWORD(ItemType.WEAPON),
  DAGGER(ItemType.WEAPON),
  STAFF(ItemType.WEAPON);

  private final ItemType parentType;

  ItemSubType(ItemType parentType) {
    this.parentType = parentType;
  }

  public ItemType getParentType() {
    return parentType;
  }

  public boolean isOfType(ItemType type) {
    return this.parentType == type;
  }

  public static List<ItemSubType> getSubtypesFor(ItemType type) {
    List<ItemSubType> result = new ArrayList<>();
    for (ItemSubType subType : values()) {
      if (subType.parentType == type) {
        result.add(subType);
      }
    }
    return result;
  }
}


