package reneakar.model.entities.behaviors;

import reneakar.model.entities.Player;

public interface UsableItem {
  boolean canUse(Player player);

  void use(Player player);
}
