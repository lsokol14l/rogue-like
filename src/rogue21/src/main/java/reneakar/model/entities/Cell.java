package reneakar.model.entities;

import reneakar.model.entities.items.Item;
import reneakar.model.enums.CellType;
import reneakar.model.valueobjects.Position;

public class Cell {
  private CellType type;
  private Position position;
  private Character occupant;
  private Item item;

  private boolean explored = false;  // Была ли клетка исследована
  private boolean visible = false;   // Видима ли клетка сейчас


  public Cell(CellType type, Position position) {
    this.type = type;
    this.position = position;
  }

  public boolean isPassable() {
    return type != CellType.WALL && type != CellType.NULL && !hasOccupant();
  }

  public boolean hasItem() {
    return item != null;
  }

  public boolean hasOccupant() {
    return occupant != null;
  }

  public CellType getType() {
    return type;
  }

  public void setType(CellType newType) {
    this.type = newType;
  }

  public Position getPosition() {
    return position;
  }

  public Character getOccupant() {
    return occupant;
  }

  public void setOccupant(Character newOccupant) {
    this.occupant = newOccupant;
  }

  public Item getItem() {
    return item;
  }

  public void setItem(Item newItem) {
    this.item = newItem;
  }

  public boolean isNeighborsWith(Cell other) {
    return this.getPosition().isNeighborsWith(other.getPosition());
  }

  public boolean isExplored() {
    return explored;
  }

  public void setExplored(boolean explored) {
    this.explored = explored;
  }

  public boolean isVisible() {
    return visible;
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }

}
