package reneakar.model.entities.items;

import reneakar.model.entities.Cell;
import reneakar.model.entities.Player;
import reneakar.model.entities.behaviors.EquippableItem;
import reneakar.model.enums.ItemSubType;
import reneakar.model.enums.ItemType;
import reneakar.model.valueobjects.Position;

import java.util.List;

public class Weapon extends Item implements EquippableItem {
    private final int strengthBonus;
    private boolean isEquipped;

    public Weapon(Position position, ItemSubType subType, int strengthBonus, boolean isEquipped) {
        super(position, ItemType.WEAPON, subType);
        this.strengthBonus = strengthBonus;
        this.isEquipped = false;
    }

    public int getStrengthBonus() {
        return strengthBonus;
    }

    public boolean isEquipped() {
        return isEquipped;
    }

    @Override
    public boolean canEquip(Player player) {
        return !isEquipped && player.getBackpack().getItems().contains(this);
    }

    @Override
    public void equip(Player player) {
        // Экипируем оружие (оно остаётся в рюкзаке!)
        this.isEquipped = true;
        player.setCurrentWeapon(this);
        // Добавляем бонус к силе
        player.setStrength(player.getStrength() + strengthBonus);
    }

    @Override
    public Position unequip(List<Cell> cells, Player player) {
        // Снимаем оружие (оно остаётся в рюкзаке!)
        this.isEquipped = false;
        player.setCurrentWeapon(null);
        // Убираем бонус силы
        player.setStrength(player.getStrength() - strengthBonus);
        return null;
    }
    
    // Метод для выбрасывания оружия на землю
    public Position dropToGround(List<Cell> cells, Player player) {
        // 1. Снимаем если экипировано
        if (this.isEquipped) {
            unequip(cells, player);
        }
        
        // 2. Удаляем из рюкзака
        player.getBackpack().removeItem(this);
        
        // 3. Ищем свободную соседнюю клетку
        for (Cell cell : cells) {
            if (cell.getPosition().isNeighborsWith(player.getPosition()) 
                && !cell.hasItem() 
                && cell.isPassable()) {
                return cell.getPosition();
            }
        }
        
        // 4. Если нет свободной - под игроком
        return player.getPosition();
    }
}

