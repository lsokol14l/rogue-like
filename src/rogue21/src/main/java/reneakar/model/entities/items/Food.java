package reneakar.model.entities.items;

import reneakar.model.entities.Player;
import reneakar.model.entities.behaviors.UsableItem;
import reneakar.model.enums.ItemSubType;
import reneakar.model.enums.ItemType;
import reneakar.model.valueobjects.Position;

public class Food extends Item implements UsableItem {

    private int healthRestore;

    public Food(Position position, ItemSubType subType, int healthRestore) {
        super(position, ItemType.FOOD, subType);
        this.healthRestore = healthRestore;
    }

    public int getHealthRestore() {
        return healthRestore;
    }

    @Override
    public boolean canUse(Player player) {
        return player.getCurrentHealth() + healthRestore < player.getMaxHealth();
    }

    @Override
    public void use(Player player) {
        if (this.canUse(player)) {
            player.setCurrentHealth(player.getCurrentHealth() + healthRestore);
        } else {
            player.setCurrentHealth(player.getMaxHealth());
        }
    }
}
