package reneakar.model.entities.behaviors;

import reneakar.model.entities.Cell;
import reneakar.model.entities.Player;
import reneakar.model.valueobjects.Position;

import java.util.List;

public interface EquippableItem {
  boolean canEquip(Player player);

  void equip(Player player);

  Position unequip(List<Cell> cells, Player player);
}
