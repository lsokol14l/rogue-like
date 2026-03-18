package reneakar.model.entities;

import reneakar.model.entities.items.Item;
import reneakar.model.entities.items.Treasure;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Backpack {
  private final int capacity;
  private final List<Item> items;

  public Backpack(int capacity) {
    this.capacity = capacity;
    this.items = new ArrayList<>();
  }

  public boolean addItem(Item item) {
    if(item instanceof Treasure) {
      var treasure = getTreasure();
      if(treasure.isPresent())
      {
        Treasure existingTreasure = treasure.get();
        existingTreasure.setValue(existingTreasure.getValue() + ((Treasure) item).getValue());
        return true;
      }
    }

    if (items.size() < capacity) {
      items.add(item);
      return true;
    }
    return false;
  }

  private Optional<Treasure> getTreasure() {
    return items.stream().filter(e -> e instanceof Treasure).map(e -> (Treasure) e).findFirst();
  }

  public void removeItem(Item item) {
    items.remove(item);
  }

  public List<Item> getItems() {
    return new ArrayList<>(items);
  }

  public int getCapacity() {
    return capacity;
  }

  public int getSize() {
    return items.size();
  }

  public boolean isFull() {
    return items.size() >= capacity;
  }
}
